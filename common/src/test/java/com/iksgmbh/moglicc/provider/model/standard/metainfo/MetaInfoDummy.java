package com.iksgmbh.moglicc.provider.model.standard.metainfo;

import java.util.ArrayList;
import java.util.List;

public class MetaInfoDummy implements MetaInfo {

	private String name;
	private String value;
	private HierarchyLevel hierarchyLevel;
	final List<String> plugins = new ArrayList<String>();

	public MetaInfoDummy(final HierarchyLevel hierarchyLevel, final String name) {
		this.hierarchyLevel = hierarchyLevel;
		this.name = name;
	}

	public MetaInfoDummy(final HierarchyLevel hierarchyLevel, final String name, final String value) {
		this.hierarchyLevel = hierarchyLevel;
		this.name = name;
		this.value = value;
	}

	public MetaInfoDummy(final String name, final String value) {
		this.name = name;
		this.value = value;
	}


	public MetaInfoDummy(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
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
