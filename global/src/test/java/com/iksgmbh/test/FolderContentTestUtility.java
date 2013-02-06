package com.iksgmbh.test;

import java.io.File;

import com.iksgmbh.utils.FileUtil;

public class FolderContentTestUtility {

	public static final String TEST_MAIN_FOLDER = "../global/target/sourceTestFolder";
	public static final String FILE1_TXT = "file1.txt";
	public static final String FILE2_XML = "file2.xml";

	public static final String SUB_FOLDER1 = "subFolder1";
	public static final String FILE11_JAVA = "file11.java";
	public static final String FILE12_XML = "file12.xml";

	public static final String SUB_FOLDER2 = "subFolder2";
	public static final String FILE21_TXT = "file21.txt";
	public static final String FILE22_TXT = "file22.txt";

	public static final String SUB_SUB_FOLDER = "subSubFolder";
	public static final String FILE_TXT = "file.txt";
	public static final String FILE_JAVA = "file.java";
	public static final String FILE_PROPERTIES = "file.properties";

	public static final String LINE_BREAK = FileUtil.getSystemLineSeparator();
	public static final String FILE_ORIG_CONTENT = "A" + LINE_BREAK + "  C";


	public static File mainTestFolder = new File(TEST_MAIN_FOLDER);

	public static File createTestFolder() throws Exception {
		final File mainTestFolder = new File(TEST_MAIN_FOLDER);
		mainTestFolder.mkdirs();

		File file = new File(mainTestFolder, FILE1_TXT);
		FileUtil.createNewFileWithContent(file, FILE_ORIG_CONTENT);
		file = new File(mainTestFolder, FILE2_XML);
		FileUtil.createNewFileWithContent(file, FILE_ORIG_CONTENT);

		File subFolder = new File(mainTestFolder, SUB_FOLDER1);
		subFolder.mkdirs();
		file = new File(subFolder, FILE11_JAVA);
		FileUtil.createNewFileWithContent(file, FILE_ORIG_CONTENT);
		file = new File(subFolder, FILE12_XML);
		FileUtil.createNewFileWithContent(file, FILE_ORIG_CONTENT);

		subFolder = new File(mainTestFolder, SUB_FOLDER2);
		subFolder.mkdirs();
		file = new File(subFolder, FILE21_TXT);
		FileUtil.createNewFileWithContent(file, FILE_ORIG_CONTENT);
		file = new File(subFolder, FILE22_TXT);
		FileUtil.createNewFileWithContent(file, FILE_ORIG_CONTENT);

		subFolder = new File(subFolder, SUB_SUB_FOLDER);
		subFolder.mkdirs();
		file = new File(subFolder, FILE_TXT);
		FileUtil.createNewFileWithContent(file, FILE_ORIG_CONTENT);
		file = new File(subFolder, FILE_JAVA);
		FileUtil.createNewFileWithContent(file, FILE_ORIG_CONTENT);
		file = new File(subFolder, FILE_PROPERTIES);
		FileUtil.createNewFileWithContent(file, FILE_ORIG_CONTENT);

		return mainTestFolder;
	}
}
