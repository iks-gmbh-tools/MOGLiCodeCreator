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
package com.iksgmbh.moglicc.test.starterclasses;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;

public class StarterClassWithMissingInterface {

	private static final String PLUGIN_ID = "MissingInterface";

	public String getId() {
		return PLUGIN_ID;
	}

	public MOGLiPlugin.PluginType getPluginType() {
		return MOGLiPlugin.PluginType.PROVIDER;
	}

	public void doYourJob() {
	}

	public void setMOGLiInfrastructure(InfrastructureService infrastructure) {
	}

	public List<String> getDependencies() {
		List<String> toReturn = new ArrayList<String>();
		toReturn.add("DummyEngineProvider");
		toReturn.add("DummyDataProvider");
		toReturn.add("DummyModelProvider");
		return toReturn;
	}

}