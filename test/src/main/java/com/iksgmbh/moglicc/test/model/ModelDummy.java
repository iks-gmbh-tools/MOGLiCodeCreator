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

	@Override
	public boolean isValueAvailable(final String metaInfoValue) {
		return false;
	}

	@Override
	public ClassDescriptor getClassDescriptor(String classname)
	{
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
}