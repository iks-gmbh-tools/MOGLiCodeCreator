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
