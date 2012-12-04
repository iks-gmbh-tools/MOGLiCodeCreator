package com.iksgmbh.moglicc.intest.provider.model.standard;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.moglicc.provider.model.standard.StandardModelProviderStarter;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;

public class StandardModelProviderIntTest extends IntTestParent {

	@Test
	public void createsStatisticsFile() throws MOGLiPluginException {
		// call functionality under test
		standardModelProviderStarter.doYourJob();

		// verify test result
		final InfrastructureService infrastructure = standardModelProviderStarter.getMOGLiInfrastructure();
		final File file = new File(infrastructure.getPluginOutputDir(), StandardModelProviderStarter.FILENAME_STATISTICS_FILE);
		assertFileExists(file);
		final File expectedFile = getTestFile("ExpectedModelStatistics.txt");
		assertFileEquals(expectedFile, file);
	}
	
	@Test
	public void createsStatisticsFileWithMetaInfoNamesThatContainSpaces() throws MOGLiPluginException {
		// prepare test
		setModelFile("ModelFileWithMetaInfosContainingSpacesInNames.txt");
		setMetaInfoValidationFile(velocityClassBasedGeneratorStarter, "MetaInfoValidatoresContainingSpacesInNames.txt");
		
		// call functionality under test
		standardModelProviderStarter.doYourJob();

		// verify test result
		final InfrastructureService infrastructure = standardModelProviderStarter.getMOGLiInfrastructure();
		final File file = new File(infrastructure.getPluginOutputDir(), StandardModelProviderStarter.FILENAME_STATISTICS_FILE);
		assertFileExists(file);
		final File expectedFile = new File(getProjectTestResourcesDir(), 
				                           "ExpectedStatisticsFileWithMetaInfosContainingSpacesInNames.txt");
		assertFileEquals(expectedFile, file);
	}

	@Test
	public void filtersMetaInfoValidatorVendorsByNameOfModel() throws MOGLiPluginException {
		// prepare test
		setMetaInfoValidationFile(velocityClassBasedGeneratorStarter, "MetaInfoValidatorsForDifferentModels.txt");
		standardModelProviderStarter.doYourJob();
		
		// call functionality under test
		final List<MetaInfoValidator> allMetaInfoValidators = standardModelProviderStarter.getAllMetaInfoValidators();

		// verify test result
		assertEquals("Number of vendors", 2, allMetaInfoValidators.size());
	}
}
