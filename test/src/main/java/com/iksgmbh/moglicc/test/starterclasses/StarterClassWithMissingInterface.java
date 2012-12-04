package com.iksgmbh.moglicc.test.starterclasses;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin2;

public class StarterClassWithMissingInterface {

	private static final String PLUGIN_ID = "MissingInterface";

	public String getId() {
		return PLUGIN_ID;
	}

	public MOGLiPlugin2.PluginType getPluginType() {
		return MOGLiPlugin2.PluginType.DATA_PROVIDER;
	}

	public void doYourJob() {
	}

	public void setMOGLiInfrastructure(InfrastructureService infrastructure) {
	}

	public List<String> getDependencies() {
		List<String> toReturn = new ArrayList<String>();
		toReturn.add("DummyEngineProvider");
		toReturn.add("DummyDataProvider");
		toReturn.add("DummyModelProvider");
		return toReturn;
	}

}
