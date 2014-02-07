package com.iksgmbh.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.iksgmbh.data.Annotation;

public class AnnotationParserUnitTests {

	@Test
	public void detectsAnnotationWithDefaultIdentificator() {
		final String line = " @dir" ;
		boolean result = AnnotationParser.getInstance().hasCorrectPrefix(line);
		assertTrue("Annotation not detected", result);
	}
	
	@Test
	public void doesNotDetectAnnotation() {
		final String line = "dir" ;
		boolean result = AnnotationParser.getInstance().hasCorrectPrefix(line);
		assertFalse("Annotation wrongly detected", result);
	}
	
	@Test
	public void detectsAnnotationWithModifiedIdentificator() {
		// prepare test
		final AnnotationParser annotationParser = 
			            AnnotationParser.getInstance("+", AnnotationParser.DEFAULT_COMMENT_IDENTIFICATOR);
		final String line = " +dir" ;
		
		// call functionality under test
		final boolean result = annotationParser.hasCorrectPrefix(line);
		
		// verify test result
		assertTrue("Annotation not detected", result);
	}
	
	@Test
	public void returnsNullIfLineIsNoAnnotation() {
		final String line = "X" ;
		final Annotation result = AnnotationParser.getInstance().doYourJob(line);
		assertNull("Null expected", result);
	}
	
	@Test
	public void returnsAnnotationWithAdditionalInfoAndComment() {
		final String line = "@dir additionalInfo # comment" ;
		final Annotation result = AnnotationParser.getInstance().doYourJob(line);
		assertNotNull("Not null expected", result);
		assertEquals("Annotation Name", "dir", result.getName());
		assertNotNull("Not null expected", result.getAdditionalInfo());
		assertEquals("additionalInfo Name", "additionalInfo", result.getAdditionalInfo());
	}
	
	@Test
	public void returnsAnnotationWithoutAdditionalInfo() {
		final String line = "@Name   " ;
		final Annotation result = AnnotationParser.getInstance().doYourJob(line);
		assertNotNull("Not null expected", result);
		assertEquals("Annotation Name", "Name", result.getName());
		assertNull("Null expected", result.getAdditionalInfo());
	}
	
	@Test
	public void returnsAnnotationWithErrorMessageAsNameIfParsingErrorOccurrs() {
		// prepare test
		final String line = "@\"Name   " ;		

		// call functionality under test
		final Annotation result = AnnotationParser.getInstance().doYourJob(line);

		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("Annotation Name", "PARSER ERROR: Unable to parse annotated line content: <\"Name>", result.getName());
		assertNull("Null expected", result.getAdditionalInfo());
	}

	@Test
	public void returnsAnnotationWithSpaceInName() {
		// prepare test
		final String line = "@\"Name of Annotation\"";		

		// call functionality under test
		final Annotation result = AnnotationParser.getInstance().doYourJob(line);

		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("Annotation Name", "Name of Annotation", result.getName());
		assertNull("Null expected", result.getAdditionalInfo());
	}

	@Test
	public void returnsAnnotationWithSpaceInNameAndAdditionalInformation() {
		// prepare test
		final String line = "@\"Name of Annotation\" \"a b c\"";		

		// call functionality under test
		final Annotation result = AnnotationParser.getInstance().doYourJob(line);

		// verify test result
		assertNotNull("Not null expected", result);
		assertEquals("Annotation Name", "Name of Annotation", result.getName());
		assertEquals("Annotation Name", "\"a b c\"", result.getAdditionalInfo());
	}

	@Test
	public void removesPartBraceIdentifier() {
		// prepare test
		final String line = "@\"Name of Annotation\" \"a b c\"";
		final AnnotationParser annotationParser = AnnotationParser.getInstance();
		final Annotation annotation = annotationParser.doYourJob(line);
		final String additionalInfo = annotation.getAdditionalInfo();
		assertEquals("additionalInfo", "\"a b c\"", additionalInfo);

		// call functionality under test
		final String result = annotationParser.removePartBraceIdentifier(additionalInfo);
		
		// verify test result
		assertEquals("Annotation Name", "a b c", result);
	}

		
}
