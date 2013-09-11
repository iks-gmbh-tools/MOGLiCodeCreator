package com.iksgmbh.moglicc.test.starterclasses;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.GeneratorResultData;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ClassBasedEngineProvider;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ModelBasedEngineProvider;

public class DummyVelocityEngineProviderStarter implements ClassBasedEngineProvider, ModelBasedEngineProvider {

	private static final String PLUGIN_ID = "VelocityEngineProvider";

	private InfrastructureService infrastructure;

	private List<VelocityGeneratorResultData> velocityGeneratorResultDataList;
	private VelocityGeneratorResultData velocityResultData;

	public void setVelocityGeneratorResultDataList(List<VelocityGeneratorResultData> velocityGeneratorResultDataList) {
		this.velocityGeneratorResultDataList = velocityGeneratorResultDataList;
	}

	public void setVelocityGeneratorResultData(VelocityGeneratorResultData velocityResultData) {
		this.velocityResultData = velocityResultData;
	}

	public String getId() {
		return PLUGIN_ID;
	}

	@Override
	public PluginType getPluginType() {
		return MOGLiPlugin.PluginType.PROVIDER;
	}

	@Override
	public void doYourJob() {
		infrastructure.getPluginLogger().logInfo(PLUGIN_ID);
	}

	@Override
	public void setInfrastructure(InfrastructureService infrastructure) {
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
	public boolean unpackDefaultInputData() throws MOGLiPluginException {
		return false;
	}

	@Override
	public void setEngineData(Object data) {
	}

	@Override
	public Object startEngine() throws MOGLiPluginException {
		return null;
	}

	@Override
	public GeneratorResultData startEngineWithModel() throws MOGLiPluginException {
		return velocityResultData;
	}

	@Override
	public List<GeneratorResultData> startEngineWithClassList() throws MOGLiPluginException {
		final List<GeneratorResultData> toReturn = new ArrayList<GeneratorResultData>();
		for (final GeneratorResultData resultData : velocityGeneratorResultDataList) {
			toReturn.add(resultData);
		}
		return toReturn;
	}

	@Override
	public InfrastructureService getInfrastructure() {
		return infrastructure;
	}

	@Override
	public boolean unpackPluginHelpFiles() throws MOGLiPluginException {
		return false;
	}

	@Override
	public String getShortReport()
	{
		return "";
	}

	@Override
	public int getNumberOfCalls()
	{
		return 0;
	}

	@Override
	public String getProviderReport()
	{
		return null;
	}

	@Override
	public int getSuggestedPositionInExecutionOrder()
	{
		return 0;
	}
}
