package com.iksgmbh.moglicc.generator.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;

public class MetaInfoValidationUtilUnitTest {

	@Test
	public void returnsMetaInfoValidatorFromLineOfInputFile() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "Validator mandatory MetaInfoTestName model";

		// call functionality under test
		final MetaInfoValidator validator = MetaInfoValidationUtil.parseLine(inputLine);

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
			MetaInfoValidationUtil.parseLine(inputLine);
		} catch (MOGLiPluginException e) {
			assertEquals("Error message", "Line not parsable as MetaInfoValidator: " + inputLine, e.getMessage());
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
			MetaInfoValidationUtil.parseLine(inputLine);
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
			MetaInfoValidationUtil.parseLine(inputLine);
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
			MetaInfoValidationUtil.parseLine(inputLine);
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
			MetaInfoValidationUtil.parseLine(inputLine);
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
			MetaInfoValidationUtil.parseLine(inputLine);
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
		final MetaInfoValidator metaInfoValidator = MetaInfoValidationUtil.parseLine(inputLine);
		
		// verify test result
		assertNull("Not expected", metaInfoValidator);
	}

	@Test
	public void returnsMetaInfoValidatorsForMetaInfoWithNamesContainingSpaces() throws MOGLiPluginException {
		// prepare test
		final String inputLine = "validator optional \"Java Type\" Attribute";

		// call functionality under test
		final MetaInfoValidator validator = MetaInfoValidationUtil.parseLine(inputLine);
		
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
		final MetaInfoValidator validator = MetaInfoValidationUtil.parseLine(inputLine);

		// verify test result
		assertNotNull("Not null expected", validator);
		assertEquals("nameOfValidModel", "ModelName", validator.getNameOfValidModel());
	}

}
