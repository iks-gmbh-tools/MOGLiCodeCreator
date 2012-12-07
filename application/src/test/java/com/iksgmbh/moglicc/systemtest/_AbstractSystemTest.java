package com.iksgmbh.moglicc.systemtest;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.FILENAME_LOG_FILE;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.MOGLiTextConstants;
import com.iksgmbh.moglicc.build.MOGLiReleaseBuilder;
import com.iksgmbh.moglicc.build.helper.VersionReplacer;
import com.iksgmbh.moglicc.build.test.ApplicationTestParent;
import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.utils.CmdUtil;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.ZipUtil;

public class _AbstractSystemTest extends ApplicationTestParent {

	private static final String FILENAME_BUILD_PROPERTIES = "test.properties";
	
	// configuration settings
	public static boolean readTestProperties = false;
	protected static boolean cleanupWhenFinished = false;
	protected static boolean buildReleaseBeforeTesting = false;
	protected static boolean setVersionInPomsBackToOldValue = false;

	protected static final String TEST_SUB_DIR_NAME = "SystemTestDir";
	protected static final String MOGLI_EXE_COMMAND = "startMOGLiCodeCreator.bat";

	protected final MOGLiReleaseBuilder releaseBuilder = new MOGLiReleaseBuilder();
	protected final String TEST_DIR_NAME = releaseBuilder.getReleaseDir() + "/" + TEST_SUB_DIR_NAME;
	protected final File testDir = new File(TEST_DIR_NAME);
	private Properties testProperties;

	protected static boolean firstTime = true;

	@Before
	@Override
	public void setup() {
		super.setup();
		if (firstTime) {
			initForFirstTime();
		}
	}

	private void initForFirstTime() {
		if (readTestProperties) {
			getTestProperties();
		}
		
		if (buildReleaseBeforeTesting) {
			boolean ok = releaseBuilder.doYourJob();
			if (! ok) System.exit(1);
		}
		
		prepareTestDir();
	}
	
	private void getTestProperties() {
		try {
			readPropertiesFile();
		} catch (Exception e) {
			throw new MOGLiCoreException("Error reading file " + FILENAME_BUILD_PROPERTIES);
		}
		cleanupWhenFinished = isTrue((String) testProperties.get("cleanupWhenFinished"));
		buildReleaseBeforeTesting = isTrue((String) testProperties.get("buildReleaseBeforeTesting"));
		setVersionInPomsBackToOldValue = isTrue((String) testProperties.get("setVersionInPomsBackToOldValue"));
	}

	private boolean isTrue(final String s) {
		if (s == null) {
			return false;
		}
		return s.toLowerCase().trim().equals("true");
	}

	@Override
	protected String initTestApplicationRootDir() {
		final String applicationRootDir = TEST_DIR_NAME;
		MOGLiCodeCreator.setApplicationRootDir(applicationRootDir);
		return applicationRootDir;
	}

	protected void prepareTestDir() {
		FileUtil.deleteDirWithContent(TEST_DIR_NAME);
		assertFalse("Directory not deleted:\n" + TEST_DIR_NAME,
				testDir.exists());
		try {
			ZipUtil.unzip(releaseBuilder.getReleaseZipFile(), TEST_DIR_NAME);			
		} catch (Exception e) {
			throw new MOGLiCoreException("Error zipping " + releaseBuilder.getReleaseZipFile().getAbsolutePath());
		}
	}

	@After
	public void teardown() {
		if (firstTime) {
			firstTime = false;
			final String dir = TEST_DIR_NAME;
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					printMogliLogFile();
					if (cleanupWhenFinished) {
						doFinalCleanup(dir);
					}
				}
			});

			if (setVersionInPomsBackToOldValue) {
				VersionReplacer.setVersionInPomsBackToOldValue();
			}
		}
	}

	final void printMogliLogFile() {
		final File logFile = new File(applicationLogDir, FILENAME_LOG_FILE);
		try {
			final String fileContent = FileUtil.getFileContent(logFile);
			final int pos1 = fileContent
					.lastIndexOf(MOGLiTextConstants.TEXT_PLUGINS_FOUND);
			final int pos2 = fileContent
					.lastIndexOf(MOGLiTextConstants.TEXT_DONE);
			System.out
					.println("\n\n#######################################################################");
			System.out.println("#############       Summary from current "
					+ FILENAME_LOG_FILE + "       #############");
			System.out
					.println("#######################################################################\n");
			System.out.println(fileContent.substring(pos1, pos2));
			System.out
					.println("#######################################################################\n");
		} catch (IOException e) {
			System.err
					.println("Error printing " + FILENAME_LOG_FILE);
		}
	}

	void doFinalCleanup(final String dir) {
		boolean ok = FileUtil.deleteDirWithContent(dir);
		if (ok) {
			System.out.println("Final cleanup finished!");
		} else {
			System.err.println("Error during final cleanup!");
		}
	}

	private void readPropertiesFile() throws FileNotFoundException, IOException {
		final String propertiesPath = getProjectResourcesDir() + "/" + FILENAME_BUILD_PROPERTIES;
		final File propertiesFile = new File(propertiesPath);
		if (propertiesFile.exists()) {
			testProperties = new Properties();
			FileInputStream fileInputStream = new FileInputStream(propertiesPath);
			testProperties.load(fileInputStream);
		} else {
			System.out.println("Properties File not found: " + propertiesPath);
		}
	}

	protected void executeMogliApplication() {
		try {
			CmdUtil.execWindowCommand(testDir, MOGLI_EXE_COMMAND, false);
		} catch (Exception e) {
			// ignore it
		}
	}
}
