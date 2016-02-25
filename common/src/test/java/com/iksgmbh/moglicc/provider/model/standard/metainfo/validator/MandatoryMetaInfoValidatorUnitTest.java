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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoDummy;

public class MandatoryMetaInfoValidatorUnitTest {
	
	@Test
	public void validatesWithoutError() {
		// prepare test
		final MandatoryMetaInfoValidator validator = new MandatoryMetaInfoValidator("MetaInfo1", HierarchyLevel.Model);
		validator.setVendorPluginId("aPluginId");
		final List<MetaInfo> metaInfoList = buildMetaInfoList(HierarchyLevel.Model, 2);

		// call functionality under test
		boolean result = validator.validate(metaInfoList);
		
		// verify test result
		assertTrue("Validation failed!", result);
		assertEquals("Error message", null, validator.getValidationErrorMessage());
	}

	@Test
	public void validatesWithoutErrorBecauseWrongHierarchyLevel() { // TODO macht das sinn?
		// prepare test
		final OptionalMetaInfoValidator validator = new OptionalMetaInfoValidator("MetaInfo0", HierarchyLevel.Model);
		final List<MetaInfo> metaInfoList = buildMetaInfoList(HierarchyLevel.Class, 1);
		metaInfoList.add(metaInfoList.get(0));

		// call functionality under test
		boolean result = validator.validate(metaInfoList);
		
		// verify test result
		assertTrue("Validation failed!", result);
	}
	
	@Test
	public void validatesWithErrorBecauseMissingMandatoryMetaInfo() {
		// prepare test
		final MandatoryMetaInfoValidator validator = new MandatoryMetaInfoValidator("MetaInfoX", HierarchyLevel.Model);
		final List<MetaInfo> metaInfoList = buildMetaInfoList(HierarchyLevel.Model, 2);

		// call functionality under test
		boolean result = validator.validate(metaInfoList);
		
		// verify test result
		assertFalse("Validation failed!", result);
		assertEquals("Error message", "Mandatory metaInfo 'MetaInfoX' was not found", validator.getValidationErrorMessage());
	}

	
	private List<MetaInfo> buildMetaInfoList(final HierarchyLevel hierarchyLevel, final int i) {
		final List<MetaInfo> toReturn = new ArrayList<MetaInfo>();
		for (int j = 0; j < i; j++) {
			final MetaInfo metaInfo = new MetaInfoDummy(hierarchyLevel, "MetaInfo" + j);
			toReturn.add(metaInfo);
		}
		return toReturn;
	}
}