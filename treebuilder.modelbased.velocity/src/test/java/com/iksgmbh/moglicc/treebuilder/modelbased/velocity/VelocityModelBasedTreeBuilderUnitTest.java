package com.iksgmbh.moglicc.treebuilder.modelbased.velocity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData.KnownGeneratorPropertyNames;
import com.iksgmbh.moglicc.treebuilder.modelbased.velocity.data.ArtefactProperties;
import com.iksgmbh.moglicc.treebuilder.modelbased.velocity.data.VelocityTreeBuilderResultData;
import com.iksgmbh.moglicc.treebuilder.modelbased.velocity.data.VelocityTreeBuilderResultData.KnownTreeBuilderPropertyNames;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class VelocityModelBasedTreeBuilderUnitTest extends VelocityModelBasedTreeBuilderTestParent {

	private static final String RENAMING_TEST_FILE = "RenamingTestFile.txt";
	private static final String RENAMING_TEST_ARTEFACT = "RenamingTestArtefact";
	private static final String RENAMING_TEST_TARGETDIR = "renametest";
	private static final String RESET_ALL_PROPERTIES = "ALL";

	private File generatorPropertiesFile;
	private VelocityTreeBuilderResultData velocityResultData;

	@Override
	@Before
	public void setup() {
		super.setup();
		buildStandardVelocityResultData();

		velocityEngineProviderDummy.setResultData(velocityResultData);
		generatorPropertiesFile = new File(infrastructure.getPluginInputDir(), VelocityModelBasedTreeBuilderStarter.PLUGIN_PROPERTIES_FILE);
		MOGLiFileUtil.createNewFileWithContent(generatorPropertiesFile, "");
	}

	private void buildStandardVelocityResultData() {
		velocityResultData = new VelocityTreeBuilderResultData(new BuildUpGeneratorResultData());

		velocityResultData.addProperty(KnownGeneratorPropertyNames.TargetDir.name(), "<applicationRootDir>");

		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.RootName.name(), METAINFO_MODEL_TARGETDIR);

		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.ReplaceIn.name(), "pom.xml mavenGroupId com.iksgmbh.moglicc");
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.ReplaceIn.name(), "pom.xml mavenArtefactId ModelTargetTestDir");
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.ReplaceIn.name(), "pom.xml mavenProjectName MOGLiJavaBean Artifacts");
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.ReplaceIn.name(), "pom.xml mavenProjectDescription An example project to demonstrate both, the file structure generator and the artifacts of the MOGLiJavaBeanGroup");
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.ReplaceIn.name(), "main.java class Main class $model.getMetaInfoValueFor(projectName)ClassOverviewPrinter");
	}

	@Test
	public void findsDefaultArtefactList() throws MOGLiPluginException {
		// call functionality under test
		final List<String> artefactList = treeBuilderGenerator.getArtefactList();

		// verify test result
		final StringBuffer sb = new StringBuffer(System.getProperty("line.separator"));
		for (String artefactName : artefactList) {
			sb.append(artefactName);
			sb.append(System.getProperty("line.separator"));
			System.out.println(artefactName);
		}
		assertEquals("Unexpected artefact list: " + sb.toString(), 2, artefactList.size());
	}

	@Test
	public void copiesFilesToOutputDir() throws MOGLiPluginException {
		// prepare test
		final File targetDir = new File(infrastructure.getPluginOutputDir(), "MOGLiCC_JavaBeanProject");
		FileUtil.deleteDirWithContent(targetDir);
		assertFileDoesNotExist(targetDir);

		// call functionality under test
		treeBuilderGenerator.doYourJob();

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

		final File artefactPropertiesFile = new File(targetDir, VelocityModelBasedTreeBuilderStarter.FILENAME_ARTEFACT_PROPERTIES);
		assertFileDoesNotExist(artefactPropertiesFile);
	}

	@Test
	public void doesReplacementsInPluginOutputDir() throws MOGLiPluginException {
		// prepare test
		final File targetDir = new File(infrastructure.getPluginOutputDir(), "MOGLiCC_JavaBeanProject");
		FileUtil.deleteDirWithContent(targetDir);
		assertFileDoesNotExist(targetDir);

		// call functionality under test
		treeBuilderGenerator.doYourJob();

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
		treeBuilderGenerator.doYourJob();

		// verify test result
		assertFolderStructure(targetDir);
		assertDirContent(targetDir);
	}

	@Test
	public void doesNotOverwriteExistingFilesInTargetDirAndCreatesCorrespondingGenerationReport() throws Exception {
		// prepare test
		FileUtil.deleteDirWithContent(applicationOutputDir);
		final File targetDir = new File(applicationRootDir, "ModelTargetTestDir");
		FileUtil.deleteDirWithContent(targetDir);
		targetDir.mkdirs();
		final File pomFile = new File(targetDir, "pom.xml");
		pomFile.createNewFile();

		final File propertiesFile = new File(infrastructure.getPluginInputDir(), "MOGLiCC_JavaBeanProject/" + VelocityModelBasedTreeBuilderStarter.FILENAME_ARTEFACT_PROPERTIES);
		FileUtil.createNewFileWithContent(propertiesFile, "@NameOfValidModel MOGLiCC_JavaBeanModel" + FileUtil.getSystemLineSeparator() +
				                                          "@RootName $model.getMetaInfoValueFor(projectName)" + FileUtil.getSystemLineSeparator() +
				                                          "@TargetDir $model.getMetaInfoValueFor(eclipseProjectDir)" + FileUtil.getSystemLineSeparator() +
				                                          "@CreateNew false" + FileUtil.getSystemLineSeparator() +
				                                          "@NameOfValidModel MOGLiCC_JavaBeanModel" + FileUtil.getSystemLineSeparator() +
				                                          "@exclude .git");


		// call functionality under test
		treeBuilderGenerator.doYourJob();

		// cleanup
		FileUtil.deleteDirWithContent(treeBuilderGenerator.getInfrastructure().getPluginInputDir());

		// verify test result
		final String generationReport = unifyFilePath(treeBuilderGenerator.getGeneratorReport());
		assertStringContains(generationReport, "pom.xml  (file existed already in the targetDir and was preserved - " +
				                               "the generated file is available in the plugin's output dir)");
		final String expected = "src/main/java/domainPathToReplace/utils/ClassOverviewPrinter.java";
		assertStringContains(generationReport, expected + FileUtil.getSystemLineSeparator());  
	}

	@Test
	public void usesArtefactNameForMissingRootName() throws Exception {
		// prepare test
		FileUtil.deleteDirWithContent(infrastructure.getPluginOutputDir());
		final String artefactName = "TestArtefact";
		final File inputDir = new File(infrastructure.getPluginInputDir(), artefactName);
		inputDir.mkdirs();
		final File propertiesFile = new File(inputDir, VelocityModelBasedTreeBuilderStarter.FILENAME_ARTEFACT_PROPERTIES);
		propertiesFile.createNewFile();
		resetVelocityResultData(KnownTreeBuilderPropertyNames.RootName.name());

		// call functionality under test
		treeBuilderGenerator.doYourJob();

		// cleanup
		FileUtil.deleteDirWithContent(infrastructure.getPluginInputDir());

		// verify test result
		assertStringEquals("rootname", artefactName, treeBuilderGenerator.getArtefactProperties().getRootName());
	}

	private void resetVelocityResultData(final String propertyToReset)
	{
		if (RESET_ALL_PROPERTIES.equals(propertyToReset)) {
			velocityResultData.getPropertyMap().clear();
		} else {
			final List<String> values = velocityResultData.getPropertyMap().get(propertyToReset.toLowerCase());
			values.clear();
		}
	}

	@Test
	public void usesApplicationRootAsTargetDirIfTargetDirIsNotDefined() throws Exception {
		// prepare test
		prepareSingleArtefactDirWithSimplePropertiesFile();

		// call functionality under test
		treeBuilderGenerator.doYourJob();

		// verify test result
		final ArtefactProperties templateProperties = treeBuilderGenerator.getArtefactProperties();
		assertStringEquals("target dir", MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER, templateProperties.getTargetDir());
	}

	@Test
	public void usesFalseAsDefaultForCreateNew() throws Exception {
		// prepare test
		prepareSingleArtefactDirWithSimplePropertiesFile();

		// call functionality under test
		treeBuilderGenerator.doYourJob();

		// verify test result
		final ArtefactProperties templateProperties = treeBuilderGenerator.getArtefactProperties();
		assertEquals("create new", false, templateProperties.isCreateNew());
	}

	private void prepareSingleArtefactDirWithSimplePropertiesFile() throws Exception {
		FileUtil.deleteDirWithContent(infrastructure.getPluginInputDir());
		final File artefactDir = new File(infrastructure.getPluginInputDir(), "MOGLiCC_JavaBeanProject");
		artefactDir.mkdirs();
		final File propertiesFile = new File(artefactDir, VelocityModelBasedTreeBuilderStarter.FILENAME_ARTEFACT_PROPERTIES);
		FileUtil.createNewFileWithContent(propertiesFile, "@RootName test");
	}

	@Test
	public void doesFileRenamingInPluginOutputDir() throws Exception {
		// prepare test
		prepareTestArtefact();
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.RenameFile.name(), RENAMING_TEST_FILE + " RenamingTestFile2.txt");

		// call functionality under test
		treeBuilderGenerator.doYourJob();

		// verify test result
		final File artefactOutputDir = new File(infrastructure.getPluginOutputDir(), RENAMING_TEST_ARTEFACT + "/subfolder");
		final File fileBeforeRenaming = new File(artefactOutputDir, RENAMING_TEST_FILE);
		assertFileDoesNotExist(fileBeforeRenaming);
		final File fileAfterRenaming = new File(artefactOutputDir, "RenamingTestFile2.txt");
		assertFileExists(fileAfterRenaming);
	}

	@Test
	public void doesLineReplacementInFileBeforeRenamingUsingOriginalFilename() throws Exception {
		// prepare test
		prepareTestArtefact();
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.RenameFile.name(), RENAMING_TEST_FILE + " RenamingTestFile2.txt");
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.ReplaceIn.name(), RENAMING_TEST_FILE + " <replaceMe> REPLACED");

		// call functionality under test
		treeBuilderGenerator.doYourJob();

		// verify test result
		final File artefactOutputDir = new File(infrastructure.getPluginOutputDir(), RENAMING_TEST_ARTEFACT + "/subfolder");
		final File resultFile = new File(artefactOutputDir, "RenamingTestFile2.txt");
		final String fileContent = FileUtil.getFileContent(resultFile);
		assertEquals("File content", "REPLACED", fileContent);
	}

	@Test
	public void replacesLineInFileAfterRenamingUsingModifiedFilename() throws Exception {
		// prepare test
		prepareTestArtefact();
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.RenameFile.name(), RENAMING_TEST_FILE + " RenamingTestFile2.txt");
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.ReplaceIn.name(), RENAMING_TEST_FILE + " <replaceMe> REPLACED");

		// call functionality under test
		treeBuilderGenerator.doYourJob();

		// verify test result
		final File artefactOutputDir = new File(infrastructure.getPluginOutputDir(), RENAMING_TEST_ARTEFACT + "/subfolder");
		final File resultFile = new File(artefactOutputDir, "RenamingTestFile2.txt");
		final String fileContent = FileUtil.getFileContent(resultFile);
		assertEquals("File content", "REPLACED", fileContent);
	}

	@Test
	public void throwsErrorForFileRenamingUsingIdenticalFilenames() throws Exception {
		// prepare test
		prepareTestArtefact();
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.RenameFile.name(), "RenamingTestFile.txt RenamingTestFile.txt");

		try {
			// call functionality under test
			treeBuilderGenerator.doYourJob();
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			assertEquals("Error message", "Following problem(s) exist for artefact '" +  RENAMING_TEST_ARTEFACT + "': "
					                      + FileUtil.getSystemLineSeparator()
					                      + "File 'RenamingTestFile.txt' is not renamed to a new name!", e.getMessage());
		}
	}

	private File prepareTestArtefact() throws Exception {
		final File renamingTargetDir = new File(applicationRootDir, RENAMING_TEST_TARGETDIR);
		FileUtil.deleteDirWithContent(renamingTargetDir);
		FileUtil.deleteDirWithContent(infrastructure.getPluginInputDir());
		FileUtil.deleteDirWithContent(infrastructure.getPluginInputDir());
		FileUtil.deleteDirWithContent(infrastructure.getPluginOutputDir());
		final File artefactDir = new File(infrastructure.getPluginInputDir(), RENAMING_TEST_ARTEFACT);
		final File artefactSubfolder = new File(artefactDir, "subfolder");
		artefactSubfolder.mkdirs();
		final File artefactFile = new File(artefactSubfolder, RENAMING_TEST_FILE);
		FileUtil.createNewFileWithContent(artefactFile, "<replaceMe>");
		final File artefactPropertiesFile = new File(artefactDir, "artefact.properties");
		return artefactPropertiesFile;
	}

	@Test
	public void renamesDirInPluginOutputDir() throws Exception {
		// prepare test
		final File artefactPropertiesFile = prepareTestArtefact();
		final String newPath = "folder1/folder2/subfolder";

		executeDirRenamingReplaceTest(artefactPropertiesFile, newPath, RENAMING_TEST_ARTEFACT, infrastructure.getPluginOutputDir());
	}

	@Test
	public void renamesDirToNameWithLeadingDotMarkedAsExcluded() throws Exception {
		// prepare test
		final File artefactPropertiesFile = prepareTestArtefact();
		final String newPath = ".hg";  // this is a excluded dir name

		executeDirRenamingReplaceTest(artefactPropertiesFile, newPath, RENAMING_TEST_TARGETDIR, applicationRootDir);
	}

	@Test
	public void replacesTwoMetoInfoReferencesInOneLine() throws Exception {
		// prepare test
		final File artefactPropertiesFile = prepareTestArtefact();
		final String newPath = "newSubfolder";

		executeDirRenamingReplaceTest(artefactPropertiesFile, newPath, RENAMING_TEST_TARGETDIR, applicationRootDir);
	}

	private void executeDirRenamingReplaceTest(final File artefactPropertiesFile,
			                            final String newPath, final String mainTargetDirName,
			                            final File targetDir) throws MOGLiPluginException
	{
		resetVelocityResultData(KnownTreeBuilderPropertyNames.RootName.name());
		resetVelocityResultData(KnownTreeBuilderPropertyNames.ReplaceIn.name());
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.RootName.name(), mainTargetDirName);
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.Exclude.name(), ".hg");
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.ReplaceIn.name(),  RENAMING_TEST_FILE + " <replaceMe> " + METAINFO_MODEL_PROJECT_DESCRIPTION);
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.RenameDir.name(), " subfolder " + newPath);

		final File mainTargetDir = new File(targetDir, mainTargetDirName);
		FileUtil.deleteDirWithContent(mainTargetDir);
		final File newFolder = new File(targetDir, mainTargetDirName + "/" + newPath );
		assertFileDoesNotExist(newFolder);

		// call functionality under test
		treeBuilderGenerator.doYourJob();

		// verify test result
		final File oldFolder = new File(infrastructure.getPluginOutputDir(), mainTargetDirName + "/subfolder");
		assertFileDoesNotExist(oldFolder);
		assertFileExists(newFolder);
		final File targetFile = new File(newFolder, RENAMING_TEST_FILE);
		assertFileExists(newFolder);
		assertFileExists(targetFile);
		assertFileContainsEntry(targetFile, METAINFO_MODEL_PROJECT_DESCRIPTION);
	}

	@Test
	public void skipsGenerationWhenConfiguredSoInArtefactProperties() throws Exception
	{
		// prepare test
		final File applicationRoot = treeBuilderGenerator.getInfrastructure().getApplicationRootDir();
		final File resultDir = new File(applicationRoot, "ModelTargetTestDir");
		FileUtil.deleteDirWithContent(resultDir);
		assertFileDoesNotExist(resultDir);

		resetVelocityResultData(RESET_ALL_PROPERTIES);
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.RootName.name(), "ModelTargetTestDir");
		velocityResultData.addProperty(KnownGeneratorPropertyNames.NameOfValidModel.name(), "MOGLiCC_JavaBeanModel");

		// Test 1: without skip instruction
		//call functionality under test
		treeBuilderGenerator.doYourJob();

		// verify test result
		assertFileExists(resultDir);

		// Test 2: with skip instruction
		FileUtil.deleteDirWithContent(resultDir);
		assertFileDoesNotExist(resultDir);
		velocityResultData.addProperty(KnownGeneratorPropertyNames.SkipGeneration.name(), "true");

		// call functionality under test
		treeBuilderGenerator.doYourJob();

		// verify test result
		assertFileDoesNotExist(resultDir);
	}

	@Test
	public void deleteTargetDirWhenConfiguredSoInArtefactProperties() throws Exception
	{
		// prepare test
		final File applicationRoot = treeBuilderGenerator.getInfrastructure().getApplicationRootDir();
		final File resultDir = new File(applicationRoot, "ModelTargetTestDir");
		resultDir.mkdirs();
		final File fileToBeCleaned = new File(resultDir, "toBeCleaned.txt");
		fileToBeCleaned.createNewFile();
		assertFileExists(fileToBeCleaned);

		resetVelocityResultData(RESET_ALL_PROPERTIES);
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.RootName.name(), "ModelTargetTestDir");
		velocityResultData.addProperty(KnownGeneratorPropertyNames.NameOfValidModel.name(), "MOGLiCC_JavaBeanModel");
		velocityResultData.addProperty(KnownTreeBuilderPropertyNames.CleanTarget.name(), "true");

		//call functionality under test
		treeBuilderGenerator.doYourJob();

		// verify test result
		assertFileDoesNotExist(fileToBeCleaned);
	}

}
