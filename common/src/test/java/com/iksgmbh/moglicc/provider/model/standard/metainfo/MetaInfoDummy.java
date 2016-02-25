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
package com.iksgmbh.moglicc.provider.model.standard.metainfo;

import java.util.ArrayList;
import java.util.List;

public class MetaInfoDummy implements MetaInfo {

	private String name;
	private String value;
	private HierarchyLevel hierarchyLevel;
	final List<String> plugins = new ArrayList<String>();

	public MetaInfoDummy(final HierarchyLevel hierarchyLevel, final String name) {
		this.hierarchyLevel = hierarchyLevel;
		this.name = name;
	}

	public MetaInfoDummy(final HierarchyLevel hierarchyLevel, final String name, final String value) {
		this.hierarchyLevel = hierarchyLevel;
		this.name = name;
		this.value = value;
	}

	public MetaInfoDummy(final String name, final String value) {
		this.name = name;
		this.value = value;
	}


	public MetaInfoDummy(final String name) {
		this.name = name;
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
		return plugins;
	}

}