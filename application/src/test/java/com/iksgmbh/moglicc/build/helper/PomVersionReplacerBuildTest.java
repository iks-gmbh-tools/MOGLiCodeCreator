package com.iksgmbh.moglicc.build.helper;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.build.MogliReleaseBuilder;
import com.iksgmbh.moglicc.build.test.ApplicationTestParent;
import com.iksgmbh.moglicc.exceptions.MogliCoreException;
import com.iksgmbh.utils.FileUtil;

public class PomVersionReplacerBuildTest extends ApplicationTestParent {
	
	public static final String TESTPOMFILE1 = "testPom1.xml";
	public static final String TESTPOMFILE2 = "testPom2.xml";
	

	@Before
	public void setup() {
		super.setup();
		final File testfile1 = new File(getProjectTestResourcesDir() + TESTPOMFILE1);
		FileUtil.copyTextFile(testfile1, MogliReleaseBuilder.getApplicationRootDir());
		final File testfile2 = new File(getProjectTestResourcesDir() + TESTPOMFILE2);
		FileUtil.copyTextFile(testfile2, MogliReleaseBuilder.getApplicationRootDir());
	}
	
	@Test
	public void testReplaceVersionInPom() throws IOException {
		final String oldVersion = "0.0.1-SNAPSHOT";
		final String newVersion = "1.2.3";
		final VersionReplacer versionReplacer = new VersionReplacer(oldVersion, newVersion);
		final String filename1 = MogliReleaseBuilder.getApplicationRootDir() + "/" + TESTPOMFILE1;
		versionReplacer.replaceVersionInPom(filename1);
		
		final File file1 = new File(filename1);
		assertFileContainsEntry(file1, newVersion);
		assertFileContainsNoEntry(file1, oldVersion);
	}

	@Test
	public void testReplaceVersionInPom_oldVersionNotFound() throws IOException {
		final String oldVersion = "a.b.c";
		final String newVersion = "1.2.3";
		final VersionReplacer versionReplacer = new VersionReplacer(oldVersion, newVersion);
		final String filename1 = MogliReleaseBuilder.getApplicationRootDir() + "/" + TESTPOMFILE1;
		try {			
			versionReplacer.replaceVersionInPom(filename1);
		} catch (MogliCoreException e) {
			String message = e.getMessage().trim();
			assertStringStartsWith(message, "Unexpected number of version matches:");
			assertStringContains(message, "Expected matches of <a.b.c>: 1");
			assertStringEndsWith(message, "Actual matches: 0");
			return;
		}
		fail("Expected exception not thrown.");
	}
	
	@Test
	public void testReplaceInLines_zeroMatches() {
		final String oldVersion = "1.2";
		final String newVersion = "1.3";
		final VersionReplacer versionReplacer = new VersionReplacer(
				oldVersion, newVersion);

		final List<String> list = new ArrayList<String>();
		for (int i = 0; i < 15; i++) {
			list.add("aaa");
		}
		list.add("1.2");

		try {
			versionReplacer.replaceInLines(list);
		} catch (MogliCoreException e) {
			String message = e.getMessage().trim();
			assertStringStartsWith(message, "Unexpected number of version matches:");
			assertStringContains(message, "Expected matches of <1.2>: 1");
			assertStringEndsWith(message, "Actual matches: 0");
			return;
		}
		fail("Expected exception not thrown.");
	}


	@Test
	public void testReplaceInLines_twoMatches() {
		final String oldVersion = "1.2";
		final String newVersion = "1.3";
		final VersionReplacer versionReplacer = new VersionReplacer(
				oldVersion, newVersion);

		final List<String> list = new ArrayList<String>();
		list.add("1.2");
		list.add("1.2");
		for (int i = 0; i < 11; i++) {
			list.add("aaa");
		}

		try {
			versionReplacer.replaceInLines(list);
		} catch (MogliCoreException e) {
			String message = e.getMessage().trim();
			assertStringStartsWith(message, "Unexpected number of version matches:");
			assertStringContains(message, "Expected matches of <1.2>: 1");
			assertStringEndsWith(message, "Actual matches: 2");
			return;
		}
		fail("Expected exception not thrown.");
	}

	@Test
	public void testReplaceInLines_oneMatch() {
		final String oldVersion = "1.2";
		final String newVersion = "1.3";
		final VersionReplacer versionReplacer = new VersionReplacer(
				oldVersion, newVersion);

		final List<String> list = new ArrayList<String>();
		list.add("1.2");
		for (int i = 0; i < 15; i++) {
			list.add("aaa");
		}
		list.add("1.2");
		list.add("1.2");

		versionReplacer.replaceInLines(list);
	}

	@Test
	public void testDoYourJob() throws IOException {
		final String oldVersion = "0.0.1-SNAPSHOT";
		final String newVersion = "1.2.3";
		final String filename1 = MogliReleaseBuilder.getApplicationRootDir() + "/" + TESTPOMFILE1;
		final String filename2 = MogliReleaseBuilder.getApplicationRootDir() + "/" + TESTPOMFILE2;
		VersionReplacer.doYourJob(oldVersion, newVersion, filename1, filename2); 
		
		final File file1 = new File(filename1);
		assertFileContainsEntry(file1, newVersion);
		assertFileContainsNoEntry(file1, oldVersion);

		final File file2 = new File(filename2);
		assertFileContainsEntry(file2, newVersion);
		assertFileContainsNoEntry(file2, oldVersion);
	}

}
