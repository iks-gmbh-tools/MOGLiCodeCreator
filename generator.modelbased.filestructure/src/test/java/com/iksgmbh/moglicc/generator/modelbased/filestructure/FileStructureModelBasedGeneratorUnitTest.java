package com.iksgmbh.moglicc.generator.modelbased.filestructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.modelbased.filestructure.test.FileStructureModelBasedGeneratorTestParent;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.StringUtil;

public class FileStructureModelBasedGeneratorUnitTest extends FileStructureModelBasedGeneratorTestParent {

	private File generatorPropertiesFile;

	@Override
	@Before
	public void setup() {
		super.setup();
		generatorPropertiesFile = new File(infrastructure.getPluginInputDir(), FilestructureModelBasedGeneratorStarter.PLUGIN_PROPERTIES_FILE);
		MOGLiFileUtil.createNewFileWithContent(generatorPropertiesFile, "");
	}

	@Test
	public void findsDefaultArtefactList() throws MOGLiPluginException {
		// call functionality under test
		final List<String> artefactList = fileStructureGenerator.getArtefactList();

		// verify test result
		assertEquals("artefact number", 2, artefactList.size());
	}

	@Test
	public void copiesFilesToOutputDir() throws MOGLiPluginException {
		// prepare test
		final File targetDir = new File(infrastructure.getPluginOutputDir(), "MOGLiCC_JavaBeanProject"); 
		FileUtil.deleteDirWithContent(targetDir);
		assertFileDoesNotExist(targetDir);
		
		// call functionality under test
		fileStructureGenerator.doYourJob();

		// verify test result
		assertFolderStructure(targetDir);
	}

	private void assertFolderStructure(final File targetDir) {
		assertFileExists(targetDir);

		final File targetPomFile = new File(targetDir, "pom.xml");
		final File srcDir = new File(targetDir, "src");
		
		assertFileExists(targetPomFile);
		assertFileExists(srcDir);
		assertChildrenNumberInDirectory(srcDir, 2);
		
		final File artefactPropertiesFile = new File(targetDir, FilestructureModelBasedGeneratorStarter.FILENAME_ARTEFACT_PROPERTIES);
		assertFileDoesNotExist(artefactPropertiesFile);
	}

	@Test
	public void doesReplacementsInPluginOutputDir() throws MOGLiPluginException {
		// prepare test
		final File targetDir = new File(infrastructure.getPluginOutputDir(), "MOGLiCC_JavaBeanProject"); 
		FileUtil.deleteDirWithContent(targetDir);
		assertFileDoesNotExist(targetDir);
		
		// call functionality under test
		fileStructureGenerator.doYourJob();

		// verify test result
		assertDirContent(targetDir);
	}

	private void assertDirContent(final File targetDir) {
		final File actualPomFile = new File(targetDir, "pom.xml");
		final File expected = new File(getProjectTestResourcesDir(), "expectedPom.xml");
		assertFileEquals(expected, actualPomFile);
	}

	@Test
	public void createsTargetDirDefinedAsProperty() throws MOGLiPluginException {
		// prepare test
		FileUtil.deleteDirWithContent(applicationOutputDir);
		final File targetDir = new File(applicationRootDir, METAINFO_MODEL_TARGETDIR); 
		FileUtil.deleteDirWithContent(targetDir);
		assertFileDoesNotExist(targetDir);
		
		// call functionality under test
		fileStructureGenerator.doYourJob();

		// verify test result
		assertFolderStructure(targetDir);
		assertDirContent(targetDir);
	}
	
	@Test
	public void createsGenerationReport() throws MOGLiPluginException {
		// prepare test
		FileUtil.deleteDirWithContent(applicationOutputDir);
		
		// call functionality under test
		fileStructureGenerator.doYourJob();

		// verify test result
		final String generationReport = cutLocalFilePath(fileStructureGenerator.getGenerationReport());
		assertTrue("unexpected generation report", generationReport.startsWith("FilestructureModelBasedGenerator has done work for following artefacts:"));
	}

	private String cutLocalFilePath(final String generationReport) {
		return StringUtil.replaceBetween(generationReport, "in: ", "..\\", ".");
	}


	@Test
	public void throwsExceptionForMissingRootName() throws Exception {
		// prepare test
		final File propertiesFile = new File(infrastructure.getPluginInputDir(), "MOGLiCC_JavaBeanProject/" + FilestructureModelBasedGeneratorStarter.FILENAME_ARTEFACT_PROPERTIES);
		FileUtil.createNewFileWithContent(propertiesFile, "@TargetDir test");

		try {
			// call functionality under test
			fileStructureGenerator.doYourJob();
			fail("Expected exception not thrown");
		} catch (Exception e) {
			// verify test result
			assertStringEquals("error message", "Mandatory property is not defined: RootName", e.getMessage());
		}
	}

	@Test
	public void usesApplicationRootAsTargetDirIfTargetDirIsNotDefined() throws Exception {
		// prepare test
		prepareSingleArtefactDirWithSimplePropertiesFile();

		// call functionality under test
		fileStructureGenerator.doYourJob();
		
		// verify test result
		final TemplateProperties templateProperties = fileStructureGenerator.getTemplateProperties();
		assertStringEquals("target dir", MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER, templateProperties.getTargetDir());
	}

	@Test
	public void usesFalseAsDefaultForCreateNew() throws Exception {
		// prepare test
		prepareSingleArtefactDirWithSimplePropertiesFile();

		// call functionality under test
		fileStructureGenerator.doYourJob();
		
		// verify test result
		final TemplateProperties templateProperties = fileStructureGenerator.getTemplateProperties();
		assertEquals("create new", false, templateProperties.isCreateNew());
	}

	private void prepareSingleArtefactDirWithSimplePropertiesFile() throws Exception {
		FileUtil.deleteDirWithContent(infrastructure.getPluginInputDir());
		final File artefactDir = new File(infrastructure.getPluginInputDir(), "MOGLiCC_JavaBeanProject");
		artefactDir.mkdirs();
		final File propertiesFile = new File(artefactDir, FilestructureModelBasedGeneratorStarter.FILENAME_ARTEFACT_PROPERTIES);
		FileUtil.createNewFileWithContent(propertiesFile, "@RootName test");
	}

	@Test
	public void doesRenamingInPluginOutputDir() throws Exception {
		// prepare test
		final File artefactPropertiesFile = prepareArtefact();
		MOGLiFileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName renametest " + FileUtil.getSystemLineSeparator() +
                "@TargetDir <applicationRootDir>" + FileUtil.getSystemLineSeparator() +
                "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@RenameFile RenamingTestFile.txt RenamingTestFile2.txt");
		assertFileExists(artefactPropertiesFile);
		
		// call functionality under test
		fileStructureGenerator.doYourJob();

		// verify test result
		final File artefactOutputDir = new File(infrastructure.getPluginOutputDir(), "RenamingTestArtefact/subfolder");
		final File fileBeforeRenaming = new File(artefactOutputDir, "RenamingTestFile.txt"); 
		assertFileDoesNotExist(fileBeforeRenaming);
		final File fileAfterRenaming = new File(artefactOutputDir, "RenamingTestFile2.txt"); 
		assertFileExists(fileAfterRenaming);
	}
	
	@Test
	public void doesLineReplacementInFileBeforeRenamingUsingOriginalFilename() throws Exception {
		// prepare test
		final File artefactPropertiesFile = prepareArtefact();
		MOGLiFileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName renametest " + FileUtil.getSystemLineSeparator() +
                "@TargetDir <applicationRootDir>" + FileUtil.getSystemLineSeparator() +
                "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@RenameFile RenamingTestFile.txt RenamingTestFile2.txt" + FileUtil.getSystemLineSeparator() +
    			"@ReplaceIn RenamingTestFile.txt <replaceMe> REPLACED");
		
		// call functionality under test
		fileStructureGenerator.doYourJob();

		// verify test result
		final File artefactOutputDir = new File(infrastructure.getPluginOutputDir(), "RenamingTestArtefact/subfolder");
		final File resultFile = new File(artefactOutputDir, "RenamingTestFile2.txt"); 
		final String fileContent = FileUtil.getFileContent(resultFile);
		assertEquals("File content", "REPLACED", fileContent);		
	}

	@Test
	public void doesLineReplacementInFileAfterRenamingUsingModifiedFilename() throws Exception {
		// prepare test
		final File artefactPropertiesFile = prepareArtefact();
		MOGLiFileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName renametest " + FileUtil.getSystemLineSeparator() +
                "@TargetDir <applicationRootDir>" + FileUtil.getSystemLineSeparator() +
                "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@RenameFile RenamingTestFile.txt RenamingTestFile2.txt" + FileUtil.getSystemLineSeparator() +
    			"@ReplaceIn RenamingTestFile2.txt <replaceMe> REPLACED");
		
		// call functionality under test
		fileStructureGenerator.doYourJob();

		// verify test result
		final File artefactOutputDir = new File(infrastructure.getPluginOutputDir(), "RenamingTestArtefact/subfolder");
		final File resultFile = new File(artefactOutputDir, "RenamingTestFile2.txt"); 
		final String fileContent = FileUtil.getFileContent(resultFile);
		assertEquals("File content", "REPLACED", fileContent);		
	}

	@Test
	public void throwsErrorForFileRenamingUsingIdenticalFilenames() throws Exception {
		// prepare test
		final File artefactPropertiesFile = prepareArtefact();
		MOGLiFileUtil.createNewFileWithContent(artefactPropertiesFile, "@RootName renametest " + FileUtil.getSystemLineSeparator() +
                "@TargetDir <applicationRootDir>" + FileUtil.getSystemLineSeparator() +
                "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@RenameFile RenamingTestFile.txt RenamingTestFile.txt");
		
		try {			
			// call functionality under test
			fileStructureGenerator.doYourJob();
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			assertEquals("Error message", "Following problem(s) exist for artefact 'RenamingTestArtefact': "
					                      + FileUtil.getSystemLineSeparator()
					                      + "File 'RenamingTestFile.txt' is not renamed to a new name!", e.getMessage());
		}
	}
	
	private File prepareArtefact() throws Exception {
		FileUtil.deleteDirWithContent(infrastructure.getPluginInputDir());
		FileUtil.deleteDirWithContent(infrastructure.getPluginOutputDir());
		final File artefactDir = new File(infrastructure.getPluginInputDir(), "RenamingTestArtefact");
		final File artefactSubfolder = new File(artefactDir, "subfolder");
		artefactSubfolder.mkdirs();
		final File artefactFile = new File(artefactSubfolder, "RenamingTestFile.txt");
		FileUtil.createNewFileWithContent(artefactFile, "<replaceMe>");
		final File artefactPropertiesFile = new File(artefactDir, "artefact.properties");
		return artefactPropertiesFile;
	}

}
