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

package org.bibliome.util.pattern.tabular.expression;

import java.util.List;

import org.bibliome.util.pattern.tabular.TabularContext;
import org.bibliome.util.pattern.tabular.TabularExpression;

public class Or extends AbstractBooleanExpression {
	private final TabularExpression left;
	private final TabularExpression right;
	
	public Or(TabularExpression left, TabularExpression right) {
		super();
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean getBoolean(TabularContext context, List<String> columns) {
		if (left.getBoolean(context, columns)) {
			return true;
		}
		return right.getBoolean(context, columns);
	}
}
