package com.iksgmbh.moglicc.inserter.modelbased.velocity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData.KnownGeneratorPropertyNames;
import com.iksgmbh.moglicc.inserter.modelbased.velocity.VelocityInserterResultData.KnownInserterPropertyNames;

public class BuildUpVelocityInserterResultDataUnitTest {

	private BuildUpVelocityInserterResultData velocityInserterResultData;
	private BuildUpGeneratorResultData buildUpGeneratorResultData;

	@Before
	public void setup() {
		buildUpGeneratorResultData = buildGeneratorResultData();
		buildUpGeneratorResultData.setGeneratedContent("Content");
		buildUpGeneratorResultData.addProperty(KnownGeneratorPropertyNames.TargetFileName.name(), "filename");
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.ReplaceStart.name(), "ReplaceStart");
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.ReplaceEnd.name(), "ReplaceEnd");
		velocityInserterResultData = new BuildUpVelocityInserterResultData(buildUpGeneratorResultData);
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
		final String targetFileName = velocityInserterResultData.getTargetFileName();

		// verify test result
		assertEquals("targetFileName", "filename", targetFileName);
	}

	@Test
	public void returnsReplaceStartIndicator() {
		// call functionality under test
		final String ReplaceStartIndicator = velocityInserterResultData.getReplaceStartIndicator();

		// verify test result
		assertEquals("ReplaceStartIndicator", "ReplaceStart", ReplaceStartIndicator);
	}

	@Test
	public void returnsReplaceEndIndicator() {
		// call functionality under test
		final String ReplaceEndIndicator = velocityInserterResultData.getReplaceEndIndicator();

		// verify test result
		assertEquals("ReplaceEndIndicator", "ReplaceEnd", ReplaceEndIndicator);
	}

	@Test
	public void returnsAboveIndicator() {
		// prepare test
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.InsertAbove.name(), "InsertAbove");
		velocityInserterResultData = new BuildUpVelocityInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		final String InsertAboveIndicator = velocityInserterResultData.getInsertAboveIndicator();

		// verify test result
		assertEquals("InsertAboveIndicator", "InsertAbove", InsertAboveIndicator);
	}

	@Test
	public void returnsBelowIndicator() {
		// prepare test
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.InsertBelow.name(), "InsertBelow");
		velocityInserterResultData = new BuildUpVelocityInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		final String InsertBelowIndicator = velocityInserterResultData.getInsertBelowIndicator();

		// verify test result
		assertEquals("InsertBelowIndicator", "InsertBelow", InsertBelowIndicator);
	}


	@Test
	public void throwsExceptionForMissingEndInstruction() {
		// prepare test
		buildUpGeneratorResultData = buildGeneratorResultData();
		buildUpGeneratorResultData.addProperty(KnownInserterPropertyNames.ReplaceStart.name(), "Start");
		velocityInserterResultData = new BuildUpVelocityInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityInserterResultData.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), BuildUpVelocityInserterResultData.MISSING_REPLACE_CONFIGURATION);
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
		velocityInserterResultData = new BuildUpVelocityInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityInserterResultData.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), BuildUpVelocityInserterResultData.MISSING_REPLACE_CONFIGURATION);
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
		velocityInserterResultData = new BuildUpVelocityInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityInserterResultData.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), BuildUpVelocityInserterResultData.INVALID_INSERT_CONFIGURATION);
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
		velocityInserterResultData = new BuildUpVelocityInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityInserterResultData.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), BuildUpVelocityInserterResultData.INVALID_MIXED_CONFIGURATION);
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
		velocityInserterResultData = new BuildUpVelocityInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityInserterResultData.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), BuildUpVelocityInserterResultData.INVALID_MIXED_CONFIGURATION);
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
		velocityInserterResultData = new BuildUpVelocityInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityInserterResultData.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), BuildUpVelocityInserterResultData.CREATE_NEW_MIXED_CONFIGURATION);
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
		velocityInserterResultData = new BuildUpVelocityInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityInserterResultData.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), BuildUpVelocityInserterResultData.CREATE_NEW_MIXED_CONFIGURATION);
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
		velocityInserterResultData = new BuildUpVelocityInserterResultData(buildUpGeneratorResultData);

		// call functionality under test
		try {
			velocityInserterResultData.validatePropertyKeys("test");
		} catch (MOGLiPluginException e) {
			assertStringContains(e.getMessage(), BuildUpVelocityInserterResultData.CREATE_NEW_MIXED_CONFIGURATION);
			return;
		}
		fail("Expected exception not thrown!");
	}

}
