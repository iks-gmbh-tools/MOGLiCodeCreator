package com.iksgmbh.moglicc.test.starterclasses;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.plugin.type.basic.Generator;

public class DummyPluginStarter implements Generator, MOGLiPlugin {

	private static final String PLUGIN_ID = "DummyPlugin";
	private InfrastructureService infrastructure;

	public String getId() {
		return PLUGIN_ID;
	}

	@Override
	public PluginType getPluginType() {
		return MOGLiPlugin.PluginType.GENERATOR;
	}

	@Override
	public void doYourJob() throws MOGLiPluginException {
		throw new MOGLiPluginException("Testfehler");
	}

	@Override
	public void setMOGLiInfrastructure(final InfrastructureService infrastructure) {
		this.infrastructure = infrastructure;
	}

	@Override
	public List<String> getDependencies() {
		List<String> toReturn = new ArrayList<String>();
		toReturn.add("OtherPlugin");
		return toReturn;
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

	@Override
	public String getGenerationReport() {
		return null;
	}

	@Override
	public int getNumberOfGenerations() {
		return 0;
	}

	@Override
	public int getNumberOfArtefacts() {
		return 0;
	}

}
