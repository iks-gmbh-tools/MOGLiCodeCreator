package com.iksgmbh.moglicc.provider.model.standard.metainfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validator.ConditionalMetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validator.NumOccurMetaInfoValidator;

public class MetaInfoValidationUtilUnitTest {

	@Test
	public void returnsMetaInfoValidatorFromLineOfInputFile() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "Validator mandatory MetaInfoTestName model";

		// call functionality under test
		final MetaInfoValidator validator = MetaInfoValidationUtil.parseValidatorLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", validator);
		assertEquals("MetaInfoName", "MetaInfoTestName", validator.getMetaInfoName());
		assertEquals("MetaInfoHierarchyLevel", MetaInfo.HierarchyLevel.Model , validator.getMetaInfoHierarchyLevel());
		assertEquals("validatorType", MetaInfoValidator.ValidationType.Mandatory, validator.getValidationType());
	}

	@Test
	public void throwsExceptionIfAnnotationTagNotFound() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "validato mandatory MetaInfoTestName Model";

		// call functionality under test
		try {
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
		} catch (MOGLiPluginException e) {
			assertEquals("Error message", "Line not parsable as MetaInfoValidator: <validato mandatory MetaInfoTestName Model>", e.getMessage());
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionIfValidatorTypeNotFound() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "validator mandatoryX MetaInfoTestName Model";

		// call functionality under test
		try {
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
		} catch (MOGLiPluginException e) {
			assertEquals("Error message", "Unknown ValidationType <mandatoryX>.", e.getMessage());
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionIfHierarchyLevelNotFound() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "validator mandatory MetaInfoTestName ModelX";

		// call functionality under test
		try {
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
		} catch (MOGLiPluginException e) {
			assertEquals("Error message", "Unknown MetaInfoHierarchyLevel <ModelX>.", e.getMessage());
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionIfMetaInfoNameNotFound() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "validator mandatory Model";

		// call functionality under test
		try {
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
		} catch (MOGLiPluginException e) {
			assertEquals("Error message", "MetaInfoName or MetaInfoHierarchyLevel is missing.\n" +
					                      "Error parsing Annotation [name=mandatory, additionalInfo=Model]", e.getMessage());
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionIfMetaInfoNameAndHierarchyLevelNotFound() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "validator mandatory ";

		// call functionality under test
		try {
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
		} catch (MOGLiPluginException e) {
			assertEquals("Error message", "Missing information parsing Annotation [name=mandatory, additionalInfo=null]", e.getMessage());
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionIfValidatorTypeAndMetaInfoNameAndHierarchyLevelNotFound() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "validator ";

		// call functionality under test
		try {
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
		} catch (MOGLiPluginException e) {
			assertEquals("Error message", "Unknown ValidationType <>.", e.getMessage());
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void returnsNullForCommentLine() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "#validator ";

		// call functionality under test
		final MetaInfoValidator metaInfoValidator = MetaInfoValidationUtil.parseValidatorLine(inputLine);

		// verify test result
		assertNull("Not expected", metaInfoValidator);
	}

	@Test
	public void returnsMetaInfoValidatorsForMetaInfoWithNamesContainingSpaces() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "validator optional \"Java Type\" Attribute";

		// call functionality under test
		final MetaInfoValidator validator = MetaInfoValidationUtil.parseValidatorLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", validator);
		assertEquals("MetaInfoName", "Java Type", validator.getMetaInfoName());
		assertEquals("MetaInfoHierarchyLevel", MetaInfo.HierarchyLevel.Attribute , validator.getMetaInfoHierarchyLevel());
		assertEquals("validatorType", MetaInfoValidator.ValidationType.Optional, validator.getValidationType());
	}

	@Test
	public void returnsMetaInfoValidatorFromLineOfInputFileWithModelName() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "Validator mandatory MetaInfoTestName model ModelName";

		// call functionality under test
		final MetaInfoValidator validator = MetaInfoValidationUtil.parseValidatorLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", validator);
		assertEquals("nameOfValidModel", "ModelName", validator.getNameOfValidModel());
	}

	@Test
	public void throwsExceptionForUnparsableConditionLine() throws MOGLiPluginException {
		// prepare test
		final File conditionFile = new File("../common/src/test/resources/ConditionParseFailure.test");

		// call functionality under test
		try {
			MetaInfoValidationUtil.getConditionList(conditionFile);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Line not parsable as Condition: <AND>", e.getMessage());
		}
	}


	@Test
	public void returnsConditionListFromFile() throws MOGLiPluginException {
		// prepare test
		final File conditionFile = new File("../common/src/test/resources/Condition.test");

		// call functionality under test
		final List<List<MetaInfoValidationCondition>> conditionList = MetaInfoValidationUtil.getConditionList(conditionFile);

		// verify test result
		assertNotNull("Not null expected", conditionList);
		assertEquals("condition block number", 4, conditionList.size());
		assertEquals("condition number", 3, conditionList.get(0).size());
		assertEquals("condition number", 1, conditionList.get(1).size());
		assertEquals("condition number", 1, conditionList.get(2).size());
		assertEquals("condition number", 2, conditionList.get(3).size());
	}

	@Test
	public void returnsMetaInfoValidatorListFromFile() throws MOGLiPluginException {
		// prepare test
		final File validationFile = new File("../common/src/test/resources/MetaInfoValidator.test");

		// call functionality under test
		final List<MetaInfoValidator> metaInfoValidatorList = MetaInfoValidationUtil.getMetaInfoValidatorList(validationFile, null);

		// verify test result
		assertNotNull("Not null expected", metaInfoValidatorList);
		assertEquals("validator number", 4, metaInfoValidatorList.size());
	}

	// #####################################  new validation DSL  ###########################################

	@Test
	public void returnsMetaInfoValidatorFromLineForSimpleOptionalValidator() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| MetaInfoTestName |is| optional |for| attributes |in| ModelName |.|";

		// call functionality under test
		final MetaInfoValidator validator = MetaInfoValidationUtil.parseValidatorLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", validator);
		assertEquals("MetaInfoName", "MetaInfoTestName", validator.getMetaInfoName());
		assertEquals("ValidationType", "Optional", validator.getValidationType().toString());
		assertEquals("HierarchyLevel", "Attribute", validator.getMetaInfoHierarchyLevel().toString());
		assertEquals("nameOfValidModel", "ModelName", validator.getNameOfValidModel());
	}

	@Test
	public void returnsMetaInfoValidatorFromLineForSimpleOptionalValidatorWithSpacesAndDoubleQuotes() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| Meta Info \"Test Name\" |is| optional |for| attributes |in| ModelName |.|";

		// call functionality under test
		final MetaInfoValidator validator = MetaInfoValidationUtil.parseValidatorLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", validator);
		assertEquals("MetaInfoName", "Meta Info \"Test Name\"", validator.getMetaInfoName());
		assertEquals("ValidationType", "Optional", validator.getValidationType().toString());
		assertEquals("HierarchyLevel", "Attribute", validator.getMetaInfoHierarchyLevel().toString());
		assertEquals("nameOfValidModel", "ModelName", validator.getNameOfValidModel());
	}

	@Test
	public void returnsMetaInfoValidatorFromLineForSimpleMandatoryValidator() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| MetaInfoTest |is| Mandatory |for| classes |in| Model |.|";

		// call functionality under test
		final MetaInfoValidator validator = MetaInfoValidationUtil.parseValidatorLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", validator);
		assertEquals("MetaInfoName", "MetaInfoTest", validator.getMetaInfoName());
		assertEquals("ValidationType", "Mandatory", validator.getValidationType().toString());
		assertEquals("HierarchyLevel", "Class", validator.getMetaInfoHierarchyLevel().toString());
		assertEquals("nameOfValidModel", "Model", validator.getNameOfValidModel());
	}

	@Test
	public void failsToParseMetaInfoValidatorDueToIncorrectMetaInfoIdentifier() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo MetaInfoTest |is| Mandatory |for| classes |in| Model";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Line not parsable as MetaInfoValidator: <|MetaInfo MetaInfoTest |is| Mandatory |for| classes |in| Model>", e.getMessage());
		}
	}

	@Test
	public void failsToParseMetaInfoValidatorDueToIncorrectValidationTypeIdentifier() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| MetaInfoTest is| Mandatory |for| classes |in| Model";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Cannot parse ValidationType/Occurrence for MetaInfoValidator <|MetaInfo| MetaInfoTest is| Mandatory |for| classes |in| Model>", e.getMessage());
		}
	}

	@Test
	public void failsToParseMetaInfoValidatorDueToIncorrectHierarchyLevelIdentifier() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| MetaInfoTest |is| Mandatory |for classes |in| Model";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Cannot parse Validation Type for MetaInfoValidator <|MetaInfo| MetaInfoTest |is| Mandatory |for classes |in| Model>", e.getMessage());
		}
	}

	@Test
	public void failsToParseMetaInfoValidatorDueToIncorrectModelIdentifier() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| MetaInfoTest |is| Mandatory |for| classes |i| Model";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Cannot parse Hierarchy Level for MetaInfoValidator <|MetaInfo| MetaInfoTest |is| Mandatory |for| classes |i| Model>", e.getMessage());
		}
	}

	@Test
	public void failsToParseMetaInfoValidatorDueToMissingDotIdentifier() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| MetaInfoTest |is| Mandatory |for| classes |in| Model ||";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Mandatory and Optional MetaInfoValidator must end with |.|: <|MetaInfo| MetaInfoTest |is| Mandatory |for| classes |in| Model ||>", e.getMessage());
		}
	}

	@Test
	public void failsToParseMetaInfoValidatorDueToIncorrectOccurrenceIdentifier() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| Name |ocurs| 1 |time(s) for| attributes  |in| Model |iff| condition1.txt |is true|";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Cannot parse ValidationType/Occurrence for MetaInfoValidator <|MetaInfo| Name |ocurs| 1 |time(s) for| attributes  |in| Model |iff| condition1.txt |is true|>", e.getMessage());
		}
	}

	@Test
	public void failsToParseMetaInfoValidatorDueToIncorrectTimesForIdentifier() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| Name |is valid to occur| 1 |time(s for| attributes  |in| Model |iff| condition1.txt |is true|";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Cannot parse occurence for MetaInfoValidator <|MetaInfo| Name |is valid to occur| 1 |time(s for| attributes  |in| Model |iff| condition1.txt |is true|>", e.getMessage());
		}
	}

	@Test
	public void failsToParseMetaInfoValidatorDueToIncorrectInIdentifier() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| Name |is valid to occur| 1 |time(s) for| attributes in| Model |iff| condition1.txt |is true|";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Cannot parse HierarchyLevel for MetaInfoValidator <|MetaInfo| Name |is valid to occur| 1 |time(s) for| attributes in| Model |iff| condition1.txt |is true|>", e.getMessage());
		}
	}

	@Test
	public void failsToParseMetaInfoValidatorDueToIncorrectConditionIdentifier() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| Name |is valid to occur| 1 |time(s) for| attributes  |in| Model |iff| condition1.txt |is true|";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Either |if|-Identifier is wrong or MetaInfoValidator without condition does not end with |.|: <|MetaInfo| Name |is valid to occur| 1 |time(s) for| attributes  |in| Model |iff| condition1.txt |is true|>", e.getMessage());
		}
	}

	@Test
	public void failsToParseMetaInfoValidatorDueToMissingCondition() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| Name |is valid to occur| 1 |time(s) for| attributes  |in| Model |if|  ";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Condition of MetaInfoValidator must end with |is true.|: <|MetaInfo| Name |is valid to occur| 1 |time(s) for| attributes  |in| Model |if|>", e.getMessage());
		}
	}

	@Test
	public void returnsMetaInfoValidatorFromLineForConditionalValidatorWithoutConditionFile() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| N a \" m e |is valid to occur| 1 |time(s) for| attributes |in| ModelName |.|";

		// call functionality under test
		final MetaInfoValidator validator = MetaInfoValidationUtil.parseValidatorLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", validator);
		assertEquals("MetaInfoName", "N a \" m e", validator.getMetaInfoName());
		assertEquals("ValidationType", "NumOccur", validator.getValidationType().toString());
		assertEquals("HierarchyLevel", "Attribute", validator.getMetaInfoHierarchyLevel().toString());
		assertEquals("nameOfValidModel", "ModelName", validator.getNameOfValidModel());

		NumOccurMetaInfoValidator numOccurMetaInfoValidator = (NumOccurMetaInfoValidator) validator;
		assertEquals("MaxOccurs", 1, numOccurMetaInfoValidator.getMaxOccurs());
		assertEquals("MinOccurs", 1, numOccurMetaInfoValidator.getMinOccurs());
	}

	@Test
	public void returnsMetaInfoValidatorFromLineForConditionalValidatorWithConditionFile() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| Name |is valid to occur| 1 |time(s) for| attributes |in| ModelName |if| test.txt |is true.|";

		// call functionality under test
		final MetaInfoValidator validator = MetaInfoValidationUtil.parseValidatorLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", validator);
		assertEquals("MetaInfoName", "Name", validator.getMetaInfoName());
		assertEquals("ValidationType", "Conditional", validator.getValidationType().toString());
		assertEquals("HierarchyLevel", "Attribute", validator.getMetaInfoHierarchyLevel().toString());
		assertEquals("nameOfValidModel", "ModelName", validator.getNameOfValidModel());

		ConditionalMetaInfoValidator conditionalMetaInfoValidator = (ConditionalMetaInfoValidator) validator;
		assertEquals("MaxOccurs", 1, conditionalMetaInfoValidator.getMaxOccurs());
		assertEquals("MinOccurs", 1, conditionalMetaInfoValidator.getMinOccurs());
		assertEquals("ConditionFilename", "test.txt", conditionalMetaInfoValidator.getConditionFilename());
	}

	@Test
	public void failsToParseMetaInfoValidatorDueToMissingDotIdendifier() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| Name |is valid to occur| 1 |time(s) for| attributes  |in| Model ";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Either |if|-Identifier is wrong or MetaInfoValidator without condition does not end with |.|: <|MetaInfo| Name |is valid to occur| 1 |time(s) for| attributes  |in| Model>", e.getMessage());
		}
	}

	@Test
	public void failsToParseMetaInfoValidatorDueToMissingTrueSetting() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| Name |is valid to occur| 1 |time(s) for| attributes  |in| Model |if| text.txt";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Condition of MetaInfoValidator must end with |is true.|: <|MetaInfo| Name |is valid to occur| 1 |time(s) for| attributes  |in| Model |if| text.txt>", e.getMessage());
		}
	}

	@Test
	public void returnsConditionFromLineWithValueAndMustExist() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|if MetaInfo| Name |with value| value |exists.|";

		// call functionality under test
		final MetaInfoValidationCondition condition = MetaInfoValidationUtil.parseConditionLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", condition);
		assertEquals("OtheMetaInfoName", "Name", condition.getOtherMetaInfoName());
		assertEquals("OtherMetaInfoValue", "value", condition.getOtherMetaInfoValue());
		assertEquals("MustNotHaveotherMetaInfo", false, condition.getMustNotHaveotherMetaInfo());
	}

	@Test
	public void returnsConditionFromLineWithValueAndMustNotExist() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|if MetaInfo| Name |with value| value |does not exist.|";

		// call functionality under test
		final MetaInfoValidationCondition condition = MetaInfoValidationUtil.parseConditionLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", condition);
		assertEquals("OtheMetaInfoName", "Name", condition.getOtherMetaInfoName());
		assertEquals("OtherMetaInfoValue", "value", condition.getOtherMetaInfoValue());
		assertEquals("MustNotHaveotherMetaInfo", true, condition.getMustNotHaveotherMetaInfo());
	}

	@Test
	public void returnsConditionFromLineWithoutValue() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|if MetaInfo| Name |exists.|";

		// call functionality under test
		final MetaInfoValidationCondition condition = MetaInfoValidationUtil.parseConditionLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", condition);
		assertEquals("OtheMetaInfoName", "Name", condition.getOtherMetaInfoName());
		assertEquals("OtherMetaInfoValue", null, condition.getOtherMetaInfoValue());
		assertEquals("MustNotHaveotherMetaInfo", false, condition.getMustNotHaveotherMetaInfo());
	}

	@Test
	public void returnsConditionFromLineWithSpacesAndDoubleQuotesInName() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|if MetaInfo| The \"other\" MetaInfo Name |exists.|   ";

		// call functionality under test
		final MetaInfoValidationCondition condition = MetaInfoValidationUtil.parseConditionLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", condition);
		assertEquals("OtheMetaInfoName", "The \"other\" MetaInfo Name", condition.getOtherMetaInfoName());
		assertEquals("OtherMetaInfoValue", null, condition.getOtherMetaInfoValue());
		assertEquals("MustNotHaveotherMetaInfo", false, condition.getMustNotHaveotherMetaInfo());
	}

	@Test
	public void returnsConditionFromLineWithSpacesAndDoubleQuotesInNameAndValue() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|if MetaInfo| The \"other\" MetaInfo Name |with value|va\" \"lue|exists.|   ";

		// call functionality under test
		final MetaInfoValidationCondition condition = MetaInfoValidationUtil.parseConditionLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", condition);
		assertEquals("OtheMetaInfoName", "The \"other\" MetaInfo Name", condition.getOtherMetaInfoName());
		assertEquals("OtherMetaInfoValue", "va\" \"lue", condition.getOtherMetaInfoValue());
		assertEquals("MustNotHaveotherMetaInfo", false, condition.getMustNotHaveotherMetaInfo());
	}

	@Test
	public void failsToParseConditionFromLineDueToMissingName() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|if MetaInfo|  ";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseConditionLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "MetaInfoValidationCondition line must end with |exists.| or |does not exist.|: <|if MetaInfo|>.", e.getMessage());
		}
	}

	@Test
	public void failsToParseConditionFromLineDueToMissingExistIdentifier() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|if MetaInfo|  Name ";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseConditionLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "MetaInfoValidationCondition line must end with |exists.| or |does not exist.|: <|if MetaInfo|  Name>.", e.getMessage());
		}
	}

	@Test
	public void returnsErrorWhenMetaInfoNameIsMissing() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| |is valid to occur| 1 |time(s) for| attributes |in| ModelName |.|";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "No MetaInfoName defined for MetaInfoValidator <|MetaInfo| |is valid to occur| 1 |time(s) for| attributes |in| ModelName |.|>", e.getMessage());
		}
	}

	@Test
	public void returnsErrorWhenOccurenceIsMissing() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo|MetaInfoName|is valid to occur||time(s) for| attributes |in| ModelName |.|";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "No occurence defined for MetaInfoValidator <|MetaInfo|MetaInfoName|is valid to occur||time(s) for| attributes |in| ModelName |.|>", e.getMessage());
		}
	}

	@Test
	public void returnsErrorWhenHierarchyLevelIsUnkown() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| MetaInfoName |is valid to occur| 1 |time(s) for| attribu |in| ModelName |.|";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Unknown Descriptor for MetaInfoHierarchyLevel <Attribu>.", e.getMessage());
		}
	}

	@Test
	public void returnsErrorWhenModelNameIMissing() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| MetaInfoName |is valid to occur| 1 |time(s) for| attributes |in| |.|";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "No Model Name defined for MetaInfoValidator <|MetaInfo| MetaInfoName |is valid to occur| 1 |time(s) for| attributes |in| |.|>", e.getMessage());
		}
	}

	@Test
	public void returnsErrorWhenMetaInfoNameForOptionalValidatorIsMissing() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| |is| optional |for| attributes |in| ModelName |.|";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "No MetaInfoName defined for MetaInfoValidator <|MetaInfo| |is| optional |for| attributes |in| ModelName |.|>", e.getMessage());
		}
	}

	@Test
	public void returnsErrorWhenModelNameForOptionalValidatorIsMissing() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| metaInfoName|is| optional |for| attributes |in| |.|";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "No Model Name defined for MetaInfoValidator <|MetaInfo| metaInfoName|is| optional |for| attributes |in| |.|>", e.getMessage());
		}
	}

	@Test
	public void returnsErrorWhenHierarchyLevelForOptionalValidatorIsMissing() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| metaInfoName|is| optional |for| |in| model |.|";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "No Hierarchy Level defined for MetaInfoValidator <|MetaInfo| metaInfoName|is| optional |for| |in| model |.|>", e.getMessage());
		}
	}

	@Test
	public void returnsErrorWhenHierarchyLevelForOptionalValidatorIsUnkown() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| metaInfoName|is| optional |for| clas |in| model |.|";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Unknown Descriptor for MetaInfoHierarchyLevel <clas>.", e.getMessage());
		}
	}

	@Test
	public void returnsErrorWhenValidationTypeIsUnkown() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| metaInfoName|is| option |for| classes |in| model |.|";

		try {
			// call functionality under test
			MetaInfoValidationUtil.parseValidatorLine(inputLine);
			fail("Expected exception was not thrown!");
		} catch (Exception e) {

			// verify test result
			assertEquals("Error message", "Unknown ValidationType <|MetaInfo| metaInfoName|is| option |for| classes |in| model |.|>.", e.getMessage());
		}
	}

	@Test
	public void returnsMetaInfoValidatorFromLineForConditionalValidatorWithValueAndConditionFile() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| Name |with value| Value |is valid to occur| 4 |time(s) for| attributes |in| ModelName |if| test.txt |is true.|";

		// call functionality under test
		final MetaInfoValidator validator = MetaInfoValidationUtil.parseValidatorLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", validator);
		assertEquals("MetaInfoName", "Name", validator.getMetaInfoName());
		assertEquals("ValidationType", "Conditional", validator.getValidationType().toString());
		assertEquals("HierarchyLevel", "Attribute", validator.getMetaInfoHierarchyLevel().toString());
		assertEquals("nameOfValidModel", "ModelName", validator.getNameOfValidModel());

		ConditionalMetaInfoValidator conditionalMetaInfoValidator = (ConditionalMetaInfoValidator) validator;
		assertEquals("MetaInfoValue", "Value", conditionalMetaInfoValidator.getMetaInfoValue());
		assertEquals("MaxOccurs", 4, conditionalMetaInfoValidator.getMaxOccurs());
		assertEquals("MinOccurs", 4, conditionalMetaInfoValidator.getMinOccurs());
		assertEquals("ConditionFilename", "test.txt", conditionalMetaInfoValidator.getConditionFilename());
	}

	@Test
	public void returnsMetaInfoValidatorFromLineForConditionalValidatorWithValue() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "|MetaInfo| Name |with value| Value |is valid to occur| 3-8 |time(s) for| attributes |in| ModelName |.|";

		// call functionality under test
		final MetaInfoValidator validator = MetaInfoValidationUtil.parseValidatorLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", validator);
		assertEquals("MetaInfoName", "Name", validator.getMetaInfoName());
		assertEquals("ValidationType", "NumOccur", validator.getValidationType().toString());
		assertEquals("HierarchyLevel", "Attribute", validator.getMetaInfoHierarchyLevel().toString());
		assertEquals("nameOfValidModel", "ModelName", validator.getNameOfValidModel());

		final NumOccurMetaInfoValidator numOccurMetaInfoValidator = (NumOccurMetaInfoValidator) validator;
		assertEquals("MetaInfoValue", "Value", numOccurMetaInfoValidator.getMetaInfoValue());
		assertEquals("MaxOccurs", 8, numOccurMetaInfoValidator.getMaxOccurs());
		assertEquals("MinOccurs", 3, numOccurMetaInfoValidator.getMinOccurs());
	}
}


