package com.iksgmbh.moglicc.test.model;

import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;

public class MetaInfoDummy implements MetaInfo {

	private HierarchyLevel hierarchyLevel;
	private String value;
	private String name;

	public MetaInfoDummy(HierarchyLevel hierarchyLevel, String value,
			String name) {
		super();
		this.hierarchyLevel = hierarchyLevel;
		this.value = value;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value ;
	}

	@Override
	public HierarchyLevel getHierarchyLevel() {
		return hierarchyLevel;
	}

	@Override
	public List<String> getPluginList() {
		return null;
	}

}
