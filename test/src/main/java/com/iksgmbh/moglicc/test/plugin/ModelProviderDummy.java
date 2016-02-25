/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iksgmbh.moglicc.test.plugin;

import java.util.List;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ModelProvider;
import com.iksgmbh.moglicc.provider.model.standard.Model;

public class ModelProviderDummy implements ModelProvider {

	private Model model;
	private String id;
	private InfrastructureService infrastructure;
	private String modelFileName;

	public ModelProviderDummy(final String id, final Model model) {
		 this.model = model;
		 this.id = id;
	}
	
	public ModelProviderDummy(final String id, final String modelFileName) {
		 this.modelFileName = modelFileName;
		 this.id = id;
	}


	@Override
	public PluginType getPluginType() {
		return PluginType.PROVIDER;
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
	public Model getModel(String pluginId) throws MOGLiPluginException {
		return model;
	}

	@Override
	public String getModelName() {
		return null;
	}

	@Override
	public String getModelFileName() {
		return modelFileName;
	}

	@Override
	public int getNumberOfCalls()
	{
		return 0;
	}

	@Override
	public String getProviderReport()
	{
		return "Model Provider Dummy Report";
	}

	@Override
	public String getShortReport()
	{
		return "Model Provider Dummy Result";
	}

	@Override
	public int getSuggestedPositionInExecutionOrder()
	{
		return 0;
	}

	@Override
	public Model getModel(String pluginId, Object inputData) throws MOGLiPluginException
	{
		return null;
	}

}