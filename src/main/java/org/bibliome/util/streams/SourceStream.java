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

package org.bibliome.util.streams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;

import org.bibliome.util.Checkable;

public interface SourceStream extends Checkable {
	String getStreamName(Object stream);
	Collection<String> getStreamNames();
	Iterator<InputStream> getInputStreams() throws IOException;
	InputStream getInputStream() throws IOException;
	Iterator<Reader> getReaders() throws IOException;
	Reader getReader() throws IOException;
	Iterator<BufferedReader> getBufferedReaders() throws IOException;
	BufferedReader getBufferedReader() throws IOException;
	String getCharset();
}
