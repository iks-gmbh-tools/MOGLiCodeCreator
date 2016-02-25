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
package com.iksgmbh.moglicc.infrastructure;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.iksgmbh.moglicc.data.InfrastructureInitData;
import com.iksgmbh.moglicc.plugin.subtypes.GeneratorPlugin;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidatorVendor;
import com.iksgmbh.moglicc.test.CoreTestParent;

public class MOGLiInfrastructureUnitTest extends CoreTestParent {
	
	@Test
	public void returnsCorrectNumberOfPluginsOfACertainType() {
		// prepare test
		final InfrastructureInitData initData = createInfrastructureInitData(null, null, null);
		initData.pluginList = getPluginListForTest();
		final MOGLiInfrastructure infrastructure = new MOGLiInfrastructure(initData);

		// call functionality under test
		final List<GeneratorPlugin> generatorPlugins = infrastructure.getPluginsOfType(GeneratorPlugin.class);
		final List<MetaInfoValidatorVendor> validatorVendorPlugins = infrastructure.getPluginsOfType(MetaInfoValidatorVendor.class);
		final List<Cloneable> emptyPluginList = infrastructure.getPluginsOfType(Cloneable.class);
		
		// verify test result
		assertEquals("Number of plugins", 1, generatorPlugins.size());
		assertEquals("Number of plugins", 0, emptyPluginList.size());
		assertEquals("Number of plugins", 1, validatorVendorPlugins.size());
	}
}