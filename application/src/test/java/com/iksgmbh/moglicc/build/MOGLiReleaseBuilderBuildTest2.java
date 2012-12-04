package com.iksgmbh.moglicc.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.build.MOGLiReleaseBuilder2.VERSION_TYPE;
import com.iksgmbh.moglicc.build.test.ApplicationTestParent;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.ZipUtil;

public class MOGLiReleaseBuilderBuildTest2 extends ApplicationTestParent {
	
	private static final String FILENAME_TEST_ZIP = "MogliTest.zip";
	
	private MOGLiReleaseBuilder2 releaseBuilder;
	
	@Before
	public void setup() {
		super.setup();
		releaseBuilder = new MOGLiReleaseBuilder2();
		final File propertiesFile = new File(MOGLiReleaseBuilder2.USER_DIR 
				+ "/src/main/resources/" + MOGLiReleaseBuilder2.FILENAME_BUILD_PROPERTIES);
		FileUtil.copyTextFile(propertiesFile, MOGLiReleaseBuilder2.getApplicationRootDir());
		final String zipFile = MOGLiReleaseBuilder2.USER_DIR 
				+ "/src/test/resources/" + FILENAME_TEST_ZIP;
		FileUtil.copyBinaryFile(zipFile, MOGLiReleaseBuilder2.getApplicationRootDir());
	}

	// *****************************  test methods  ************************************
	
	@Test
	public void returnsPropertiesFile() {
		final Properties properties = releaseBuilder.getProperties();
		assertNotNull("Properties File not found", properties);
	}
	
	@Test
	public void returnsVersionString() {
		String version = releaseBuilder.getVersion(VERSION_TYPE.Current);
		assertNotNull("Unknown version.", version);
		
		version = releaseBuilder.getVersion(VERSION_TYPE.Release);
		assertNotNull("Unknown version.", version);
		
		version = releaseBuilder.getVersion(VERSION_TYPE.Next);
		assertNotNull("Unknown version.", version);
	}
	
	@Test
	public void returnsMavenRootDir() {
		final String mavenRootDir = releaseBuilder.getMavenRootDir();
		assertNotNull("Unknown Maven install dir.", mavenRootDir);
	}
	
	@Test
	public void returnsMavenRepositoryDir() {
		final String mavenRepositoryDir = releaseBuilder.getMavenRepositoryDir();
		assertNotNull("Unknown Maven install dir.", mavenRepositoryDir);
	}		
	@Test
	public void returnsModuleParentDir() {
		// call functionality under test
		final List<String> mavenModuleList = releaseBuilder.getListOfAllMavenModules();
		
		// verify test result
		for (String module : mavenModuleList) {
			final File modulDir = releaseBuilder.getModuleParentDir(module);
			assertTrue("Module Dir does not exists:\n" + modulDir, modulDir.exists() );
		}
	}
	
	@Test
	public void returnsListOfAllMavenModules() {
		final List<String> mavenModuleList = releaseBuilder.getListOfAllMavenModules();
		assertNotNull(mavenModuleList);
		assertEquals("module number", 13, mavenModuleList.size());
	}
	
	@Test
	public void returnsParentBuildDir() {
		final File parentBuildDir = releaseBuilder.getParentBuildDir();
		assertNotNull(parentBuildDir);
		assertTrue("ParentBuildDir does not exist:\n" + parentBuildDir, parentBuildDir.exists());
		assertTrue("ParentBuildDir is empty:\n", 0 != parentBuildDir.listFiles().length);
	}
	
	@Test
	public void returnsCoreJarFiles() {
		File[] jarFiles = releaseBuilder.getJarFiles(releaseBuilder.getListOfCoreModules());
		assertNotNull(jarFiles);
		assertEquals("Wrong number of jarFiles!", 4, jarFiles.length);
	}
	
	@Test
	public void returnsPomFiles() {
		final String[] pomFiles = releaseBuilder.getPomFiles();
		
		// verify test result
		assertNotNull(pomFiles);
		for (int i = 0; i < pomFiles.length; i++) {
			assertNotNull(pomFiles[i]);
			assertTrue("", pomFiles[i].endsWith("pom.xml"));
			final File pom = new File(pomFiles[i]);
			assertTrue("Pom File does not exist:\n" + pomFiles[i], pom.exists());
		}
	}
	
	@Test
	public void buildsReleaseZipFile() throws IOException {
		// prepare test
		final File releaseDir = releaseBuilder.getReleaseDir();
		FileUtil.deleteDirWithContent(releaseDir);
		assertFalse("ReleaseDir not deleted!", releaseDir.exists());
		releaseDir.mkdirs();
		ZipUtil.unzip(MOGLiReleaseBuilder2.getApplicationRootDir() + "/MogliTest.zip", releaseDir.getAbsolutePath());
		assertEquals("Unexpected file number!", 2, releaseDir.listFiles().length);
		
		// call functionality under test
		releaseBuilder.buildReleaseZipFile();
		
		// verify test result
		File releaseZipFile = releaseBuilder.getReleaseZipFile();
		assertTrue("ReleaseZipFile does not exist:\n" + releaseZipFile.getAbsolutePath(), releaseZipFile.exists());
		assertEquals("Unexpected file size!", 52170, releaseZipFile.length());
		assertEquals("Unexpected file number!", 1, releaseDir.listFiles().length);
	}

}
