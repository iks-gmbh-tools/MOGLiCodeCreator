package com.iksgmbh.moglicc.lineinserter.modelbased.velocity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData.KnownGeneratorPropertyNames;
import com.iksgmbh.moglicc.lineinserter.modelbased.velocity.VelocityLineInserterResultData.KnownInserterPropertyNames;

public class VelocityLineInserterResultDataUnitTest {

	private VelocityLineInserterResultData velocityResult;
	private BuildUpGeneratorResultData buildUpGeneratorResultData;

	@Before
	public void setup() {
		buildUpGeneratorResultData = buildGeneratorResultData();
		buildUpGeneratorResultData.setGeneratedContent("Content");
		buildUpGeneratorResultData.addProperty(KnownGeneratorPropertyNames.TargetFileName.name(), "filename");
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.ReplaceStart.name(), "ReplaceStart");
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.ReplaceEnd.name(), "ReplaceEnd");
		velocityResult = new VelocityLineInserterResultData(buildUpGeneratorResultData);
	}

	private BuildUpGeneratorResultData buildGeneratorResultData() {
		final BuildUpGeneratorResultData buildUpGeneratorResultData = new BuildUpGeneratorResultData();
		buildUpGeneratorResultData.setGeneratedContent("Content");
		buildUpGeneratorResultData.addProperty(KnownGeneratorPropertyNames.TargetFileName.name(), "filename");
		buildUpGeneratorResultData.addProperty(KnownGeneratorPropertyNames.TargetDir.name(), "targetDir");
		return buildUpGeneratorResultData;
	}

	@Test
	public void returnsTargetFileName() {
		// call functionality under test
		final String targetFileName = velocityResult.getTargetFileName();

		// verify test result
		assertEquals("targetFileName", "filename", targetFileName);
	}

	@Test
	public void returnsReplaceStartIndicator() {
		// call functionality under test
		final String ReplaceStartIndicator = velocityResult.getReplaceStartIndicator();

		// verify test result
		assertEquals("ReplaceStartIndicator", "ReplaceStart", ReplaceStartIndicator);
	}

	@Test
	public void returnsReplaceEndIndicator() {
		// call functionality under test
		final String ReplaceEndIndicator = velocityResult.getReplaceEndIndicator();

		// verify test result
		assertEquals("ReplaceEndIndicator", "ReplaceEnd", ReplaceEndIndicator);
	}

	@Test
	public void returnsAboveIndicator() {
		// prepare test
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.InsertAbove.name(), "InsertAbove");
		velocityResult = new VelocityLineInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		final String InsertAboveIndicator = velocityResult.getInsertAboveIndicator();

		// verify test result
		assertEquals("InsertAboveIndicator", "InsertAbove", InsertAboveIndicator);
	}

	@Test
	public void returnsBelowIndicator() {
		// prepare test
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.InsertBelow.name(), "InsertBelow");
		velocityResult = new VelocityLineInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		final String InsertBelowIndicator = velocityResult.getInsertBelowIndicator();

		// verify test result
		assertEquals("InsertBelowIndicator", "InsertBelow", InsertBelowIndicator);
	}


	@Test
	public void throwsExceptionForMissingEndInstruction() {
		// prepare test
		buildUpGeneratorResultData = buildGeneratorResultData();
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.ReplaceStart.name(), "Start");
		velocityResult = new VelocityLineInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityResult.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), VelocityLineInserterResultData.MISSING_REPLACE_CONFIGURATION);
			return;
		}
		fail("Expected exception not thrown!");
	}

	private void assertStringContains(final String s, final String substring) {
		final boolean expectedSubstringFound = s.contains(substring);
		assertTrue("Expected substring not found in String."
				+ "\nSubstring <" + substring + ">"
				+ "\nString <" + s + ">", expectedSubstringFound);
	}


	@Test
	public void throwsExceptionForMissingStartInstruction() {
		// prepare test
		buildUpGeneratorResultData = buildGeneratorResultData();
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.ReplaceEnd.name(), "End");
		velocityResult = new VelocityLineInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityResult.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), VelocityLineInserterResultData.MISSING_REPLACE_CONFIGURATION);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionForInsertBelowAndAboveInstruction() throws Exception {
		// prepare test
		buildUpGeneratorResultData = buildGeneratorResultData();
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.InsertAbove.name(), "ABOVE");
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.InsertBelow.name(), "BELOW");
		velocityResult = new VelocityLineInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityResult.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), VelocityLineInserterResultData.INVALID_INSERT_CONFIGURATION);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionForReplaceAndAboveInstruction() throws Exception {
		// prepare test
		buildUpGeneratorResultData = buildGeneratorResultData();
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.ReplaceStart.name(), "START");
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.ReplaceEnd.name(), "END");
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.InsertBelow.name(), "BELOW");
		velocityResult = new VelocityLineInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityResult.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), VelocityLineInserterResultData.INVALID_MIXED_CONFIGURATION);
			return;
		}
		fail("Expected exception not thrown!");
	}


	@Test
	public void throwsExceptionForReplaceAndBelowInstruction() throws Exception {
		// prepare test
		buildUpGeneratorResultData = buildGeneratorResultData();
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.ReplaceStart.name(), "START");
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.ReplaceEnd.name(), "END");
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.InsertAbove.name(), "ABOVE");
		velocityResult = new VelocityLineInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityResult.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), VelocityLineInserterResultData.INVALID_MIXED_CONFIGURATION);
			return;
		}
		fail("Expected exception not thrown!");
	}


	@Test
	public void throwsExceptionForCreateNewTrueAndAboveInstruction() throws Exception {
		// prepare test
		buildUpGeneratorResultData = buildGeneratorResultData();
		buildUpGeneratorResultData.addProperty(KnownGeneratorPropertyNames.CreateNew.name(), "true");
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.InsertAbove.name(), "ABOVE");
		velocityResult = new VelocityLineInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityResult.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), VelocityLineInserterResultData.CREATE_NEW_MIXED_CONFIGURATION);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionForCreateNewTrueAndBelowInstruction() throws Exception {
		// prepare test
		buildUpGeneratorResultData = buildGeneratorResultData();
		buildUpGeneratorResultData.addProperty(KnownGeneratorPropertyNames.CreateNew.name(), "true");
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.InsertBelow.name(), "Below");
		velocityResult = new VelocityLineInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityResult.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), VelocityLineInserterResultData.CREATE_NEW_MIXED_CONFIGURATION);
			return;
		}
		fail("Expected exception not thrown!");
	}

	@Test
	public void throwsExceptionForCreateNewTrueAndReplaceInstruction() throws Exception {
		// prepare test
		buildUpGeneratorResultData = buildGeneratorResultData();
		buildUpGeneratorResultData.addProperty(KnownGeneratorPropertyNames.CreateNew.name(), "true");
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.ReplaceStart.name(), "ReplaceStart");
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.ReplaceEnd.name(), "ReplaceEnd");
		velocityResult = new VelocityLineInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityResult.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), VelocityLineInserterResultData.CREATE_NEW_MIXED_CONFIGURATION);
			return;
		}
		fail("Expected exception not thrown!");
	}

}
