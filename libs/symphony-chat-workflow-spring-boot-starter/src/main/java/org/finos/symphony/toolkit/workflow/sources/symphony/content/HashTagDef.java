package org.finos.symphony.toolkit.workflow.sources.symphony.content;

public class HashTagDef extends TagDef implements HashTag {
		
	public HashTagDef() {
		super();
	}

	public HashTagDef(String id) {
		super(id, id, Type.HASH);
	}


}
