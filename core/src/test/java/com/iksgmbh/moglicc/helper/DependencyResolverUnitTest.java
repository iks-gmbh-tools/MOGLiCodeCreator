package com.iksgmbh.moglicc.helper;

import static com.iksgmbh.moglicc.MOGLiTextConstants2.TEXT_INFOMESSAGE_OK;
import static com.iksgmbh.moglicc.MOGLiTextConstants2.TEXT_UNRESOLVABLE_DEPENDENCIES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.PluginMetaData;
import com.iksgmbh.moglicc.exceptions.UnresolvableDependenciesException;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin2;
import com.iksgmbh.moglicc.test.CoreTestParent;
import com.iksgmbh.moglicc.test.starterclasses.DummyDataProviderStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyVelocityEngineProviderStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyGeneratorStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyStandardModelProviderStarter;
import com.iksgmbh.moglicc.test.starterclasses.DummyPluginStarter;
import com.iksgmbh.moglicc.utils.MOGLiLogUtil2;

public class DependencyResolverUnitTest extends CoreTestParent {
	// **************************  Instance fields  *********************************	
	
	private DependencyResolver dependencyResolver;


	// **************************  Instantiation stuff  *********************************	
	
	@Before
	public void setup() {
		MOGLiLogUtil2.setCoreLogfile(null);
		dependencyResolver = new DependencyResolver(null);
		super.setup();
	}
	

	// **************************  Test Methods  *********************************	
	
	@Test
	public void testResolveDependencies_4PluginsInInappropriateOrder() throws UnresolvableDependenciesException {
		final DummyGeneratorStarter generator = new DummyGeneratorStarter();
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyDataProviderStarter dataProvider = new DummyDataProviderStarter();
		final DummyVelocityEngineProviderStarter engineProvider = new DummyVelocityEngineProviderStarter();
		
		final List<MOGLiPlugin2> pluginListToSort = new ArrayList<MOGLiPlugin2>();
		pluginListToSort.add(generator);
		pluginListToSort.add(engineProvider);
		pluginListToSort.add(dataProvider);
		pluginListToSort.add(modelProvider);

		final List<MOGLiPlugin2> sortedPluginList = dependencyResolver.resolveDependencies(pluginListToSort);
		
		assertNotNull("Plugin List is null", pluginListToSort);
		assertStringEquals("Unexpected Plugin!", pluginListToSort.get(3).getId(), sortedPluginList.get(0).getId());
		
	}

	@Test
	public void testResolveDependencies_5Plugins1PluginUnresolvable() {
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
		
		final List<MOGLiPlugin2> pluginListToSort = new ArrayList<MOGLiPlugin2>();
		pluginListToSort.add(generator);
		pluginListToSort.add(engineProvider);
		pluginListToSort.add(dataProvider);
		pluginListToSort.add(modelProvider);
		pluginListToSort.add(dummyPluginStarter);
		
		
		boolean expectedExceptionThrown = false;
		try {
			dependencyResolver.resolveDependencies(pluginListToSort);
		} catch (UnresolvableDependenciesException e) {
			expectedExceptionThrown = true;
		}
		assertTrue("Expected exception not thrown", expectedExceptionThrown);
	}
	
	@Test
	public void testSolveDependenciesIfPossible_1PluginUnresolvable() {
		final DummyVelocityEngineProviderStarter engineProvider = new DummyVelocityEngineProviderStarter();
		final List<MOGLiPlugin2> pluginListToSort = new ArrayList<MOGLiPlugin2>();
		pluginListToSort.add(engineProvider);
		List<MOGLiPlugin2> sortedPluginList = new ArrayList<MOGLiPlugin2>();
		
		final List<MOGLiPlugin2> updatedPluginListToSort = 
			dependencyResolver.solveDependenciesIfPossible(pluginListToSort, sortedPluginList);
		
		assertEquals("Unexpected size of pluginListToSort.", 1, pluginListToSort.size());
		assertEquals("Unexpected size of updatedPluginListToSort.", 1, updatedPluginListToSort.size());
		assertEquals("Unexpected size of sortedPluginList.", 0, sortedPluginList.size());
	}
	
	@Test
	public void testSolveDependenciesIfPossible_1PluginResolved() {
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final List<MOGLiPlugin2> pluginListToSort = new ArrayList<MOGLiPlugin2>();
		pluginListToSort.add(modelProvider);
		List<MOGLiPlugin2> sortedPluginList = new ArrayList<MOGLiPlugin2>();
		
		final List<MOGLiPlugin2> updatedPluginListToSort = 
			dependencyResolver.solveDependenciesIfPossible(pluginListToSort, sortedPluginList);
		
		assertEquals("Unexpected size of pluginListToSort.", 1, pluginListToSort.size());
		assertEquals("Unexpected size of updatedPluginListToSort.", 0, updatedPluginListToSort.size());
		assertEquals("Unexpected size of sortedPluginList.", 1, sortedPluginList.size());
	}
	
	@Test
	public void testSolveDependenciesIfPossible_2Plugins1Resolved() {
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyVelocityEngineProviderStarter engineProvider = new DummyVelocityEngineProviderStarter();
		final List<MOGLiPlugin2> pluginListToSort = new ArrayList<MOGLiPlugin2>();
		pluginListToSort.add(modelProvider);
		pluginListToSort.add(engineProvider);
		List<MOGLiPlugin2> sortedPluginList = new ArrayList<MOGLiPlugin2>();
		
		final List<MOGLiPlugin2> updatedPluginListToSort = 
			dependencyResolver.solveDependenciesIfPossible(pluginListToSort, sortedPluginList);
		
		assertEquals("Unexpected size of pluginListToSort.", 2, pluginListToSort.size());
		assertEquals("Unexpected size of updatedPluginListToSort.", 1, updatedPluginListToSort.size());
		assertEquals("Unexpected size of sortedPluginList.", 1, sortedPluginList.size());
	}
	
	@Test
	public void testSolveDependenciesIfPossible_2PluginsBothResolvedDueToCorrectOrder() {
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyDataProviderStarter dataProvider = new DummyDataProviderStarter();
		final List<MOGLiPlugin2> pluginListToSort = new ArrayList<MOGLiPlugin2>();
		pluginListToSort.add(modelProvider);
		pluginListToSort.add(dataProvider);
		List<MOGLiPlugin2> sortedPluginList = new ArrayList<MOGLiPlugin2>();
		
		final List<MOGLiPlugin2> updatedPluginListToSort = 
			dependencyResolver.solveDependenciesIfPossible(pluginListToSort, sortedPluginList);
		
		assertEquals("Unexpected size of pluginListToSort.", 2, pluginListToSort.size());
		assertEquals("Unexpected size of updatedPluginListToSort.", 0, updatedPluginListToSort.size());
		assertEquals("Unexpected size of sortedPluginList.", 2, sortedPluginList.size());
	}
	
	@Test
	public void testSolveDependenciesIfPossible_2Plugins1ResolvedDueToInappropriateOrder() {
		final DummyStandardModelProviderStarter modelProvider = new DummyStandardModelProviderStarter();
		final DummyDataProviderStarter dataProvider = new DummyDataProviderStarter();
		final List<MOGLiPlugin2> pluginListToSort = new ArrayList<MOGLiPlugin2>();
		pluginListToSort.add(dataProvider);
		pluginListToSort.add(modelProvider);
		List<MOGLiPlugin2> sortedPluginList = new ArrayList<MOGLiPlugin2>();
		
		final List<MOGLiPlugin2> updatedPluginListToSort = 
			dependencyResolver.solveDependenciesIfPossible(pluginListToSort, sortedPluginList);
		
		assertEquals("Unexpected size of pluginListToSort.", 2, pluginListToSort.size());
		assertEquals("Unexpected size of updatedPluginListToSort.", 1, updatedPluginListToSort.size());
		assertEquals("Unexpected size of sortedPluginList.", 1, sortedPluginList.size());
	}
	
	@Test
	public void testUpdatePluginMetaData() {
		List<PluginMetaData> pluginMetaDataList = new ArrayList<PluginMetaData>();
		PluginMetaData pluginMetaData2 = new PluginMetaData("JAR2", "DummyPluginStarter2");
		pluginMetaData2.setId("DummyPlugin2");
		pluginMetaDataList.add(pluginMetaData2);
		PluginMetaData pluginMetaData = new PluginMetaData("JAR", "DummyPluginStarter");
		pluginMetaData.setId("DummyPlugin");
		pluginMetaDataList.add(pluginMetaData);
		dependencyResolver = new DependencyResolver(pluginMetaDataList);
		
		List<MOGLiPlugin2> listOfPluginsWithUnresolvableDependencies = new ArrayList<MOGLiPlugin2>();
		final DummyPluginStarter dummyPluginStarter = new DummyPluginStarter();
		listOfPluginsWithUnresolvableDependencies.add(dummyPluginStarter);
		dependencyResolver.setListOfPluginsWithUnresolvableDependencies(listOfPluginsWithUnresolvableDependencies);
		
		dependencyResolver.updatePluginMetaData();
		
		assertStringEquals("Unexpected Info Messages", TEXT_INFOMESSAGE_OK, pluginMetaDataList.get(0).getInfoMessage());
		assertStringEquals("Unexpected Info Messages", TEXT_UNRESOLVABLE_DEPENDENCIES, pluginMetaDataList.get(1).getInfoMessage());
	}
}
