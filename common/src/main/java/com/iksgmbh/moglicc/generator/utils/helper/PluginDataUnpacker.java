package com.iksgmbh.moglicc.generator.utils.helper;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.iksgmbh.moglicc.core.Logger;
import com.iksgmbh.moglicc.exceptions.MogliPluginException;
import com.iksgmbh.utils.FileUtil;

public class PluginDataUnpacker {
	
	private File targetDir;
	private Logger pluginLogger; 
	private PluginPackedData defaultData;
	
	public static void doYourJob(final PluginPackedData defaultData, 
			                     final File targetDir, 
			                     final Logger pluginLogger) throws MogliPluginException {
		final PluginDataUnpacker defaultDataUnpacker = new PluginDataUnpacker(defaultData, targetDir, pluginLogger);
		defaultDataUnpacker.unpack();
	}
	
	private PluginDataUnpacker(final PluginPackedData defaultData, final File targetDir, final Logger pluginLogger) {
		this.defaultData = defaultData;
		this.targetDir = targetDir;
		this.pluginLogger = pluginLogger;
	}	
	
	/**
	 * Currently the default data is supposed to be a list of directories 
	 * containing a list of files (no subdirs!)
	 */
	private void unpack() throws MogliPluginException {
		final Set<String> artefacts = defaultData.getArtefactList();
		for (final String artefact : artefacts) {
			final String[] filenames = defaultData.getFileNamesForArtefact(artefact);
			if (filenames != null) { // handle directory 
				createTemplateFiles(artefact, filenames);
				pluginLogger.logInfo(filenames.length + " template files created for " + artefact);
			} else { // handle file
				final String fileContent = readContentFromFile(defaultData.getTargetSubDir() + "/" + artefact);
				targetDir.mkdirs();
				createFile(targetDir, artefact, fileContent);
				pluginLogger.logInfo(artefact + " created.");
			}
		}
	}
	
	private void createTemplateFiles(final String subdirName, final String[] filenames) throws MogliPluginException {
		final File subInputDir = new File(targetDir, subdirName);
		subInputDir.mkdirs();
		for (String filename : filenames) {
			final String fileContent = readContentFromFile(defaultData.getTargetSubDir() + "/" + subdirName + "/" + filename);
			createFile(subInputDir, filename, fileContent);
		}
	}

	private void createFile(final File dir, final String filename, final String fileContent) throws MogliPluginException {
		final File file = new File(dir, filename);
		try {
			FileUtil.createFileWithContent(file, fileContent);
		} catch (Exception e) {
			throw new MogliPluginException("Error creating file\n" + file.getAbsolutePath(), e);
		}
	}

	private String readContentFromFile(final String pathToResource) throws MogliPluginException {
		try {
			return FileUtil.readTextResourceContentFromClassPath(defaultData.getClazz(), pathToResource);
		} catch (IOException e) {
			throw new MogliPluginException("Error reading file: " + pathToResource, e);
		}
	}

}
