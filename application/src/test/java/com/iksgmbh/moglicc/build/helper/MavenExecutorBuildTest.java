package com.iksgmbh.moglicc.build.helper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.build.MOGLiReleaseBuilder;
import com.iksgmbh.moglicc.build.helper.MavenExecutor.MavenData;
import com.iksgmbh.moglicc.build.test.ApplicationTestParent;

public class MavenExecutorBuildTest extends ApplicationTestParent {

	private MavenExecutor mavenExecutor;

	@Before
	public void setup() {
		super.setup();
		MavenData mavenData = new MavenData("compile", (new MOGLiReleaseBuilder()).getMavenRootDir(),
				new File(PROJECT_ROOT_DIR + "../global"));
		mavenExecutor = new MavenExecutor(mavenData);
	}

	@Test
	public void testCallMaven() {
		String mavenResult = mavenExecutor.callMaven();
		assertNotNull(mavenResult);
		System.out.println(mavenResult);
		assertTrue("Unexpected mavenResult: " + mavenResult, mavenResult.contains("[INFO] Building Global"));
		assertTrue("Maven Build not successful: " + mavenResult, mavenResult.contains("[INFO] BUILD SUCCESS"));
	}
}
