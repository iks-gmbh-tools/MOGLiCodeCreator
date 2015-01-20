package com.iksgmbh.moglicc.provider.model.standard.metainfo.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoDummy;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validation.MetaInfoValidationCondition;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validation.MetaInfoValidationData;


public class ConditionalMetaInfoValidatorUnitTest {

	private List<MetaInfo> metaInfoList;

	@Before
	public void setup() {
		metaInfoList = DataHelper.createMetaInfoTestStandardList();
	}

	@Test
	public void returnsTrueForValidatingMandatoryFieldWithOneCondition() {
		// prepare test
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition("OtherMetaInfo"); // true condition

		final MetaInfoValidationData metaInfoValidationData = new  MetaInfoValidationData().withMetaInfoName("metaInfoName")
				                                                                           .withOccurrence("1") // mandatory
				                                                                           .addCondition(condition);
		// call functionality under test
		final boolean result = DataHelper.getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertTrue("Validation failed!", result); // condition true & mandatory MetaInfo exists -> result is true
	}

	@Test
	public void returnsTrueForValidatingMandatoryFieldWithThreeConditions() {
		// prepare test
		final MetaInfoValidationCondition condition1 = new MetaInfoValidationCondition("singleMetaInfo");
		final MetaInfoValidationCondition condition2 = new MetaInfoValidationCondition("OtherMetaInfo");
		final MetaInfoValidationCondition condition3 = new MetaInfoValidationCondition("doubleMetaInfo");

		final MetaInfoValidationData metaInfoValidationData = new  MetaInfoValidationData().withMetaInfoName("metaInfoName")
				                                                                           .withOccurrence("1") // mandatory
				                                                                           .withHierarchyLevel(HierarchyLevel.Attribute)
				                                                                           .addCondition(condition1)
				                                                                           .addCondition(condition2)
        																				   .addCondition(condition3);
		// call functionality under test
		final boolean result = DataHelper.getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertTrue("Validation failed!", result); // all three condition true & mandatory MetaInfo exists -> result is true
	}

	@Test
	public void returnsTrueForValidatingMandatoryFieldWithThreeConditionsBecauseSecondConditionIsNotMet() {
		// prepare test
		final MetaInfoValidationCondition condition1 = new MetaInfoValidationCondition("singleMetaInfo");
		final MetaInfoValidationCondition condition2 = new MetaInfoValidationCondition("anotherNotExistingMetaInfo");
		final MetaInfoValidationCondition condition3 = new MetaInfoValidationCondition("doubleMetaInfo");

		// the following mandatory validator should return false, because notExistingMetaInfo does not exist
		final MetaInfoValidationData metaInfoValidationData = new  MetaInfoValidationData().withMetaInfoName("notExistingMetaInfo")
				                                                                           .withOccurrence("1") // mandatory
				                                                                           .withHierarchyLevel(HierarchyLevel.Attribute)
				                                                                           .addCondition(condition1)
				                                                                           .addCondition(condition2)
        																				   .addCondition(condition3);
		// call functionality under test
		final boolean result = DataHelper.getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertTrue("Validation failed!", result);  // true because second condition is false and therefore occurrence is not validated
	}

	@Test
	public void returnsTrueForValidatingMandatoryFieldWithConditionBecauseValueIsWrong() {
		// prepare test
		final MetaInfoValidationData metaInfoValidationData = new  MetaInfoValidationData().withMetaInfoName("metaInfoName")
																						   .withMetaInfoValue("metaInfoValue")
				                                                                           .withOccurrence("1") // mandatory
				                                                                           .withHierarchyLevel(HierarchyLevel.Attribute);
		// call functionality under test
		final boolean result = DataHelper.getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertTrue("Validation failed!", result); // mandatory MetaInfo with the defined value exists -> result is true
	}

	@Test
	public void returnsFalseForValidatingMandatoryFieldWithConditionBecauseValueIsWrong() {
		// prepare test
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition("OtherMetaInfo", "OtherMetaInfoValue1");
		final MetaInfoValidationData metaInfoValidationData = new  MetaInfoValidationData().withMetaInfoName("metaInfoName")
																						   .withMetaInfoValue("wrongValue")
				                                                                           .withOccurrence("1") // mandatory
				                                                                           .withHierarchyLevel(HierarchyLevel.Attribute)
        																				   .addCondition(condition);
		// call functionality under test
		final boolean result = DataHelper.getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertFalse("Validation failed!", result); // condition true & mandatory MetaInfo exists, but not with the defined value -> false
	}

	@Test
	public void returnsTrueForValidatingMandatoryFieldWithValueCondition() {
		// prepare test
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition("OtherMetaInfo", "OtherMetaInfoValue2");
		final MetaInfoValidationData metaInfoValidationData = new  MetaInfoValidationData().withMetaInfoName("metaInfoName")
				                                                                           .withMaxOccurs(1)
				                                                                           .withMinOccurs(1)
				                                                                           .withHierarchyLevel(HierarchyLevel.Attribute)
        																				   .addCondition(condition);
		// call functionality under test
		final boolean result = DataHelper.getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertTrue("Validation failed!", result);
	}

	@Test
	public void returnsTrueForValidatingMandatoryFieldWithAbsenceCondition() {
		// prepare test
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition(true, "singleMetaInfo");

		// the following mandatory validator should return false, because notExistingMetaInfo does not exist
		final MetaInfoValidationData metaInfoValidationData = new  MetaInfoValidationData().withMetaInfoName("notExistingMetaInfo")
				                                                                           .withMaxOccurs(1)
				                                                                           .withMinOccurs(1)
				                                                                           .withHierarchyLevel(HierarchyLevel.Attribute)
        																				   .addCondition(condition);
		// call functionality under test
		final boolean result = DataHelper.getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertTrue("Validation failed!", result); // true because condition is false and therefore occurence is not validated
	}

	@Test
	public void returnsFalseForValidatingMandatoryFieldWithAbsenceCondition() {
		// prepare test
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition(true, "anotherNotExistingMetaInfo");

		final MetaInfoValidationData metaInfoValidationData = new  MetaInfoValidationData().withMetaInfoName("notExistingMetaInfo")
				                                                                           .withMaxOccurs(1)
				                                                                           .withMinOccurs(1)
				                                                                           .withHierarchyLevel(HierarchyLevel.Attribute)
        																				   .addCondition(condition);
		// call functionality under test
		final boolean result = DataHelper.getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertFalse("Validation failed!", result); // false because condition is true and notExistingMetaInfo does not exist
	}


	@Test
	public void returnsTrueForValidatingMandatoryFieldWithAbsenceValueCondition() {
		// prepare test
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition(true, "OtherMetaInfo", "OtherMetaInfoValue1");


		// the following mandatory validator should return false, because notExistingMetaInfo does not exist
		final MetaInfoValidationData metaInfoValidationData = new  MetaInfoValidationData().withMetaInfoName("notExistingMetaInfo")
				                                                                           .withMaxOccurs(1)
				                                                                           .withMinOccurs(1)
				                                                                           .withHierarchyLevel(HierarchyLevel.Attribute)
        																				   .addCondition(condition);
		// call functionality under test
		final boolean result = DataHelper.getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertTrue("Validation failed!", result); // true because condition is false and therefore occurence is not validated
	}

	@Test
	public void returnsFalseForValidatingMandatoryFieldWithAbsenceValueCondition() {
		// prepare test
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition(true, "OtherMetaInfo", "NotExistingMetaInfoValue");

		final MetaInfoValidationData metaInfoValidationData = new  MetaInfoValidationData().withMetaInfoName("notExistingMetaInfo")
				                                                                           .withMaxOccurs(1)
				                                                                           .withMinOccurs(1)
				                                                                           .withHierarchyLevel(HierarchyLevel.Attribute)
        																				   .addCondition(condition);
		// call functionality under test
		final boolean result = DataHelper.getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertFalse("Validation failed!", result); // false because condition is true and notExistingMetaInfo does not exist
	}

	protected MetaInfoValidationData getNullableValidationDataWithConditionMinOccursEqualsZero(final String occurrence) {
		return getNullableValidationDataWithConditionMinOccursEqualsZero(occurrence, true);
	}

	/**
	 * Defines rule:
	 * If conditionsMustBeTrue=true:  MetaInfo "Nullable" must have value "true" if MetaInfo "MinOccurs" has value "0"
	 * If conditionsMustBeTrue=false: MetaInfo "Nullable" must have value "true" if MetaInfo "MinOccurs" with value "0" does not exist
	 *
	 * @param occurrence
	 * @param conditionsMustBeTrue
	 * @return MetaInfoValidationData
	 */
	protected MetaInfoValidationData getNullableValidationDataWithConditionMinOccursEqualsZero(
			final String occurrence,
			final boolean conditionsMustBeTrue)
	{
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition("MinOccurs", "0");
		final MetaInfoValidationData metaInfoValidationData = new MetaInfoValidationData()
				.withMetaInfoName("Nullable")
				.withMetaInfoValue("true")
				.withOccurrence(occurrence)
				.withConditionsMustBeTrue(conditionsMustBeTrue)
				.addCondition(condition);
		return metaInfoValidationData;
	}

	@Test
	public void returnsFalseForRule_MetaInfo_Nullable_must_have_value_true_if_MetaInfo_MinOccurs_has_value_0_dueToWrongValue() {
		// prepare test
		final List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
		metaInfoList.add(new MetaInfoDummy("Nullable", "false"));  // false lets validator return false
		metaInfoList.add(new MetaInfoDummy("MinOccurs", "0")); // this makes condition true

		final MetaInfoValidationData metaInfoValidationData = getNullableValidationDataWithConditionMinOccursEqualsZero("1");

		// call functionality under test
		final boolean result = DataHelper.getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertFalse("Validation failed!", result);
	}

	@Test
	public void returnsTrueForRule_MetaInfo_Nullable_must_have_value_true_if_MetaInfo_MinOccurs_has_value_0() {
		// prepare test
		final List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
		metaInfoList.add(new MetaInfoDummy("Nullable", "true")); // false lets validator return true
		metaInfoList.add(new MetaInfoDummy("MinOccurs", "0")); // this makes condition true

		final MetaInfoValidationData metaInfoValidationData = getNullableValidationDataWithConditionMinOccursEqualsZero("1");

		// call functionality under test
		final boolean result = DataHelper.getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertTrue("Validation failed!", result);
	}

	@Test
	public void returnsFalseForRule_MetaInfo_Nullable_must_have_value_true_if_MetaInfo_MinOccurs_has_value_0_BecauseNullableIsMissing() {
		// prepare test
		final List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
		metaInfoList.add(new MetaInfoDummy("MinOccurs", "0")); // this makes condition true

		final MetaInfoValidationData metaInfoValidationData = getNullableValidationDataWithConditionMinOccursEqualsZero("1");

		// call functionality under test
		final boolean result = DataHelper.getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertFalse("Validation failed!", result);
	}

	@Test
	public void returnsTrueForRule_MetaInfo_Nullable_must_have_value_true_if_MetaInfo_MinOccurs_has_value_0_BecauseNullableIsOptionalAndMissing() {
		// prepare test
		final List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
		metaInfoList.add(new MetaInfoDummy("MinOccurs", "0")); // this makes condition true

		final MetaInfoValidationData metaInfoValidationData =
			getNullableValidationDataWithConditionMinOccursEqualsZero("0-1");  // "0-1" means that nullable is optional !

		// call functionality under test
		final boolean result = DataHelper.getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertTrue("Validation failed!", result);
	}

	@Test
	public void returnsTrueForRule_MetaInfo_Nullable_must_have_value_true_if_MetaInfo_MinOccurs_has_value_0_BecauseConditionMustBeFalse() {
		// prepare test
		final List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
		metaInfoList.add(new MetaInfoDummy("MinOccurs", "1"));   // missing MinOccurrs metainfo makes conditions false
		metaInfoList.add(new MetaInfoDummy("Nullable", "true")); // this makes occurrence check resulting true

		final MetaInfoValidationData metaInfoValidationData =
			getNullableValidationDataWithConditionMinOccursEqualsZero("1", false); // 'false' causes the occurrence check to be performed

		// call functionality under test
		final boolean result = DataHelper.getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertTrue("Validation failed!", result);
	}

}