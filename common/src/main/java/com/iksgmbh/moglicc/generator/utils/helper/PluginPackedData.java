package com.iksgmbh.moglicc.generator.utils.helper;

import java.util.HashMap;
import java.util.Set;

/**
 * Used to unpack default data in empty input directory.
 * 
 * @author Reik Oberrath
 */
public class PluginPackedData {
	private HashMap<String, String[]> packedDataMap = new HashMap<String, String[]>();
	private Class<?> clazz;
	private String targetSubDir;

	public PluginPackedData(final Class<?> clazz, final String targetSubDir) {
		this.clazz = clazz;
		this.targetSubDir = targetSubDir;
	}
	
	public void addDirectory(final String dirName, final String[] filenames) {
		packedDataMap.put(dirName, filenames);
	}
	
	public void addFile(final String filename) {
		packedDataMap.put(filename, null);
	}
	
	public String getTargetSubDir() {
		return targetSubDir;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	Set<String> getArtefactList() {
		return packedDataMap.keySet();
	}
	
	String[] getFileNamesForArtefact(final String artefact) {
		return packedDataMap.get(artefact);
	}
}
