package com.iksgmbh.moglicc.treebuilder.modelbased.velocity;

import java.util.List;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.data.GeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.plugin.type.ModelBasedEngineProvider;

public class VelocityEngineProviderDummy implements ModelBasedEngineProvider {

	private GeneratorResultData resultData;

	@Override
	public PluginType getPluginType() {
		return PluginType.ENGINE_PROVIDER;
	}

	@Override
	public String getId() {
		return "VelocityEngineProvider";
	}

	public void setResultData(GeneratorResultData resultData) {
		this.resultData = resultData;
	}

	@Override
	public GeneratorResultData startEngineWithModel() throws MOGLiPluginException {
		return resultData;
	}

	@Override
	public void setEngineData(Object engineData) throws MOGLiPluginException {
	}

	@Override
	public Object startEngine() throws MOGLiPluginException {
		return null;
	}

	@Override
	public List<String> getDependencies() {
		return null;
	}

	@Override
	public void setInfrastructure(InfrastructureService infrastructure) {
	}

	@Override
	public InfrastructureService getInfrastructure() {
		return null;
	}

	@Override
	public void doYourJob() throws MOGLiPluginException {
	}

	@Override
	public boolean unpackDefaultInputData() throws MOGLiPluginException {
		return false;
	}

	@Override
	public boolean unpackPluginHelpFiles() throws MOGLiPluginException {
		return false;
	}


}
