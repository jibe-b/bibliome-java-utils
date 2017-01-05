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

import org.bibliome.util.StringComparisonOperator;
import org.bibliome.util.pattern.tabular.TabularContext;
import org.bibliome.util.pattern.tabular.TabularExpression;

public class StringComparison extends AbstractBooleanExpression {
	private final StringComparisonOperator operator;
	private final TabularExpression left;
	private final TabularExpression right;
	
	public StringComparison(StringComparisonOperator operator, TabularExpression left, TabularExpression right) {
		super();
		this.operator = operator;
		this.left = left;
		this.right = right;
	}

	public static boolean any(List<String> columns, StringComparisonOperator operator, String right) {
		for (String left : columns) {
			if (operator.accept(left, right)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean getBoolean(TabularContext context, List<String> columns) {
		if (left == null) {
			String right = this.right.getString(context, columns);
			return any(columns, operator, right);
		}
		String left = this.left.getString(context, columns);
		String right = this.right.getString(context, columns);
		return operator.accept(left, right);
	}
}
