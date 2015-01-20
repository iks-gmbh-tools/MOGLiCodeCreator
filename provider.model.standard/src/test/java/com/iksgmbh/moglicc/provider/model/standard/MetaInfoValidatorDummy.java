package com.iksgmbh.moglicc.provider.model.standard;

import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;

public class MetaInfoValidatorDummy implements MetaInfoValidator
{
	private String metaInfoName;
	private HierarchyLevel hierarchyLevel;
	private ValidationType validationType;

	public MetaInfoValidatorDummy(String metaInfoName, HierarchyLevel hierarchyLevel, ValidationType validationType) {
		this.metaInfoName = metaInfoName;
		this.hierarchyLevel = hierarchyLevel;
		this.validationType = validationType;
	}

	@Override
	public String getMetaInfoName()
	{
		return metaInfoName;
	}

	@Override
	public HierarchyLevel getMetaInfoHierarchyLevel()
	{
		return hierarchyLevel;
	}

	@Override
	public ValidationType getValidationType()
	{
		return validationType;
	}

	@Override
	public boolean validate(List<MetaInfo> metaInfoList)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getValidationErrorMessage()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVendorPluginId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNameOfValidModel()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValidatorValidForHierarchyLevel(HierarchyLevel currentHierarchyLevel)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValidatorValidForModel(String nameOfCurrentModel)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
