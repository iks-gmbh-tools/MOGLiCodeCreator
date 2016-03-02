/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iksgmbh.moglicc.provider.model.standard.excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.data.ClassNameData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.ClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpAttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpMetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpModel;
import com.iksgmbh.moglicc.provider.model.standard.excel.ExcelDataProvider.ExcelData;
import com.iksgmbh.moglicc.provider.model.standard.excel.ExcelTableReader.AttributeSubset;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;

public class ModelEnricherUnitTest
{
	private static boolean ATTRIBUTE_IN_ROWS = true;
	private static boolean ATTRIBUTE_IN_COLUMNS = false;
	
	private Properties pluginProperties;

	@Before
	public void setup()
	{
		pluginProperties = new Properties();
	}


	@Test
	public void enrichesModelByAnAttributeSubsetOfMatrix() throws Exception
	{
		// arrange
		final BuildUpModel model = buildTestStandardModel();
		final ExcelData excelData = buildNonOverlappingExcelTestData(ATTRIBUTE_IN_ROWS);
		excelData.attributeSubset.put(1, new AttributeSubset(1, 2));
		
		// act
		ModelEnricher.doYourJob(pluginProperties, excelData, model);

		// assert
		assertEquals("number of classes", 2, model.getClassDescriptorList().size());		
		
		final ClassDescriptor classDescriptor = model.getClassDescriptorList().get(1);
		assertEquals("AttributeName", "AttrB1", classDescriptor.getAttributeDescriptorList().get(0).getName());
		assertEquals("result", 2, classDescriptor.getAttributeDescriptorList().size());
	}
	
	@Test
	public void enrichesModelByAnAttributeSubsetOfRotatedMatrix() throws Exception
	{
		// arrange
		final BuildUpModel model = buildTestStandardModel();
		final ExcelData excelData = buildNonOverlappingExcelTestData(ATTRIBUTE_IN_COLUMNS);
		excelData.attributeSubset.put(1, new AttributeSubset(2, 1));
		pluginProperties.put(model.getName() + ExcelStandardModelProviderStarter.ROTATION_MODE_IDENTIFIER, 
	                         ExcelStandardModelProviderStarter.ROTATION_MODE_ATTRIBUTE_IN_FIRST_COLUMN);
		
		// act
		ModelEnricher.doYourJob(pluginProperties, excelData, model);

		// assert
		assertEquals("number of classes", 2, model.getClassDescriptorList().size());		
		
		final ClassDescriptor classDescriptor = model.getClassDescriptorList().get(1);
		assertEquals("AttributeName", "AttrB1", classDescriptor.getAttributeDescriptorList().get(0).getName());
		assertEquals("result", 2, classDescriptor.getAttributeDescriptorList().size());
	}	
	
	@Test
	public void removesCharsToIgnoreFromRawName() throws Exception
	{
		// arrange
		final String rawName = "Abbr. expr. with spaces";
		
		// act
		final String result = ModelEnricher.removeCharsToIgnore(rawName);

		// assert
		assertEquals("result", "Abbrexprwithspaces", result);
	}
	
	
	@Test
	public void usesFirstCellContentAsClassMetaInfo() throws Exception
	{
		// arrange
		final BuildUpModel model = buildTestStandardModel();
		final ExcelData excelData = buildNonOverlappingExcelTestData(ATTRIBUTE_IN_ROWS);
		excelData.matrixData[0][0][0] = "classMetaInfoValue";
		pluginProperties.put(model.getName() + ExcelStandardModelProviderStarter.FIRST_CELL_USAGE_IDENTIFIER, "classMetaInfoName");
		
		// act
		ModelEnricher.doYourJob(pluginProperties, excelData, model);

		// assert
		final ClassDescriptor classDescriptor = model.getClassDescriptorList().get(1);
		assertEquals("classMetaInfoValue", excelData.matrixData[0][0][0], classDescriptor.getMetaInfoValueFor("classMetaInfoName"));
	}	
	
	@Test
	public void enrichesNonOverlappingModelData() throws Exception
	{
		// arrange
		final BuildUpModel model = buildTestStandardModel();
		final ExcelData excelData = buildNonOverlappingExcelTestData(ATTRIBUTE_IN_ROWS);
		
		// act
		ModelEnricher.doYourJob(pluginProperties, excelData, model);

		// assert
		assertEquals("model name", "TestModel", model.getName());
		assertEquals("number of classes", 2, model.getClassDescriptorList().size());		
		assertEquals("Number of metainfos in model", 1, model.getMetaInfoList().size());

		// assert ClassA from standard model
		ClassDescriptor classDescriptor = model.getClassDescriptorList().get(0);
		assertClassA(classDescriptor, 1, 3);

		// assert ClassB from excel data
		classDescriptor = model.getClassDescriptorList().get(1);
		assertClassB(classDescriptor);		
	}
	
	@Test
	public void rotatesMatrix() throws Exception
	{
		// arrange
		final BuildUpModel model = buildTestStandardModel();
		final ExcelData excelData = buildOverlappingExcelTestData(ATTRIBUTE_IN_COLUMNS);
		pluginProperties.put(model.getName() + ExcelStandardModelProviderStarter.ROTATION_MODE_IDENTIFIER, 
				             ExcelStandardModelProviderStarter.ROTATION_MODE_ATTRIBUTE_IN_FIRST_COLUMN);
		
		// act
		ModelEnricher.doYourJob(pluginProperties, excelData, model);

		// assert
		assertEquals("model name", "TestModel", model.getName());
		assertEquals("number of classes", 2, model.getClassDescriptorList().size());		
		assertEquals("Number of metainfos in model", 1, model.getMetaInfoList().size());

		// assert ClassA from standard model
		ClassDescriptor classDescriptor = model.getClassDescriptorList().get(0);
		assertClassA(classDescriptor, 2, 4);

		// assert ClassB from excel data
		classDescriptor = model.getClassDescriptorList().get(1);
		assertClassB(classDescriptor);		
	}

	@Test
	public void enrichesOverlappingModelData() throws Exception
	{
		// arrange
		final ExcelData excelData = buildOverlappingExcelTestData(ATTRIBUTE_IN_ROWS);
		final BuildUpModel model = buildTestStandardModel();
		ClassDescriptor classDescriptor = new BuildUpClassDescriptor(new ClassNameData("ClassC"));
		model.addClassDescriptor(classDescriptor);
		
		// act
		ModelEnricher.doYourJob(pluginProperties, excelData, model);

		// assert
		assertEquals("model name", "TestModel", model.getName());
		assertEquals("number of classes", 3, model.getClassDescriptorList().size());		
		assertEquals("Number of metainfos in model", 1, model.getMetaInfoList().size());

		// assert enriched ClassA from standard model
		classDescriptor = model.getClassDescriptorList().get(0);
		assertClassA(classDescriptor, 2, 4);
	
		// assert ClassB from excel data
		classDescriptor = model.getClassDescriptorList().get(2);
		assertClassB(classDescriptor);
		
		// assert ClassC from standard model
		classDescriptor = model.getClassDescriptorList().get(1);
		assertEquals("classname", "ClassC", classDescriptor.getSimpleName());
		assertEquals("Number of metainfos in class <" + classDescriptor.getSimpleName() + ">", 
			     0, classDescriptor.getMetaInfoList().size());
		
			
	}

	@Test
	public void throwsExceptionForDoubleAttributeMetaInfoNames() throws MOGLiPluginException
	{
		// arrange
		final BuildUpModel model = buildTestStandardModel();
		final ExcelData excelData = buildOverlappingExcelTestData(ATTRIBUTE_IN_ROWS);
		excelData.matrixData[1][0][1] = "AttrMetaInfoA1";
		
		try {
			// act
			ModelEnricher.doYourJob(pluginProperties, excelData, model);
			fail("Expected exception was not thrown!");
		} catch (MOGLiPluginException e) 
		{
			// assert
			assertEquals("Error message", "MetaInfo 'AttrMetaInfoA1' of attribute 'AttributeA in class ClassA " +
					                      "cannot be added from ExcelData to the standard model because it " +
					                      "does exist already in standard model.", e.getMessage());
		}
	}

	private void assertClassA(final ClassDescriptor classDescriptor,
			                  final int numAtttibutes,
			                  final int numAtttibuteMetaInfos)
	{
		assertEquals("classMetaInfoValue", "!MetaInfo FOR 'classMetaInfoName' NOT FOUND!", classDescriptor.getMetaInfoValueFor("classMetaInfoName"));

		assertEquals("classname", "ClassA", classDescriptor.getSimpleName());
		assertEquals("Number of metainfos in class <" + classDescriptor.getSimpleName() + ">", 
			     2, classDescriptor.getMetaInfoList().size());
		
		assertEquals("Number of attributes in class <" + classDescriptor.getSimpleName() + ">", 
				      numAtttibutes, classDescriptor.getAttributeDescriptorList().size());
		assertEquals("Name of attribute 1 in class <" + classDescriptor.getSimpleName() + ">", 
				     "AttributeA", classDescriptor.getAttributeDescriptorList().get(0).getName());
		assertEquals("Number of metainfos in the attributes in class <" + classDescriptor.getSimpleName() + ">", 
				      numAtttibuteMetaInfos, classDescriptor.getAttributeDescriptorList().get(0).getAllMetaInfos().size());
	}

	private void assertClassB(final ClassDescriptor classDescriptor)
	{
		assertEquals("classname", "ClassB", classDescriptor.getSimpleName());
		assertEquals("Number of metainfos in class <" + classDescriptor.getSimpleName() + ">", 
			     0, classDescriptor.getMetaInfoList().size());
		
		assertEquals("Number of attributes in class <" + classDescriptor.getSimpleName() + ">", 
			     3, classDescriptor.getAttributeDescriptorList().size());
		assertEquals("Number of metainfos in the attributes in class <" + classDescriptor.getSimpleName() + ">", 
			     2, classDescriptor.getAttributeDescriptorList().get(0).getAllMetaInfos().size());
		
		assertEquals("Value of metainfo 2 in attribute 1 in class <" + classDescriptor.getSimpleName() + ">", 
	                 "metaInfoValue12", classDescriptor.getAttributeDescriptorList().get(0).getAllMetaInfos().get(1).getValue());

		assertEquals("Value of last metainfo in last attribute in class <" + classDescriptor.getSimpleName() + ">", 
                     "metaInfoValue32", classDescriptor.getAttributeDescriptorList().get(2).getAllMetaInfos().get(1).getValue());
	}
	

	private ExcelData buildOverlappingExcelTestData(final boolean attributeInRows)
	{
		final ExcelData toReturn = buildNonOverlappingExcelTestData(attributeInRows);

		// data for ClassB that is unknown to StandardModel
		final String[][] matrix1 = toReturn.matrixData[0];
		
		// data to enrich ClassA of StandardModel
        final String[][] matrix2;
		toReturn.classNames.add("ClassA");
		if (attributeInRows)
		{
			final String[][] tmpMatrix = { {"enrichClassA",      "EnrichedMetainfo" }, 
                                           {"AttributeA",        "enrichedMetaInfoValue1" },
                                           {"EnrichedAttribute", "enrichedMetaInfoValue2" } }  ;
        	matrix2 = tmpMatrix;
		}
		else
		{
			final String[][] tmpMatrix = { {"enrichClassA",      "AttributeA",             "EnrichedAttribute" }, 
                                           {"EnrichedMetainfo",  "enrichedMetaInfoValue1", "enrichedMetaInfoValue2" } }  ;
			matrix2 = tmpMatrix;
			
		}

        final String[][][] matrixData = {   matrix1, matrix2  };
        toReturn.matrixData = matrixData;
	
		return toReturn;
	}

	private ExcelData buildNonOverlappingExcelTestData(final boolean attributeInRows)
	{
		final ExcelData excelData = new ExcelData();
		final HashMap<String, HashMap<Integer, String>> renamingSetting = new HashMap<String, HashMap<Integer, String>>();
		excelData.attributeRenamingSettings = renamingSetting;
		excelData.metainfoRenamingSettings = renamingSetting;
        final String[][] matrix1;

        excelData.classNames.add("ClassB");
        if (attributeInRows)
        {        	
        	final String[][] tmpMatrix = { {"firstCellContent", "AttrMetaInfoB1",  "AttrMetaInfoB2"}, 
        			                       {"AttrB1",           "metaInfoValue11", "metaInfoValue12"},
        			                       {"AttrB2",           "metaInfoValue21", "metaInfoValue22"},
        			                       {"AttrB3",           "metaInfoValue31", "metaInfoValue32"} };
        	matrix1 = tmpMatrix;
        }
        else
        {
        	final String[][] tmpMatrix = { {"firstCellContent", "AttrB1",          "AttrB2",          "AttrB3"}, 
                                           {"AttrMetaInfoB1",   "metaInfoValue11", "metaInfoValue21", "metaInfoValue31"},
                                           {"AttrMetaInfoB2",   "metaInfoValue12", "metaInfoValue22", "metaInfoValue32"} };
        	matrix1 = tmpMatrix;
        }
        
        final String[][][] matrixData = {   matrix1  };
		excelData.matrixData = matrixData;
		return excelData;
	}

	private BuildUpModel buildTestStandardModel()
	{
		final BuildUpModel model = new BuildUpModel("TestModel");
		
		BuildUpClassDescriptor classDescriptor = new BuildUpClassDescriptor(new ClassNameData("ClassA"));
		model.addClassDescriptor(classDescriptor);
		
		BuildUpAttributeDescriptor attributeDescriptor = new BuildUpAttributeDescriptor("AttributeA");
		classDescriptor.addAttributeDescriptor(attributeDescriptor);
		
		BuildUpMetaInfo metaInfo = new BuildUpMetaInfo("AttrMetaInfoA1");
		metaInfo.setLevel(HierarchyLevel.Attribute);
		metaInfo.setValue("valueAttrMetaInfoA1");
		attributeDescriptor.addMetaInfo(metaInfo);
		
		metaInfo = new BuildUpMetaInfo("AttrMetaInfoA2");
		metaInfo.setLevel(HierarchyLevel.Attribute);
		metaInfo.setValue("valueAttrMetaInfoA2");
		attributeDescriptor.addMetaInfo(metaInfo);
		
		metaInfo = new BuildUpMetaInfo("AttrMetaInfoA3");
		metaInfo.setLevel(HierarchyLevel.Attribute);
		metaInfo.setValue("valueAttrMetaInfoA3");
		attributeDescriptor.addMetaInfo(metaInfo);

		metaInfo = new BuildUpMetaInfo("ClassMetaInfoA1");
		metaInfo.setLevel(HierarchyLevel.Class);
		metaInfo.setValue("valueClassMetaInfoA1");
		classDescriptor.addMetaInfo(metaInfo);
		
		metaInfo = new BuildUpMetaInfo("ClassMetaInfoA2");
		metaInfo.setLevel(HierarchyLevel.Class);
		metaInfo.setValue("valueClassMetaInfoA2");
		classDescriptor.addMetaInfo(metaInfo);
		
		metaInfo = new BuildUpMetaInfo("ModelMetaInfoA");
		metaInfo.setLevel(HierarchyLevel.Model);
		metaInfo.setValue("valueModelMetaInfoA");
		model.addMetaInfo(metaInfo);
		
		return model;
		
	}
}