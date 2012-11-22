package com.iksgmbh.moglicc.test;

import static com.iksgmbh.moglicc.MogliSystemConstants.DIR_INPUT_FILES;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.MogliCodeCreator;
import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.infrastructure.MogliInfrastructure;
import com.iksgmbh.moglicc.plugin.PluginExecutable;
import com.iksgmbh.moglicc.provider.model.standard.StandardModelProviderStarter;
import com.iksgmbh.utils.ImmutableUtil;

public class StandardModelProviderTestParent extends AbstractMogliTest {

	protected static final String PROJECT_ROOT_DIR = "../provider.model.standard/";
	private static boolean isFirstTest = true;
	
	protected MogliInfrastructure infrastructure;
	protected File modelTextfile;
	

	@Override
	public void setup() {
		super.setup();
		final File pluginInputDir = new File(applicationRootDir + "/" + DIR_INPUT_FILES + "/" 
				+ StandardModelProviderStarter.PLUGIN_ID); 
		if (isFirstTest) {
			isFirstTest = false;
			initForFirstUnitTest();
			pluginInputDir.mkdirs();
		}
		modelTextfile = new File(pluginInputDir, StandardModelProviderStarter.FILENAME_STANDARD_MODEL_TEXTFILE);
		modelTextfile.delete();
		final List<PluginExecutable> emptyImmutableList = ImmutableUtil.getEmptyImmutableListOf(null);
		final InfrastructureInitData initInfrastructureData = 
			createInfrastructureInitData(null, emptyImmutableList, StandardModelProviderStarter.PLUGIN_ID);
		infrastructure = new MogliInfrastructure(initInfrastructureData);
	}
	
	@Override
	protected String getProjectRootDir() {
		return PROJECT_ROOT_DIR;
	}

	@Override
	protected String initTestApplicationRootDir() {
		final String applicationRootDir = PROJECT_ROOT_DIR + TEST_SUBDIR;
		MogliCodeCreator.setApplicationRootDir(applicationRootDir);
		return applicationRootDir;
	}
	
	protected List<String> getFileContent(final String... lines) {
		final List<String> fileContentAsList = new ArrayList<String>();
		for (String line : lines) {
			fileContentAsList.add(line);
		}
		return fileContentAsList;
	}
}
