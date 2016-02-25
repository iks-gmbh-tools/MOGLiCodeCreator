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
package com.iksgmbh.moglicc.provider.model.standard.metainfo.validator;

import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validation.MetaInfoValidatorParent;

/**
 * Validation result is false if HierarchyLevel of MetaInfo elements is equal
 * to the one of the validator and if the name of the validator's MetaInfo 
 * is found at least one in metaInfoList.
 * Also used to analyse the usage of MetaInfo elements by generator plugins.
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class MandatoryMetaInfoValidator extends MetaInfoValidatorParent {

	public MandatoryMetaInfoValidator(final String metaInfoName, 
									  final HierarchyLevel metaInfoHierarchyLevel,
									  final String nameOfValidModel) {
		super(metaInfoName, metaInfoHierarchyLevel, ValidationType.Mandatory, nameOfValidModel);
	}

	public MandatoryMetaInfoValidator(final String metaInfoName, 
			                          final HierarchyLevel metaInfoHierarchyLevel) {
		super(metaInfoName, metaInfoHierarchyLevel, ValidationType.Mandatory, null);
	}

	@Override
	public boolean validate(List<MetaInfo> metaInfoList) {
		metaInfoList = filterForHierarchyLevel(metaInfoList);
		int counter = 0;
		
		for (final MetaInfo metaInfo : metaInfoList) {
			if (metaInfo.getName().equals(metaInfoName)) {
				count(metaInfo);
				counter++;
			}
		}

		if (counter == 0) {
			errorMessage = "Mandatory metaInfo '" + metaInfoName + "' was not found"; 
			return false;
			
		}
		if (counter > 1) {
			errorMessage = "Mandatory metaInfo '" + metaInfoName + "' must occur no more than once"; 
			return false;  
		}
		return true;		
	}

}