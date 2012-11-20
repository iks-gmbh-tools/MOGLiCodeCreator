package com.iksgmbh.moglicc.test.starterclasses;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MogliPluginException;
import com.iksgmbh.moglicc.plugin.MogliPlugin;
import com.iksgmbh.moglicc.plugin.type.basic.DataProvider;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidatorVendor;

public class DummyDataProviderStarter implements DataProvider, MetaInfoValidatorVendor {

	private static final String PLUGIN_ID = "DummyDataProvider";
	private InfrastructureService infrastructure;

	public String getId() {
		return PLUGIN_ID;
	}

	@Override
	public PluginType getPluginType() {
		return MogliPlugin.PluginType.DATA_PROVIDER;
	}

	@Override
	public void doYourJob() {
		infrastructure.getPluginLogger().logInfo(PLUGIN_ID);
	}

	@Override
	public void setMogliInfrastructure(InfrastructureService infrastructure) {
		this.infrastructure = infrastructure;
	}

	@Override
	public List<String> getDependencies() {
		List<String> toReturn = new ArrayList<String>();
		toReturn.add("StandardModelProvider");
		return toReturn;
	}

	@Override
	public boolean unpackDefaultInputData() throws MogliPluginException {
		return false;
	}

	@Override
	public List<MetaInfoValidator> getMetaInfoValidatorList() throws MogliPluginException {
		return null;
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
