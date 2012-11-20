package com.iksgmbh.moglicc.provider.model.standard.metainfo;

import java.util.ArrayList;
import java.util.List;


public class MetaInfoDummy implements MetaInfo {

	private HierarchyLevel hierarchyLevel;
	private String name;
	final List<String> plugins = new ArrayList<String>();
	
	public MetaInfoDummy(final HierarchyLevel hierarchyLevel, final String name) {
		this.hierarchyLevel = hierarchyLevel;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return null;
	}

	@Override
	public HierarchyLevel getHierarchyLevel() {
		return hierarchyLevel;
	}

	@Override
	public List<String> getPluginList() {
		return plugins;
	}

}
