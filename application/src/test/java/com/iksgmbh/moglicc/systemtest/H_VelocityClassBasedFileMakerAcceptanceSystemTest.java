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
package com.iksgmbh.moglicc.systemtest;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_INPUT_FILES;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.iksgmbh.data.ClassNameData;
import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.moglicc.filemaker.classbased.velocity.VelocityClassBasedFileMakerStarter;
import com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants;
import com.iksgmbh.moglicc.provider.model.standard.StandardModelProviderStarter;
import com.iksgmbh.moglicc.provider.model.standard.parser.ModelParser;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class H_VelocityClassBasedFileMakerAcceptanceSystemTest extends __AbstractSystemTest {

	public static final String GENERATOR_PLUGIN_ID = VelocityClassBasedFileMakerStarter.PLUGIN_ID;


	// *****************************  test methods  ************************************

	@Test
	public void createsPluginLogFile() {
		// prepare test
		FileUtil.deleteDirWithContent(applicationLogDir);
		final File pluginLogFile = new File(applicationLogDir, GENERATOR_PLUGIN_ID + ".log");
		assertFileDoesNotExist(pluginLogFile);

		// call functionality under test
		executeMogliApplication();

		// verify test result
		assertFileExists(pluginLogFile);
	}

	@Test
	public void createsGeneratorResultFiles() throws IOException {
		// prepare test
		FileUtil.deleteDirWithContent(applicationOutputDir);
		assertFileDoesNotExist(applicationOutputDir);

		// call functionality under test
		executeMogliApplication();

		// verify test result
		assertFileExists(applicationOutputDir);
		assertAllResultFileCreated();
	}

	private void assertAllResultFileCreated() throws IOException {
		final List<String> classnamesFromModelfile = readAllClassnamesFromModelfile();
		final File pluginOutputDir = new File(applicationOutputDir, GENERATOR_PLUGIN_ID
				                              + "/" + VelocityClassBasedFileMakerStarter.ARTEFACT_JAVABEAN);
		assertChildrenNumberInDirectory(pluginOutputDir, classnamesFromModelfile.size());
		for (String classname : classnamesFromModelfile) {
			assertFileExists(new File(pluginOutputDir, classname + ".java"));
		}
	}

	private String doVariableReplacement(String line, final HashMap<String, String> variableMap) {
		final Set<String> keySet = variableMap.keySet();
		for (final String key : keySet)
		{
			final String placeholder = ModelParser.VARIABLE_START_INDICATOR + key + ModelParser.VARIABLE_END_INDICATOR;
			line = line.replace(placeholder, variableMap.get(key));
		}
		return line;
	}
	
	private List<String> readAllClassnamesFromModelfile() throws IOException 
	{
		final HashMap<String, String> variableMap = new HashMap<String, String>();
		final File modelDir = new File(testDir + "/" + DIR_INPUT_FILES + "/" + StandardModelProviderStarter.PLUGIN_ID);
		List<String> fileContentAsList = FileUtil.getFileContentAsList(new File(modelDir, StandardModelProviderStarter.FILENAME_STANDARD_MODEL_FILE));
		final List<String> list = new ArrayList<String>();
		for (String line : fileContentAsList) {
			line = line.trim();
			if (line.startsWith(MetaModelConstants.CLASS_IDENTIFIER)) {
				final String[] splitResult = line.split(" ");
				if (splitResult.length < 2) {
					throw new MOGLiCoreException("Error reading model file");
				}
				final String className = doVariableReplacement(splitResult[1], variableMap);
				final ClassNameData classNameData = new ClassNameData(className);
				list.add(classNameData.getSimpleClassName());
			}
			if (line.startsWith(MetaModelConstants.VARIABLE_IDENTIFIER)) {
				final String[] splitResult = line.split(" ");
				if (splitResult.length < 3) {
					throw new MOGLiCoreException("Error reading model file");
				}
				variableMap.put(splitResult[1], splitResult[2]);
			}
		}
		return list;
	}


	@Test
	public void createsHelpData() {
		// prepare test
		final File pluginHelpDir = new File(applicationHelpDir, GENERATOR_PLUGIN_ID);
		FileUtil.deleteDirWithContent(applicationHelpDir);
		assertFileDoesNotExist(pluginHelpDir);

		// call functionality under test
		executeMogliApplication();

		// verify test result
		assertFileExists(pluginHelpDir);
		assertChildrenNumberInDirectory(pluginHelpDir, 1);
	}

	@Test
	public void createsOutputFilesWithASCIIEncodingReadFromMainTemplate() throws Exception {
		// prepare test
		final File templateFile = prepareArtefactDirectory("main.tpl", GENERATOR_PLUGIN_ID, "myNewArtefact");
		MOGLiFileUtil.createNewFileWithContent(templateFile, "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@TargetFileName UmlautTest.txt" + FileUtil.getSystemLineSeparator() +
                "@TargetDir "  + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER + "/example" + FileUtil.getSystemLineSeparator() +
                "@OutputEncodingFormat ASCII" + FileUtil.getSystemLineSeparator() + "äüößÜÖÄ");

		// call functionality under test
		executeMogliApplication();

		// verify test result
		final File outputFile = new File(applicationOutputDir, GENERATOR_PLUGIN_ID + "/myNewArtefact/UmlautTest.txt");
		assertStringEquals("outputFileContent", "???????", MOGLiFileUtil.getFileContent(outputFile));
	}

	@Test
	public void createsOutputFilesWithDefaultEncodingReadFromMainTemplate() throws Exception {
		// prepare test
		final File templateFile = prepareArtefactDirectory("main.tpl", GENERATOR_PLUGIN_ID, "myNewArtefact");

		MOGLiFileUtil.createNewFileWithContent(templateFile, "@CreateNew true" + FileUtil.getSystemLineSeparator() +
                "@TargetFileName UmlautTest.txt" + FileUtil.getSystemLineSeparator() +
                "@TargetDir "  + MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER + "/example" + FileUtil.getSystemLineSeparator() +
                "äüößÜÖÄ");

		// call functionality under test
		executeMogliApplication();

		// verify test result
		final File outputFile = new File(applicationOutputDir, GENERATOR_PLUGIN_ID + "/myNewArtefact/UmlautTest.txt");
		assertStringEquals("outputFileContent", "äüößÜÖÄ", MOGLiFileUtil.getFileContent(outputFile));
	}

}