package com.iksgmbh.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ClassNameDataUnitTest {
	
	@Test
	public void canNotCreateInstance_withLeadingAsterisk() {
		try {
			new ClassNameData("*Test.Test");
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void canNotCreateInstance_withLeadingSpace() {
		try {
			new ClassNameData(" Test.Test");
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void canNotCreateInstance_withTraildingSpace() {
		try {
			new ClassNameData("Test.Test ");
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void canNotCreateInstance_withLeadingDot() {
		try {
			new ClassNameData(".Test.Test");
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void canNotCreateInstance_withTraildingDot() {
		try {
			new ClassNameData("Test.Test.");
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void canNotCreateInstance_withLeadingLowerCaseLetterInSimpelName() {
		try {
			new ClassNameData("Test.aTest");
		} catch (Exception e) {
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void canNotCreateInstance_withDefaultPackage() {
		try {
			new ClassNameData("Test");
		} catch (Exception e) {
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	@Test
	public void canNotCreateInstance_withUpperCasePackage() {
		try {
			new ClassNameData("test1.Test2.Test3");
		} catch (Exception e) {
			return;
		}
		fail("Expected exception not thrown!");
	}
	
	
	@Test
	public void canCreateInstanceSuccessfullyWithFullyQualifiedClassname() {
		final ClassNameData className = new ClassNameData("test1.test2.Test3");
		assertEquals("simpelClassName", "Test3", className.getSimpleClassName());
		assertEquals("simpelClassName", "test1.test2", className.getPackageName());
		assertEquals("simpelClassName", "test1.test2.Test3", className.getFullyQualifiedClassname());
	}
	
	@Test
	public void canCreateInstanceSuccessfullyWithSimpleClassname() {
		final ClassNameData className = new ClassNameData("DateTime");
		assertEquals("simpelClassName", "DateTime", className.getSimpleClassName());
		assertEquals("simpelClassName", "org.joda.time", className.getPackageName());
		assertEquals("simpelClassName", "org.joda.time.DateTime", className.getFullyQualifiedClassname());
	}
	
	@Test
	public void returnsSubdirPackageHierarchy() {
		final ClassNameData className = new ClassNameData("DateTime");
		assertEquals("subdirPackageHierarchy", "org/joda/time", className.getSubdirPackageHierarchy());
	}}
