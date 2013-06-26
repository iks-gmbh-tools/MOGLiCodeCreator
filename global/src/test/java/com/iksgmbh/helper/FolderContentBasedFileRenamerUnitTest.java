package com.iksgmbh.helper;

import static com.iksgmbh.test.FolderContentTestUtility.FILE_TXT;
import static com.iksgmbh.test.FolderContentTestUtility.SUB_FOLDER2;
import static com.iksgmbh.test.FolderContentTestUtility.SUB_FOLDER3;
import static com.iksgmbh.test.FolderContentTestUtility.SUB_SUB_FOLDER;
import static com.iksgmbh.test.FolderContentTestUtility.initTestFolder;
import static com.iksgmbh.test.FolderContentTestUtility.mainTestFolder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.iksgmbh.data.FolderContent;
import com.iksgmbh.helper.FolderContentBasedFileRenamer.RenamingData;

public class FolderContentBasedFileRenamerUnitTest {

	@Test
	public void renameFileThatExistsOnce() throws Exception {
		// prepare test
		initTestFolder();
		final FolderContentBasedFileRenamer renamer = new FolderContentBasedFileRenamer(mainTestFolder, null);
		final RenamingData renaming = new RenamingData("file1.txt", "file1Renamed.txt");

		// call functionality under test
		renamer.doYourJob(renaming);

		// verify test result
		final FolderContent folderContent = new FolderContent(mainTestFolder, null);
		
		final List<File> oldFiles = folderContent.getFilesWithEndingPattern("file1.txt");
		assertEquals("number files", 0, oldFiles.size());
		final List<File> newFiles = folderContent.getFilesWithEndingPattern("file1Renamed.txt");
		assertEquals("number files", 1, newFiles.size());
		assertEquals("Renaming result", "File 'file1.txt' renamed to 'file1Renamed.txt' in directory '" +
				                      "..\\global\\target\\sourceTestFolder'", renaming.getRenamingResults().get(0));
	}	

	@Test
	public void renameFileThatExistsTwice() throws Exception {
		// prepare test
		initTestFolder();
		final File file = new File(mainTestFolder, FILE_TXT);
		file.createNewFile();
		
		FolderContent folderContent = new FolderContent(mainTestFolder, null);
		List<File> oldFiles = folderContent.getFilesWithEndingPattern(FILE_TXT);
		assertEquals("number files", 2, oldFiles.size());
		
		final FolderContentBasedFileRenamer renamer = new FolderContentBasedFileRenamer(mainTestFolder, null);
		final String newFileName = FILE_TXT + "2";
		final RenamingData renaming = new RenamingData(FILE_TXT, newFileName);

		// call functionality under test
		renamer.doYourJob(renaming);

		// verify test result
		folderContent = new FolderContent(mainTestFolder, null);
		oldFiles = folderContent.getFilesWithEndingPattern(FILE_TXT);
		assertEquals("number files", 0, oldFiles.size());
		final List<File> newFiles = folderContent.getFilesWithEndingPattern(newFileName);
		assertEquals("number files", 2, newFiles.size());
		assertEquals("Renaming results", 2, renaming.getRenamingResults().size());
	}	

	@Test
	public void renameFileThatExistsTwiceInOnlyOneSubfolder() throws Exception {
		// prepare test
		initTestFolder();
		final File file = new File(mainTestFolder, FILE_TXT);
		file.createNewFile();
		
		FolderContent folderContent = new FolderContent(mainTestFolder, null);
		List<File> oldFiles = folderContent.getFilesWithEndingPattern(FILE_TXT);
		assertEquals("number files", 2, oldFiles.size());
		
		final FolderContentBasedFileRenamer renamer = new FolderContentBasedFileRenamer(mainTestFolder, null);
		final String newFileName = FILE_TXT + "2";
		final RenamingData renaming = new RenamingData(SUB_SUB_FOLDER + "\\" + FILE_TXT, newFileName);

		// call functionality under test
		renamer.doYourJob(renaming);

		// verify test result
		folderContent = new FolderContent(mainTestFolder, null);
		oldFiles = folderContent.getFilesWithEndingPattern(FILE_TXT);
		assertEquals("number files", 1, oldFiles.size());
		final List<File> newFiles = folderContent.getFilesWithEndingPattern(newFileName);
		assertEquals("number files", 1, newFiles.size());
		assertEquals("Renaming result", "File 'file.txt' renamed to 'file.txt2' in directory '" 
                                        + "..\\global\\target\\sourceTestFolder\\subFolder2\\subSubFolder'", renaming.getRenamingResults().get(0));
	}
	
	@Test
	public void renameWithMultipleDatasets() throws Exception {
		// prepare test
		initTestFolder();
		final File file = new File(mainTestFolder, FILE_TXT);
		file.createNewFile();
		
		FolderContent folderContent = new FolderContent(mainTestFolder, null);
		List<File> oldFiles = folderContent.getFilesWithEndingPattern(FILE_TXT);
		assertEquals("number files", 2, oldFiles.size());
		
		final FolderContentBasedFileRenamer renamer = new FolderContentBasedFileRenamer(mainTestFolder, null);
		final String newFileName = FILE_TXT + "2";
		final RenamingData renaming1 = new RenamingData(FILE_TXT, newFileName, false);
		final RenamingData renaming2 = new RenamingData("file1.txt", "file1Renamed.txt");
		final List<RenamingData> renamings = new ArrayList<RenamingData>();
		renamings.add(renaming1);
		renamings.add(renaming2);

		// call functionality under test
		renamer.doYourJob(renamings);

		// verify test result
		assertEquals("Renaming results 1", 2, renaming1.getRenamingResults().size());
		assertEquals("Renaming results 2", 1, renaming2.getRenamingResults().size());
	}
	
	@Test
	public void renamesDirectory() throws Exception {
		// prepare test
		initTestFolder();
		final RenamingData renaming = new RenamingData(SUB_FOLDER2, SUB_FOLDER3, true);
		final FolderContentBasedFileRenamer renamer = new FolderContentBasedFileRenamer(mainTestFolder, null);
		final List<RenamingData> renamings = new ArrayList<RenamingData>();
		renamings.add(renaming);
		final File oldDir = new File(mainTestFolder, SUB_FOLDER2);
		assertTrue(oldDir.exists());
		final File newDir = new File(mainTestFolder, SUB_FOLDER3);
		assertFalse(newDir.exists());
		
		// call functionality under test
		renamer.doYourJob(renamings);
		
		// verify test result
		assertFalse(oldDir.exists());
		assertTrue(newDir.exists());
	}

	@Test
	public void movesDirectoryToAnotherPath() throws Exception {
		// prepare test
		initTestFolder();
		final String newPath = "level1/level2/level3";
		final RenamingData renaming = new RenamingData(SUB_FOLDER2, newPath + "/" + SUB_FOLDER2, true);
		final FolderContentBasedFileRenamer renamer = new FolderContentBasedFileRenamer(mainTestFolder, null);
		final List<RenamingData> renamings = new ArrayList<RenamingData>();
		renamings.add(renaming);
		final File oldDir = new File(mainTestFolder, SUB_FOLDER2);
		assertTrue(oldDir.exists());
		final File newDir = new File(mainTestFolder, newPath);
		assertFalse(newDir.exists());
		
		// call functionality under test
		renamer.doYourJob(renamings);
		
		// verify test result
		assertFalse(oldDir.exists());
		assertTrue(newDir.exists());
	}

	@Test
	public void throwsExceptionIfDirToRenameDoesNotExist() throws Exception {
		// prepare test
		initTestFolder();
		final RenamingData renaming = new RenamingData("NotExistingDir", SUB_FOLDER2, true);
		final FolderContentBasedFileRenamer renamer = new FolderContentBasedFileRenamer(mainTestFolder, null);
		final List<RenamingData> renamings = new ArrayList<RenamingData>();
		renamings.add(renaming);
		
		try {
			// call functionality under test
			renamer.doYourJob(renamings);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// verify test result
			assertEquals("Error message", "Cannot rename unkown directory: NotExistingDir", e.getMessage());
		}
		

	}

}
