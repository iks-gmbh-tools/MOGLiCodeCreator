package com.iksgmbh.moglicc.build.test;

import com.iksgmbh.moglicc.MogliCodeCreator;
import com.iksgmbh.moglicc.test.AbstractMogliTest;

public class ApplicationTestParent extends AbstractMogliTest {

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
		MogliCodeCreator.setApplicationRootDir(applicationRootDir);
		return applicationRootDir;
	}

}
