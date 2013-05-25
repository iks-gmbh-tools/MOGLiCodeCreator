package com.iksgmbh.moglicc.intest.generator.modelbased.filestructure;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import com.iksgmbh.data.FolderContent;
import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.modelbased.filestructure.FilestructureModelBasedGeneratorStarter;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class FileStructureModelBasedGeneratorIntTest extends IntTestParent {

	private static final String UMLAUT_PLATZHALTER = "<platzhalter>";
	private static final String UMLAUT_TEST_FILE = "UmlautTestFile.txt";

	@Test
	public void createsNewGeneartorPluginUsing_MOGLiCC_NewPluginModel() throws Exception {
		// prepare test
		FileUtil.createNewFileWithContent(modelPropertiesFile, "modelfile=MOGLiCC_NewPluginModel.txt");
		standardModelProviderStarter.doYourJob();
		final File targetDir = new File(applicationRootDir, "MOGLiCC_NewPluginProject");
		assertFileDoesNotExist(targetDir);	

		// call functionality under test
		velocityClassBasedGeneratorStarter.doYourJob();
		filestructureModelBasedGeneratorStarter.doYourJob();

		// cleanup
		FileUtil.deleteDirWithContent(modelPropertiesFile.getParent());
		
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
		
		final File targetDir = new File(applicationInputDir, FilestructureModelBasedGeneratorStarter.PLUGIN_ID + "/umlautArtefact");
		targetDir.mkdirs();
		
		final File artefactPropertiesFile = createArtefactPropertiesFile(targetDir);
		
		final File contentFile = new File(artefactPropertiesFile.getParentFile(), UMLAUT_TEST_FILE);
		MOGLiFileUtil.createNewFileWithContent(contentFile, UMLAUT_PLATZHALTER);
		
		standardModelProviderStarter.doYourJob();
		velocityClassBasedGeneratorStarter.doYourJob();

		// call functionality under test
		filestructureModelBasedGeneratorStarter.doYourJob();

		// verify test result
		assertFileExists(targetDir);
		final File outputFile = new File(applicationRootDir, "FileStructureGeneratorUmlautTest/UmlautTestFile.txt");
		assertStringEquals("outputFileContent", "???????", MOGLiFileUtil.getFileContent(outputFile));
	}

	private File createArtefactPropertiesFile(final File targetDir) {
		final File templateFile = new File(targetDir, "artefact.properties");
		MOGLiFileUtil.createNewFileWithContent(templateFile, "@RootName FileStructureGeneratorUmlautTest" + FileUtil.getSystemLineSeparator() +
                "@TargetDir <applicationRootDir>" + FileUtil.getSystemLineSeparator() +
                "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@OutputEncodingFormat ASCII" + FileUtil.getSystemLineSeparator() + 
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
		FileUtil.deleteDirWithContent(filestructureModelBasedGeneratorStarter.getMOGLiInfrastructure().getPluginInputDir());
		
		final String filenameToReplace = "ToReplace.txt";
		final String filenameReplaced = "Replaced.txt";
		final String modelName = "FileStructureRenamingTest";
		final String artefactName = "RenamingTestArtefact";
		
		prepareRenamingTest(modelName, artefactName, filenameToReplace, filenameReplaced);

		// call functionality under test
		filestructureModelBasedGeneratorStarter.doYourJob();
		
		// cleanup
		FileUtil.deleteDirWithContent(modelPropertiesFile.getParent());

		// verify test result
		final File fileToReplace = new File(applicationOutputDir, FilestructureModelBasedGeneratorStarter.PLUGIN_ID + "/" + artefactName + "/" + filenameToReplace );
		assertFileDoesNotExist(fileToReplace);
		final File fileReplaced = new File(applicationOutputDir, FilestructureModelBasedGeneratorStarter.PLUGIN_ID + "/" + artefactName + "/" + filenameReplaced );
		assertFileExists(fileReplaced);
	}

	private void prepareRenamingTest(final String modelName, final String artefactName,
									 final String filenameToReplace,
			                         final String filenameReplaced) throws MOGLiPluginException 
	{
		final String nameOfValidModel = "FileStructureRenamingTest";
		final String targetDir = "RenamingTestResult";
		final File artefactPropertiesFile = prepareArtefactDirectory("artefact.properties", 
				                                                     FilestructureModelBasedGeneratorStarter.PLUGIN_ID, artefactName);
		MOGLiFileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName " + targetDir + " " + FileUtil.getSystemLineSeparator() +
                "@TargetDir <applicationRootDir>" + FileUtil.getSystemLineSeparator() +
                "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@NameOfValidModel " + nameOfValidModel + FileUtil.getSystemLineSeparator() +
                "@RenameFile " + filenameToReplace + " ${ModelMetaInfo=filename}");
		final File artefactFile = new File(applicationInputDir, FilestructureModelBasedGeneratorStarter.PLUGIN_ID + "/" + artefactName + "/" + filenameToReplace );
		MOGLiFileUtil.createNewFileWithContent(artefactFile, "");
		assertFileExists(artefactFile);
		
		modelPropertiesFile.getParentFile().mkdirs();
		MOGLiFileUtil.createNewFileWithContent(modelFile, "model " + modelName + FileUtil.getSystemLineSeparator() +
                "  metainfo filename " + filenameReplaced + FileUtil.getSystemLineSeparator() + 
                FileUtil.getSystemLineSeparator() +
                "class de.Test" + FileUtil.getSystemLineSeparator());
				
		standardModelProviderStarter.doYourJob();
		velocityClassBasedGeneratorStarter.doYourJob();
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
		filestructureModelBasedGeneratorStarter.doYourJob();

		// cleanup
		FileUtil.deleteDirWithContent(modelPropertiesFile.getParent());

		// verify test result
		final File fileToReplace = new File(applicationOutputDir, FilestructureModelBasedGeneratorStarter.PLUGIN_ID + "/" + artefactName + "/" + filenameToReplace );
		assertFileDoesNotExist(fileToReplace);
		final File fileReplaced = new File(applicationOutputDir, FilestructureModelBasedGeneratorStarter.PLUGIN_ID + "/" + artefactName + "/" + filenameReplaced );
		assertFileDoesNotExist(fileReplaced);

	}

}
