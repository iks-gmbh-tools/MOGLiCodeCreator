package com.iksgmbh.moglicc.helper;

import static com.iksgmbh.moglicc.MOGLiSystemConstants2.DIR_LIB_PLUGIN;
import static com.iksgmbh.moglicc.MOGLiTextConstants2.TEXT_DEACTIVATED_PLUGIN_INFO;
import static com.iksgmbh.moglicc.MOGLiTextConstants2.TEXT_DEACTIVATED_PLUGIN_PROPERTY;
import static com.iksgmbh.moglicc.MOGLiTextConstants2.TEXT_NO_MANIFEST_FOUND;
import static com.iksgmbh.moglicc.MOGLiTextConstants2.TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE;
import static com.iksgmbh.moglicc.MOGLiTextConstants2.TEXT_STARTERCLASS_MANIFEST_PROPERTIES;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.iksgmbh.moglicc.PluginMetaData;
import com.iksgmbh.moglicc.exceptions.MOGLiCoreException2;
import com.iksgmbh.moglicc.exceptions.MissingManifestException;
import com.iksgmbh.moglicc.exceptions.MissingStarterclassException;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin2;
import com.iksgmbh.moglicc.utils.MOGLiFileUtil2;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil2;
import com.iksgmbh.utils.FileUtil;

/**
 * Helps MOGLiCodeCreator class to do its job
 * @author Reik Oberrath
 */
public class MetaDataLoader {

	private Properties applicationProperties;

	MetaDataLoader(final Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	};
	
	public static List<PluginMetaData> doYourJob(final Properties applicationProperties) {
		MOGLiLogUtil2.logInfo("Searching for plugins...");
		MetaDataLoader metaDataLoader = new MetaDataLoader(applicationProperties);
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
				MOGLiLogUtil2.logWarning(TEXT_NO_MANIFEST_FOUND + " " + pluginJars[i]);
			} catch (MissingStarterclassException e) {
				starterClassFromJar = PluginMetaData.NO_STARTERCLASS;
				MOGLiLogUtil2.logWarning(TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE + " " + pluginJars[i]);
			}
			PluginMetaData pluginMetaData = new PluginMetaData(pluginJars[i].getName(), 
					                                           starterClassFromJar);
			pluginMetaDataList.add(pluginMetaData);
		}
		return pluginMetaDataList;
	}

	File[] searchPluginJars() {
		final File pluginDir = MOGLiFileUtil2.getNewFileInstance(DIR_LIB_PLUGIN);
		File[] files = pluginDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dirOfFile, String name) {
				return name.endsWith(".jar");
			}
		});
		
		if (files == null) {
			files = new File[0];
		}
		MOGLiLogUtil2.logInfo("Number of jar files in " + pluginDir.getAbsolutePath() + " found: " + files.length);
		return files;
	}

	 String readStarterClassFromJar(File pluginJar) {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(pluginJar);
		} catch (IOException e) {
			throw new MOGLiCoreException2("Error reading " + pluginJar.getAbsolutePath());
		}
		 final Properties p = getPropertiesFromPluginManifest(jarFile);
		 String propertyStarterclass = p.getProperty(TEXT_STARTERCLASS_MANIFEST_PROPERTIES);
		 if (propertyStarterclass == null) {
			 throw new MissingStarterclassException();
		 }
		 return propertyStarterclass;
	}

	private Properties getPropertiesFromPluginManifest(final JarFile jarFile) {
		final ZipEntry entry = jarFile.getEntry(MOGLiPlugin2.FILENAME_PLUGIN_JAR_PROPERTIES);
		if (entry == null) {
			 throw new MissingManifestException();
		}
		Properties p = new Properties();
		try {
			InputStream inputStream = jarFile.getInputStream(entry);
			p.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			throw new MOGLiCoreException2("Error reading " + MOGLiPlugin2.FILENAME_PLUGIN_JAR_PROPERTIES 
					+ " from "+ jarFile.getName());
		}
		return p;
	}

	void checkActivationPluginState(List<PluginMetaData> pluginMetaDataList) {
		for (PluginMetaData pluginMetaData : pluginMetaDataList) {
			if (pluginMetaData.isStatusOK()) { 
				String jarName = pluginMetaData.getJarName();
				String activationProperty = applicationProperties.getProperty(jarName);
				if (activationProperty == null) {
					jarName = FileUtil.removeFileExtension(pluginMetaData.getJarName());
					activationProperty = applicationProperties.getProperty(jarName);
				}
				if (activationProperty != null) {
					if (activationProperty.toUpperCase().equals(TEXT_DEACTIVATED_PLUGIN_PROPERTY)) {
						pluginMetaData.setInfoMessage(TEXT_DEACTIVATED_PLUGIN_INFO); 
					}
				}
			} else {
				// no need to consider
			}
		}
	}

}
