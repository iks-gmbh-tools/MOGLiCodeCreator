package com.iksgmbh.moglicc.test;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_HELP_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_INPUT_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_LIB_PLUGIN;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_LOGS_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_OUTPUT_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_TEMP_FILES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_APPLICATION_PROPERTIES;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_LOG_FILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.utils.FileUtil;

public abstract class AbstractMOGLiTest {
	
	// *****************************  Constants  ************************************
	
	private static final String PROJECT_ROOT_DIR = "../test/";
	//private static final String MAVEN_INSTALL_DIR = "D:/Reik/dev/Maven/apache-maven-2.2.1";
	
	protected static final String TARGET_DIR = "target/";
	protected static final String TEST_RESOURCES_DIR = "src/test/resources/";
	protected static final String RESOURCES_DIR = "src/main/resources/";
	protected static final String TEST_SUBDIR = TARGET_DIR + "TestDir"; // in target it will be deleted automatically with each clean
	
	
	// **************************  Instance fields  *********************************
	
	protected File projectResourcesDir;
	protected File projectTestResourcesDir;
	protected File applicationRootDir;
	protected File applicationPropertiesFile;
	protected Properties applicationProperties;
	protected String applicationTestDirAsString;
	protected File applicationLogDir;
	protected File applicationLogfile;
	protected File applicationInputDir;
	protected File applicationHelpDir;
	protected File applicationOutputDir;
	protected File applicationTempDir;
	
	// **************************  Abstract Methods  *********************************
	
	abstract protected String getProjectRootDir();
	abstract protected String initTestApplicationRootDir();
	
	
	// **************************  Setup Helper Methods  *********************************
	
	protected void setup() {
		applicationTestDirAsString = initTestApplicationRootDir();
		applicationRootDir = new File(applicationTestDirAsString);
		projectTestResourcesDir = new File(getProjectTestResourcesDir());
		projectResourcesDir = new File(getProjectResourcesDir());
		applicationLogDir = new File(applicationRootDir, DIR_LOGS_FILES);
		applicationLogfile = new File(applicationLogDir, FILENAME_LOG_FILE);
		applicationInputDir = new File(applicationRootDir, DIR_INPUT_FILES);
		applicationOutputDir = new File(applicationRootDir, DIR_OUTPUT_FILES);
		applicationTempDir = new File(applicationRootDir, DIR_TEMP_FILES);
		applicationHelpDir = new File(applicationRootDir, DIR_HELP_FILES);
	}
	
	protected void initForFirstUnitTest() {
		initApplicationTestDir();
		initProperties();
		initPluginSubdir();
		createMogliLogFile();
	}
	
	protected void initProperties() {
		initPropertiesWith("");
	}

	protected void initPropertiesWith(final String propertiesFileContent) {
		applicationPropertiesFile = new File(applicationRootDir, FILENAME_APPLICATION_PROPERTIES);
		try {
			if (applicationPropertiesFile.exists()) {
				applicationPropertiesFile.delete();
			}
			applicationPropertiesFile.createNewFile();
			FileUtil.appendToFile(applicationPropertiesFile, propertiesFileContent);
			loadProperties();			
		} catch (Exception e) {
			throw new RuntimeException("Error initializing " + applicationPropertiesFile.getAbsolutePath(), e);
		}
	}
	
	protected void loadProperties() throws IOException {
		applicationProperties = new Properties();
		final FileInputStream fileInputStream = new FileInputStream(applicationPropertiesFile);
		applicationProperties.load(fileInputStream);
		fileInputStream.close();
	}

	protected final String getProjectResourcesDir() {
		return getProjectRootDir() + RESOURCES_DIR;
	}
	
	protected final String getProjectTestResourcesDir() {
		return getProjectRootDir() + TEST_RESOURCES_DIR;
	}

	protected void initApplicationTestDir() {
		FileUtil.deleteDirWithContent(applicationRootDir);
		applicationRootDir.mkdirs();
	}

	protected void initPluginSubdir() {
		final File plugindir = new File(applicationRootDir, DIR_LIB_PLUGIN);
		FileUtil.deleteDirWithContent(plugindir);
		if (! plugindir.exists()) {
			plugindir.mkdirs();
		}
		final File projectTestResourcesLibPluginDir = new File(projectTestResourcesDir, DIR_LIB_PLUGIN);
		final File[] listFiles = projectTestResourcesLibPluginDir.listFiles();
		if (listFiles != null) {
			for (File file : listFiles) {
				FileUtil.copyBinaryFile(file, new File(plugindir, file.getName()));
			} 
		}
	}
	

	protected void createMogliLogFile() {
		if (! applicationLogDir.exists()) {
			applicationLogDir.mkdirs();
		}
		applicationLogfile.delete();
		try {
			applicationLogfile.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException("Error creating file " + applicationLogfile.getAbsolutePath());
		}
		
	}

	// **************************  Test Helper Methods  *********************************
	
	protected InfrastructureInitData createInfrastructureInitData(final Properties properties,
			                                                      final List<MOGLiPlugin> pluginList, 
                                                                  final String pluginId) {
		final InfrastructureInitData infrastructureInitData = new InfrastructureInitData(
					applicationRootDir,	applicationLogDir, applicationOutputDir, applicationTempDir, 
					applicationInputDir, applicationHelpDir, properties);
		infrastructureInitData.pluginList = pluginList;
		infrastructureInitData.idOfThePluginToThisInfrastructure = pluginId;
		return infrastructureInitData;
	}
	
	public void assertStringEquals(String message, String expected, String actual) {
		if (expected == null) expected = "null";
		if (actual == null) actual = "null";
		assertEquals(message, expected, actual);
	}
	
	public void assertStringContains(final String s, final String substring) {
		final boolean expectedSubstringFound = s.contains(substring);
		assertTrue("Expected substring not found in String." 
				+ "\nSubstring <" + substring + ">" 
				+ "\nString <" + s + ">", expectedSubstringFound);
	}
	
	public void assertFileContainsEntry(final File file, final String expectedEntry) {
		final String actualFileContent = TestUtil.getFileContent(file);
		final boolean expectedEntryFound = actualFileContent.contains(expectedEntry);
		assertTrue("Expected Entry not found in file: " + expectedEntry 
				+ "\nFile: " + file.getAbsolutePath(), expectedEntryFound);
	}
	
	
	public void assertFileContainsEntryNTimes(final File file, final String searchString, final int n) {
		final List<String> fileContentAsList = TestUtil.getFileContentAsList(file);
		int counter = 0;
		for (final String line : fileContentAsList) {
			if (line.contains(searchString)) {
				counter++;
			}
		}
		assertEquals("counter", n, counter);
	}


	public void assertFileDoesNotContainEntry(final File f, final String entry) {
		final String actualFileContent = TestUtil.getFileContent(f);
		final boolean entryFound = actualFileContent.contains(entry);
		assertFalse("Expected Entry was found in file: " + entry 
				+ "\nFile: " + f.getAbsolutePath(), entryFound);
	}
	
	public int countMatchesInContainedFile(final File f, final String expectedEntry) {
		String actualFileContent = TestUtil.getFileContent(f);
		int counter = 0;
		while (actualFileContent.contains(expectedEntry)) {
			actualFileContent = actualFileContent.replaceFirst(expectedEntry, "");
			counter++;
		}
		return counter;
	}

	public void assertStringEndsWith(final String text, final String expected) {
		if (text.length() < expected.length()) {
			throw new IllegalArgumentException("First string must have greater length than second");
		}
		final int length = expected.length();
		final String suffix = text.substring(text.length()-length);
		assertEquals("Unexpected suffix!", expected, suffix);
	}


	public void assertStringStartsWith(final String text, final String expected) {
		if (text.length() < expected.length()) {
			throw new IllegalArgumentException("First string must have greater length than second");
		}
		final int length = expected.length();
		final String suffix = text.substring(0, length);
		assertEquals("Unexpected prefix!", expected, suffix);
	}

	public void assertFileContainsNoEntry(final File f, final String entry) {
		final String actualFileContent = TestUtil.getFileContent(f);
		final boolean searchResult = actualFileContent.contains(entry);
		assertFalse("Wrong Entry found in file: " + entry 
				+ "\nFile: " + f.getAbsolutePath(), searchResult);
	}
	
	public void assertFileExists(final File f) {
		assertTrue("Expected file does not exist:\n" + f.getAbsolutePath(), f.exists());
	}
	
	public void assertFileDoesNotExist(final File f) {
		assertFalse("File exists unexpectedly:\n" + f.getAbsolutePath(), f.exists());
	}
	
	public void assertChildrenNumberInDirectory(final File dir, final int expectedNumber) {
		final File[] files = dir.listFiles();
		final int actualNumber;
		if (files == null) {
			actualNumber = 0;
		} else {
			actualNumber = files.length;
		}
		assertEquals("Number of children in dir " + dir.getAbsolutePath(), expectedNumber, actualNumber);
	}
	
	public void setEmptyProperties() throws FileNotFoundException, IOException {
		initPropertiesWith("");
	}
	
	
	protected void assertPluginInLogfile(String jarName, String pluginName, String pluginType, 
										 String pluginStatus, String starterClass, String infoMessage) {
		
		final String s = "PluginMetaData [jarName=" + jarName + ", " +
				         "id=" + pluginName + ", " +
				         "pluginType=" + pluginType + ", " +
				         "status=" + pluginStatus + ", " + 
				         "infoMessage=" + infoMessage + "]";
		
		assertFileContainsEntry(applicationLogfile, s);
	}

	protected void assertFileEquals(final File expectedFile, final File actualFile) {
		try {
			final List<String> expectedFileContent = cutTrailingEmptyLines(FileUtil.getFileContentAsList(expectedFile));
			final List<String> actualFileContent = cutTrailingEmptyLines(FileUtil.getFileContentAsList(actualFile));
			if (expectedFileContent.size() != actualFileContent.size()) {
				System.out.println("----------------------------------------------------");
				System.out.println("expectedFileContent:");
				for (int i = 0; i < expectedFileContent.size(); i++) {
					System.out.println(expectedFileContent.get(i));
				}
				System.out.println("----------------------------------------------------");
				System.out.println("actualFileContent:");
				for (int i = 0; i < actualFileContent.size(); i++) {
					System.out.println(actualFileContent.get(i));
				}
				System.out.println("----------------------------------------------------");
			}
			assertEquals("Number lines in file", expectedFileContent.size(), actualFileContent.size());
			for (int i = 0; i < expectedFileContent.size(); i++) {
				assertStringEquals((i+1) + ". line of file", expectedFileContent.get(i), actualFileContent.get(i));
			}
		} catch (Exception e) {
			throw new RuntimeException("Error comparing files", e);
		}
	}
	
	private List<String> cutTrailingEmptyLines(final List<String> fileContent) {
		final List<Integer> emptyLineList = new ArrayList<Integer>();
		for (int i = fileContent.size() - 1; i > 0; i--) {
			if (StringUtils.isEmpty(fileContent.get(i))) {
				emptyLineList.add(i);
			} else {
				break;
			}
		}
		final List<String> toReturn = new ArrayList<String>();
		for (int i = 0; i < fileContent.size(); i++) {
			if (! emptyLineList.contains(i)) {
				toReturn.add(fileContent.get(i));
			}
		}
		return toReturn;
	}
	
	protected File getTestFile(final String filename) {
		final File file = new File(PROJECT_ROOT_DIR, "src/main/resources/testdata/" + filename);
		assertFileExists(file);
		return file;
	}
	
}
