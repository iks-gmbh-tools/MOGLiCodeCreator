package com.iksgmbh.moglicc.intest.metainfovalidation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.moglicc.provider.model.standard.TextConstants;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidationUtil;
import com.iksgmbh.utils.FileUtil;

public class MetainfoValidationIntTest extends IntTestParent {

	@Test
	public void inserterPluginCausesValidationErrorWhileOtherGeneratorsExecuteSuccessfully() throws Exception {
		// prepare test
		final File dir = velocityModelBasedInserterStarter.getMOGLiInfrastructure().getPluginInputDir();
		final File validationFile = new File(dir, MetaInfoValidationUtil.FILENAME_VALIDATION);
		FileUtil.createNewFileWithContent(validationFile, "|MetaInfo| NotExisting |is| mandatory |for| attributes |in| MOGLiCC_JavaBeanModel |.|");

		// call functionality under test
		standardModelProviderStarter.doYourJob();
		velocityClassBasedGeneratorStarter.doYourJob();      // causes no MOGLiPluginException
		filestructureModelBasedGeneratorStarter.doYourJob(); // causes no MOGLiPluginException

		try {
			// call functionality under test
			velocityModelBasedInserterStarter.doYourJob();
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// verify test result
			assertEquals("Error message", TextConstants.TEXT_MODEL_BREAKS_METAINFO_VALIDATORS, e.getMessage());
		}
	}
}
