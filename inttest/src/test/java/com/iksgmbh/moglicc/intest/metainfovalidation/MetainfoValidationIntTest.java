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
		final File dir = velocityModelBasedLineInserterStarter.getMOGLiInfrastructure().getPluginInputDir();
		final File validationFile = new File(dir, MetaInfoValidationUtil.FILENAME_VALIDATION);
		FileUtil.createNewFileWithContent(validationFile, "|MetaInfo| NotExisting |is| mandatory |for| attributes |in| MOGLiCC_JavaBeanModel |.|");

		// call functionality under test
		standardModelProviderStarter.doYourJob();
		velocityClassBasedFileMakerStarter.doYourJob();      // causes no MOGLiPluginException
		velocityModelBasedTreeBuilderStarter.doYourJob(); // causes no MOGLiPluginException

		try {
			// call functionality under test
			velocityModelBasedLineInserterStarter.doYourJob();
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// verify test result
			assertEquals("Error message", TextConstants.TEXT_MODEL_BREAKS_METAINFO_VALIDATORS, e.getMessage());
		}
	}

	@Test
	public void forbidMetaInfoByConditionalValidationRuleWithZeroOccurrence() throws Exception {
		// prepare test
		final File dir = velocityClassBasedFileMakerStarter.getMOGLiInfrastructure().getPluginInputDir();
		final File validationFile = new File(dir, MetaInfoValidationUtil.FILENAME_VALIDATION);
		final File conditionFile = new File(dir, "condition.txt");
		FileUtil.createNewFileWithContent(conditionFile, "|if MetaInfo| JavaType |exists.|");
		final String validationRule = "|MetaInfo| ExampleData |is valid to occur| 0 |time(s) for| attributes |in| MOGLiCC_JavaBeanModel " +
                                      "|if| " + conditionFile.getName() + " |is true.|";
		FileUtil.createNewFileWithContent(validationFile, validationRule);
		standardModelProviderStarter.doYourJob();

		try {
			// call functionality under test
			velocityClassBasedFileMakerStarter.doYourJob();
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// verify test result
			assertEquals("Error message", TextConstants.TEXT_MODEL_BREAKS_METAINFO_VALIDATORS, e.getMessage());
		}

		// Test 2 now with not existing metainfo -> no validation failure
		FileUtil.createNewFileWithContent(validationFile, validationRule.replace("ExampleData", "forbiddenWithJavaType"));
		standardModelProviderStarter.doYourJob();

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result -> no exception
	}

	@Test
	public void doesNotAllowDoubleOccurrencesForOptionalMetaInfoValidation() throws Exception {
		// prepare test
		final File dir = velocityClassBasedFileMakerStarter.getMOGLiInfrastructure().getPluginInputDir();
		final File validationFile = new File(dir, MetaInfoValidationUtil.FILENAME_VALIDATION);
		final String validationRule = "|MetaInfo| Single |is| optional |for| classes |in| MOGLiCC_JavaBeanModel |.|";
		FileUtil.createNewFileWithContent(validationFile, validationRule);
		final String modelFileContent = "model MOGLiCC_JavaBeanModel" + FileUtil.getSystemLineSeparator() +
                                        "class de.Test" + FileUtil.getSystemLineSeparator() +
                                        "  metainfo Single value";
		FileUtil.createNewFileWithContent(modelFile, modelFileContent);
		standardModelProviderStarter.doYourJob();

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result -> no exception, because Single occurrs only once


		// Test 2 now with double occurrence
		FileUtil.createNewFileWithContent(modelFile, modelFileContent + FileUtil.getSystemLineSeparator() +
				                                     "  metainfo Single value");

		try {
			// call functionality under test
			standardModelProviderStarter.doYourJob();
			velocityClassBasedFileMakerStarter.doYourJob();
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// verify test result
			assertEquals("Error message", TextConstants.TEXT_MODEL_BREAKS_METAINFO_VALIDATORS, e.getMessage());
		}
	}

	@Test
	public void doesNotAllowDoubleOccurrencesForMandatoryMetaInfoValidation() throws Exception {
		// prepare test
		final File dir = velocityClassBasedFileMakerStarter.getMOGLiInfrastructure().getPluginInputDir();
		final File validationFile = new File(dir, MetaInfoValidationUtil.FILENAME_VALIDATION);
		final String validationRule = "|MetaInfo| Single |is| mandatory |for| classes |in| MOGLiCC_JavaBeanModel |.|";
		FileUtil.createNewFileWithContent(validationFile, validationRule);
		final String modelFileContent = "model MOGLiCC_JavaBeanModel" + FileUtil.getSystemLineSeparator() +
                                        "class de.Test" + FileUtil.getSystemLineSeparator() +
                                        "  metainfo Single value";
		FileUtil.createNewFileWithContent(modelFile, modelFileContent);
		standardModelProviderStarter.doYourJob();

		// call functionality under test
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result -> no exception, because Single occurrs only once


		// Test 2 now with double occurrence
		FileUtil.createNewFileWithContent(modelFile, modelFileContent + FileUtil.getSystemLineSeparator() +
				                                     "  metainfo Single value");

		try {
			// call functionality under test
			standardModelProviderStarter.doYourJob();
			velocityClassBasedFileMakerStarter.doYourJob();
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// verify test result
			assertEquals("Error message", TextConstants.TEXT_MODEL_BREAKS_METAINFO_VALIDATORS, e.getMessage());
		}
	}
	
}
