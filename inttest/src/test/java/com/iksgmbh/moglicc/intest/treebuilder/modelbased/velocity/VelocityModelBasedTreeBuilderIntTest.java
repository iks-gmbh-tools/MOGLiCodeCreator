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
package com.iksgmbh.moglicc.intest.treebuilder.modelbased.velocity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.data.FolderContent;
import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.BuildUpVelocityGeneratorResultData;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.moglicc.treebuilder.modelbased.velocity.VelocityModelBasedTreeBuilderStarter;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class VelocityModelBasedTreeBuilderIntTest extends IntTestParent {

	private static final String UMLAUT_PLATZHALTER = "<platzhalter>";
	private static final String UMLAUT_TEST_FILE = "UmlautTestFile.txt";
	
	@Before
	@Override
	public void setup() {
		super.setup();
		FileUtil.deleteDirWithContent(modelPropertiesFile.getParent());
		try {
			standardModelProviderStarter.unpackDefaultInputData();
		} catch (MOGLiPluginException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void createsNewGeneratorPluginUsing_MOGLiCC_NewPluginModel() throws Exception {
		// prepare test
		FileUtil.createNewFileWithContent(modelPropertiesFile, "modelfile=MOGLiCC_NewPluginModel.txt");
		standardModelProviderStarter.doYourJob();
		final File targetDir = new File(applicationRootDir, "MOGLiCC_NewPluginProject");
		assertFileDoesNotExist(targetDir);	

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();
		velocityModelBasedTreeBuilderStarter.doYourJob();

		// verify test result
		assertFileExists(targetDir);
		final FolderContent folderContent = new FolderContent(targetDir, null);
		assertEquals("folder number", 14, folderContent.getFolders().size());
		assertEquals("total file number", 3, folderContent.getFiles().size());	
		assertEquals("pom file", "pom.xml", folderContent.getFiles().get(0).getName());
		assertEquals("pom file", "readme.md", folderContent.getFiles().get(1).getName());
		assertEquals("starter class", "MyTestGeneratorStarter.java", folderContent.getFiles().get(2).getName());
	}

	@Test
	public void createsNewArtefactWithUmlautFile() throws MOGLiPluginException {
		// prepare test
		final String targetDirPath = MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER + "/test";
		createNewModelFileContent(targetDirPath);
		
		final File targetDir = new File(applicationInputDir, VelocityModelBasedTreeBuilderStarter.PLUGIN_ID + "/umlautArtefact");
		targetDir.mkdirs();
		
		final boolean withTargetDirProperty = true;
		final File artefactPropertiesFile = createArtefactPropertiesFile(targetDir, withTargetDirProperty);
		
		final File contentFile = new File(artefactPropertiesFile.getParentFile(), UMLAUT_TEST_FILE);
		MOGLiFileUtil.createNewFileWithContent(contentFile, UMLAUT_PLATZHALTER);
		
		standardModelProviderStarter.doYourJob();
		velocityClassBasedFileMakerStarter.doYourJob();

		// call functionality under test
		velocityModelBasedTreeBuilderStarter.doYourJob();

		// verify test result
		assertFileExists(targetDir);
		final File outputFile = new File(applicationRootDir, "TreeBuilderGeneratorUmlautTest/UmlautTestFile.txt");
		assertStringStartsWith(MOGLiFileUtil.getFileContent(outputFile), "???????");
	}

	private File createArtefactPropertiesFile(final File targetDir, final boolean withTargetDirProperty) 
	{
		final String targetDirLine;
		if (withTargetDirProperty) {
			targetDirLine = "@TargetDir <applicationRootDir>" + FileUtil.getSystemLineSeparator();
		} else {
			targetDirLine = "";
		}
		
		final File templateFile = new File(targetDir, "artefact.properties");
		MOGLiFileUtil.createNewFileWithContent(templateFile, "@RootName TreeBuilderGeneratorUmlautTest" + FileUtil.getSystemLineSeparator() +
                targetDirLine +
                "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@OutputEncodingFormat ASCII" + FileUtil.getSystemLineSeparator() + 
                "@NameOfValidModel UmlautTestModel" + FileUtil.getSystemLineSeparator() +
                "@ReplaceIn " + UMLAUT_TEST_FILE + " " + UMLAUT_PLATZHALTER + " �������");
		return templateFile;
	}

	private void createNewModelFileContent(final String targetDirPath) {
		MOGLiFileUtil.createNewFileWithContent(modelFile, "model UmlautTestModel" 
											                + FileUtil.getSystemLineSeparator()
											                + "   MetaInfo ProjectName NewTestGenerator" 
											                + FileUtil.getSystemLineSeparator()
											                + "   MetaInfo eclipseProjectDir " + targetDirPath
											                + FileUtil.getSystemLineSeparator()
											                + "class de.test.TestClass");
	}

	@Test
	public void renamesFileWithNameReadFromModelFile() throws Exception {
		// prepare test
		FileUtil.deleteDirWithContent(velocityModelBasedTreeBuilderStarter.getInfrastructure().getPluginInputDir());
		
		final String filenameToReplace = "ToReplace.txt";
		final String filenameReplaced = "Replaced.txt";
		final String modelName = "TreeBuilderRenamingTest";
		final String artefactName = "RenamingTestArtefact";
		
		prepareRenamingTest(modelName, artefactName, filenameToReplace, filenameReplaced);

		// call functionality under test
		velocityModelBasedTreeBuilderStarter.doYourJob();
		
		// verify test result
		final File fileToReplace = new File(applicationOutputDir, VelocityModelBasedTreeBuilderStarter.PLUGIN_ID + "/" + artefactName + "/" + filenameToReplace );
		assertFileDoesNotExist(fileToReplace);
		final File fileReplaced = new File(applicationOutputDir, VelocityModelBasedTreeBuilderStarter.PLUGIN_ID + "/" + artefactName + "/" + filenameReplaced );
		assertFileExists(fileReplaced);
	}

	private void prepareRenamingTest(final String modelName, final String artefactName,
									 final String filenameToReplace,
			                         final String filenameReplaced) throws MOGLiPluginException 
	{
		final String nameOfValidModel = "TreeBuilderRenamingTest";
		final String targetDir = "RenamingTestResult";
		final File artefactPropertiesFile = prepareArtefactDirectory("artefact.properties", 
				                                                     VelocityModelBasedTreeBuilderStarter.PLUGIN_ID, artefactName);
		MOGLiFileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName " + targetDir + " " + FileUtil.getSystemLineSeparator() +
                "@TargetDir <applicationRootDir>" + FileUtil.getSystemLineSeparator() +
                "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@NameOfValidModel " + nameOfValidModel + FileUtil.getSystemLineSeparator() +
                "@RenameFile " + filenameToReplace + " $model.getMetaInfoValueFor(\"filename\")");
		final File artefactFile = new File(applicationInputDir, VelocityModelBasedTreeBuilderStarter.PLUGIN_ID + "/" + artefactName + "/" + filenameToReplace );
		MOGLiFileUtil.createNewFileWithContent(artefactFile, "");
		assertFileExists(artefactFile);
		
		modelPropertiesFile.getParentFile().mkdirs();
		MOGLiFileUtil.createNewFileWithContent(modelFile, "model " + modelName + FileUtil.getSystemLineSeparator() +
                "  metainfo filename " + filenameReplaced + FileUtil.getSystemLineSeparator() + 
                FileUtil.getSystemLineSeparator() +
                "class de.Test" + FileUtil.getSystemLineSeparator());
				
		standardModelProviderStarter.doYourJob();
		velocityClassBasedFileMakerStarter.doYourJob();
	}

	protected File prepareArtefactDirectory(final String fileName, final String pluginId, final String parentName) {
		final File targetDir = new File(applicationInputDir, pluginId + "/" + parentName);
		targetDir.mkdirs();
		final File templateFile = new File(targetDir, fileName);
		return templateFile;
	}

	@Test
	public void appliesArtefactOnlyToDefinedModel() throws Exception {
		// prepare test
		final String filenameToReplace = "ToReplace.txt";
		final String filenameReplaced = "Replaced.txt";
		final String modelName = "NotExistingModel";
		final String artefactName = "RenamingTestArtefact";
		
		prepareRenamingTest(modelName, artefactName, filenameToReplace, filenameReplaced);

		// call functionality under test
		velocityModelBasedTreeBuilderStarter.doYourJob();

		// verify test result
		final File fileToReplace = new File(applicationOutputDir, VelocityModelBasedTreeBuilderStarter.PLUGIN_ID + "/" + artefactName + "/" + filenameToReplace );
		assertFileDoesNotExist(fileToReplace);
		final File fileReplaced = new File(applicationOutputDir, VelocityModelBasedTreeBuilderStarter.PLUGIN_ID + "/" + artefactName + "/" + filenameReplaced );
		assertFileDoesNotExist(fileReplaced);

	}
	
	@Test
	public void renamesPath() throws Exception {
		// prepare test
		final String artefactName = "RenamingDirTestArtefact";
		final String modelName = "RenameDirModel";
		final String targetDir = "RenamingDirTestResult";
		final String newPath = "com/iksgmbh/moglicc";
		modelPropertiesFile.getParentFile().mkdirs();
		MOGLiFileUtil.createNewFileWithContent(modelFile, "model " + modelName + FileUtil.getSystemLineSeparator() +
				"  metainfo projectPath " + newPath);

		final File artefactPropertiesFile = prepareArtefactDirectory("artefact.properties", 
                VelocityModelBasedTreeBuilderStarter.PLUGIN_ID, artefactName);
		MOGLiFileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName " + targetDir + " " + FileUtil.getSystemLineSeparator() +
                "@TargetDir <applicationRootDir>" + FileUtil.getSystemLineSeparator() +
                "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@NameOfValidModel " + modelName + FileUtil.getSystemLineSeparator() +
                "@RenameDir komponentenSpezifischerPfad $model.getMetaInfoValueFor(\"projectPath\")" + FileUtil.getSystemLineSeparator() +
                "@RenameDir hg_init .hg");
		
		final File toRename1 = new File(velocityModelBasedTreeBuilderStarter.getInfrastructure().getPluginInputDir(), 
                                        artefactName + "/hg_init");
		toRename1.mkdirs();
		final File toRename2 = new File(velocityModelBasedTreeBuilderStarter.getInfrastructure().getPluginInputDir(), 
                artefactName + "/src/main/java/komponentenSpezifischerPfad/package");
		toRename2.mkdirs();
		final File file = new File(toRename2, "Test.txt");
		file.createNewFile();
		final File newDirFile1 = new File(applicationRootDir, targetDir + "/src/main/java/" + newPath + "/package/"+ file.getName());
		assertFileDoesNotExist(newDirFile1);
		final File newDir2 = new File(applicationRootDir, targetDir + "/.hg");
		assertFileDoesNotExist(newDir2);

		// call functionality under test		
		standardModelProviderStarter.doYourJob();
		velocityModelBasedTreeBuilderStarter.doYourJob();

		// verify test result
		assertFileExists(newDirFile1);
		assertFileExists(newDir2);	
	}

	@Test
	public void throwsExceptionForUnkownPlaceHolder() throws Exception {
		// prepare test
		final File artefactDir = new File(velocityModelBasedTreeBuilderStarter.getInfrastructure().getPluginInputDir(), "testArtefact");
		artefactDir.mkdirs();
		final File artefactPropertiesFile = new File(artefactDir, VelocityModelBasedTreeBuilderStarter.FILENAME_ARTEFACT_PROPERTIES);
		FileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName $model.getMetaInfoValueFor(\"UnkownPlaceholder\")" 
				                                                   + FileUtil.getSystemLineSeparator() +
				                                                  "@NameOfValidModel MOGLiCC_JavaBeanModel"  );
		standardModelProviderStarter.doYourJob();

		try {
			// call functionality under test
			velocityModelBasedTreeBuilderStarter.doYourJob();
			fail("Expected exception not thrown");
		} catch (Exception e) {
			// verify test result
			assertStringContains(e.getMessage(), BuildUpVelocityGeneratorResultData.META_INFO_NOT_FOUND);
		}
	}

	@Test
	public void createsNoTargetDirIfNotDefinedInArtefactProperties() throws Exception 
	{
		// prepare test
		final String targetDirPath = MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER + "/noTargetDirArtefact";
		createNewModelFileContent(targetDirPath);
		
		final File artefactInputDir = new File(applicationInputDir, VelocityModelBasedTreeBuilderStarter.PLUGIN_ID + "/noTargetDirArtefact");
		artefactInputDir.mkdirs();
		
		final boolean withTargetDirProperty = false;
		final File artefactPropertiesFile = createArtefactPropertiesFile(artefactInputDir, withTargetDirProperty);
		
		final File contentFile = new File(artefactPropertiesFile.getParentFile(), "noTargetDirFile.txt");
		MOGLiFileUtil.createNewFileWithContent(contentFile, "noTargetDirFileContent");
		
		standardModelProviderStarter.doYourJob();
		velocityClassBasedFileMakerStarter.doYourJob();

		// call functionality under test
		velocityModelBasedTreeBuilderStarter.doYourJob();

		// verify test result
		final File artefactOutputDir = new File(applicationOutputDir, VelocityModelBasedTreeBuilderStarter.PLUGIN_ID + "/noTargetDirArtefact");
		assertFileExists(artefactOutputDir);
		final File targetDir = new File(applicationRootDir, "noTargetDirArtefact");
		assertFileDoesNotExist(targetDir);
	}

}