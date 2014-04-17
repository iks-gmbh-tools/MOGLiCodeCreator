package com.iksgmbh.moglicc.provider.model.standard.metainfo.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoDummy;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidationData;


public class NumOccurMetaInfoValidatorUnitTest  {

	private List<MetaInfo> metaInfoList;

	@Before
	public void setup() {
		metaInfoList = createMetaInfoTestStandardList();
	}

	@Test
	public void returnsTrueForValidatingMaxOccurs0() {
		// prepare test
		final MetaInfoValidationData metaInfoValidationData = new  MetaInfoValidationData().withMetaInfoName("notExistingMetaInfo")
				                                                                           .withHierarchyLevel(HierarchyLevel.Attribute);

		// call functionality under test
		final boolean result = getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertTrue("Validation failed!", result);
	}

	@Test
	public void returnsTrueForValidatingMaxOccurs1() {
		// prepare test
		final MetaInfoValidationData metaInfoValidationData = new  MetaInfoValidationData().withMetaInfoName("singleMetaInfo")
																						   .withMinOccurs(1)
																						   .withMaxOccurs(1)
				                                                                           .withHierarchyLevel(HierarchyLevel.Attribute);

		// call functionality under test
		final boolean result = getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertTrue("Validation failed!", result);
	}

	@Test
	public void returnsTrueForValidatingMaxOccurs2() {
		// prepare test
		final MetaInfoValidationData metaInfoValidationData = new  MetaInfoValidationData().withMetaInfoName("doubleMetaInfo")
																						   .withMinOccurs(2)
																						   .withMaxOccurs(2)
				                                                                           .withHierarchyLevel(HierarchyLevel.Attribute);

		// call functionality under test
		final boolean result = getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertTrue("Validation failed!", result);
	}

	@Test
	public void returnsFalseValidatingMaxOccurs1() {
		// prepare test
		final MetaInfoValidationData metaInfoValidationData = new MetaInfoValidationData().withMetaInfoName("notExistingMetaInfo")
																						   .withMaxOccurs(1)
																						   .withMinOccurs(1)
				                                                                           .withHierarchyLevel(HierarchyLevel.Attribute);

		// call functionality under test
		final boolean result = getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertFalse("Validation failed!", result);
	}

	@Test
	public void returnsFalseForValidatingForExeedingMaxOccurs2() {
		// prepare test
		final MetaInfoValidationData metaInfoValidationData = new MetaInfoValidationData().withMetaInfoName("tripleMetaInfo")
																						   .withMaxOccurs(2)
				                                                                           .withHierarchyLevel(HierarchyLevel.Attribute);

		// call functionality under test
		final boolean result = getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertFalse("Validation failed!", result);
	}

	@Test
	public void returnsFalseForValidatingLessThanMinOccurs2() {
		// prepare test
		final MetaInfoValidationData metaInfoValidationData = new MetaInfoValidationData().withMetaInfoName("singleMetaInfo")
																						   .withMinOccurs(2)
																						   .withMaxOccurs(2)
				                                                                           .withHierarchyLevel(HierarchyLevel.Attribute);

		// call functionality under test
		final boolean result = getValidator(metaInfoValidationData).validate(metaInfoList);

		// verify test result
		assertFalse("Validation failed!", result);
	}


	@Test
	public void throwsExceptionForMinOccursGreaterThanMaxOccurs() {
		// prepare test
		final MetaInfoValidationData metaInfoValidationData = new MetaInfoValidationData().withMinOccurs(1);

		// call functionality under test
		try {
			new ConditionalMetaInfoValidator(metaInfoValidationData);
			fail("Expected exception was not thrown!");
		} catch (IllegalArgumentException e) {
			assertEquals("Error message", "Error: MinOccurs > MaxOccurs", e.getMessage());
		}
	}

	@Test
	public void validatesFalseForMaxOccur0() {
		// prepare test
		final MetaInfoValidationData metaInfoValidationData = new MetaInfoValidationData().withMetaInfoName("singleMetaInfo")
				                                                     .withOccurrence("0").withHierarchyLevel(HierarchyLevel.Attribute);
		
		final ConditionalMetaInfoValidator validator = getValidator(metaInfoValidationData);

		// call functionality under test
		final boolean result = validator.validate(metaInfoList);

		assertFalse("Validation failed!", result);
		assertEquals("error message", "MetaInfo 'singleMetaInfo' was found too many times (expected: 0, actual: 1)", 
				      validator.getValidationErrorMessage());
	}

	protected List<MetaInfo> createMetaInfoTestStandardList() {
		final List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
		metaInfoList.add(new MetaInfoDummy("singleMetaInfo", "singleMetaInfoValue"));
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo", "OtherMetaInfoValue1"));
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo", "OtherMetaInfoValue2"));
		metaInfoList.add(new MetaInfoDummy("doubleMetaInfo", "doubleMetaInfoValue1"));
		metaInfoList.add(new MetaInfoDummy("doubleMetaInfo", "doubleMetaInfoValue2"));
		metaInfoList.add(new MetaInfoDummy("metaInfoName", "metaInfoValue"));
		metaInfoList.add(new MetaInfoDummy("tripleMetaInfo", "tripleMetaInfoValue1"));
		metaInfoList.add(new MetaInfoDummy("tripleMetaInfo", "tripleMetaInfoValue2"));
		metaInfoList.add(new MetaInfoDummy("tripleMetaInfo", "tripleMetaInfoValue3"));
		return metaInfoList;
	}

	public ConditionalMetaInfoValidator getValidator(final MetaInfoValidationData metaInfoValidationData) {
		return new ConditionalMetaInfoValidator(metaInfoValidationData);
	}

}