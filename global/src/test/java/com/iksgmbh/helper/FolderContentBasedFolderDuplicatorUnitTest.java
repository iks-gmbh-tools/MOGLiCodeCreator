package com.iksgmbh.helper;

import static com.iksgmbh.test.FolderContentTestUtility.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.ImmutableUtil;

public class FolderContentBasedFolderDuplicatorUnitTest {
	
	public static File targetTestFolder = new File("../global/target/targetTestFolder");

	@Before
	public void setup() {
		FileUtil.deleteDirWithContent(mainTestFolder);
		FileUtil.deleteDirWithContent(targetTestFolder);
	}
	
	@Test
	public void duplicatesSourceFolder() throws Exception {
		// prepare test
		createTestFolder();
		assertFalse("Folder does already exist:\n" + targetTestFolder.getAbsolutePath(), targetTestFolder.exists());
		
		// call functionality under test
		final List<String> ignoreList = ImmutableUtil.getImmutableListOf(".svn");
		final FolderContentBasedFolderDuplicator duplicator = new FolderContentBasedFolderDuplicator(mainTestFolder, ignoreList);
		duplicator.duplicateTo(targetTestFolder);

		// verify test result
		assertTrue("Folder does not exist:\n" + targetTestFolder.getAbsolutePath(), targetTestFolder.exists());
		File file = new File(targetTestFolder, FILE1_TXT);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		file = new File(targetTestFolder, FILE2_XML);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		
		File folder = new File(targetTestFolder, SUB_FOLDER1);
		assertTrue("Folder does not exist:\n" + folder.getAbsolutePath(), folder.exists());
		file = new File(folder, FILE11_JAVA);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		file = new File(folder, FILE12_XML);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());

		
		folder = new File(targetTestFolder, SUB_FOLDER2);
		assertTrue("Folder does not exist:\n" + folder.getAbsolutePath(), folder.exists());
		file = new File(folder, FILE21_TXT);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		file = new File(folder, FILE22_TXT);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		
		folder = new File(folder, SUB_SUB_FOLDER);
		assertTrue("Folder does not exist:\n" + folder.getAbsolutePath(), folder.exists());
		file = new File(folder, FILE_TXT);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		file = new File(folder, FILE_JAVA);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		file = new File(folder, FILE_PROPERTIES);
		assertTrue("File does not exist:\n" + file.getAbsolutePath(), file.exists());
		
		final String fileContent = FileUtil.getFileContent(file);
		assertEquals("file content", FILE_ORIG_CONTENT, fileContent);
	}
}
