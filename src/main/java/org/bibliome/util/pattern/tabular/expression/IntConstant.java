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

import org.bibliome.util.pattern.tabular.ConstantFilter;
import org.bibliome.util.pattern.tabular.TabularContext;

public class IntConstant extends AbstractIntExpression {
	private final int value;

	public IntConstant(int value) {
		super();
		this.value = value;
	}

	@Override
	public int getInt(TabularContext context, List<String> columns) {
		return value;
	}

	@Override
	public boolean getBoolean(TabularContext context, List<String> columns) {
		ConstantFilter constantFilter = context.getConstantFilter();
		return constantFilter.acceptConstantInt(columns, value);
	}
}
