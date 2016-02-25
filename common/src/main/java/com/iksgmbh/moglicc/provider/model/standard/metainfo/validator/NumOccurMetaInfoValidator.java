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
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validation.MetaInfoValidationData;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validation.MetaInfoValidatorParent;

/**
 * Validation result is false when the number of occurrences of the
 * defined MetaInfo is not within the defined range.
 * As occurrences count matches with given list of MetoInfos by name and (if not null) value.
 * Also used to analyse the usage of MetaInfo elements by generator plugins.
 *
 * @author Reik Oberrath
 * @since 1.1.0
 */
public class NumOccurMetaInfoValidator extends MetaInfoValidatorParent {

	protected String metaInfoValue;
	protected int minOccurs;
	protected int maxOccurs;

	public NumOccurMetaInfoValidator(final MetaInfoValidationData metaInfoValidationData) {
		super(metaInfoValidationData.getMetaInfoName(), metaInfoValidationData.getMetaInfoHierarchyLevel(), ValidationType.NumOccur, metaInfoValidationData.getNameOfValidModel());

		minOccurs = metaInfoValidationData.getMinOccurs();
		maxOccurs = metaInfoValidationData.getMaxOccurs();
		metaInfoValue = metaInfoValidationData.getMetaInfoValue();

		if (minOccurs > maxOccurs) {
			throw new IllegalArgumentException("Error: MinOccurs > MaxOccurs");
		}
	}

	@Override
	public boolean validate(final List<MetaInfo> metaInfoList) {
		final int occurences = countOccurences(metaInfoList);
		return checkOccurrences(occurences);
	}


	protected int countOccurences(final List<MetaInfo> metaInfoList) {
		int occurrences = 0;
		for (final MetaInfo metaInfo : metaInfoList) {
			if (metaInfoName.equals(metaInfo.getName())) {
				count(metaInfo);  // for statistics only - value irrelevant
				if (metaInfoValue == null || metaInfoValue.equals(metaInfo.getValue())) {
					occurrences++;
				}
			}
		}
		return occurrences;
	}

	protected boolean checkOccurrences(final int occurences) {
		if (maxOccurs < 0) {
			//return true; // this is an optional validator
		}

		if (occurences == 0 && minOccurs > 0) {
			errorMessage = "MetaInfo '" + metaInfoName + "' was not found";
			return false;
		}

		if (minOccurs > 0 && minOccurs > occurences) {
			errorMessage = getMetaInfoDescription() + "' was found too few times (expected: " + minOccurs + ", actual: " + occurences + ")";
			return false;
		}

		if (maxOccurs < occurences) {
			errorMessage = getMetaInfoDescription() + "' was found too many times (expected: " + maxOccurs + ", actual: " + occurences + ")";
			return false;
		}

		return true;
	}
	
	private String getMetaInfoDescription() {
		String toReturn = "MetaInfo '" + metaInfoName;
		if (metaInfoValue != null) {
			toReturn += "with value '" + metaInfoValue + "'";
		}
		return toReturn;
	}

	public String getMetaInfoValue() {
		return metaInfoValue;
	}

	public int getMinOccurs() {
		return minOccurs;
	}

	public int getMaxOccurs() {
		return maxOccurs;
	}
}