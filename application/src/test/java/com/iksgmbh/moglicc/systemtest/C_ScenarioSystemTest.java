package com.iksgmbh.moglicc.systemtest;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.iksgmbh.data.FolderContent;
import com.iksgmbh.utils.FileUtil;

public class C_ScenarioSystemTest extends __AbstractSystemTest {

	private static final String METAINFO_PROJECT_NAME = "metainfo projectName";
	public static final String FILENAME_MODELFILE_NewPluginModel = "MOGLiCC_NewPluginModel.txt";

	@Override
	public void setup() {
		super.setup();
		executeMogliApplication();  // unpack default input data
	}
	
	@Override
	public void teardown() {
		super.teardown();
		FileUtil.deleteDirWithContent(applicationInputDir);
	}

	@Test
	public void executeWith_JavaBeanModel() throws Exception {
		// prepare test
		final File targetDir = readTargetDirFromModelFile();
		FileUtil.deleteDirWithContent(targetDir);
		assertFileDoesNotExist(targetDir);
		
		// call functionality under test
		executeMogliApplication();  // model MOGLiCC_JavaBeanModel is used per default

		// verify test result
		assertFileExists(targetDir);
		final FolderContent folderContent = new FolderContent(targetDir, null);
		assertEquals("folder number", 25, folderContent.getFolders().size());
		assertEquals("total file number", 45, folderContent.getFiles().size());
	}

	@Test
	public void executeWith_NewPluginModel() throws Exception {
		// prepare test
		modelTextfile = new File(modelTextfile.getParentFile(), FILENAME_MODELFILE_NewPluginModel);
		final File targetDir = readTargetDirFromModelFile();
		FileUtil.deleteDirWithContent(targetDir);
		assertFileDoesNotExist(targetDir);
		FileUtil.createNewFileWithContent(modelPropertiesFile, "modelfile=MOGLiCC_NewPluginModel.txt");
		
		// call functionality under test
		executeMogliApplication();   

		// verify test result
		assertFileExists(targetDir);
		final FolderContent folderContent = new FolderContent(targetDir, null);
		assertEquals("folder number", 14, folderContent.getFolders().size());
		assertEquals("total file number", 3, folderContent.getFiles().size());	
	}

	private File readTargetDirFromModelFile() throws IOException {
		final List<String> fileContent = FileUtil.getFileContentAsList(modelTextfile);
		for (final String line : fileContent) {
			if (line.contains(METAINFO_PROJECT_NAME)) {
				final int pos = line.indexOf(METAINFO_PROJECT_NAME);
				final String projectName = line.substring(pos + METAINFO_PROJECT_NAME.length());
				return new File (applicationRootDir, projectName.trim());
			}
		}
		throw new RuntimeException(METAINFO_PROJECT_NAME + " not found in " + modelTextfile.getName());
	}
}
