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
package com.iksgmbh.moglicc.generator.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;

public class TemplateUtilUnitTest {

	@Test
	public void returnsMainTemplateFromTemplateTestDir() throws MOGLiPluginException {
		// prepare test
		final File dir = new File("../common/src/test/resources/templateTestDir");

		// call functionality under test
		final String mainTemplate = TemplateUtil.findMainTemplate(dir, null);

		// verify test result
		assertNotNull("Not null expected", mainTemplate);
		assertEquals("mainTemplate", "test.txt", mainTemplate);
	}
}