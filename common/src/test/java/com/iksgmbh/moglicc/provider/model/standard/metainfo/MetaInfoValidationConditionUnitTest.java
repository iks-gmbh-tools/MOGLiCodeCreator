package com.iksgmbh.moglicc.provider.model.standard.metainfo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class MetaInfoValidationConditionUnitTest {

	@Test
	public void returnsTrueForValidatingPresenceOfMetaInfoWithoutValue() {
		// prepare test
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition("OtherMetaInfo");
		final List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
		metaInfoList.add(new MetaInfoDummy("name1", "value"));		
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo"));
		metaInfoList.add(new MetaInfoDummy("name2"));

		// call functionality under test
		final boolean result = condition.isTrueFor(metaInfoList);
		
		// verify test result
		assertTrue("Validation failed!", result);
	}
	
	@Test
	public void returnsFalseForValidatingPresenceOfMetaInfoWithoutValue() {
		// prepare test
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition("OtherMetaInfo");
		final List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
		metaInfoList.add(new MetaInfoDummy("name1", "value"));		
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo2"));
		metaInfoList.add(new MetaInfoDummy("name2"));

		// call functionality under test
		final boolean result = condition.isTrueFor(metaInfoList);
		
		// verify test result
		assertFalse("Validation failed!", result);
	}

	@Test
	public void returnsTrueForValidatingPresenceOfMetaInfoWithValue() {
		// prepare test
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition("OtherMetaInfo", "otherValue");
		final List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo", "value"));		
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo", "otherValue"));		
		metaInfoList.add(new MetaInfoDummy("name"));

		// call functionality under test
		final boolean result = condition.isTrueFor(metaInfoList);
		
		// verify test result
		assertTrue("Validation failed!", result);
	}
	
	@Test
	public void returnsFalseForValidatingPresenceOfMetaInfoWithValue() {
		// prepare test
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition("OtherMetaInfo", "otherValue");
		final List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo", "value"));		
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo", "otherValue2"));		
		metaInfoList.add(new MetaInfoDummy("name"));

		// call functionality under test
		final boolean result = condition.isTrueFor(metaInfoList);
		
		// verify test result
		assertFalse("Validation failed!", result);
	}

	@Test
	public void returnsTrueForValidatingAbsenceOfMetaInfoWithoutValue() {
		// prepare test
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition(true, "OtherMetaInfo");
		final List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
		metaInfoList.add(new MetaInfoDummy("name1", "value"));		
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo2"));
		metaInfoList.add(new MetaInfoDummy("name2"));

		// call functionality under test
		final boolean result = condition.isTrueFor(metaInfoList);
		
		// verify test result
		assertTrue("Validation failed!", result);
	}
	
	@Test
	public void returnsFalseForValidatingAbsenceOfMetaInfoWithoutValue() {
		// prepare test
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition(true, "OtherMetaInfo");
		final List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
		metaInfoList.add(new MetaInfoDummy("name1", "value"));		
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo"));
		metaInfoList.add(new MetaInfoDummy("name2"));

		// call functionality under test
		final boolean result = condition.isTrueFor(metaInfoList);
		
		// verify test result
		assertFalse("Validation failed!", result);
	}

	@Test
	public void returnsTrueForValidatingAbsenceOfMetaInfoWithValue() {
		// prepare test
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition(true, "OtherMetaInfo", "otherValue");
		final List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo", "value"));		
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo", "otherValue2"));		
		metaInfoList.add(new MetaInfoDummy("name"));

		// call functionality under test
		final boolean result = condition.isTrueFor(metaInfoList);
		
		// verify test result
		assertTrue("Validation failed!", result);
	}
	
	@Test
	public void returnsFalseForValidatingAbsenceOfMetaInfoWithValue() {
		// prepare test
		final MetaInfoValidationCondition condition = new MetaInfoValidationCondition(true, "OtherMetaInfo", "otherValue");
		final List<MetaInfo> metaInfoList = new ArrayList<MetaInfo>();
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo", "value"));		
		metaInfoList.add(new MetaInfoDummy("OtherMetaInfo", "otherValue"));		
		metaInfoList.add(new MetaInfoDummy("name"));

		// call functionality under test
		final boolean result = condition.isTrueFor(metaInfoList);
		
		// verify test result
		assertFalse("Validation failed!", result);
	}

}
