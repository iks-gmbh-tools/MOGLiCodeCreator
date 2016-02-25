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