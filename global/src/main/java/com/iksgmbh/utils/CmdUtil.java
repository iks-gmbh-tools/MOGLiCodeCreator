package com.iksgmbh.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CmdUtil {
	
	public static String[] getEnvParams() {
		Map<String, String> envParamMap = System.getenv();
		String[] envParamArray = new String[envParamMap.size() + 1];
		Set<String> keySet = envParamMap.keySet();
		int counter = 0;
		for (Iterator<?> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			envParamArray[counter] = key + "=" + envParamMap.get(key);
			counter++;
		}
		envParamArray[counter] = "MAVEN_OPTS=-Xmx256m";
		return envParamArray;
	}
	
	public static String execWindowCommand(File parentProject, String exeCommand, boolean sysOutMessage) {
		final String[] cmdstart = { "cmd.exe", "/c", exeCommand};
		ProcessResultMessages messages = null;
		
		try {
			Process execProcess= Runtime.getRuntime().exec(cmdstart, getEnvParams(), parentProject);
			messages = readMessagesFromExecProcess(execProcess);
			Thread.sleep(100); // give time to read messages
		}
		catch (Exception e) {
			String errorMessage = "Aufruf von '" + exeCommand + "' in '" + parentProject.getAbsolutePath() + "' fehlgeschlagen!";
			throw new RuntimeException(errorMessage, e);
		}	
		
		if (messages.errorMessage != null) {
			throw new RuntimeException(messages.errorMessage);
		}
		
		if (sysOutMessage && messages.infoMessage != null) {
			System.out.println(messages.infoMessage);
		}
		
		return messages.infoMessage;
	}

	public static String getMessageFromStream(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = "no content";
		StringBuffer message = new StringBuffer();
		while (line != null) {
			line = reader.readLine();
			if (line != null)
				message.append("\r\n" + line);
		}
		return message.toString();
	}

	private static ProcessResultMessages readMessagesFromExecProcess(Process execProcess) throws IOException {
		ProcessResultMessages toReturn = new ProcessResultMessages();
		try {
			// Give runtime process time to build and fill streams...
			Thread.sleep(500);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		InputStream errorStream = execProcess.getErrorStream();
		if (errorStream.available() > 0) {
			toReturn.errorMessage = getMessageFromStream(errorStream);
			return toReturn;
		}
		String message = getMessageFromStream(execProcess.getInputStream());
		if (message.contains("ERROR")) {
			if (message.contains("BUILD SUCCESSFUL")) {
				message = extractMavenSummary(message);
				toReturn.infoMessage = "Note: Maven possibly detected a problem, " +
						"however the build was successful!\n" + message;
			} else {
				toReturn.infoMessage = message;
			}
		} else {
			toReturn.infoMessage = message;
		}
		return toReturn;
	}

	private static String extractMavenSummary(String message) {
		String[] lines = message.split("\r\n");
		String extract = null;
		boolean summaryFound = false;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (summaryFound) {
				extract += "\r\n" + line;
			}
			else if (line.trim().startsWith("[INFO] Reactor Summary:")) {
				extract = lines[i-1] + "\r\n" + line;
				summaryFound = true;
			} 
		}
		if (! summaryFound) {
			extract = message;  // on extract found -> return full message
		}
		return extract;
	}

	static class ProcessResultMessages {
		String infoMessage;
		String errorMessage;
	}


}
