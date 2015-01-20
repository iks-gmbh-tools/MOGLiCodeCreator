package com.iksgmbh.moglicc.provider.engine.velocity;

import static org.junit.Assert.assertEquals;

import java.util.Date;
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

	
}
