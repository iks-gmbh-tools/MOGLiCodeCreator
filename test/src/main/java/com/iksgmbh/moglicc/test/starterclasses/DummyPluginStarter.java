package com.iksgmbh.moglicc.test.starterclasses;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MogliPluginException;
import com.iksgmbh.moglicc.plugin.MogliPlugin;
import com.iksgmbh.moglicc.plugin.PluginExecutable;
import com.iksgmbh.moglicc.plugin.type.basic.Generator;

public class DummyPluginStarter implements Generator, PluginExecutable {

	private static final String PLUGIN_ID = "DummyPlugin";
	private InfrastructureService infrastructure;

	public String getId() {
		return PLUGIN_ID;
	}

	@Override
	public PluginType getPluginType() {
		return MogliPlugin.PluginType.GENERATOR;
	}

	@Override
	public void doYourJob() throws MogliPluginException {
		throw new MogliPluginException("Testfehler");
	}

	@Override
	public void setMogliInfrastructure(final InfrastructureService infrastructure) {
		this.infrastructure = infrastructure;
	}

	@Override
	public List<String> getDependencies() {
		List<String> toReturn = new ArrayList<String>();
		toReturn.add("OtherPlugin");
		return toReturn;
	}

	@Override
	public boolean unpackDefaultInputData() throws MogliPluginException {
		return false;
	}

	@Override
	public InfrastructureService getMogliInfrastructure() {
		return infrastructure;
	}
	
	@Override
	public boolean unpackPluginHelpFiles() throws MogliPluginException {
		return false;
	}

}
