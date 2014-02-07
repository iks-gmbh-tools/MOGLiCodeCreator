package com.iksgmbh.moglicc.helper;

import static com.iksgmbh.moglicc.MOGLiSystemConstants.DIR_LIB_PLUGIN;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_DEACTIVATED_PLUGIN_INFO;
import static com.iksgmbh.moglicc.MOGLiTextConstants.*;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_NO_MANIFEST_FOUND;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_STARTERCLASS_MANIFEST_PROPERTIES;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.iksgmbh.moglicc.MOGLiCodeCreator;
import com.iksgmbh.moglicc.PluginMetaData;
import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.moglicc.exceptions.MissingManifestException;
import com.iksgmbh.moglicc.exceptions.MissingStarterclassException;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.StringUtil;

/**
 * Helps MOGLiCodeCreator class to do its job
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class MetaDataLoader {

	private Properties workspaceProperties;

	MetaDataLoader(final Properties workspaceProperties) {
		this.workspaceProperties = workspaceProperties;
	};

	public static List<PluginMetaData> doYourJob(final Properties workspaceProperties) {
		MOGLiLogUtil.logInfo("Searching for plugins...");
		MetaDataLoader metaDataLoader = new MetaDataLoader(workspaceProperties);
		List<PluginMetaData> availablePluginList = metaDataLoader.searchForAvailablePlugins();
		metaDataLoader.checkActivationPluginState(availablePluginList);
		return availablePluginList;
	}

	 List<PluginMetaData> searchForAvailablePlugins() {
		final File[] pluginJars = searchPluginJars();

		final List<PluginMetaData> pluginMetaDataList = new ArrayList<PluginMetaData>();

		for (int i = 0; i < pluginJars.length; i++) {
			String starterClassFromJar;
			try {
				starterClassFromJar = readStarterClassFromJar(pluginJars[i]);
			} catch (MissingManifestException e) {
				starterClassFromJar = PluginMetaData.NO_MANIFEST;
				MOGLiLogUtil.logWarning(TEXT_NO_MANIFEST_FOUND + " " + pluginJars[i]);
			} catch (MissingStarterclassException e) {
				starterClassFromJar = PluginMetaData.NO_STARTERCLASS;
				MOGLiLogUtil.logWarning(TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE + " " + pluginJars[i]);
			}
			PluginMetaData pluginMetaData = new PluginMetaData(pluginJars[i].getName(),
					                                           starterClassFromJar);
			pluginMetaDataList.add(pluginMetaData);
		}
		return pluginMetaDataList;
	}

	File[] searchPluginJars() {
		final File pluginDir = MOGLiFileUtil.getNewFileInstance(DIR_LIB_PLUGIN);
		File[] files = pluginDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dirOfFile, String name) {
				return name.endsWith(".jar");
			}
		});

		if (files == null) {
			files = new File[0];
		}
		MOGLiLogUtil.logInfo("Number of jar files in " + pluginDir.getAbsolutePath() + " found: " + files.length);
		return files;
	}

	 String readStarterClassFromJar(File pluginJar) {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(pluginJar);
		} catch (IOException e) {
			throw new MOGLiCoreException("Error reading " + pluginJar.getAbsolutePath());
		}
		 final Properties p = getPropertiesFromPluginManifest(jarFile);
		 String propertyStarterclass = p.getProperty(TEXT_STARTERCLASS_MANIFEST_PROPERTIES);
		 if (propertyStarterclass == null) {
			 throw new MissingStarterclassException();
		 }
		 return propertyStarterclass;
	}

	private Properties getPropertiesFromPluginManifest(final JarFile jarFile) {
		final ZipEntry entry = jarFile.getEntry(MOGLiPlugin.FILENAME_PLUGIN_JAR_PROPERTIES);
		if (entry == null) {
			 throw new MissingManifestException();
		}
		Properties p = new Properties();
		try {
			InputStream inputStream = jarFile.getInputStream(entry);
			p.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			throw new MOGLiCoreException("Error reading " + MOGLiPlugin.FILENAME_PLUGIN_JAR_PROPERTIES
					+ " from "+ jarFile.getName());
		}
		return p;
	}

	void checkActivationPluginState(List<PluginMetaData> pluginMetaDataList) {
		for (PluginMetaData pluginMetaData : pluginMetaDataList) {
			if (pluginMetaData.isStatusOK()) {
				final String jarName = FileUtil.removeFileExtension(pluginMetaData.getJarName());
				final String basicJarName = removeVersionFromFilename(jarName);
				final String activationProperty = workspaceProperties.getProperty(basicJarName);

				if (activationProperty == null) {
					// no setting in workspace properties, thus deactivate plugin by default
					pluginMetaData.setInfoMessage(TEXT_DEACTIVATED_PLUGIN_INFO);					
				}
				else if (! activationProperty.toUpperCase().equals(TEXT_ACTIVATED_PLUGIN_PROPERTY)) {
					// plugin deactivated by user setting
					pluginMetaData.setInfoMessage(TEXT_DEACTIVATED_PLUGIN_INFO);
				} else {
					// plugin is activated, thus do nothing here
				}

			} else {
				// no need to consider
			}
		}
	}

	private String removeVersionFromFilename(final String jarName) {
		String versionSuffix =  "-" + MOGLiCodeCreator.VERSION;
		if (! jarName.contains(versionSuffix)) {
			versionSuffix = versionSuffix.replace("-SNAPSHOT", "");
		}

		return StringUtil.removeSuffixIfExisting(jarName, versionSuffix);
	}

}