package com.iksgmbh.moglicc.build.helper;

import java.io.File;

import com.iksgmbh.utils.CmdUtil;

public class MavenExecutor {
	
	public static final String EXECUTION_OK = "ok";
	private MavenData mavenData;

	public MavenExecutor(MavenData mavenData) {
		this.mavenData = mavenData;
	}

	public static String doYourJob(MavenData mavenData) {
		MavenExecutor mavenExecutor = new MavenExecutor(mavenData);
		final String result = mavenExecutor.callMaven();
		if (result.contains("FAILURE")) {
			return result;
		}
		return EXECUTION_OK;
	}
	
	String callMaven() {
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

}
