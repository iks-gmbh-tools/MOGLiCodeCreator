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
package com.iksgmbh.moglicc.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.iksgmbh.utils.FileUtil;

public class TestUtil {
	
	public static String getFileContent(final File file) {
		try {
			return FileUtil.getFileContent(file);
		} catch (IOException e) {
			throw new RuntimeException("Error reading file " + file.getName(), e);
		}	
	}
	
	public static List<String> getFileContentAsList(final File file) {
		try {
			return FileUtil.getFileContentAsList(file);
		} catch (IOException e) {
			throw new RuntimeException("Error reading file " + file.getName(), e);
		}	
	}
}