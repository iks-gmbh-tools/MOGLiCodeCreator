package com.iksgmbh.moglicc.build.test;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.test.AbstractMOGLiTest;

public class ApplicationTestParent extends AbstractMOGLiTest {

	private static final String PROJECT_ROOT_DIR = "../application/";

	@Override
	public void setup() {
		super.setup();
	}

	@Override
	protected String getProjectRootDir() {
		return PROJECT_ROOT_DIR;
	}

	@Override
	protected String initTestApplicationRootDir() {
		final String applicationRootDir = PROJECT_ROOT_DIR + TEST_SUBDIR;
		MOGLiCodeCreator.setApplicationRootDir(applicationRootDir);
		return applicationRootDir;
	}

}
