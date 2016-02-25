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
package com.iksgmbh.moglicc.provider.engine.velocity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

import com.iksgmbh.moglicc.provider.engine.velocity.test.VelocityEngineProviderTestParent;

public class TemplateStringUtilityUnitTest extends VelocityEngineProviderTestParent {

	@Test
	public void returnsDateAsFormattedString() 
	{
		// prepare test
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
		final String dateFormat = "yyyyMMddHHmm";
		final Date date = new Date(1370512924998L);
		System.out.println(date.toString());
		// call functionality under test
		final String nowAsFormattedString = TemplateStringUtility.getDateAsFormattedString(date, dateFormat);
		
		// verify test result
		assertEquals("formatted date", "201306061102", nowAsFormattedString);
	}

	@Test
	public void returnsStringWithReplacements() 
	{
		// prepare test
		final String s = "com.iksgmbh.moglicc.provider.engine.velocity";
		
		// call functionality under test
		final String replacedString = TemplateStringUtility.replaceAllIn(s,	".", "/");

		// verify test result
		assertEquals("replacedString", "com/iksgmbh/moglicc/provider/engine/velocity", replacedString);
	}

	@Test
	public void returnsCommaSeparatedStringToStringArray() 
	{
		// prepare test
		final String s = "a a, b b, c c";
		
		// call functionality under test
		final String[] result = TemplateStringUtility.commaSeparatedStringToStringArray(s);

		// verify test result
		assertEquals("result length", 3, result.length);
		assertEquals("result[0]", "a a", result[0]);
		assertEquals("result[1]", "b b", result[1]);
		assertEquals("result[2]", "c c", result[2]);
	}

	@Test
	public void readsFileContentFromTextFile() throws IOException 
	{
		// prepare test
		final String filename = getProjectTestResourcesDir() + "testFileToRead.txt";
		
		// call functionality under test
		final List<String> result = TemplateStringUtility.getTextFileContent(filename);

		// verify test result
		assertEquals("result length", 5, result.size());
		assertEquals("line 1", "1", result.get(0));
		assertEquals("line 2", "", result.get(1));
		assertEquals("line 3", "2", result.get(2));
		assertEquals("line 4", "", result.get(3));
		assertEquals("line 5", "3", result.get(4));
	}

	@Test
	public void returnsErrorMessageAsSingleElementIfFileToReadWasNotFound() throws IOException 
	{
		// prepare test
		final String filename = "notExisting.txt";
		
		// call functionality under test
		final List<String> result = TemplateStringUtility.getTextFileContent(filename);

		// verify test result
		assertNotNull("not null expected", result);
		assertEquals("result length", 1, result.size());
		assertStringContains(result.get(0), "File");
		assertStringContains(result.get(0), "not found");
	}
	
}