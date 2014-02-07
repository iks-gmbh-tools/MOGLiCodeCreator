package com.iksgmbh.moglicc.build.helper;

import java.io.File;
import java.util.List;

import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.utils.FileUtil;

public class ReleaseFileCollector {
	
	private FileCollectionData fileData;
	private File libDir;
	private File pluginDir;

	ReleaseFileCollector(FileCollectionData fileData) {
		this.fileData = fileData;
	}

	public static void doYourJob(FileCollectionData fileData) {
		ReleaseFileCollector fileCollector = new ReleaseFileCollector(fileData);
		fileCollector.initReleaseDir();
		fileCollector.copyFileIntoRootDir();
		fileCollector.createLibDirectory();
		fileCollector.copyCoreJarFiles();
		fileCollector.createPluginDirectory();
		fileCollector.copyPluginJarFiles();
		fileCollector.copyThirdPartyJars();
	}

	void initReleaseDir() {
		FileUtil.deleteDirWithContent(fileData.releaseDir);
		fileData.releaseDir.mkdirs();
	}
	

	public static class FileCollectionData {
		public String libSubdir;
		public String pluginsSubdir;
		public File sourceDir;
		public File releaseDir;
		public List<String> fileListForRootDir;
		public File[] jarsOfCoreComponents;
		public File[] jarsOfPlugins;
		public File[] thirdPartyJars;
	}

	void copyFileIntoRootDir() {
		if (fileData.fileListForRootDir == null) {
			return;
		}
		for (String filename : fileData.fileListForRootDir) {
			final File file = new File(fileData.sourceDir + "/" + filename);
			if (file.exists()) {
				FileUtil.copyTextFile(file, fileData.releaseDir.getAbsolutePath());
			} else {
				throw new MOGLiCoreException("File does not exist: " + file.getAbsolutePath());

			}
		}
	}

	void createLibDirectory() 
	{
		libDir = new File(fileData.releaseDir + "/" + fileData.libSubdir);
		if (! libDir.exists()) {
			boolean ok = libDir.mkdirs();
			if (! ok) {
				throw new MOGLiCoreException("Directory not created: " + libDir.getAbsolutePath());
			}
		}
	}
	
	void createPluginDirectory() {
		pluginDir = new File(fileData.releaseDir + "/" + fileData.libSubdir 
				                                 + "/" + fileData.pluginsSubdir);
		if (! pluginDir.exists()) {			
			boolean ok = pluginDir.mkdirs();
			if (! ok) {
				throw new MOGLiCoreException("Directory not created: " + fileData.pluginsSubdir);
			}
		}
	}

	void copyCoreJarFiles() {
		for (int i = 0; i < fileData.jarsOfCoreComponents.length; i++) {
			File file = fileData.jarsOfCoreComponents[i];
			if (! file.exists()) {
				file = new File(cutSnapshot(file.getAbsolutePath()));
				if (! file.exists()) {
					throwMissingJarFileException(file);
				}
			}
			FileUtil.copyBinaryFile(file.getAbsolutePath(), libDir.getAbsolutePath());
		}
	}

	protected void throwMissingJarFileException(final File file) {
		throw new MOGLiCoreException("File does not exist: " 
				                     + FileUtil.getSystemLineSeparator()
				                     + file.getAbsolutePath()
                                     + FileUtil.getSystemLineSeparator()
                                     + "Call 'maven install' on parent project!");
	}
	
	void copyPluginJarFiles() {
		for (int i = 0; i < fileData.jarsOfPlugins.length; i++) {
			File file = fileData.jarsOfPlugins[i];
			if (! file.exists()) {
				file = new File(cutSnapshot(file.getAbsolutePath()));
				if (! file.exists()) {
					throwMissingJarFileException(file);
				}
			}
			FileUtil.copyBinaryFile(file.getAbsolutePath(), pluginDir.getAbsolutePath());
		}
	}

	void copyThirdPartyJars() {
		for (int i = 0; i < fileData.thirdPartyJars.length; i++) {
			final File file = fileData.thirdPartyJars[i];
			if (file.exists()) {
				FileUtil.copyBinaryFile(file.getAbsolutePath(), libDir.getAbsolutePath());
			} else {
				throwMissingJarFileException(file);
			}
		}
	}
	
	private String cutSnapshot(final String filename)  {
		return filename.replace("-SNAPSHOT", "");
	}

}
