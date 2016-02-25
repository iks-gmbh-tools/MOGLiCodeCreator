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
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validation.MetaInfoValidationUtil;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validation.MetaInfoValidatorParent;
/**
 * Validation results in always true. 
 * Used to analyse the usage of MetaInfo elements by generator plugins.
 * 
 * Instanziation by reflection {@link MetaInfoValidationUtil}
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class OptionalMetaInfoValidator extends MetaInfoValidatorParent {

	public OptionalMetaInfoValidator(final String metaInfoName,
			                         final HierarchyLevel metaInfoHierarchyLevel,
			                         final String nameOfValidModel) {
		super(metaInfoName, metaInfoHierarchyLevel, ValidationType.Optional, nameOfValidModel);
	}
	
	public OptionalMetaInfoValidator(final String metaInfoName, 
                                     final HierarchyLevel metaInfoHierarchyLevel) {
		super(metaInfoName, metaInfoHierarchyLevel, ValidationType.Optional, null);
	}

	@Override
	public boolean validate(final List<MetaInfo> metaInfoList) {
		int counter = 0; // t
		for (final MetaInfo metaInfo : metaInfoList) {
			if (metaInfo.getHierarchyLevel() == metaInfoHierarchyLevel
				&& metaInfo.getName().equals(metaInfoName)) {
				count(metaInfo);
				counter++;
			}
		}
		if (counter > 1) {
			errorMessage = "Optional metaInfo '" + metaInfoName + "' must occur no more than once"; 
			return false;  
		}
		return true;
	}

}