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
package com.iksgmbh.moglicc.provider.model.standard.metainfo.validation;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;

/**
 * Data used from the ConditionalMetaInfoValidator.
 *
 * @author Reik Oberrath
 */
public class MetaInfoValidationData {

	private static final String ANY = "any";

	private String nameOfValidModel;
	private HierarchyLevel hierarchyLevel;
	private String metaInfoName;
	private String metaInfoValue;
	private int minOccurs = 0;
	private int maxOccurs = 0;
	private String conditionFilename;
	private boolean conditionsMustBeTrue = true;

	private List<MetaInfoValidationCondition> conditionBlock = new ArrayList<MetaInfoValidationCondition>();


	public MetaInfoValidationData withNameOfValidModel(final String nameOfValidModel) {
		this.nameOfValidModel = nameOfValidModel;
		return this;
	}

	public MetaInfoValidationData withHierarchyLevel(final HierarchyLevel hierarchyLevel) {
		this.hierarchyLevel = hierarchyLevel;
		return this;
	}

	public MetaInfoValidationData withMetaInfoName(final String metaInfoName) {
		this.metaInfoName = metaInfoName;
		return this;
	}

	public MetaInfoValidationData withMetaInfoValue(final String metaInfoValue) {
		this.metaInfoValue = metaInfoValue;
		return this;
	}

	public MetaInfoValidationData withMinOccurs(final int minOccurs) {
		this.minOccurs = minOccurs;
		return this;
	}

	public MetaInfoValidationData withMaxOccurs(final int maxOccurs) {
		this.maxOccurs = maxOccurs;
		return this;
	}

	public MetaInfoValidationData withConditionFilename(final String conditionFilename) {
		this.conditionFilename = conditionFilename;
		return this;
	}

	public MetaInfoValidationData withConditionsMustBeTrue(final boolean conditionsMustBeTrue) {
		this.conditionsMustBeTrue = conditionsMustBeTrue;
		return this;
	}

	public MetaInfoValidationData addCondition(final MetaInfoValidationCondition condition) {
		this.conditionBlock.add(condition);
		return this;
	}


	public String getNameOfValidModel() {
		return nameOfValidModel;
	}

	public HierarchyLevel getMetaInfoHierarchyLevel() {
		return hierarchyLevel;
	}

	public String getMetaInfoName() {
		return metaInfoName;
	}

	public int getMinOccurs() {
		return minOccurs;
	}

	public int getMaxOccurs() {
		return maxOccurs;
	}

	public boolean mustConditionsBeTrue() {
		return conditionsMustBeTrue;
	}

	public String getConditionFilename() {
		return conditionFilename;
	}

	public List<MetaInfoValidationCondition> getConditionBlock() {
		return conditionBlock;
	}

	public String getMetaInfoValue() {
		return metaInfoValue;
	}

	/**
	 * Parses input string
	 * @param occurence string containing an ordinal number (e.g. 2) or a range (e.g. 0-1)
	 * @return instance with minOccur and maxOccur set
	 */
	public MetaInfoValidationData withOccurrence(final String occurence) {
		if (ANY.equalsIgnoreCase(occurence.trim())) {
			maxOccurs = 999999999;
			minOccurs = 0;
			return this;
		}

		final Integer result = parseToInt(occurence); //, "Value <"a1"> is no valid occurence!")

		if (result == null) {
			final String[] splitResult = occurence.split("-");
			if (splitResult.length == 2) {
				final Integer min = parseToInt(splitResult[0]);
				final Integer max = parseToInt(splitResult[1]);
				if (min == null || max == null) {
					throw new IllegalArgumentException("Range <" + occurence + "> is no valid occurence!");
				}
				minOccurs = min.intValue();
				maxOccurs = max.intValue();
			} else {
				throw new IllegalArgumentException("Value <" + occurence + "> is no valid occurence!");
			}
		} else {
			if (result.intValue() < 0) {
				throw new IllegalArgumentException("Value <" + occurence + "> is no valid occurence!");
			}
			maxOccurs = result;
			minOccurs = result;
		}

		return this;
	}

	private Integer parseToInt(final String s) {
		try {
			return Integer.valueOf(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}