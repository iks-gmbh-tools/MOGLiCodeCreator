package com.iksgmbh.moglicc.build.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apache.maven.shared.invoker.InvocationResult;
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
		MavenData mavenData = new MavenData("clean compile", (new MOGLiReleaseBuilder()).getMavenRootDir(),
				new File(PROJECT_ROOT_DIR + "../cpsuite"));
		mavenExecutor = new MavenExecutor(mavenData);
	}

	@Test
	public void testCallMaven() {
		final InvocationResult mavenResult = mavenExecutor.callMaven();
		assertNotNull(mavenResult);
		System.out.println(mavenResult.getExecutionException());
		assertEquals("Exit Code", 0, mavenResult.getExitCode());
	}
}
