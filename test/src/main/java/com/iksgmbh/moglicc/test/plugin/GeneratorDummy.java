package com.iksgmbh.moglicc.test.plugin;

import java.util.List;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.plugin.subtypes.GeneratorPlugin;

public class GeneratorDummy implements GeneratorPlugin {

	private String id;
	private InfrastructureService infrastructure;

	public GeneratorDummy(final String id) {
		 this.id = id;
	}

	@Override
	public PluginType getPluginType() {
		return PluginType.GENERATOR;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public List<String> getDependencies() {
		return null;
	}

	@Override
	public void setInfrastructure(final InfrastructureService infrastructure) {
		this.infrastructure = infrastructure;
	}

	@Override
	public InfrastructureService getInfrastructure() {
		return infrastructure;
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

	@Override
	public String getGeneratorReport()
	{
		return "Generator Dummy Report";
	}

	@Override
	public String getShortReport()
	{
		return "Generator Dummy Result";
	}

	@Override
	public int getNumberOfGenerations()
	{
		return 11;
	}

	@Override
	public int getNumberOfGeneratedArtefacts()
	{
		return 2;
	}

	@Override
	public int getSuggestedPositionInExecutionOrder()
	{
		return 0;
	}

}

