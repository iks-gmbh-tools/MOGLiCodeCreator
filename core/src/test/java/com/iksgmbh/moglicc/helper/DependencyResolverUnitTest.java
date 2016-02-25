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
package com.iksgmbh.moglicc.helper;

import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_INFOMESSAGE_OK;
import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_UNRESOLVABLE_DEPENDENCIES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.PluginMetaData;
import com.iksgmbh.moglicc.exceptions.UnresolvableDependenciesException;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.test.CoreTestParent;
import com.iksgmbh.moglicc.test.starterclasses.DummyDataProviderStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyGeneratorStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyOtherEngineProviderStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyPluginStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyStandardModelProviderStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyVelocityEngineProviderStarter;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil;

public class DependencyResolverUnitTest extends CoreTestParent {
	// **************************  Instance fields  *********************************	
	
	private DependencyResolver dependencyResolver;


	// **************************  Instantiation stuff  *********************************	
	
	@Before
	public void setup() {
		MOGLiLogUtil.setCoreLogfile(null);
		dependencyResolver = new DependencyResolver(null);
		super.setup();
	}
	

	// **************************  Test Methods  *********************************	
	
	@Test
	public void testResolveDependencies_4PluginsInInappropriateOrder() throws UnresolvableDependenciesException {
		// prepare test
		final DummyGeneratorStarter generator = new DummyGeneratorStarter();
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyDataProviderStarter dataProvider = new DummyDataProviderStarter();
		final DummyVelocityEngineProviderStarter engineProvider = new DummyVelocityEngineProviderStarter();
		
		final List<MOGLiPlugin> pluginListToSort = new ArrayList<MOGLiPlugin>();
		pluginListToSort.add(generator);
		pluginListToSort.add(engineProvider);
		pluginListToSort.add(dataProvider);
		pluginListToSort.add(modelProvider);

		// call functionality under test
		final List<MOGLiPlugin> sortedPluginList = dependencyResolver.resolveDependencies(pluginListToSort);
		
		// verify test result
		assertStringEquals("Unexpected Plugin", "StandardModelProvider", sortedPluginList.get(0).getId());
		assertStringEquals("Unexpected Plugin", "DummyDataProvider", sortedPluginList.get(1).getId());
		assertStringEquals("Unexpected Plugin", "VelocityEngineProvider", sortedPluginList.get(2).getId());
		assertStringEquals("Unexpected Plugin", "DummyGenerator", sortedPluginList.get(3).getId());
		
	}

	@Test
	public void testResolveDependencies_5Plugins1PluginUnresolvable() {
		// prepare test
		List<PluginMetaData> pluginMetaDataList = new ArrayList<PluginMetaData>();
		PluginMetaData pluginMetaData = new PluginMetaData("JAR", "DummyPluginStarter");
		pluginMetaData.setId("DummyPlugin");
		pluginMetaDataList.add(pluginMetaData);
		dependencyResolver = new DependencyResolver(pluginMetaDataList);
		
		final DummyGeneratorStarter generator = new DummyGeneratorStarter();
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyDataProviderStarter dataProvider = new DummyDataProviderStarter();
		final DummyVelocityEngineProviderStarter engineProvider = new DummyVelocityEngineProviderStarter();
		final DummyPluginStarter dummyPluginStarter = new DummyPluginStarter();
		
		final List<MOGLiPlugin> pluginListToSort = new ArrayList<MOGLiPlugin>();
		pluginListToSort.add(generator);
		pluginListToSort.add(engineProvider);
		pluginListToSort.add(dataProvider);
		pluginListToSort.add(modelProvider);
		pluginListToSort.add(dummyPluginStarter);
		
		
		// call functionality under test
		boolean expectedExceptionThrown = false;
		try {
			dependencyResolver.resolveDependencies(pluginListToSort);
		} catch (UnresolvableDependenciesException e) {
			expectedExceptionThrown = true;
		}

		// verify test result
		assertTrue("Expected exception not thrown", expectedExceptionThrown);
	}
	
	@Test
	public void testSolveDependenciesIfPossible_1PluginUnresolvable() {
		// prepare test
		final DummyVelocityEngineProviderStarter engineProvider = new DummyVelocityEngineProviderStarter();
		final List<MOGLiPlugin> pluginListToSort = new ArrayList<MOGLiPlugin>();
		pluginListToSort.add(engineProvider);
		List<MOGLiPlugin> sortedPluginList = new ArrayList<MOGLiPlugin>();
		
		// call functionality under test
		final List<MOGLiPlugin> updatedPluginListToSort = 
			dependencyResolver.solveDependenciesIfPossible(pluginListToSort, sortedPluginList);
		
		// verify test result
		assertEquals("Unexpected size of pluginListToSort.", 1, pluginListToSort.size());
		assertEquals("Unexpected size of updatedPluginListToSort.", 1, updatedPluginListToSort.size());
		assertEquals("Unexpected size of sortedPluginList.", 0, sortedPluginList.size());
	}
	
	@Test
	public void testSolveDependenciesIfPossible_1PluginResolved() {
		// prepare test
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final List<MOGLiPlugin> pluginListToSort = new ArrayList<MOGLiPlugin>();
		pluginListToSort.add(modelProvider);
		List<MOGLiPlugin> sortedPluginList = new ArrayList<MOGLiPlugin>();
		
		// call functionality under test
		final List<MOGLiPlugin> updatedPluginListToSort = 
			dependencyResolver.solveDependenciesIfPossible(pluginListToSort, sortedPluginList);
		
		// verify test result
		assertEquals("Unexpected size of pluginListToSort.", 1, pluginListToSort.size());
		assertEquals("Unexpected size of updatedPluginListToSort.", 0, updatedPluginListToSort.size());
		assertEquals("Unexpected size of sortedPluginList.", 1, sortedPluginList.size());
	}
	
	@Test
	public void testSolveDependenciesIfPossible_2Plugins1Resolved() {
		// prepare test
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyVelocityEngineProviderStarter engineProvider = new DummyVelocityEngineProviderStarter();
		final List<MOGLiPlugin> pluginListToSort = new ArrayList<MOGLiPlugin>();
		pluginListToSort.add(modelProvider);
		pluginListToSort.add(engineProvider);
		List<MOGLiPlugin> sortedPluginList = new ArrayList<MOGLiPlugin>();
		
		// call functionality under test
		final List<MOGLiPlugin> updatedPluginListToSort = 
			dependencyResolver.solveDependenciesIfPossible(pluginListToSort, sortedPluginList);
		
		// verify test result
		assertEquals("Unexpected size of pluginListToSort.", 2, pluginListToSort.size());
		assertEquals("Unexpected size of updatedPluginListToSort.", 1, updatedPluginListToSort.size());
		assertEquals("Unexpected size of sortedPluginList.", 1, sortedPluginList.size());
	}
	
	@Test
	public void testSolveDependenciesIfPossible_2PluginsBothResolvedDueToCorrectOrder() {
		// prepare test
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyDataProviderStarter dataProvider = new DummyDataProviderStarter();
		final List<MOGLiPlugin> pluginListToSort = new ArrayList<MOGLiPlugin>();
		pluginListToSort.add(modelProvider);
		pluginListToSort.add(dataProvider);
		List<MOGLiPlugin> sortedPluginList = new ArrayList<MOGLiPlugin>();
		
		// call functionality under test
		final List<MOGLiPlugin> updatedPluginListToSort = 
			dependencyResolver.solveDependenciesIfPossible(pluginListToSort, sortedPluginList);
		
		// verify test result
		assertEquals("Unexpected size of pluginListToSort.", 2, pluginListToSort.size());
		assertEquals("Unexpected size of updatedPluginListToSort.", 0, updatedPluginListToSort.size());
		assertEquals("Unexpected size of sortedPluginList.", 2, sortedPluginList.size());
	}
	
	@Test
	public void testSolveDependenciesIfPossible_2Plugins1ResolvedDueToInappropriateOrder() {
		// prepare test
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyDataProviderStarter dataProvider = new DummyDataProviderStarter();
		final List<MOGLiPlugin> pluginListToSort = new ArrayList<MOGLiPlugin>();
		pluginListToSort.add(dataProvider);
		pluginListToSort.add(modelProvider);
		List<MOGLiPlugin> sortedPluginList = new ArrayList<MOGLiPlugin>();
		
		// call functionality under test
		final List<MOGLiPlugin> updatedPluginListToSort = 
			dependencyResolver.solveDependenciesIfPossible(pluginListToSort, sortedPluginList);
		
		// verify test result
		assertEquals("Unexpected size of pluginListToSort.", 2, pluginListToSort.size());
		assertEquals("Unexpected size of updatedPluginListToSort.", 1, updatedPluginListToSort.size());
		assertEquals("Unexpected size of sortedPluginList.", 1, sortedPluginList.size());
	}
	
	@Test
	public void resolvesDependenciesDueToSuggestedExecutionOrder() 
	{
		// prepare test
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyOtherEngineProviderStarter engineProvider = new DummyOtherEngineProviderStarter();
		final DummyDataProviderStarter dataProvider = new DummyDataProviderStarter();
		final List<MOGLiPlugin> pluginListToSort = new ArrayList<MOGLiPlugin>();
		pluginListToSort.add(engineProvider);  // suggested exec order 20 - index 1: before data provider!
		pluginListToSort.add(dataProvider);    // suggested exec order 10 - index 2
		pluginListToSort.add(modelProvider);   // suggested exec order 0
		
		// call functionality under test
		final List<MOGLiPlugin> sortedPluginList = dependencyResolver.resolveDependencies(pluginListToSort);
		
		// verify test result
		assertEquals("Unexpected plugin execution order", "StandardModelProvider", sortedPluginList.get(0).getId());
		assertEquals("Unexpected plugin execution order", "DummyDataProvider", sortedPluginList.get(1).getId());
		assertEquals("Unexpected plugin execution order", "OtherEngineProvider", sortedPluginList.get(2).getId());   // after data provider!
	}

	
	@Test
	public void testUpdatePluginMetaData() {
		// prepare test
		List<PluginMetaData> pluginMetaDataList = new ArrayList<PluginMetaData>();
		PluginMetaData pluginMetaData2 = new PluginMetaData("JAR2", "DummyPluginStarter2");
		pluginMetaData2.setId("DummyPlugin2");
		pluginMetaDataList.add(pluginMetaData2);
		PluginMetaData pluginMetaData = new PluginMetaData("JAR", "DummyPluginStarter");
		pluginMetaData.setId("DummyPlugin");
		pluginMetaDataList.add(pluginMetaData);
		dependencyResolver = new DependencyResolver(pluginMetaDataList);
		
		List<MOGLiPlugin> listOfPluginsWithUnresolvableDependencies = new ArrayList<MOGLiPlugin>();
		final DummyPluginStarter dummyPluginStarter = new DummyPluginStarter();
		listOfPluginsWithUnresolvableDependencies.add(dummyPluginStarter);
		dependencyResolver.setListOfPluginsWithUnresolvableDependencies(listOfPluginsWithUnresolvableDependencies);
		
		// call functionality under test
		dependencyResolver.updatePluginMetaData();
		
		// verify test result
		assertStringEquals("Unexpected Info Messages", TEXT_INFOMESSAGE_OK, pluginMetaDataList.get(0).getInfoMessage());
		assertStringEquals("Unexpected Info Messages", TEXT_UNRESOLVABLE_DEPENDENCIES, pluginMetaDataList.get(1).getInfoMessage());
	}
}