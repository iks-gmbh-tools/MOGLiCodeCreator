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
				new File(PROJECT_ROOT_DIR + "../global"));
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