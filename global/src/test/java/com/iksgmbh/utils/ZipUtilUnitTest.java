/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iksgmbh.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class ZipUtilUnitTest {
	
	public static final String USER_DIR = "../global/target/test-classes/";
	
	@Test
	public void testZipDir() throws Exception {
		// prepare test
		final String targetFilename = USER_DIR + "Test.zip";
		final File target = new File(targetFilename);
		target.delete();
		assertFalse("Target not deleted!", target.exists());
		
		// call functionality under test
		ZipUtil.zipDir(USER_DIR + "zipTestDir", targetFilename);
		
		// verify test result
		assertTrue("Target not created!", target.exists());
		assertEquals("Unexpected file size!", 6041, target.length());
	}
	
	@Test
	public void testUnzip() throws IOException {;
		// prepare test
		final String targetFilename = USER_DIR + "testUnzipDir";
		final File target = new File(targetFilename);
		FileUtil.deleteDirWithContent(target);
		assertFalse("Target not deleted!", target.exists());
		
		// call functionality under test
		final String filename = USER_DIR + "UnzipTest.zip";
		ZipUtil.unzip(filename, targetFilename);
		
		// verify test result
		assertTrue("Target does not exist!", target.exists());
		assertEquals("Unexpected file number!", 4, target.listFiles().length);
		final File subDir = FileUtil.getSubDir(target, "misc");
		assertEquals("Unexpected file number!", 2, subDir.listFiles().length);
	}
}