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

import java.io.File;
import java.util.Collections;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

import com.iksgmbh.utils.CmdUtil;

public class MavenExecutor {
	
	public static final String EXECUTION_OK = "ok";
	private MavenData mavenData;

	public MavenExecutor(MavenData mavenData) {
		this.mavenData = mavenData;
	}

	public static String doYourJob(MavenData mavenData) {
		final MavenExecutor mavenExecutor = new MavenExecutor(mavenData);
		final InvocationResult result = mavenExecutor.callMaven();
		if ( result.getExitCode() != 0 )
		{
		    return "Build failed due to unexpected exit code " 
		           + result.getExitCode() + "ExecutionException: " + result.getExecutionException(); 
		}
		return EXECUTION_OK;
	}
	
	String callMavenOld() {
		final String mavenHome = mavenData.mavenRootDir;
		String exeCommand = mavenHome + "/bin/mvn " + mavenData.mavenTargetString 
		                    + " --settings " + mavenHome + "/conf/settings.xml " 
		                    + mavenData.mavenBuildOptions;
		if (mavenData.skipTest) {
			exeCommand += " -Dmaven.test.skip=true"; 
		}
		System.out.println("Calling command\n" + exeCommand 
				           + "\n in \n" + mavenData.parentBuildDir.getAbsolutePath());
		return CmdUtil.execWindowCommand(mavenData.parentBuildDir, exeCommand, true);
	}
	
	InvocationResult callMaven() 
	{
		final InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile( new File( mavenData.parentBuildDir, "pom.xml" ) );
		request.setGoals( Collections.singletonList( mavenData.mavenTargetString ) );
		final Invoker invoker = new DefaultInvoker();
		invoker.setMavenHome(new File(mavenData.mavenRootDir));
		InvocationResult result;
		try {
			result = invoker.execute( request );
		} catch (MavenInvocationException e) {
			throw new IllegalStateException( "Build failed due to error in execution." );
		}

		return result;
	}
	
	public static class MavenData {
		String mavenTargetString;
		String mavenRootDir;
		File parentBuildDir;
		String mavenBuildOptions = ""; // "-Dmaven.test.failure.ignore=true"
		boolean skipTest = false;

		public MavenData(String mavenTargetString, String mavenRootDir, File parentBuildDir) {
			this.mavenTargetString = mavenTargetString;
			this.mavenRootDir = mavenRootDir;
			this.parentBuildDir = parentBuildDir;
		}
	}
	
	public static void main(String[] args) {
		File parentBuildDir = new File("C:/dev/eclipse/workspaces/mogli/parent");
		MavenData data = new MavenData("clean install", "C:/dev/maven/apache-maven-3.0.4", parentBuildDir );
		MavenExecutor.doYourJob(data);
		//new MavenExecutor(data).callMaven();
	}

}