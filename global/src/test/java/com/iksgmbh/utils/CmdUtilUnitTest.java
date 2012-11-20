package com.iksgmbh.utils;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class CmdUtilUnitTest {
	
	@Test
	public void testExecWindowCommand() {
		final String execCommand = "dir" ;
		final File dir = new File("C:\\");
		String execWindowCommand = CmdUtil.execWindowCommand(dir, execCommand, true);
		assertNotNull(execWindowCommand);
		assertTrue("Unexpected message", execWindowCommand.contains("Verzeichnis von C:\\"));
	}

	@Test
	public void testExecWindowCommandWithError() {
		final String execCommand = "unknown" ;
		final File dir = new File("C:\\");
		try {
			CmdUtil.execWindowCommand(dir, execCommand, true);
		} catch (RuntimeException e) {
			assertTrue("Wrong Error message!", e.getMessage().trim().startsWith(
					"Der Befehl \"unknown\" ist entweder falsch geschrieben oder"));
			assertTrue("Wrong Error message!", e.getMessage().trim().endsWith(
					"konnte nicht gefunden werden."));		
			return;
		}
		fail("Expected exception not thrown!");
	}
}
