package com.iksgmbh.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {
	
	public static String getStackTraceAsString(Throwable t) {
		final StringWriter writer = new StringWriter();
		final PrintWriter out = new PrintWriter(writer);
		t.printStackTrace(out);
		return writer.getBuffer().toString();
	}
}
