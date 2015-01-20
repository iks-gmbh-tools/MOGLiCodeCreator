package com.iksgmbh.moglicc.provider.model.standard.metainfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpMetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpModel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoSupport;

public class MetaInfoSupportUnitTest {

	private final String metaInfoTestName = "MetaInfoTest";
	private final String metaInfoTestValue1 = "value1";
	
	private MetaInfoSupport metaInfoSupport;
	
	@Before
	public void setup() {
		final BuildUpModel buildUpModel = new BuildUpModel("Test");
		final BuildUpMetaInfo buildUpMetaInfo1 = new BuildUpMetaInfo(metaInfoTestName);
		buildUpMetaInfo1.setValue(metaInfoTestValue1);
		buildUpModel.addMetaInfo(buildUpMetaInfo1);
		final BuildUpMetaInfo buildUpMetaInfo2 = new BuildUpMetaInfo(metaInfoTestName);
		buildUpMetaInfo2.setValue("value2");
		buildUpModel.addMetaInfo(buildUpMetaInfo2);
		this.metaInfoSupport = buildUpModel;
	}
	
	@Test
	public void returnsValueForMetaInfoName() {
		// call functionality under test
		final String value = metaInfoSupport.getMetaInfoValueFor(metaInfoTestName);
		
		// verify test result
		assertEquals("value of MetaInfo", metaInfoTestValue1, value);
	}
	
	@Test
	public void returnsValueListForMetaInfoName() {
		// call functionality under test
		final List<String> matches = metaInfoSupport.getAllMetaInfoValuesFor(metaInfoTestName);
		
		// verify test result
		assertEquals("number values", 2, matches.size());
		assertEquals("value of MetaInfo", metaInfoTestValue1, matches.get(0));
	}
	
	@Test
	public void findsMetaInfoByNameAndValue() {
		// call functionality under test
		final boolean found1 = metaInfoSupport.doesHaveMetaInfo(metaInfoTestName, metaInfoTestValue1);
		
		// verify test result
		assertTrue("doesHaveMetaInfo", found1);
	}
	
	@Test
	public void doesNotfindMetaInfoByNameAndValue() {
		// call functionality under test
		final boolean found1 = metaInfoSupport.doesHaveMetaInfo(metaInfoTestName, "");
		final boolean found2 = metaInfoSupport.doesHaveMetaInfo(metaInfoTestName, "a");
		final boolean found3 = metaInfoSupport.doesHaveMetaInfo("a", "a");
		final boolean found4 = metaInfoSupport.doesHaveMetaInfo("", "a");
		
		// verify test result
		assertFalse("doesHaveMetaInfo", found1 && found2 && found3 && found4);
	}
		
	@Test
	public void findsMetaInfoOnlyByName() {
		// call functionality under test
		final boolean found1 = metaInfoSupport.doesHaveAnyMetaInfosWithName(metaInfoTestName);
		final boolean found2 = metaInfoSupport.doesHaveAnyMetaInfosWithName("");
		
		// verify test result
		assertTrue("doesHaveMetaInfo", found1);
		assertFalse("doesHaveMetaInfo", found2);
	}
	
	@Test
	public void returnsMetaInfosWithNameStartingWith() {
		// call functionality under test
		final List<MetaInfo> result1 = metaInfoSupport.getMetaInfosWithNameStartingWith("Meta");
		final List<MetaInfo> result2 = metaInfoSupport.getMetaInfosWithNameStartingWith("unkown");
		
		// verify test result
		assertEquals("number of metainfos", 2, result1.size());
		assertEquals("number of metainfos", 0, result2.size());
	}
	
}
