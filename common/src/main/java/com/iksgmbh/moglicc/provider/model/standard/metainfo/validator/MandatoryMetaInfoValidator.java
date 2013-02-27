package com.iksgmbh.moglicc.provider.model.standard.metainfo.validator;

import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidatorParent;

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
	public boolean validate(final List<MetaInfo> metaInfoList, final HierarchyLevel hierarchyLevel) {
		if (metaInfoHierarchyLevel != hierarchyLevel) {
			return true; // this validator does not validate MetaInfo elements of this HierarchyLevel
		}
		for (final MetaInfo metaInfo : metaInfoList) {
			if (metaInfoName.equals(metaInfo.getName())) {
				count(metaInfo);
				return true; 
			}
		}
		errorMessage = "MetaInfo '" + metaInfoName + "' was not found "; 
		return false;
	}

}