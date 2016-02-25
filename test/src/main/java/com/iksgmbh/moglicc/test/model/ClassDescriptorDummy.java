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
package com.iksgmbh.moglicc.test.model;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.AttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.ClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;

public class ClassDescriptorDummy implements ClassDescriptor {

	private List<AttributeDescriptor> attributeDescriptorList = new ArrayList<AttributeDescriptor>();
	private List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();

	public ClassDescriptorDummy(final String name, final AttributeDescriptor attributeDescriptor,
			                    final MetaInfo metaInfo) {
		attributeDescriptorList.add(attributeDescriptor);
		metaInfoList.add(metaInfo);
		metaInfoList.addAll(attributeDescriptor.getAllMetaInfos());
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
		return metaInfoList;
	}

	@Override
	public List<MetaInfo> getAllMetaInfos() {
		return metaInfoList;
	}

	@Override
	public String getSimpleName() {
		return null;
	}

	@Override
	public String getPackage() {
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		return null;
	}

	@Override
	public List<AttributeDescriptor> getAttributeDescriptorList() {
		return attributeDescriptorList;
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
	public boolean isValueAvailable(final String metaInfoValue) {
		return false;
	}

	@Override
	public AttributeDescriptor getAttributeDescriptor(String attributeName) {
		return null;
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

	@Override
	public String getName()
	{
		return null;
	}
}