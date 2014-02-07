package com.iksgmbh.moglicc.provider.model.standard.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.moglicc.provider.model.standard.AttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;

/**
 * Used to build a AttributeDescriptor Object.
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class BuildUpAttributeDescriptor extends MetaModelObject implements AttributeDescriptor {
	
	private String name;

	public BuildUpAttributeDescriptor(final String name) {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Name of AttributeDescriptor must not be empty!");
		}
		this.name = name;
		metaInfoList = new ArrayList<MetaInfo>();
	}
	
	@Override
	public String toString() {
		return "BuildUpAttributeDescriptor [name=" + name + ", metaInfoList=" + getCommaSeparatedListOfMetaInfoNames() + "]";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<MetaInfo> getAllMetaInfos() {
		// no containing hierarchy level
		return getMetaInfoList();
	}
}