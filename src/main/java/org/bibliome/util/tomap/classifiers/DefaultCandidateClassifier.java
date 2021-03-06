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

package org.bibliome.util.tomap.classifiers;

import java.util.Collections;
import java.util.List;

import org.bibliome.util.tomap.Candidate;

public class DefaultCandidateClassifier extends AbstractCandidateClassifier {
	private final String conceptID;
	
	public DefaultCandidateClassifier(String name, String conceptID) {
		super(name);
		this.conceptID = conceptID;
	}

	@Override
	public List<Attribution> classify(Candidate candidate) {
		Attribution attr = new Attribution(this, conceptID, 1);
		return Collections.singletonList(attr);
	}
}
