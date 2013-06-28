package com.iksgmbh.moglicc.provider.model.standard.metainfo.validator;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidationCondition;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidationData;

/**
 * Validation result is false if the defined conditions are false.
 * Conditions are the presents of other metainfo elements with or without a certain value.
 * Also used to analyse the usage of MetaInfo elements by generator plugins.
 *
 * @author Reik Oberrath
 * @since 1.1.0
 */
public class ConditionalMetaInfoValidator extends NumOccurMetaInfoValidator {

	/**
	 * Inner List (condition block) contains conditions that are linked by an AND-Relation (conjunction).
	 * Outer List (condition list) contains blocks of conditions that are linked by an OR-Relation (disjunction).
	 */
	private List<List<MetaInfoValidationCondition>> conditionList;
	private String conditionFilename;

	/**
	 * In case the conditions contained in the condition file are true,
	 * but conditionsMustBeTrue is true, the validation is successful.
	 *
	 * In case the conditions contained in the condition file are true,
	 * but conditionsMustBeTrue is false, the validation will result in an error.
	 *
	 * In case the conditions contained in the condition file are false,
	 * but conditionsMustBeTrue is false, the validation is successful.
	 *
	 * In case the conditions contained in the condition file are false,
	 * but conditionsMustBeTrue is true, the validation will result in an error.
	 */
	private boolean conditionsMustBeTrue = true;


	public ConditionalMetaInfoValidator(final MetaInfoValidationData metaInfoValidationData) {
		super(metaInfoValidationData);
		validationType = ValidationType.Conditional;
		conditionList = new ArrayList<List<MetaInfoValidationCondition>>();
		conditionList.add(metaInfoValidationData.getConditionBlock());     // this is relevant for test purpose
		conditionFilename = metaInfoValidationData.getConditionFilename();
		conditionsMustBeTrue = metaInfoValidationData.mustConditionsBeTrue();
	}

	@Override
	public boolean validate(final List<MetaInfo> metaInfoList) {
		final boolean atleastOneConditionBlockIsTrue = isThereAtleastOneTrueConditionBlock(metaInfoList);
		final boolean validateOccurrences = atleastOneConditionBlockIsTrue == conditionsMustBeTrue;
		if (validateOccurrences) {
			final int occurences = countOccurences(metaInfoList);
			final boolean ok = checkOccurrences(occurences);
			if (! ok) {
				return false;
			}
		} else {
			// conditions are not meet, thus occurrences will be not validated
		}

		return true;
	}

	private boolean areAllConditionsValid(final List<MetaInfoValidationCondition> innerList, final
			                              List<MetaInfo> metaInfoList) {
		for (MetaInfoValidationCondition condition : innerList) {
			if (condition.isTrueFor(metaInfoList))  {
				continue;
			}
			// conditions within a block (inner list) 
			// are linked by an "and"-relation (conjunction)
			// the first false condition causes this inner list to be "false"
			return false;
		}
		return true;  // all conditions in innerList are true
	}

	private boolean isThereAtleastOneTrueConditionBlock(final List<MetaInfo> metaInfoList) {
		for (List<MetaInfoValidationCondition> innerList : conditionList) {
			if (areAllConditionsValid(innerList, metaInfoList))  {
				// condition blocks (inner lists) 
				// are linked by an "or"-relation (disjunction)
				// the first true block within the condition file
				// causes the whole condition list to be "true"
				return true;
			}
		}
		return false;  // all inner condition lists are false
	}

	public List<List<MetaInfoValidationCondition>> getConditionList() {
		return conditionList;
	}

	public String getConditionFilename() {
		return conditionFilename;
	}

	public void setConditionList(List<List<MetaInfoValidationCondition>> conditionList) {
		this.conditionList = conditionList;
	}

	public int getTotalNumberOfConditions() {
		int counter = 0;
		for (List<MetaInfoValidationCondition> innerList : conditionList) {
			counter += innerList.size();
		}
		return counter;
	}

	public boolean mustConditionsBeTrue() {
		return conditionsMustBeTrue;
	}

}