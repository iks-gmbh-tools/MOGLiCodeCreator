package com.iksgmbh.moglicc.provider.model.standard.metainfo.validator;

import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidationUtil;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidatorParent;
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
		for (final MetaInfo metaInfo : metaInfoList) {
			if (metaInfo.getName().equals(metaInfoName)) {
				count(metaInfo);
			}
		}
		return true;
	}

}