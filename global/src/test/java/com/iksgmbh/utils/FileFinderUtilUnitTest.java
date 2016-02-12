package com.iksgmbh.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class FileFinderUtilUnitTest 
{
	private File mainFolder = new File("..//global");
	final List<String> subdirsToIgnore = new ArrayList<String>();
	
	@Before
	public void setup() {
		subdirsToIgnore.clear();
		subdirsToIgnore.add("\\target\\");
	}
	
	@Test
	public void findsAllJavaFileInGlobalProject() throws IOException
	{
		// act
		final List<File> result = FileFinderUtil.findFiles(mainFolder, null, subdirsToIgnore, "java", null);
		
		// assert
		assertEquals("number of java files", 35, result.size());
		
	}
	
	@Test
	public void findsAllJavaFileInSourceMainFolderOfGlobalProject() throws IOException
	{
		// arrange
		final List<String> subdirsToSearch = new ArrayList<String>();
		subdirsToSearch.add("src/main/java");
		
		// act
		final List<File> result = FileFinderUtil.findFiles(mainFolder, subdirsToSearch, subdirsToIgnore, "java", null);
		
		// assert
		assertEquals("number of java files", 20, result.size());
		
	}
	
	@Test
	public void findsAllJavaFileInSourceTestFolderOfGlobalProject() throws IOException
	{
		// arrange
		final List<String> subdirsToSearch = new ArrayList<String>();
		subdirsToSearch.add("\\src\\test\\java\\");
		
		// act
		final List<File> result = FileFinderUtil.findFiles(mainFolder, subdirsToSearch, subdirsToIgnore, "java", null);
		
		// assert
		assertEquals("number of java files", 15, result.size());		
	}

	@Test
	public void findsJavaFileButIgnoreTestSubdirProject() throws IOException
	{
		// arrange
		subdirsToIgnore.add("\\src\\test\\java\\");

		// act
		final List<File> result = FileFinderUtil.findFiles(mainFolder, null, subdirsToIgnore, "java", null);
		
		// assert
		System.err.println("### " + result.size());
		assertEquals("number of java files", 20, result.size());		
	}	

	@Test
	public void findsJavaFileInSourceFoldersButIgnoreTestSubdirProject() throws IOException
	{
		// arrange
		subdirsToIgnore.add("\\src\\test\\java\\");
		final List<String> subdirsToSearch = new ArrayList<String>();
		subdirsToSearch.add("\\src\\main\\java\\");
		subdirsToSearch.add("\\src\\test\\java\\");

		// act
		final List<File> result = FileFinderUtil.findFiles(mainFolder, subdirsToSearch, subdirsToIgnore, "java", null);
		
		// assert
		assertEquals("number of java files", 20, result.size());		
	}	
	
	@Test
	public void findsAllJavaFileInBothSourceFoldersOfGlobalProject() throws IOException
	{
		// act
		final List<File> result = FileFinderUtil.findJavaFilesInMavenStructure(mainFolder);
		
		// assert
		assertEquals("number of java files", 35, result.size());		
	}

	@Test
	public void findsAllJavaTestFilesInGlobalProject() throws IOException
	{
		// arrange
		final List<String> subdirsToSearch = new ArrayList<String>();
		subdirsToSearch.add("\\src\\main\\java\\");
		subdirsToSearch.add("\\src\\test\\java\\");
		
		// act
		final List<File> result = FileFinderUtil.findFiles(mainFolder, subdirsToSearch, subdirsToIgnore, "java", "Test");
		
		// assert
		assertEquals("number of java files", 15, result.size());		
	}

	@Test
	public void findsZipUtilFilesInGlobalProject() throws IOException
	{
		// act
		final List<File> result = FileFinderUtil.findFiles(mainFolder, null, subdirsToIgnore, null, "Util.");
		
		// assert
		assertEquals("number of java files", 8, result.size());		
	}

	@Test
	public void findsSingleFileInGlobalProject() throws IOException
	{
		// act
		final List<File> result1 = FileFinderUtil.findFiles(mainFolder, null, subdirsToIgnore, null, "ZipUtilUnitTest.java");
		final List<File> result2 = FileFinderUtil.findFiles(mainFolder, null, subdirsToIgnore, ".java", "ZipUtilUnitTest");
		
		// assert
		assertEquals("number of java files", 1, result1.size());		
		assertEquals("number of java files", 1, result2.size());		
	}
	
}