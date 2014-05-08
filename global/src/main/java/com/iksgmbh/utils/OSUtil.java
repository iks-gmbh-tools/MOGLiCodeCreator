package com.iksgmbh.utils;

public class OSUtil {

	public static boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}
}
