package com.iksgmbh.moglicc.generator.utils.helper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.iksgmbh.moglicc.core.Logger;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.utils.FileUtil;

public class PluginDataUnpacker {
	
	private File targetDir;
	private Logger pluginLogger; 
	private PluginPackedData defaultData;
	
	public static void doYourJob(final PluginPackedData defaultData, 
			                     final File targetDir, 
			                     final Logger pluginLogger) throws MOGLiPluginException {
		final PluginDataUnpacker defaultDataUnpacker = new PluginDataUnpacker(defaultData, targetDir, pluginLogger);
		targetDir.mkdirs();
		defaultDataUnpacker.unpack();
	}
	
	private PluginDataUnpacker(final PluginPackedData defaultData, final File targetDir, final Logger pluginLogger) {
		this.defaultData = defaultData;
		this.targetDir = targetDir;
		this.pluginLogger = pluginLogger;
	}	
	
	private void unpack() throws MOGLiPluginException {
		// create subdirs
		final List<String> subdirs = defaultData.getSubdirs();
		for (final String subdir : subdirs) {
			new File(targetDir, subdir).mkdirs();
		}

		// unpack files
		final Set<String> files = defaultData.getFiles();
		for (final String file : files) {
			unpackFile(file, defaultData.getSubdirToFile(file));
		}

		// unpack flat folders
		final Set<String> flatFolders = defaultData.getFlatFolders();
		for (final String flatFolder : flatFolders) {
			unpackFlatFolder(flatFolder);
		}

		// unpack root files
		final List<String> rootInputFiles = defaultData.getRootInputFileList();
		for (final String rootInputFile : rootInputFiles) {
			unpackRootFiles(rootInputFile);
		}
	}

	private void unpackFile(final String fileName, final String subdir) throws MOGLiPluginException {
		final File subInputDir;
		final String path;
		if ("".equals(subdir)) {
			subInputDir = targetDir;
			path = "";
		} else {
			subInputDir = new File(targetDir, subdir);
			path = "/" + subdir;
		}
		subInputDir.mkdirs();
		final String fileContent = readContentFromFile(defaultData.getTargetSubDir() + path + "/" + fileName);
		createFile(subInputDir, fileName, fileContent);
	}

	/**
	 * See readmeClasspathProblem.txt for information why the root file problem exist. 
	 * @param rootInputFile file to unpack into the root of the plugin input dir
	 * @throws MOGLiPluginException
	 */
	private void unpackRootFiles(final String rootInputFile) throws MOGLiPluginException {
		final String pathToResource = defaultData.getTargetSubDir() + "/" + defaultData.getSubdirForRootInputFiles() + "/" + rootInputFile;
		final String fileContent = readContentFromFile(pathToResource);
		if (fileContent == null) {
			throw new MOGLiPluginException("Error unpacking embedded resource file from jar. " +
					                       "Cannot find: " + pathToResource);
		}
		createFile(targetDir, rootInputFile, fileContent);
		pluginLogger.logInfo(rootInputFile + " created.");
	}

	private void unpackFlatFolder(final String folderName) throws MOGLiPluginException {
		final String[] filenames = defaultData.getFileNamesForFlatFolder(folderName);
		if (filenames == null) {
			throw new MOGLiPluginException("No files defined for flat folder " + folderName);
		} else {
			final File subInputDir = new File(targetDir, folderName);
			subInputDir.mkdirs();
			for (String filename : filenames) {
				final String fileContent = readContentFromFile(defaultData.getTargetSubDir() + "/" + folderName + "/" + filename);
				createFile(subInputDir, filename, fileContent);
			}
		}
	}
	
	private void createFile(final File dir, final String filename, final String fileContent) throws MOGLiPluginException {
		final File file = new File(dir, filename);
		try {
			FileUtil.createNewFileWithContent(file, fileContent);
		} catch (Exception e) {
			throw new MOGLiPluginException("Error creating file\n" + file.getAbsolutePath(), e);
		}
	}

	private String readContentFromFile(final String pathToResource) throws MOGLiPluginException {
		try {
			return FileUtil.readTextResourceContentFromClassPath(defaultData.getClazz(), pathToResource);
		} catch (IOException e) {
			throw new MOGLiPluginException("Error reading file: " + pathToResource, e);
		}
	}

}
