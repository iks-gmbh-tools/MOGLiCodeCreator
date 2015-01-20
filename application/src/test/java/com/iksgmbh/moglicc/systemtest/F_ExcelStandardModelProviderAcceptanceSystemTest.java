package com.iksgmbh.moglicc.systemtest;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_INPUT_FILES;

import java.io.File;

import org.junit.Test;

import com.iksgmbh.moglicc.provider.model.standard.excel.ExcelStandardModelProviderStarter;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.utils.FileUtil;

public class F_ExcelStandardModelProviderAcceptanceSystemTest extends __AbstractSystemTest {
	
	private static final String PROVIDER_PLUGIN_ID = ExcelStandardModelProviderStarter.PLUGIN_ID;

	// *****************************  test methods  ************************************
		
	@Test
	public void createsDefaultPropertiesFileOfExcelStandardModelProvider() {
		// prepare test
		final File modelDir = new File(testDir + "/" + DIR_INPUT_FILES + "/" + PROVIDER_PLUGIN_ID); 
		FileUtil.deleteDirWithContent(modelDir);
		assertFileDoesNotExist(modelDir);
		
		// call functionality under test
		executeMogliApplication();
		
		// verify test result
		final File modelFile = new File(modelDir, ExcelStandardModelProviderStarter.FILENAME_STANDARD_EXCEL_FILE); 
		assertFileExists(modelFile);
		String fileContent = MOGLiFileUtil.getFileContent(modelFile);
		assertFileContainsEntry(modelFile, fileContent);
	}
	
	@Test
	public void createsHelpData() {
		// prepare test
		final File pluginHelpDir = new File(applicationHelpDir, PROVIDER_PLUGIN_ID); 
		FileUtil.deleteDirWithContent(applicationHelpDir);
		assertFileDoesNotExist(pluginHelpDir);
		
		// call functionality under test
		executeMogliApplication();
		
		// verify test result
		assertFileExists(pluginHelpDir);
		assertChildrenNumberInDirectory(pluginHelpDir, 1);
	}
	
	@Test
	public void createsPluginLogFile() {
		// prepare test
		FileUtil.deleteDirWithContent(applicationLogDir);
		final File pluginLogFile = new File(applicationLogDir, PROVIDER_PLUGIN_ID + ".log");
		assertFileDoesNotExist(pluginLogFile);
		
		// call functionality under test
		executeMogliApplication();
		
		// verify test result
		assertFileExists(pluginLogFile);
	}
}
