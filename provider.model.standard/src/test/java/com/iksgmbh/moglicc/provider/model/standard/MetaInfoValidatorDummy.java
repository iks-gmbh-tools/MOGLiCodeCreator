/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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