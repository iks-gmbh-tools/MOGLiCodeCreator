package com.iksgmbh.moglicc.build.helper;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_LIB;
import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_PLUGIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.build.MOGLiReleaseBuilder;
import com.iksgmbh.moglicc.build.helper.ReleaseFileCollector.FileCollectionData;
import com.iksgmbh.moglicc.build.test.ApplicationTestParent;
import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.ImmutableUtil;

public class ReleaseFileCollectorBuildTest extends ApplicationTestParent {
	
	private File rootDir;
	private File releaseDir;
	private FileCollectionData fileData;
	private boolean firstTime = true;

	private ReleaseFileCollector releaseFileCollector;
	
	public void initFirstTime() {
		if (firstTime) {
			firstTime = false;
			
			final MOGLiReleaseBuilder mogliReleaseBuilder = new MOGLiReleaseBuilder();
			rootDir = new File(MOGLiReleaseBuilder.getApplicationRootDir());
			releaseDir = new File(rootDir + "/releasetest");
			final File[] testCoreFiles = {new File(rootDir + "/" + MOGLiReleaseBuilder.FILENAME_BUILD_PROPERTIES), 
					new File(rootDir + "/" + MOGLiReleaseBuilder.FILENAME_STARTBAT),
					new File(rootDir + "/" + MOGLiReleaseBuilder.FILENAME_STARTSH)};
			final File[] testPluginFiles = {new File(rootDir + "/" + MOGLiReleaseBuilder.FILENAME_STARTBAT),
					                        new File(rootDir + "/" + MOGLiReleaseBuilder.FILENAME_STARTSH)};
			final File[] thirdPartyLibraries = mogliReleaseBuilder.getThirdPartyJars();
			fileData = new FileCollectionData();
			fileData.libSubdir = DIR_LIB;
			fileData.pluginsSubdir = DIR_PLUGIN;
			fileData.sourceDir = rootDir;
			fileData.releaseDir = releaseDir;
			fileData.fileListForRootDir = ImmutableUtil.getImmutableListOf(MOGLiReleaseBuilder.FILENAME_STARTBAT,
					                                                       MOGLiReleaseBuilder.FILENAME_STARTSH);
			fileData.jarsOfCoreComponents = testCoreFiles;
			fileData.jarsOfPlugins = testPluginFiles;
			fileData.thirdPartyJars = thirdPartyLibraries;
		}
	}
	
	@Before
	public void setup() {
		super.setup();
		initFirstTime();

		releaseFileCollector = new ReleaseFileCollector(fileData);
		releaseFileCollector.initReleaseDir();
		
		final File batfile = new File(getProjectResourcesDir() + MOGLiReleaseBuilder.RELEASE_DATA_SOURCE_SUBDIR
				+ "/" + MOGLiReleaseBuilder.FILENAME_STARTBAT);
		final File shfile = new File(getProjectResourcesDir() + MOGLiReleaseBuilder.RELEASE_DATA_SOURCE_SUBDIR
				+ "/" + MOGLiReleaseBuilder.FILENAME_STARTSH);
		
		FileUtil.copyTextFile(batfile, MOGLiReleaseBuilder.getApplicationRootDir());
		FileUtil.copyTextFile(shfile, MOGLiReleaseBuilder.getApplicationRootDir());
	}
	
	@Test
	public void initsReleaseDir() {
		assertTrue("ReleaseDir does not exist:\n" + fileData.releaseDir, fileData.releaseDir.exists());
		assertEquals("ReleaseDir is not empty:\n", 0, fileData.releaseDir.listFiles().length);
	}
	
	@Test
	public void copiesFileIntoRootDir() {
		releaseFileCollector.copyFileIntoRootDir();
		final File[] filelist = fileData.releaseDir.listFiles();
		assertEquals("Unexpected number of files:", 2, filelist.length);
<<<<<<< HEAD
<<<<<<< HEAD
		assertEquals("Unexpected filename.", MOGLiReleaseBuilder.FILENAME_STARTSH, filelist[0].getName());
		assertEquals("Unexpected filename.", MOGLiReleaseBuilder.FILENAME_STARTBAT, filelist[1].getName());		
=======
>>>>>>> originReikOberrath/master
		if (filelist[0].getName().endsWith(".sh"))
		{
			assertEquals("Unexpected filename.", MOGLiReleaseBuilder.FILENAME_STARTSH, filelist[0].getName());
			assertEquals("Unexpected filename.", MOGLiReleaseBuilder.FILENAME_STARTBAT, filelist[1].getName());
		}
		else
=======
		if (filelist[0].getName().endsWith(".bat"))
>>>>>>> development
		{
			assertEquals("Unexpected filename.", MOGLiReleaseBuilder.FILENAME_STARTBAT, filelist[0].getName());
			assertEquals("Unexpected filename.", MOGLiReleaseBuilder.FILENAME_STARTSH, filelist[1].getName());
		}
<<<<<<< HEAD
=======
		else
		{			
			assertEquals("Unexpected filename.", MOGLiReleaseBuilder.FILENAME_STARTSH, filelist[0].getName());
			assertEquals("Unexpected filename.", MOGLiReleaseBuilder.FILENAME_STARTBAT, filelist[1].getName());
		}
>>>>>>> development
	}
	
	@Test
	public void copiesFileIntoRootDir_unknownFile () {
		FileCollectionData fileData = new FileCollectionData();
		fileData.sourceDir = rootDir;
		fileData.releaseDir = releaseDir;
		fileData.fileListForRootDir = ImmutableUtil.getImmutableListOf("unknown");
		releaseFileCollector = new ReleaseFileCollector(fileData);
		releaseFileCollector.initReleaseDir();
		try {
			releaseFileCollector.copyFileIntoRootDir();
		} catch (MOGLiCoreException e) {
			assertTrue("Wrong Error message.", e.getMessage().startsWith("File does not exist: "));
			return;
		}
		fail("Expected exception not thrown");
	}
	
	@Test
	public void createsLibDirectory() {
		releaseFileCollector.createLibDirectory();
		File libDir = new File(fileData.releaseDir + "/" + fileData.libSubdir);
		assertTrue("Directory does not exist: " + libDir.getAbsolutePath(), libDir.exists());
		assertTrue("", libDir.isDirectory());
	}
	
	@Test
	public void createsPluginDirectory() {
		releaseFileCollector.createPluginDirectory();
		File pluginDir = new File(fileData.releaseDir + "/" + fileData.libSubdir 
				                                      + "/" + fileData.pluginsSubdir);
		assertTrue("Directory does not exist: " + fileData.pluginsSubdir, pluginDir.exists());
		assertTrue("", pluginDir.isDirectory());
	}
	
	@Test
	public void copiesCoreJarFilesIntoReleaseDir() {
		releaseFileCollector.createLibDirectory();
		releaseFileCollector.copyCoreJarFiles();
		File releaseDir = new File(fileData.releaseDir + "/" + fileData.libSubdir);
		File[] listFiles = releaseDir.listFiles();
		assertEquals("Unexpected file number.", 3, listFiles.length);
	}	
	
	@Test
	public void copiesThirdPartyJarsIntoReleaseDir() {
		releaseFileCollector.createLibDirectory();
		releaseFileCollector.copyThirdPartyJars();
		File libDir = new File(fileData.releaseDir + "/" + fileData.libSubdir);
		File[] listFiles = libDir.listFiles();
		assertEquals("Unexpected file number.", 8, listFiles.length);
	}	
	
	@Test
	public void copiesPluginJarFilesIntoReleaseDir() {
		releaseFileCollector.createPluginDirectory();
		releaseFileCollector.copyPluginJarFiles();
		File pluginDir = new File(fileData.releaseDir + "/" + fileData.libSubdir 
				                                      + "/" + fileData.pluginsSubdir);
		File[] listFiles = pluginDir.listFiles();
		assertEquals("Unexpected file number.", 2, listFiles.length);
	}	
	
	@Test
	public void testDoYourJob() {
		FileUtil.deleteDirWithContent(fileData.releaseDir);
		assertFalse("ReleaseDir exists!\n" + fileData.releaseDir, fileData.releaseDir.exists());
		ReleaseFileCollector.doYourJob(fileData);
		assertTrue("ReleaseDir does not exist:\n" + fileData.releaseDir, fileData.releaseDir.exists());
	}
}
