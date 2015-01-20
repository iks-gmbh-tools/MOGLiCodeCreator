package com.iksgmbh.moglicc.provider.model.standard.buildup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;

public class BuildUpMetaInfo implements MetaInfo {

	private String name;
	private String value;
	private HierarchyLevel hierarchyLevel;
	private List<String> pluginList;
	
	public BuildUpMetaInfo(String name) {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Name of MetaAttributeDescriptor must not be empty!");
		}
		this.name = name;
		pluginList = new ArrayList<String>();
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
		return pluginList;
	}

	public void setValue(final String value) {
		this.value = value;
	}
	
	public void setLevel(final HierarchyLevel level) {
		this.hierarchyLevel = level;
	}

	@Override
	public String toString() {
		return "BuildUpMetaInfo [name=" + name + ", value=" + value
				+ ", hierarchyLevel=" + hierarchyLevel + ", pluginList="
				+ pluginList + "]";
	}

}
