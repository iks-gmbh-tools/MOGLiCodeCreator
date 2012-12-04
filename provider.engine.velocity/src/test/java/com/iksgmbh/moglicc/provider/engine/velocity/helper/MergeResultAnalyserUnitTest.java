package com.iksgmbh.moglicc.provider.engine.velocity.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData;
import com.iksgmbh.moglicc.provider.engine.velocity.test.VelocityEngineProviderTestParent;
import com.iksgmbh.utils.FileUtil;

public class MergeResultAnalyserUnitTest extends VelocityEngineProviderTestParent {

	@Test
	public void analysesGeneratorTemplateFile() throws Exception {
		// prepare test
		final File originalTemplateFile = new File(getProjectTestResourcesDir(), TEMPLATE_NO_INCLUDED_SUBTEMPLATE);
		final String textToParse = FileUtil.getFileContent(originalTemplateFile);

		// call functionality under test
		final BuildUpGeneratorResultData buildUpGeneratorResultData = MergeResultAnalyser.doYourJob(textToParse);
		
		// verify test result
		assertNotNull("Not null expected", buildUpGeneratorResultData);
		final String velocityFileContent = "public class ${classDescriptor.simpleName} {" + FileUtil.getSystemLineSeparator() + "}";
		assertEquals("Result File Content", velocityFileContent, buildUpGeneratorResultData.getGeneratedContent());
		assertEquals("Number of Properties", 2, buildUpGeneratorResultData.getPropertiesNumber());
	}
	
	@Test
	public void removesCommentLines() throws MOGLiPluginException {
		// prepare test
		final String textToParse = "@TargetFileName ${classDescriptor.simpleName}.java # Name of file with extension without path"
									 + FileUtil.getSystemLineSeparator()
									 + "#@TargetDir ../targetDirFromTemplateFile"
									 + FileUtil.getSystemLineSeparator()
									 + "public class ${classDescriptor.simpleName} {"
									 + FileUtil.getSystemLineSeparator() + "}";

		// call functionality under test
		final BuildUpGeneratorResultData buildUpGeneratorResultData = MergeResultAnalyser.doYourJob(textToParse);
		
		// verify test result
		final String velocityFileContent = "public class ${classDescriptor.simpleName} {" + FileUtil.getSystemLineSeparator() + "}";
		assertEquals("Result File Content", velocityFileContent, buildUpGeneratorResultData.getGeneratedContent());
	}
	
	@Test
	public void parsesPropertiesCaseInsensitive() throws MOGLiPluginException {
		// prepare test
		final String textToParse = "@targetFileName TestName.java # Name of file with extension without path"
									 + FileUtil.getSystemLineSeparator()
									 + "@targetdir dir"
									 + FileUtil.getSystemLineSeparator()
									 + "@TEST foo";

		// call functionality under test
		final BuildUpGeneratorResultData buildUpGeneratorResultData = MergeResultAnalyser.doYourJob(textToParse);
		
		// verify test result
		String property = VelocityGeneratorResultData.KnownGeneratorPropertyNames.TargetFileName.name();
		assertEquals("property", "TestName.java", buildUpGeneratorResultData.getProperty(property));
		property = VelocityGeneratorResultData.KnownGeneratorPropertyNames.TargetDir.name();
		assertEquals("property", "dir", buildUpGeneratorResultData.getProperty(property));
		property = "TEST";
		assertEquals("property", "foo", buildUpGeneratorResultData.getProperty(property));
	}

}
