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
