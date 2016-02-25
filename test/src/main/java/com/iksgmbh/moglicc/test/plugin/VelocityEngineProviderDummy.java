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
import com.iksgmbh.moglicc.generator.GeneratorResultData;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ModelBasedEngineProvider;

public class VelocityEngineProviderDummy implements ModelBasedEngineProvider {

	private GeneratorResultData resultData;

	@Override
	public PluginType getPluginType() {
		return PluginType.PROVIDER;
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