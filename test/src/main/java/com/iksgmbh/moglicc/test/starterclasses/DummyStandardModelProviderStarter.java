package com.iksgmbh.moglicc.test.starterclasses;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.plugin.type.basic.ModelProvider;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.test.MockDataBuilder;

public class DummyStandardModelProviderStarter implements ModelProvider, MOGLiPlugin {

	private static final String PLUGIN_ID = "StandardModelProvider";
	private InfrastructureService infrastructure;

	public String getId() {
		return PLUGIN_ID;
	}

	@Override
	public PluginType getPluginType() {
		return MOGLiPlugin.PluginType.MODEL_PROVIDER;
	}

	@Override
	public void doYourJob() {
		infrastructure.getPluginLogger().logInfo(PLUGIN_ID);
	}

	@Override
	public void setMOGLiInfrastructure(InfrastructureService infrastructure) {
		this.infrastructure = infrastructure;
	}

	@Override
	public List<String> getDependencies() {
		List<String> toReturn = new ArrayList<String>();
		return toReturn;
	}

	@Override
	public Model getModel() {
		return MockDataBuilder.buildStandardModel();
	}

	@Override
	public boolean unpackDefaultInputData() throws MOGLiPluginException {
		return false;
	}

	@Override
	public InfrastructureService getMOGLiInfrastructure() {
		return infrastructure;
	}
	
	@Override
	public boolean unpackPluginHelpFiles() throws MOGLiPluginException {
		return false;
	}

}
