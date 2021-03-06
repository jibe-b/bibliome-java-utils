/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.bibliome.util.taxonomy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.bibliome.util.filelines.FileLines;
import org.bibliome.util.filelines.InvalidFileLineEntry;
import org.bibliome.util.filelines.TabularFormat;
import org.bibliome.util.files.InputFile;
import org.bibliome.util.streams.FileSourceStream;
import org.bibliome.util.streams.SourceStream;
import org.bibliome.util.taxonomy.reject.RejectConjunction;
import org.bibliome.util.taxonomy.reject.RejectName;
import org.bibliome.util.taxonomy.reject.RejectNamePattern;
import org.bibliome.util.taxonomy.reject.RejectNone;
import org.bibliome.util.taxonomy.reject.RejectTaxid;
import org.bibliome.util.taxonomy.saturate.Saturate;
import org.bibliome.util.taxonomy.saturate.SaturatePattern;

/**
 * NCBI taxonomy.
 * @author rbossy
 *
 */
public class NCBITaxonomy {
	private final Map<Integer,Taxon> taxa = new HashMap<Integer,Taxon>();
	
	private final class NodesFileLines extends DmpFileLines<int[]> {
		private final StringCache rankCache = new StringCache();
		
		private NodesFileLines() {
			super(14);
		}

		@Override
		public void processEntry(int[] data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			int id = Integer.parseInt(entry.get(0));
			data[id] = Integer.parseInt(entry.get(1));
			Taxon taxon = new Taxon(id, rankCache.get(entry.get(2)), Integer.parseInt(entry.get(4)));
			addTaxon(taxon);
		}
	}
	
	private void addTaxon(Taxon taxon) {
		int id = taxon.getTaxid();
		if (taxa.containsKey(id))
			throw new IllegalArgumentException();
		taxa.put(id, taxon);
	}
	
	/**
	 * Reads a NCBI nodes.dmp file.
	 * @param logger
	 * @param file
	 * @param size
	 * @throws IOException
	 * @throws InvalidFileLineEntry
	 */
	public void readNodes(Logger logger, File file, int size) throws IOException, InvalidFileLineEntry {
		logger.info("reading nodes file: " + file.getCanonicalPath());
		NodesFileLines fl = new NodesFileLines();
		fl.setLogger(logger);
		int[] parent = new int[size];
		fl.process(file, DmpFileLines.CHARSET, parent);
		for (Taxon taxon : taxa.values()) {
			int taxid = taxon.getTaxid();
			int parentId = parent[taxid];
			taxon.setParent(taxa.get(taxid == parentId ? null : parentId));
		}
	}
	
	private static final class NamesFileLines extends DmpFileLines<NCBITaxonomy> {
		private final StringCache typeCache = new StringCache();
		private final RejectName reject;
		
		private NamesFileLines() {
			this(RejectNone.INSTANCE);
		}
		
		private NamesFileLines(RejectName reject) {
			super(5);
			this.reject = reject;
		}

		@Override
		public void processEntry(NCBITaxonomy data, int lineno, List<String> entry)	throws InvalidFileLineEntry {
			int id = Integer.parseInt(entry.get(0));
			Taxon taxon = data.taxa.get(id);
			taxon.addName(reject, entry.get(1), typeCache.get(entry.get(3)));
		}
	}
	
	/**
	 * Reads a NCBI names.dmp file.
	 * @param logger
	 * @param file
	 * @throws IOException
	 * @throws InvalidFileLineEntry
	 */
	public void readNames(Logger logger, File file) throws IOException, InvalidFileLineEntry {
		logger.info("reading names file: " + file.getCanonicalPath());
		NamesFileLines fl = new NamesFileLines();
		fl.setLogger(logger);
		fl.process(file, DmpFileLines.CHARSET, this);
	}

	public void readNames(Logger logger, File file, RejectName reject) throws IOException, InvalidFileLineEntry {
		logger.info("reading names file: " + file.getCanonicalPath());
		NamesFileLines fl = new NamesFileLines(reject);
		fl.setLogger(logger);
		fl.process(file, DmpFileLines.CHARSET, this);
	}

	/**
	 * Removes all names rejected by the specified name reject.
	 * @param reject
	 */
	public void reject(RejectName reject) {
		for (Taxon taxon : taxa.values())
			taxon.reject(reject);
	}

	/**
	 * Adds all names according to the specified saturator.
	 * @param saturate
	 */
	public void saturate(Saturate saturate) {
		for (Taxon taxon : taxa.values())
			taxon.saturate(saturate);
	}
	
	public void saturate(RejectName reject, Saturate saturate) {
		for (Taxon taxon : taxa.values())
			taxon.saturate(reject, saturate);
	}

	/**
	 * Reads a name reject file.
	 * @param logger
	 * @param file
	 * @throws IOException
	 */
	public static Collection<RejectName> readReject(Logger logger, File file) throws IOException {
		logger.info("reading rejection file: " + file.getCanonicalPath());
		Collection<RejectName> result = new ArrayList<RejectName>();
		InputFile inputFile = new InputFile(file.getCanonicalPath());
		SourceStream source = new FileSourceStream(DmpFileLines.CHARSET, inputFile);
		BufferedReader r = source.getBufferedReader();
		while (true) {
			String line = r.readLine();
			if (line == null)
				break;
			line = line.trim();
			if (line.isEmpty())
				continue;
			result.add(getReject(line));
		}
		return result;
	}

	private static RejectName getReject(String line) {
		int tab = line.indexOf('\t');
		if (tab >= 0)
			return new RejectConjunction(getReject(line.substring(0, tab)), getReject(line.substring(tab+1)));
		try {
			return new RejectTaxid(Integer.parseInt(line));
		}
		catch (NumberFormatException nfe) {
			return new RejectNamePattern(Pattern.compile(line));
		}
	}
	
	private static class SaturateFileLines extends FileLines<Collection<Saturate>> {
		private SaturateFileLines() {
			super();
			TabularFormat format = getFormat();
			format.setSeparator('\t');
			format.setMinColumns(3);
			format.setMaxColumns(Integer.MAX_VALUE);
			format.setTrimColumns(true);
			format.setSkipBlank(true);
			format.setSkipEmpty(true);
		}

		@Override
		public void processEntry(Collection<Saturate> data, int lineno, List<String> entry) throws InvalidFileLineEntry {
			Collection<MessageFormat> formats = new ArrayList<MessageFormat>(entry.size() - 2);
			for (int i = 2; i < entry.size(); ++i)
				formats.add(new MessageFormat(entry.get(i)));
			data.add(new SaturatePattern(Pattern.compile(entry.get(0)), entry.get(1), formats));
		}
	}
	
	/**
	 * Reads a name saturator file.
	 * @param logger
	 * @param file
	 * @throws IOException
	 * @throws InvalidFileLineEntry
	 */
	public static Collection<Saturate> readSaturate(Logger logger, File file) throws IOException, InvalidFileLineEntry {
		logger.info("reading saturation file: " + file.getCanonicalPath());
		SaturateFileLines fl = new SaturateFileLines();
		Collection<Saturate> result = new ArrayList<Saturate>();
		fl.process(file, DmpFileLines.CHARSET, result);
		return result;
	}
	
	/**
	 * Returns all taxa in this taxonomy.
	 */
	public Collection<Taxon> getTaxa() {
		return Collections.unmodifiableCollection(taxa.values());
	}
	
	public Taxon getTaxon(int taxId) {
		return taxa.get(taxId);
	}
}
