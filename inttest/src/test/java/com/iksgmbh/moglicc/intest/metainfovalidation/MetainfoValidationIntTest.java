package com.iksgmbh.moglicc.intest.metainfovalidation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.iksgmbh.moglicc.intest.IntTestParent;
import com.iksgmbh.moglicc.provider.model.standard.TextConstants;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validation.MetaInfoValidationUtil;
import com.iksgmbh.utils.FileUtil;

public class MetainfoValidationIntTest extends IntTestParent {

	@Test
	public void inserterPluginCausesValidationErrorWhileOtherGeneratorsExecuteSuccessfully() throws Exception {
		// prepare test
		final File dir = velocityModelBasedLineInserterStarter.getInfrastructure().getPluginInputDir();
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
		final File dir = velocityClassBasedFileMakerStarter.getInfrastructure().getPluginInputDir();
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
		final File dir = velocityClassBasedFileMakerStarter.getInfrastructure().getPluginInputDir();
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
		final File dir = velocityClassBasedFileMakerStarter.getInfrastructure().getPluginInputDir();
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

	@Test
	public void validatesAttributesForNotHavingACertainMetaInfoIfACertainConditionIsFalse() throws Exception {
		// prepare test
		final File dir = velocityClassBasedFileMakerStarter.getInfrastructure().getPluginInputDir();
		final File validationFile = new File(dir, MetaInfoValidationUtil.FILENAME_VALIDATION);
		final File testValidatorFile = new File(getProjectTestResourcesDir(), "metainfovalidation/MetaInfoValidatorsForConditionIsFalseTest.txt");
		final String testValidatorFileContent = FileUtil.getFileContent(testValidatorFile);
		final String withoutFalseValidatorFileContent = testValidatorFileContent.replace(
														MetaInfoValidationUtil.FALSE_IDENTIFIER,
														MetaInfoValidationUtil.TRUE_IDENTIFIER);

		final File conditionFile = new File(dir, "fieldTypeIsNumeric.condition");
		FileUtil.createNewFileWithContent(conditionFile, "|if MetaInfo| field-type |with value| numeric |exists.|");

		final File testModelfile = new File(getProjectTestResourcesDir(), "modelfiles/ModelFileConditionIsFalseTest.txt");
		final String modelFileContentWithProblem = FileUtil.getFileContent(testModelfile);
		final String textToReplace = "metainfo field-type text" + FileUtil.getSystemLineSeparator()
		                             + "    metainfo numeric-format Constants.FORMAT_NUMERIC";
		final String correctedModelFileContent = StringUtils.replace(modelFileContentWithProblem, 
				                                                     textToReplace, "metainfo field-type text");
		FileUtil.createNewFileWithContent(modelFile, modelFileContentWithProblem);

		final File logFile = standardModelProviderStarter.getInfrastructure().getPluginLogFile();

		// Test 1: call functionality under test WITH 'condtion is true'
		FileUtil.createNewFileWithContent(validationFile, withoutFalseValidatorFileContent);

		try {
			// call functionality under test
			standardModelProviderStarter.doYourJob();
			velocityClassBasedFileMakerStarter.doYourJob();
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// verify test result
			assertEquals("Error message", TextConstants.TEXT_MODEL_BREAKS_METAINFO_VALIDATORS, e.getMessage());

			final String expected = "MetaInfo 'numeric-format' was found too many times (expected: 0, actual: 1) " +
					                "for attribute 'netValue' of class 'Invoice' in model 'ConditionIsFalseTest'";
			assertFileContainsEntry(logFile, expected);
		}

		// Test 2: now call functionality under test WITH 'condtion is false'
		FileUtil.createNewFileWithContent(logFile, ""); // delete existing content
		FileUtil.createNewFileWithContent(validationFile, testValidatorFileContent);

		try {
			// call functionality under test
			standardModelProviderStarter.doYourJob();
			velocityClassBasedFileMakerStarter.doYourJob();
			fail("Expected exception was not thrown!");
		} catch (Exception e) {
			// verify test result
			assertEquals("Error message", TextConstants.TEXT_MODEL_BREAKS_METAINFO_VALIDATORS, e.getMessage());

			final String expected = "MetaInfo 'numeric-format' was found too many times (expected: 0, actual: 1) for attribute " +
					                "'taxId' of class 'Invoice' in model 'ConditionIsFalseTest'";
			assertFileContainsEntry(logFile, expected);
		}

		// Test 3: now call functionality under test WITH correct model file
		FileUtil.createNewFileWithContent(logFile, ""); // delete existing content
		FileUtil.createNewFileWithContent(modelFile, correctedModelFileContent);

		// call functionality under test
		standardModelProviderStarter.doYourJob();
		velocityClassBasedFileMakerStarter.doYourJob();

		// verify test result -> no exception
		assertFileContainsNoEntry(logFile, "Warning:");
	}

	
	@Test
	public void writesMetainfoValidationExceptionInReportFile() throws Exception {
		
		// prepare test
		final File dir = velocityClassBasedFileMakerStarter.getInfrastructure().getPluginInputDir();
		final File validationFile = new File(dir, MetaInfoValidationUtil.FILENAME_VALIDATION);
		final String validationRule = "|MetaInfo| JavaType  |is valid to occur| 2 |time(s) for| attributes |in| MOGLiCC_JavaBeanModel |.|";
		FileUtil.createNewFileWithContent(validationFile, validationRule);
		standardModelProviderStarter.doYourJob();
		
		// call functionality under test
		try {
			velocityClassBasedFileMakerStarter.doYourJob();
			fail("Expected exception was not thrown!");
		} 
		catch (Exception e) 
		{
			// verify test result
			final String report = standardModelProviderStarter.getProviderReport();
			assertStringContains(report, "MetaInfo 'JavaType' was found too few times");
		}
		 
	}
	
}
