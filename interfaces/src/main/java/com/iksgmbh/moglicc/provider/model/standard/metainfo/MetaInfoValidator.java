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

import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;

/**
 * Functionality to validate list of MetaInfo elements
 * and to analyse the usage of MetaInfo elements by generator plugins.
 *
 * @author Reik Oberrath
 * @since 1.0.0
 */
public interface MetaInfoValidator {

	enum ValidationType {Optional, Mandatory, NumOccur, Conditional};

	String getMetaInfoName();

	HierarchyLevel getMetaInfoHierarchyLevel();

	ValidationType getValidationType();

	boolean validate(final List<MetaInfo> metaInfoList);

	String getValidationErrorMessage();

	/**
	 * Id of the plugin that has defined this validator
	 * @return
	 */
	String getVendorPluginId();

	/**
	 * For null the validator is valid -> per default a validator is applied to all models
	 * Invalid validators are ignored when the StandardModelProvider validates the model!
	 * @return name of model for which this validator is valid
	 */
	String getNameOfValidModel();
	
	boolean isValidatorValidForHierarchyLevel(final HierarchyLevel currentHierarchyLevel);
	boolean isValidatorValidForModel(final String nameOfCurrentModel);

}