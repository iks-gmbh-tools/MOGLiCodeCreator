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

import org.junit.Test;

public class CmdUtilUnitTest {

	public boolean isOPWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}

	@Test
	public void testExecWindowCommand() {
		if (isOPWindows()) {
			final String execCommand = "dir";
			final File dir = new File("C:\\");
			String execWindowCommand = CmdUtil.execWindowCommand(dir, execCommand, true);
			assertNotNull(execWindowCommand);
			assertTrue("Unexpected message", execWindowCommand.contains("Verzeichnis von C:\\"));
		}
	}

	@Test
	public void testExecWindowCommandWithError() 
	{
		if (isOPWindows()) {
			final String execCommand = "unknown";
			final File dir = new File("C:\\");
			try {
				CmdUtil.execWindowCommand(dir, execCommand, true);
			} catch (RuntimeException e) {
				assertTrue("Wrong Error message!", e.getMessage().trim().startsWith("Der Befehl \"unknown\" ist entweder falsch geschrieben oder"));
				System.err.println(e.getMessage());
				assertTrue("Wrong Error message!", e.getMessage().trim().endsWith("konnte nicht gefunden werden."));
				return;
			}
			fail("Expected exception not thrown!");
		}
	}
}