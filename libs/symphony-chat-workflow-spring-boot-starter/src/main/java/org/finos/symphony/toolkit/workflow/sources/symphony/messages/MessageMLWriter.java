package org.finos.symphony.toolkit.workflow.sources.symphony.messages;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.OrderedContent;
import org.springframework.web.util.HtmlUtils;

public class MessageMLWriter implements Function<Content, String> {
	
	Map<Class<?>, Function<Content, String>> tagMap;
	
	public MessageMLWriter(Map<Class<?>, Function<Content, String>> tagMap) {
		super();
		this.tagMap = tagMap;
	}

	public MessageMLWriter() {
		tagMap = new HashMap<Class<?>, Function<Content,String>>();
	}

	@Override
	public String apply(Content t) {
		if (t == null) { 
			return "";
		}
		Function<Content, String> writer = findWriter(t);
		
		if (writer == null) {
			return "";
		} else {
			return writer.apply(t);
		}
	}

	protected Function<Content, String> findWriter(Content t) {
		return tagMap.keySet().stream()
			.filter(c -> c.isAssignableFrom(t.getClass()))
			.map(c -> tagMap.get(c))
			.findFirst().orElseGet(() -> null);
	}
	
	public class PlainWriter implements Function<Content, String> {

		@Override
		public String apply(Content t) {
			return " " + HtmlUtils.htmlEscape(t.getText())+ " ";		
		}
		
	}
	
	public class SimpleTagWriter implements Function<Content, String> {

		String tag;
		
		public SimpleTagWriter(String tag) {
			super();
			this.tag = tag;
		}

		@Override
		public String apply(Content t) {
			return "<"+tag+">" + HtmlUtils.htmlEscape(t.getText())+ "</"+tag+">";		
		}
		
	}
	
	public class OrderedTagWriter implements Function<Content, String> {
		
		String tag;
		
		public OrderedTagWriter(String tag) {
			super();
			this.tag = tag;
		}

		@Override
		public String apply(Content t) {
			return "<"+tag+">" + 
				((OrderedContent<?>)t).getContents().stream()
					.map(c -> MessageMLWriter.this.apply(c)) 
					.reduce("", (a, b) -> a.trim() + " "+ b.trim()) + 
					"</"+tag+">";		
		}
		
	}
	
	public void add(Class<? extends Content> cl, Function<Content, String> mapper) {
		tagMap.put(cl, mapper);
	}

}
