package com.iksgmbh.moglicc.intest.core;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_LOG_FILE;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_SHORT_REPORT_FILE;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.helper.FolderContentBasedFolderDuplicator;
import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.build.MOGLiReleaseBuilder;
import com.iksgmbh.moglicc.build.helper.ReleaseFileCollector;
import com.iksgmbh.moglicc.build.helper.ReleaseFileCollector.FileCollectionData;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.moglicc.provider.model.standard.TextConstants;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil;
import com.iksgmbh.utils.FileUtil;

public class CoreIntTest extends IntTestParent {

	public final String[] args = new String[0];

	@Override
	@Before
	public void setup() {
		super.setup();
	}
	
	/** 
	 * NOTE: This method represents no regular integration test!
	 *       It is used to debug a problem with production data!
	 * @throws IOException 
	 **/
	//@Test  // comment this line out when building a MOGLiCC release
	public void runWithExternalInputData() throws IOException {
		// prepare test
		initTestRootDir();
		
		copyExternalInputDataIntoMOGLiCCWorkspace("C://dev//MOGLiCC//iks-github//application//target//releaseDir//SystemTestDir");
		//copyExternalInputDataIntoMOGLiCCWorkspace("/home/localci/development/sources/iks-github/application/target/releaseDir/SystemTestDir");
		
		// call functionality under test
		MOGLiCodeCreator.main(args);

		// no verifying
		//final File reportFile = new File(applicationReportDir, SHORT_REPORT_FILE);
		//final File reportFile = new File(applicationReportDir, GENERATION_REPORT_FILE);
		//final File reportFile = applicationLogfile;
		final File reportFile = new File(applicationRootDir, ERROR_REPORT_FILE);
		System.err.println(FileUtil.getFileContent(reportFile));
		fail("Do not forget to comment out the test annotation of this method before checking in.");
	}

	private void copyExternalInputDataIntoMOGLiCCWorkspace(final String parentDirOfInputFolder) {
		FileUtil.deleteDirWithContent(applicationInputDir);
		applicationInputDir.mkdir();
		final File inputFolder = new File(parentDirOfInputFolder, "input");
		if (! inputFolder.exists()) {
			throw new RuntimeException("Input folder does not exist: " + inputFolder.getAbsolutePath());
		}
		final FolderContentBasedFolderDuplicator folderDuplicator = new FolderContentBasedFolderDuplicator(inputFolder, null);
		folderDuplicator.duplicateTo(applicationInputDir, true);
		
	}

	private void initTestRootDir() 
	{
		FileUtil.deleteDirWithContent(applicationRootDir);

		initBuildPropertiesFile();

		final File root = new File(applicationRootDir);
		root.mkdirs();

		ReleaseFileCollector.doYourJob(createFileCollectionData());
	}

	private void initBuildPropertiesFile() {
		final File sourceFile = new File(getProjectRootDir(), "../application/"
				                         + RESOURCES_DIR + MOGLiReleaseBuilder.FILENAME_BUILD_PROPERTIES);
		final List<String> fileContentAsList = MOGLiFileUtil.getFileContentAsList(sourceFile);
		String newContent = "";
		for (final String line : fileContentAsList) {
			if (line.startsWith(MOGLiReleaseBuilder.PROPERTY_RELEASE_VERSION)) {
				System.out.println(MOGLiCodeCreator.VERSION);
				newContent += MOGLiReleaseBuilder.PROPERTY_RELEASE_VERSION + "="
                              + MOGLiCodeCreator.VERSION + FileUtil.getSystemLineSeparator();
			} else {
				newContent += line + FileUtil.getSystemLineSeparator();
			}
		}
		final File targetFile = new File(getProjectRootDir(), "target/classes/" + MOGLiReleaseBuilder.FILENAME_BUILD_PROPERTIES);
		MOGLiFileUtil.appendToFile(targetFile, newContent);
	}

	private FileCollectionData createFileCollectionData() {
		final MOGLiReleaseBuilder mogliReleaseBuilder = new MOGLiReleaseBuilder();
		final FileCollectionData fileCollectionData = new FileCollectionData();
		fileCollectionData.libSubdir = MOGLiSystemConstants.DIR_LIB;
		fileCollectionData.pluginsSubdir = MOGLiSystemConstants.DIR_PLUGIN;
		fileCollectionData.sourceDir = null;
		fileCollectionData.releaseDir = new File(applicationRootDir);
		fileCollectionData.fileListForRootDir = null;
		fileCollectionData.jarsOfCoreComponents = mogliReleaseBuilder.getJarFiles(mogliReleaseBuilder.getListOfCoreModules());
		fileCollectionData.jarsOfPlugins = mogliReleaseBuilder.getJarFiles(mogliReleaseBuilder.getListOfPluginModules());
		fileCollectionData.thirdPartyJars = mogliReleaseBuilder.getThirdPartyJars();
		return fileCollectionData;
	}

	@Test
	public void testFinalLogout() {
		// prepare test
		initTestRootDir();

		// call functionality under test
		MOGLiCodeCreator.main(args);

		// verify test result
		final File logfile = MOGLiFileUtil.getNewFileInstance(LOGFILE);
		assertFileContainsEntry(logfile, "Execution of all 6 plugins successful.");
	}

	@Test
	public void createsGeneratorReportFile() {
		// prepare test
		initTestRootDir();

		// call functionality under test
		MOGLiCodeCreator.main(args);

		// verify test result
		final File reportFile = new File(applicationReportDir, GENERATION_REPORT_FILE);
		assertFileExists(reportFile);
		final File expectedFile = new File(getProjectTestResourcesDir(), "expectedReports/ExpectedGeneratorReport.txt");
		assertFileEquals(expectedFile , reportFile);
	}

	@Test
	public void createsReportFilesForInvalidTemplates() throws Exception {
		// prepare test
		initTestRootDir();
		MOGLiCodeCreator.main(args);
		final File templateFile = new File(applicationInputDir, "VelocityClassBasedFileMaker/MOGLiJavaBean/A_MainTemplate.tpl");
		final String newContent = "@nonsense " + FileUtil.getSystemLineSeparator() + FileUtil.getFileContent(templateFile); 
		FileUtil.createNewFileWithContent(templateFile, newContent);

		// call functionality under test
		MOGLiCodeCreator.main(args);

		// verify test result
		
		File reportFile = new File(applicationReportDir, SHORT_REPORT_FILE);
		assertFileExists(reportFile);
		File expectedFile = new File(getProjectTestResourcesDir(), "expectedReports/ExpectedShortReportForInvalidTemplate.txt");
		assertFileEquals(expectedFile , reportFile);	

		reportFile = new File(applicationReportDir, GENERATION_REPORT_FILE);
		assertFileExists(reportFile);
		assertFileContainsEntry(reportFile, "Error parsing artefact properties for artefact 'MOGLiJavaBean': " +
				                        "Header attribute 'nonsense' in line 1 of main template of artefact 'MOGLiJavaBean' needs additional information.");

		reportFile = new File(applicationReportDir, PROVIDER_REPORT_FILE);
		assertFileExists(reportFile);
		assertFileContainsEntry(reportFile, "All 3 provider plugins executed successfully.");
		
		reportFile = new File(applicationRootDir, ERROR_REPORT_FILE);
		assertFileExists(reportFile);
		assertFileContainsEntry(reportFile, "Problem for plugin 'VelocityClassBasedFileMaker': Header attribute 'nonsense' in line 1 " +
				                            "of main template of artefact 'MOGLiJavaBean' needs additional information.");
	}
	
	@Test
	public void createsReportFilesForInvalidModel() throws Exception {
		// prepare test
		initTestRootDir();
		MOGLiCodeCreator.main(args);
		FileUtil.appendToFile(modelFile, FileUtil.getSystemLineSeparator() + "nonsense");

		// call functionality under test
		MOGLiCodeCreator.main(args);

		// verify test result
		File reportFile = new File(applicationReportDir, GENERATION_REPORT_FILE);
		assertFileExists(reportFile);
		File expectedFile = new File(getProjectTestResourcesDir(), "expectedReports/ExpectedGeneratorReportForInvalidModel.txt");
		assertFileEquals(expectedFile , reportFile);

		reportFile = new File(applicationReportDir, PROVIDER_REPORT_FILE);
		assertFileExists(reportFile);
		expectedFile = new File(getProjectTestResourcesDir(), "expectedReports/ExpectedProviderReportForInvalidModel.txt");
		assertFileEquals(expectedFile , reportFile);
		
		reportFile = new File(applicationReportDir, SHORT_REPORT_FILE);
		assertFileExists(reportFile);
		expectedFile = new File(getProjectTestResourcesDir(), "expectedReports/ExpectedShortReportForInvalidModel.txt");
		assertFileEquals(expectedFile , reportFile);	
		
		reportFile = new File(applicationRootDir, ERROR_REPORT_FILE);
		assertFileExists(reportFile);
		assertFileContainsEntry(reportFile, "Problem for plugin 'StandardModelProvider': " +
				                            "Error parsing model file 'MOGLiCC_JavaBeanModel.txt'");
	}
	
	@Test
	public void createsProviderReportFile() {
		// prepare test
		initTestRootDir();

		// call functionality under test
		MOGLiCodeCreator.main(args);

		// verify test result
		final File reportFile = new File(applicationReportDir, PROVIDER_REPORT_FILE);
		assertFileExists(reportFile);
		final File expectedFile = new File(getProjectTestResourcesDir(), "expectedReports/ExpectedProviderReport.txt");
		assertFileEquals(expectedFile , reportFile);
	}

	@Test
	public void createsShortReportFile() {
		// prepare test
		initTestRootDir();

		// call functionality under test
		MOGLiCodeCreator.main(args);

		// verify test result
		final File resultFile = new File(applicationReportDir, MOGLiSystemConstants.FILENAME_SHORT_REPORT_FILE);
		assertFileExists(resultFile);
		final File expectedFile = new File(getProjectTestResourcesDir(), "expectedReports/ExpectedShortReport.txt");
		assertFileEquals(expectedFile , resultFile);
	}

	@Test
	public void createsErrorReportFileForStatusError() throws IOException {
		// prepare test
		initTestRootDir();
		MOGLiCodeCreator.main(args);
		FileUtil.appendToFile(modelFile, FileUtil.getSystemLineSeparator() + "nonsense");

		// call functionality under test
		MOGLiCodeCreator.main(args);

		// verify test result
		final File resultFile = new File(applicationRootDir, MOGLiSystemConstants.FILENAME_ERROR_REPORT_FILE);
		assertFileExists(resultFile);
		final File expectedFile = new File(getProjectTestResourcesDir(), "expectedReports/ExpectedErrorReport.txt");
		assertFileEquals(expectedFile , resultFile);	}	
	
	@Test
	public void createsNoErrorReportFileForStatusOK() {
		// prepare test
		initTestRootDir();

		// call functionality under test
		MOGLiCodeCreator.main(args);

		// verify test result
		final File resultFile = new File(applicationRootDir, MOGLiSystemConstants.FILENAME_ERROR_REPORT_FILE);
		assertFileDoesNotExist(resultFile);
	}	
	
	@Test
	public void namesModelNameInMOGLiResultFileIfModelContainsSyntacticalErrors() throws IOException {
		// prepare test
		initTestRootDir();
		MOGLiCodeCreator.main(args);
		FileUtil.appendToFile(modelFile, "nonsense");

		// call functionality under test
		MOGLiCodeCreator.main(args);

		// verify test result
		final File reportFile = new File(applicationReportDir, FILENAME_SHORT_REPORT_FILE);
		assertFileContainsEntry(reportFile, TextConstants.TEXT_PARSE_ERROR_FOUND );
		assertFileContainsEntry(reportFile, modelFile.getName());
	}	
	
	@Test
	public void createsEmergencyLogFileIfDefinedWorkspaceDirCannotBeCreated() {
		// prepare test
		MOGLiLogUtil.setCoreLogfile(null);
		initApplicationPropertiesWith("workspace=");
		final File emergencyLogFile = new File(applicationRootDir, FILENAME_LOG_FILE);
		assertFileDoesNotExist(emergencyLogFile);

		// call functionality under test
		MOGLiCodeCreator.main(args);

		// cleanup critical stuff before possible test failures
		applicationPropertiesFile.delete();
		assertFileDoesNotExist(applicationPropertiesFile);

		// verify test result
		assertFileExists(emergencyLogFile);
		assertFileContainsEntry(emergencyLogFile, "ERROR: Error creating workspaceDir");
	}


	@Test
	public void createsExplainingReportEntryForMissingPackageOfJavaBeanClass() throws IOException {
		// prepare test
		initTestRootDir();
		MOGLiCodeCreator.main(args);  // unpack default stuff

		// test1
		MOGLiFileUtil.createNewFileWithContent(modelFile, "model MOGLiCC_JavaBeanModel" + System.getProperty("line.separator") + "class NoPackageClassName");
		MOGLiCodeCreator.main(args);
		giveSystemTimeToExecute(2000);
		File errorReportFile = new File(applicationRootDir, MOGLiSystemConstants.FILENAME_ERROR_REPORT_FILE);
		assertFileExists(errorReportFile);
		final String errorReportFileContent1 = FileUtil.getFileContent(errorReportFile);
		
		// test1
		MOGLiFileUtil.createNewFileWithContent(modelFile, "model MOGLiCC_JavaBeanModel" + System.getProperty("line.separator") + "class a.smallLetterInTheBeginninClassName");
		MOGLiCodeCreator.main(args);
		giveSystemTimeToExecute(2000);
		assertFileExists(errorReportFile);
		errorReportFile = new File(applicationRootDir, MOGLiSystemConstants.FILENAME_ERROR_REPORT_FILE);
		final String errorReportFileContent2 = FileUtil.getFileContent(errorReportFile);
		
		// test3
		MOGLiFileUtil.createNewFileWithContent(modelFile, "model MOGLiCC_JavaBeanModel" + System.getProperty("line.separator") + "class a.CorrectClassName");
		MOGLiCodeCreator.main(args);
		giveSystemTimeToExecute(2000);
		errorReportFile = new File(applicationRootDir, MOGLiSystemConstants.FILENAME_ERROR_REPORT_FILE);
		assertFileExists(errorReportFile);
		errorReportFile = new File(applicationRootDir, MOGLiSystemConstants.FILENAME_ERROR_REPORT_FILE);
		final String errorReportFileContent3 = FileUtil.getFileContent(errorReportFile);
		
		// verify test result
		final File expected = new File(getProjectTestResourcesDir(), "ExpectedReportErrorFileForMissingPackage.txt");
		final String expectedErrorMessage = FileUtil.getFileContent(expected);
		assertStringContains(errorReportFileContent1, expectedErrorMessage); 
		assertStringContains(errorReportFileContent2, expectedErrorMessage); 
		assertStringDoesNotContain(errorReportFileContent3, expectedErrorMessage); 
	}	

}
