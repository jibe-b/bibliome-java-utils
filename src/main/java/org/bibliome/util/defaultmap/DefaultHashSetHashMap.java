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

package org.bibliome.util.defaultmap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Default map with HashSet values, backed by a HashMap.
 * @author rbossy
 */
public final class DefaultHashSetHashMap<T,U> extends DefaultMap<T,Set<U>> {
	public DefaultHashSetHashMap() {
		super(true, new HashMap<T,Set<U>>());
	}

	@Override
	protected Set<U> defaultValue(T key) {
		return new HashSet<U>();
	}
	
}