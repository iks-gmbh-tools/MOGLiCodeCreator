package com.iksgmbh.moglicc.test.model;

import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.ClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;

public class ModelDummy implements Model {

	private String name;

	public ModelDummy(String name) {
		this.name = name;
	}

	@Override
	public String getMetaInfoValueFor(String metaInfoName) {
		return null;
	}

	@Override
	public List<String> getAllMetaInfoValuesFor(String metaInfoName) {
		return null;
	}

	@Override
	public List<MetaInfo> getMetaInfoList() {
		return null;
	}

	@Override
	public List<MetaInfo> getAllMetaInfos() {
		return null;
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
	public List<ClassDescriptor> getClassDescriptorList() {
		return null;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getCommaSeparatedListOfAllMetaInfoValuesFor(
			String metaInfoName) {
		return null;
	}

}
