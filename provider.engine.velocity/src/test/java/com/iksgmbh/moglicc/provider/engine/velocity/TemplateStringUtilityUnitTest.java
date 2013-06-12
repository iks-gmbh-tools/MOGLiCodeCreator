package com.iksgmbh.moglicc.provider.engine.velocity;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.iksgmbh.moglicc.provider.engine.velocity.test.VelocityEngineProviderTestParent;

public class TemplateStringUtilityUnitTest extends VelocityEngineProviderTestParent {

	@Test
	public void returnsNowAsFormattedString() {
		// prepare test
		final String dateFormat = "yyyyMMddHHmm";
		final Date date = new Date(1370512924998L);
		// call functionality under test
		final String nowAsFormattedString = TemplateStringUtility
				.getDateAsFormattedString(date, dateFormat);
		// verify test result
		assertEquals("formatted date", "201306061202", nowAsFormattedString);
	}

	@Test
	public void returnsStringWithReplacements() {
		// prepare test
		final String s = "com.iksgmbh.moglicc.provider.engine.velocity";
		// call functionality under test
		final String replacedString = TemplateStringUtility.replaceAllIn(s,	".", "/");
		// verify test result
		assertEquals("replacedString", "com/iksgmbh/moglicc/provider/engine/velocity", replacedString);
	}

}
