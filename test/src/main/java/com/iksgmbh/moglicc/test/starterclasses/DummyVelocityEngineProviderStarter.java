package com.iksgmbh.moglicc.test.starterclasses;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.data.GeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException2;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData;
import com.iksgmbh.moglicc.inserter.modelbased.velocity.VelocityInserterResultData;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin2;
import com.iksgmbh.moglicc.plugin.type.ClassBasedEngineProvider;
import com.iksgmbh.moglicc.plugin.type.ModelBasedEngineProvider;

public class DummyVelocityEngineProviderStarter implements ClassBasedEngineProvider, ModelBasedEngineProvider {

	private static final String PLUGIN_ID = "VelocityEngineProvider";
	
	private InfrastructureService infrastructure;

	private List<VelocityGeneratorResultData> velocityGeneratorResultDataList;
	private VelocityInserterResultData velocityInserterResultData;

	public void setVelocityGeneratorResultDataList(List<VelocityGeneratorResultData> velocityGeneratorResultDataList) {
		this.velocityGeneratorResultDataList = velocityGeneratorResultDataList;
	}

	public void setVelocityInserterResultData(VelocityInserterResultData velocityInserterResultData) {
		this.velocityInserterResultData = velocityInserterResultData;
	}

	public String getId() {
		return PLUGIN_ID;
	}

	@Override
	public PluginType getPluginType() {
		return MOGLiPlugin2.PluginType.ENGINE_PROVIDER;
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
		toReturn.add("DummyDataProvider");
		toReturn.add("StandardModelProvider");
		return toReturn;
	}
	
	@Override
	public boolean unpackDefaultInputData() throws MOGLiPluginException2 {
		return false;
	}

	@Override
	public void setEngineData(Object data) {
	}

	@Override
	public Object startEngine() throws MOGLiPluginException2 {
		return null;
	}

	@Override
	public GeneratorResultData startEngineWithModel() throws MOGLiPluginException2 {
		return velocityInserterResultData;
	}

	@Override
	public List<GeneratorResultData> startEngineWithClassList() throws MOGLiPluginException2 {
		final List<GeneratorResultData> toReturn = new ArrayList<GeneratorResultData>();
		for (final GeneratorResultData resultData : velocityGeneratorResultDataList) {
			toReturn.add(resultData); 
		}
		return toReturn;
	}

	@Override
	public InfrastructureService getMOGLiInfrastructure() {
		return infrastructure;
	}
	
	@Override
	public boolean unpackPluginHelpFiles() throws MOGLiPluginException2 {
		return false;
	}

}
