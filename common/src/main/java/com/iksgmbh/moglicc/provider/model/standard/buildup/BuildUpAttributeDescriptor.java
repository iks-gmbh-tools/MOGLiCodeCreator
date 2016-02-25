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

import com.iksgmbh.moglicc.provider.model.standard.AttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaModelObject;

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