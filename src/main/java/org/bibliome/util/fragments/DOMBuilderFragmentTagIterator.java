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

package org.bibliome.util.fragments;

import org.bibliome.util.Strings;
import org.bibliome.util.mappers.ParamMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class DOMBuilderFragmentTagIterator<F extends Fragment> implements FragmentTagIterator<String,F> {
	private final ParamMapper<F,Element,Document> elementBuilder;
	private final Document document;
	private final int headStart;
	private final int tailEnd;
	private Node innermostNode;
	
	public DOMBuilderFragmentTagIterator(ParamMapper<F,Element,Document> elementBuilder, Document document, Node topNode, int headStart, int tailEnd) {
		super();
		this.elementBuilder = elementBuilder;
		this.document = document;
		this.innermostNode = topNode;
		this.headStart = headStart;
		this.tailEnd = tailEnd;
	}
	
	public DOMBuilderFragmentTagIterator(ParamMapper<F,Element,Document> elementBuilder, Document document, Node topNode) {
		this(elementBuilder, document, topNode, 0, Integer.MAX_VALUE);
	}
	
	public DOMBuilderFragmentTagIterator(ParamMapper<F,Element,Document> elementBuilder, Document document) {
		this(elementBuilder, document, document.createDocumentFragment());
	}

	@Override
	public void handleTag(String param, FragmentTag<F> tag) {
		FragmentTagType type = tag.getTagType();
		switch (type) {
		case CLOSE:
			innermostNode = innermostNode.getParentNode();
			break;
		case EMPTY:
			createElement(tag.getFragment());
			break;
		case OPEN:
			innermostNode = createElement(tag.getFragment());
			break;
		}
	}
	
	private Element createElement(F frag) {
		Element result = elementBuilder.map(frag, document);
		innermostNode.appendChild(result);
		return result;
	}
	
	@Override
	public void handleGap(String param, int from, int to) {
		Text text = document.createTextNode(Strings.clip(param, from, to));
		innermostNode.appendChild(text);
	}

	@Override
	public void handleHead(String param, int to) {
//		System.err.println("param = " + param);
//		System.err.println("headStart = " + headStart);
//		System.err.println("to = " + to);
		Text text = document.createTextNode(Strings.clip(param, headStart, to));
		innermostNode.appendChild(text);
	}
	
	@Override
	public void handleTail(String param, int from) {
		Text text = document.createTextNode(Strings.clip(param, from, tailEnd));
		innermostNode.appendChild(text);
	}
}
