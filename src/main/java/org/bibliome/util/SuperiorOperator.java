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

package org.bibliome.util;

public enum SuperiorOperator implements BinaryNumericOperator {
	MIN {
		@Override
		public int compute(int a, int b) {
			return Math.min(a, b);
		}

		@Override
		public double compute(double a, double b) {
			return Math.min(a, b);
		}
	},
	
	MAX {
		@Override
		public int compute(int a, int b) {
			return Math.max(a, b);
		}

		@Override
		public double compute(double a, double b) {
			return Math.max(a, b);
		}
	};
	
	@Override
	public abstract int compute(int a, int b);
	@Override
	public abstract double compute(double a, double b);
}
