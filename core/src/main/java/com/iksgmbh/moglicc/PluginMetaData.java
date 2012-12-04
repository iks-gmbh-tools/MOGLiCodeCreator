package com.iksgmbh.moglicc;

import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_INFOMESSAGE_OK;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_NO_MANIFEST_FOUND;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE;

import java.util.List;

import com.iksgmbh.moglicc.exceptions.MOGLiCoreException;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin.PluginType;

/**
 * Holds all necessary information about the Mogli plugins
 * @author Reik Oberrath
 */
public class PluginMetaData {
	
	public static final String NO_MANIFEST = "<" + TEXT_NO_MANIFEST_FOUND + ">";
	public static final String NO_STARTERCLASS = "<" + TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE + ">";
	
	public enum PluginStatus { ANALYSED, LOADED, EXECUTED };
	
	private String jarName;
	private String starterClass;
	private String id;
	private PluginType pluginType;
	private List<String> dependencies;
	private PluginStatus status;	
	private String infoMessage;

	public PluginMetaData(String jarName, String starterClass) {
		super();
		if (jarName == null) {
			throw new MOGLiCoreException("JarName Missing");
		}
		this.jarName = jarName;
		this.starterClass = starterClass;
		if (NO_MANIFEST.equals(starterClass)) {
			infoMessage = TEXT_NO_MANIFEST_FOUND;
		} else if (NO_STARTERCLASS.equals(starterClass))  {
			infoMessage = TEXT_NO_STARTERCLASS_IN_PROPERTY_FILE;
		} else {
			infoMessage = TEXT_INFOMESSAGE_OK;
		}
		this.status = PluginStatus.ANALYSED;
	}

	public String getJarName() {
		return jarName;
	}

	public String getStarterClass() {
		return starterClass;
	}

	public PluginStatus getStatus() {
		return status;
	}

	public void setPluginStatus(PluginStatus status) {
		this.status = status;
	}

	public String getInfoMessage() {
		return infoMessage;
	}
	
	public void setInfoMessage(String infoMessage) {
		this.infoMessage = infoMessage;
	}
	
	
	public boolean isStatusOK() {
		return TEXT_INFOMESSAGE_OK.equals(infoMessage);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PluginType getPluginType() {
		return pluginType;
	}

	public void setPluginType(PluginType pluginType) {
		this.pluginType = pluginType;
	}

	public List<String> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}

	@Override
	public String toString() {
		return "PluginMetaData [jarName=" + jarName + ", id=" + id
				+ ", pluginType=" + pluginType + ", status=" + status
				+ ", infoMessage=" + infoMessage + "]";
	}

	public String toStringAllArrtibutes() {
		StringBuffer dependsOn = new StringBuffer();
		for (int i = 0; i < dependencies.size(); i++) {
			dependsOn.append(dependencies.get(0));
			dependsOn.append(", ");
		}
		String dependsOnString = dependsOn.toString().substring(0, dependsOn.length()-3);
		return id + "  " + jarName + "  " + starterClass + "  " 
		       + pluginType + "  " + status + "  " 
		       + infoMessage + "  " + dependsOnString;
	}

}
