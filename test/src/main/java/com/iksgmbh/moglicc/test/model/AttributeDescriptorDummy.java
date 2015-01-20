package com.iksgmbh.moglicc.test.model;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.AttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;

public class AttributeDescriptorDummy implements AttributeDescriptor {

	private String name;
	private String javaType;
	private List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();

	public AttributeDescriptorDummy(final String name, final String javaType) {
		this.name = name;
		this.javaType = javaType;
		metaInfoList.add(new MetaInfoDummy(HierarchyLevel.Attribute, javaType, "JavaType"));
	}

	@Override
	public String getMetaInfoValueFor(String metaInfoName) {
		return javaType;
	}

	@Override
	public List<String> getAllMetaInfoValuesFor(String metaInfoName) {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<MetaInfo> getMetaInfoList() {
		return metaInfoList;
	}

	@Override
	public List<MetaInfo> getAllMetaInfos() {
		return metaInfoList;
	}

	@Override
	public boolean doesHaveMetaInfo(String metaInfoName, String value) {
		return false;
	}

	@Override
	public boolean doesHaveAnyMetaInfosWithName(String metaInfoName) {
		return false;
	}

	@Override
	public String getCommaSeparatedListOfAllMetaInfoValuesFor(
			String metaInfoName) {
		return null;
	}

	@Override
	public boolean isValueAvailable(String metaInfoValue) {
		return false;
	}

	@Override
	public List<MetaInfo> getMetaInfosWithNameStartingWith(String prefix)
	{
		return null;
	}

	@Override
	public boolean isValueNotAvailable(String metaInfoValue)
	{
		return false;
	}

}
