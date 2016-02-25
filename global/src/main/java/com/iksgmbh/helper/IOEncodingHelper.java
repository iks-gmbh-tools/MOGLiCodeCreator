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