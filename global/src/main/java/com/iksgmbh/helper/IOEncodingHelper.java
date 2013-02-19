package com.iksgmbh.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class IOEncodingHelper {

	private static final String STANDARD_ENCODING = "UTF-8";

	public static final IOEncodingHelper STANDARD = getInstance(STANDARD_ENCODING);

	private String encoding = STANDARD_ENCODING;

	private IOEncodingHelper(String encoding) {
		this.encoding = encoding;
	}

	public static IOEncodingHelper getInstance(final String encoding) {
		return new IOEncodingHelper(encoding);
	}

	public String getEncoding() {
		return encoding;
	}

	public BufferedReader getBufferedReader(final File file) throws IOException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
	}

	public BufferedWriter getBufferedWriter(final File file) throws IOException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
	}

	public BufferedReader getBufferedReader(final InputStream inputStream) throws IOException {
		return new BufferedReader(new InputStreamReader(inputStream, encoding));
	}

	public OutputStreamWriter getOutputStreamWriter(final File file) throws IOException {
		return new OutputStreamWriter(new FileOutputStream(file), encoding);
	}


}
