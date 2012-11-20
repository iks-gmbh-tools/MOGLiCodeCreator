package com.iksgmbh.moglicc.systemtest;

import static com.iksgmbh.moglicc.MogliSystemConstants.DIR_INPUT_FILES;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.iksgmbh.moglicc.exceptions.MogliCoreException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityClassBasedGeneratorStarter;
import com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants;
import com.iksgmbh.moglicc.provider.model.standard.StandardModelProviderStarter;
import com.iksgmbh.data.ClassNameData;
import com.iksgmbh.utils.FileUtil;

public class D_VelocityModelBasedGeneratorAcceptanceSystemTest extends _AbstractSystemTest {
	
	public static final String GENERATOR_PLUGIN_ID = VelocityClassBasedGeneratorStarter.PLUGIN_ID;
	

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
		final List<String> classnamesFromModelfile = readClassnameFromModelfile();
		final File pluginOutputDir = new File(applicationOutputDir, GENERATOR_PLUGIN_ID 
				                              + "/" + VelocityClassBasedGeneratorStarter.ARTEFACT_JAVABEAN);
		assertChildrenNumberInDirectory(pluginOutputDir, classnamesFromModelfile.size());
		for (String classname : classnamesFromModelfile) {
			assertFileExists(new File(pluginOutputDir, classname + ".java"));
		}
	}

	private List<String> readClassnameFromModelfile() throws IOException {
		final File modelDir = new File(testDir + "/" + DIR_INPUT_FILES + "/" + StandardModelProviderStarter.PLUGIN_ID);
		List<String> fileContentAsList = FileUtil.getFileContentAsList(new File(modelDir, StandardModelProviderStarter.FILENAME_STANDARD_MODEL_TEXTFILE));
		final List<String> list = new ArrayList<String>();
		for (String line : fileContentAsList) {
			line = line.trim();
			if (line.startsWith(MetaModelConstants.CLASS_IDENTIFIER)) {
				String[] splitResult = line.split(" ");
				if (splitResult.length < 2) {
					throw new MogliCoreException("Error reading model file");
				}
				final ClassNameData classNameData = new ClassNameData(splitResult[1]);
				list.add(classNameData.getSimpleClassName());
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
}
