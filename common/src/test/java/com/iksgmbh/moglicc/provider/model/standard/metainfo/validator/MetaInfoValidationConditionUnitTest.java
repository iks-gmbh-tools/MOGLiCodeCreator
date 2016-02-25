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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoDummy;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validation.MetaInfoValidationCondition;

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