package com.iksgmbh.moglicc.generator.utils.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Used to unpack default data in empty input directory.
 * For a standard artefact dir use {@link flatFolders}.
 * For root files use {@link rootInputFiles}.
 * For deep file structures use {@link subdirs} and {@link files}.
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class PluginPackedData {
	
	/** subdirs of any depth to create separately */
	private List<String> subdirs = new ArrayList<String>();  
	
	/**
	 * flat folders consist of a dir (map key) and a list of files (map value) in it,
	 * these folder are unpacked into the plugin input root dir of the current plugin 
	 * typically an artefact folder and its files
	 * no subdir possible here
	 */
	private HashMap<String, String[]> flatFolders = new HashMap<String, String[]>(); //
	
	/**
	 * individual files (map key) to store in subdir of arbitrary depth (map value)
	 * the dir must be created separately ({@link subdirs})
	 * no subdir possible here
	 */
	private HashMap<String, String> files = new HashMap<String, String>(); //
	
	/**
	 * files to read from a plugin specific dir within the jar file
	 * and to unpack into the plugin input root dir of the current plugin
	 */
	private List<String> rootInputFiles = new ArrayList<String>();  

	/**
	 * class object to use to get a class loader to load the data files
	 * that must be to read from the jar-file and unpacked into the target dir
	 */
	private Class<?> clazz;

	/**
	 * plugin specific target dir where all data is unpacked
	 */
	private String targetDir;
	
	/**
	 * plugin specific source dir for root input files
	 */
	private String subdirForRootInputFiles;


	public PluginPackedData(final Class<?> clazz, final String targetDir, final String subdirForRootInputFiles) {
		this.clazz = clazz;
		this.targetDir = targetDir;
		this.subdirForRootInputFiles = subdirForRootInputFiles;
	}
	
	/**
	 * flat folders consist of a dir (map key) and a list of files (map value) in it,
	 * these folder are unpacked into the plugin input root dir of the current plugin 
	 * typically an artefact folder and its files
	 * no subdir possible here

	 * @param dirName name of the folder
	 * @param filenames list of files to unpack into the folder
	 */
	public void addFlatFolder(final String dirName, final String[] filenames) {
		flatFolders.put(dirName, filenames);
	}

	public void addFile(final String filename, final String subdir) {
		files.put(filename, subdir);
	}

	public void addFile(final String filename) {
		files.put(filename, "");
	}

	public void addRootFile(final String filename) {
		rootInputFiles.add(filename);
	}

	public void addSubDir(final String subDir) {
		subdirs.add(subDir);
	}

	public String getTargetSubDir() {
		return targetDir;
	}

	public Class<?> getClazz() {
		return clazz;
	}
	
	public List<String> getSubdirs() {
		return subdirs;
	}


	Set<String> getFiles() {
		return files.keySet();
	}

	Set<String> getFlatFolders() {
		return flatFolders.keySet();
	}
	
	String[] getFileNamesForFlatFolder(final String folderName) {
		return flatFolders.get(folderName);
	}

	List<String> getRootInputFileList() {
		return rootInputFiles;
	}

	String getSubdirForRootInputFiles() {
		return subdirForRootInputFiles;
	}

	String getSubdirToFile(final String file) {
		return files.get(file);
	}
}