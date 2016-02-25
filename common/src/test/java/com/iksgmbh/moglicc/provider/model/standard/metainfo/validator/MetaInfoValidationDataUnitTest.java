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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validation.MetaInfoValidationData;

public class MetaInfoValidationDataUnitTest {
	
	@Test
	public void parsesOccurrenceWithSimpleValue() throws MOGLiPluginException {
		// call functionality under test
		final MetaInfoValidationData metaInfoValidationData = new MetaInfoValidationData().withOccurrence("1");

		// verify test result
		assertEquals("maxOccurs", 1, metaInfoValidationData.getMaxOccurs());
		assertEquals("minOccurs", 1, metaInfoValidationData.getMinOccurs());
	}

	@Test
	public void parsesOccurrenceWithRange() throws MOGLiPluginException {
		// call functionality under test
		final MetaInfoValidationData metaInfoValidationData = new MetaInfoValidationData().withOccurrence("1-3");

		// verify test result
		assertEquals("maxOccurs", 3, metaInfoValidationData.getMaxOccurs());
		assertEquals("minOccurs", 1, metaInfoValidationData.getMinOccurs());
	}

	@Test
	public void failsToParseDueToCharacter() throws MOGLiPluginException {
		try {
			// call functionality under test
			new MetaInfoValidationData().withOccurrence("a1");
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			assertEquals("Error message", "Value <a1> is no valid occurence!", e.getMessage());
		}
	}

	@Test
	public void failsToParseRangeDueToCharacterInValue1() throws MOGLiPluginException {
		try {
			// call functionality under test
			new MetaInfoValidationData().withOccurrence("q1-2");
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			assertEquals("Error message", "Range <q1-2> is no valid occurence!", e.getMessage());
		}
	}
	
	@Test
	public void failsToParseRangeDueToCharacterInValue2() throws MOGLiPluginException {
		try {
			// call functionality under test
			new MetaInfoValidationData().withOccurrence("1-a1");
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			assertEquals("Error message", "Range <1-a1> is no valid occurence!", e.getMessage());
		}
	}

	@Test
	public void failsToParseRangeDueToMissingValue2() throws MOGLiPluginException {
		try {
			// call functionality under test
			new MetaInfoValidationData().withOccurrence("1-");
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			assertEquals("Error message", "Value <1-> is no valid occurence!", e.getMessage());
		}
	}

	@Test
	public void failsToParseRangeDueToMissingValue1() throws MOGLiPluginException {
		try {
			// call functionality under test
			new MetaInfoValidationData().withOccurrence("-7");
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			assertEquals("Error message", "Value <-7> is no valid occurence!", e.getMessage());
		}
	}
}