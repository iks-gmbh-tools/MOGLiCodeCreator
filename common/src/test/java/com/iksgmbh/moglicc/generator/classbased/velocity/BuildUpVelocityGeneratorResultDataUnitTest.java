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
package com.iksgmbh.moglicc.generator.classbased.velocity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData.KnownGeneratorPropertyNames;

public class BuildUpVelocityGeneratorResultDataUnitTest
{
	private VelocityGeneratorResultData velocityGeneratorResultData;
	private BuildUpGeneratorResultData buildUpGeneratorResultData;

	@Before
	public void setup()
	{
		buildResultData("Content", "targetDir", "filename", false);
	}

	private void buildResultData(final String content, final String targetDir, final String filename, final boolean createNew)
	{
		buildUpGeneratorResultData = new BuildUpGeneratorResultData();
		buildUpGeneratorResultData.setGeneratedContent(content);
		buildUpGeneratorResultData.addProperty(KnownGeneratorPropertyNames.TargetDir.name(), targetDir);
		buildUpGeneratorResultData.addProperty(KnownGeneratorPropertyNames.TargetFileName.name(), filename);

		if (createNew) {
			buildUpGeneratorResultData.addProperty(KnownGeneratorPropertyNames.CreateNew.name(), "true");
		}

		velocityGeneratorResultData = new BuildUpVelocityGeneratorResultData(buildUpGeneratorResultData);
	}

	@Test
	public void returnsTargetFileName()
	{
		// call functionality under test
		final String targetFileName = velocityGeneratorResultData.getTargetFileName();

		// verify test result
		assertEquals("targetFileName", "filename", targetFileName);
	}

	@Test
	public void returnsTargetDir()
	{
		// call functionality under test
		final String targetDir = velocityGeneratorResultData.getTargetDir();
		
		// verify test result
		assertEquals("targetDir", "targetDir", targetDir);
	}

	@Test
	public void returnsGeneratedContent()
	{
		// call functionality under test
		final String content = velocityGeneratorResultData.getGeneratedContent();

		// verify test result
		assertEquals("Content", "Content", content);
	}

	@Test
	public void returnsOverwriteWithNullValue()
	{
		// call functionality under test
		final boolean overwrite = velocityGeneratorResultData.isTargetToBeCreatedNewly();

		// verify test result
		assertEquals("overwrite", false, overwrite);
	}

	@Test
	public void returnsCreateNewWithWrongValue()
	{
		// prepare test
		buildUpGeneratorResultData.addProperty(KnownGeneratorPropertyNames.CreateNew.name(), "foo");
		velocityGeneratorResultData = new BuildUpVelocityGeneratorResultData(buildUpGeneratorResultData);

		// call functionality under test
		final boolean overwrite = velocityGeneratorResultData.isTargetToBeCreatedNewly();

		// verify test result
		assertEquals("overwrite", false, overwrite);
	}

	@Test
	public void returnsCreateNewWithTrueValueIgnoringCase()
	{
		// prepare test
		buildUpGeneratorResultData.addProperty(KnownGeneratorPropertyNames.CreateNew.name(), "trUe");
		velocityGeneratorResultData = new BuildUpVelocityGeneratorResultData(buildUpGeneratorResultData);

		// call functionality under test
		final boolean createNew = velocityGeneratorResultData.isTargetToBeCreatedNewly();

		// verify test result
		assertEquals("overwrite", true, createNew);
	}
	
	

	@Test
	public void returnsTargetDirWithApplicationRootButWithNullValue() throws MOGLiPluginException
	{
		// prepare test
		final File file = new File("target/temp");
		if (! file.exists()) file.mkdirs();		
		buildResultData("Content", VelocityGeneratorResultData.ROOT_IDENTIFIER + "/target/temp", "filename", false);

		// call functionality under test
		String targetDir = velocityGeneratorResultData.getTargetDirAsFile(null, "").getAbsolutePath();

		// verify test result
		targetDir = targetDir.replace('\\', '/');
		assertTrue("Unexpected targetDir", targetDir.endsWith("target/temp"));
	}

	@Test
	public void returnsTargetDirWithoutApplicationRoot() throws MOGLiPluginException, IOException
	{
		// prepare test
		final File file = new File("target/temp");
		if (! file.exists()) file.mkdirs();
		buildResultData("Content", "target/temp", "filename", false);

		// call functionality under test
		String targetDir = velocityGeneratorResultData.getTargetDirAsFile("root", "").getAbsolutePath();

		// verify test result
		targetDir = targetDir.replace('\\', '/');
		assertTrue("Unexpected targetDir", targetDir.endsWith("target/temp"));
	}

	@Test
	public void returnsTargetDirWithApplicationRoot() throws MOGLiPluginException
	{
		// prepare test
		final String applicationRootDir = "root";
		buildResultData("Content", VelocityGeneratorResultData.ROOT_IDENTIFIER + "/target/temp", "filename", true);

		// call functionality under test
		String targetDir = velocityGeneratorResultData.getTargetDirAsFile(applicationRootDir, "").getAbsolutePath();

		// verify test result
		targetDir = targetDir.replace('\\', '/');
		assertTrue("targetDir not correct", targetDir.endsWith("/" + applicationRootDir + "/target/temp"));
	}

	@Test
	public void returnsSkipGenerationTrue() throws MOGLiPluginException
	{
		// prepare test 1
		setNewBuildUpVelocityGeneratorResultData("true");
		
		// call functionality under test 
		boolean result = velocityGeneratorResultData.isGenerationToSkip();

		// verify test result 
		assertTrue("skipGeneration true expected", result);
		
		
		
		// prepare test 2
		setNewBuildUpVelocityGeneratorResultData("NOT false");
		
		// call functionality under test 
		result = velocityGeneratorResultData.isGenerationToSkip();

		// verify test result 
		assertTrue("skipGeneration true expected", result);
		

		
		// prepare test 3
		setNewBuildUpVelocityGeneratorResultData(" ABC == ABC ");
		
		// call functionality under test 
		result = velocityGeneratorResultData.isGenerationToSkip();

		// verify test result 
		assertTrue("skipGeneration true expected", result);
		

		
		// prepare test 4
		setNewBuildUpVelocityGeneratorResultData(" ABC != ABD ");
		
		// call functionality under test 
		result = velocityGeneratorResultData.isGenerationToSkip();

		// verify test result 
		assertTrue("skipGeneration true expected", result);		
	}

	private void setNewBuildUpVelocityGeneratorResultData(final String skipGenerationValue)
	{
		buildUpGeneratorResultData = new BuildUpGeneratorResultData();
		buildUpGeneratorResultData.addProperty(KnownGeneratorPropertyNames.SkipGeneration.name(), skipGenerationValue);
		velocityGeneratorResultData = new BuildUpVelocityGeneratorResultData(buildUpGeneratorResultData);
	}
	
	@Test
	public void returnsSkipGenerationFalse() throws MOGLiPluginException
	{
		// prepare test 1
		setNewBuildUpVelocityGeneratorResultData("false");
		
		// call functionality under test 1
		boolean result = velocityGeneratorResultData.isGenerationToSkip();

		// verify test result 1
		assertFalse("skipGeneration false expected", result);
		
		
		
		// prepare test 2
		setNewBuildUpVelocityGeneratorResultData("not true");
		
		// call functionality under test 
		result = velocityGeneratorResultData.isGenerationToSkip();

		// verify test result 
		assertFalse("skipGeneration false expected", result);
		
		
		
		// prepare test 3
		setNewBuildUpVelocityGeneratorResultData(" ABC != ABC ");
		
		// call functionality under test 
		result = velocityGeneratorResultData.isGenerationToSkip();

		// verify test result 
		assertFalse("skipGeneration false expected", result);
		

		
		// prepare test 4
		setNewBuildUpVelocityGeneratorResultData(" ABC == ABD ");
		
		// call functionality under test 
		result = velocityGeneratorResultData.isGenerationToSkip();

		// verify test result 
		assertFalse("skipGeneration false expected", result);
		
	}
	
}