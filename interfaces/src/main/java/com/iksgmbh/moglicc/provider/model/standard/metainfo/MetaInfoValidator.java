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