package com.iksgmbh.moglicc.intest.core;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_LOG_FILE;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.intest.IntTestParent;

public class CoreIntTest extends IntTestParent {

	public final String[] args = new String[0];

	@Override
	@Before
	public void setup() {
		super.setup();
	}

/*
   currently not needed:

	private void initTestRootDir() {
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
		fileCollectionData.libSubdir = DIR_LIB;
		fileCollectionData.pluginsSubdir = DIR_PLUGIN;
		fileCollectionData.sourceDir = null;
		fileCollectionData.releaseDir = new File(applicationRootDir);
		fileCollectionData.fileListForRootDir = null;
		fileCollectionData.jarsOfCoreComponents = mogliReleaseBuilder.getJarFiles(mogliReleaseBuilder.getListOfCoreModules());
		fileCollectionData.jarsOfPlugins = mogliReleaseBuilder.getJarFiles(mogliReleaseBuilder.getListOfPluginModules());
		fileCollectionData.thirdPartyJars = mogliReleaseBuilder.getThirdPartyJars();
		return fileCollectionData;
	}

*/

	@Test
	public void createsEmergencyLogFileIfDefinedWorkspaceDirCannotBeCreated() {
		// prepare test
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


}
