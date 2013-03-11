package com.iksgmbh.moglicc.provider.model.standard.metainfo;

import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;

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
	public boolean validate(final List<MetaInfo> metaInfoList) {
		for (final MetaInfo metaInfo : metaInfoList) {
			if (metaInfoName.equals(metaInfo.getName())) {
				count(metaInfo);
				return true; 
			}
		}
		errorMessage = "MetaInfo '" + metaInfoName + "' does not found "; 
		return false;
	}

}