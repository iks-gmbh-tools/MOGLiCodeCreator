package com.iksgmbh.moglicc.provider.model.standard.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.iksgmbh.data.ClassNameData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.AttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.ClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpAttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpMetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpModel;
import com.iksgmbh.moglicc.provider.model.standard.excel.ExcelDataProvider.ExcelData;
import com.iksgmbh.moglicc.provider.model.standard.excel.ExcelTableReader.AttributeSubset;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;
import com.iksgmbh.utils.ImmutableUtil;

public class ModelEnricher
{
	// this ImmutableList contains regex of chars that will be removed from raw names
	private static final List<String> charsToIgnore = ImmutableUtil.getImmutableListOf(" ", "\\.");
	
	private Properties pluginProperties;
	private String modelName;
	private int renamingCounter = 0;
	private StringBuffer providerReport = new StringBuffer();

	private ModelEnricher(final Properties pluginProperties, 
			              final String modelName) 
	{
		this.pluginProperties = pluginProperties;
		this.modelName = modelName;
	}

	public static StringBuffer doYourJob(final Properties pluginProperties,
			                             final ExcelData excelData, 
			                             final BuildUpModel model) throws MOGLiPluginException
	{
		final ModelEnricher excelDataProvider = new ModelEnricher(pluginProperties, model.getName());
		
		excelDataProvider.parseExcelDataIntoModel(excelData, model);
		
		return excelDataProvider.providerReport;
	}

	private void parseExcelDataIntoModel(final ExcelData excelData, 
			                             final BuildUpModel model) throws MOGLiPluginException
	{
		for (int i = 0; i < excelData.matrixData.length; i++) 
		{
			final String className = excelData.classNames.get(i);
			final BuildUpClassDescriptor classDescriptor = getClassDescriptorAndCreateItIfNecessary(model, className);
			checkClassMetaInfo(classDescriptor, excelData.matrixData[i][0][0]);
			enrichAttributes(classDescriptor, 
					         excelData.matrixData[i],
					         excelData.attributeSubset.get(new Integer(i+1)),
					         excelData.attributeRenamingSettings, 
					         excelData.metainfoRenamingSettings);
		}
	}

	private BuildUpClassDescriptor getClassDescriptorAndCreateItIfNecessary(final BuildUpModel model, final String className)
	{
		final BuildUpClassDescriptor classDescriptor;
		if (doesModelKnowsClass(model, className))
		{
			classDescriptor = (BuildUpClassDescriptor) model.getClassDescriptor(className);
		}
		else
		{
			final ClassNameData classnameData = new ClassNameData(className);
			classDescriptor = new BuildUpClassDescriptor(classnameData);
			model.addClassDescriptor(classDescriptor);
		}
		
		return classDescriptor;
	}

	private void checkClassMetaInfo(final BuildUpClassDescriptor classDescriptor, 
			                        final String firstCellContent)
	{
		final String classMetainfoName = (String) pluginProperties.get(modelName + ExcelStandardModelProviderStarter.FIRST_CELL_USAGE_IDENTIFIER);
		
		if (classMetainfoName != null)
		{
			final BuildUpMetaInfo metaInfo = new BuildUpMetaInfo(classMetainfoName);
			metaInfo.setLevel(HierarchyLevel.Class);
			metaInfo.setValue(firstCellContent);
			classDescriptor.addMetaInfo(metaInfo);
		}		
	}

	private boolean doesModelKnowsClass(final BuildUpModel model, 
                            			final String className)
	{
		for (final ClassDescriptor classDescriptor : model.getClassDescriptorList()) 
		{
			if (classDescriptor.getFullyQualifiedName().equals(className))
			{
				return true;
			}
		}
		
		return false;
	}

	private void enrichAttributes(final BuildUpClassDescriptor classDescriptor,
			                      final String[][] originalMatrixData, 
			                      final AttributeSubset attributeSubset, 
			                      final HashMap<String, HashMap<Integer, String>> attributeRenamingSettings, 
			                      final HashMap<String, HashMap<Integer, String>> metainfoRenamingSettings) throws MOGLiPluginException
	{
		final String[][] matrixData = getActualMatrixData(originalMatrixData, attributeSubset);
		final List<String> attributeNames = getAttributeNames(matrixData, attributeRenamingSettings, classDescriptor.getSimpleName());
		final List<String> attributeMetaInfoNames = getMetaInfoNames(matrixData, metainfoRenamingSettings, classDescriptor.getSimpleName());
		providerReport.append("Class " + classDescriptor.getFullyQualifiedName() + " built with ");
		providerReport.append(attributeNames.size() + " attributes." + System.getProperty("line.separator"));
		
		for (int row = 1; row < matrixData.length; row++) 
		{
			final String attributeName = attributeNames.get(row - 1);
			final BuildUpAttributeDescriptor attributeDescriptor = addAttributeDescriptorAndCreateItIfNecessary(classDescriptor, attributeName);
			
			providerReport.append("    Attribute " + attributeDescriptor.getName() + " built with ");
			providerReport.append(attributeMetaInfoNames.size() + " metainfos." + System.getProperty("line.separator"));
			
			for (int col = 1; col < matrixData[0].length; col++) 
			{
				final String metaInfoName = attributeMetaInfoNames.get(col - 1);
				final BuildUpMetaInfo metaInfo = addAttributeMetaInfoAndCreateIt(attributeDescriptor, metaInfoName, classDescriptor.getSimpleName());
				metaInfo.setValue(matrixData[row][col]);
			}	
		}
		
		if (renamingCounter > 0)
		{
			providerReport.append(renamingCounter + " metainfos have been renamed due to plugin properties.");
			providerReport.append(System.getProperty("line.separator"));
		}
	}

	private String[][] getActualMatrixData(final String[][] originalMatrixData, 
			                               final AttributeSubset attributeSubset)
	{
		final String[][] matrixWithAttributesInRows = rotateIfNecesarry(originalMatrixData);
		
		if (attributeSubset != null)
		{
			return extractSubset(matrixWithAttributesInRows, attributeSubset);
		}
			
		return matrixWithAttributesInRows;
	}

	private String[][] extractSubset(final String[][] matrixWithAttributesInRows, 
			                      final AttributeSubset attributeSubset)
	{
		final int subsetSize = attributeSubset.maxIndex - attributeSubset.minIndex + 1;
		final String[][] toReturn = new String[subsetSize + 1][matrixWithAttributesInRows.length];
		
		toReturn[0] = matrixWithAttributesInRows[0];
		for (int i = 1; i < toReturn.length; i++) 
		{
			toReturn[i] = matrixWithAttributesInRows[i + attributeSubset.minIndex - 1];
		}
		
		return toReturn;
	}

	private String[][] rotateIfNecesarry(String[][] originalMatrixData)
	{
		if (isMatrixToRotate()) 
		{
			final String[][] toReturn = new String[originalMatrixData[0].length][originalMatrixData.length];
			for (int origRow = 0; origRow < originalMatrixData.length; origRow++) 
			{
				for (int origCol = 0; origCol < originalMatrixData[0].length; origCol++) 
				{
					toReturn[origCol][origRow] = originalMatrixData[origRow][origCol];
				}
				
			}
			return toReturn;
		}
		return originalMatrixData;
	}


	private BuildUpMetaInfo addAttributeMetaInfoAndCreateIt(final BuildUpAttributeDescriptor attributeDescriptor, 
			                                        final String metaInfoName,
			                                        final String className) throws MOGLiPluginException
	{
		for (final MetaInfo metaInfo : attributeDescriptor.getMetaInfoList()) 
		{
			if (metaInfo.getName().equals(metaInfoName))
			{
				if (! areMetaInfoDoublesAllowed())
				{					
					throw new MOGLiPluginException("MetaInfo '" + metaInfoName + "' of attribute '" + attributeDescriptor.getName() 
							+ " in class " + className + " cannot be added from ExcelData to the " 
							+ "standard model because it does exist already in standard model.");
				}
			}
		}
		
		final BuildUpMetaInfo metaInfo = new BuildUpMetaInfo(metaInfoName);
		attributeDescriptor.addMetaInfo(metaInfo);
		metaInfo.setLevel(MetaInfo.HierarchyLevel.Attribute);
		return metaInfo;
	}

	private BuildUpAttributeDescriptor addAttributeDescriptorAndCreateItIfNecessary(final BuildUpClassDescriptor classDescriptor, final String attributeName)
	{
		final BuildUpAttributeDescriptor attributeDescriptor;
		if (doesClassDescriptorKnowsAttribute(classDescriptor, attributeName))
		{
			attributeDescriptor = (BuildUpAttributeDescriptor) classDescriptor.getAttributeDescriptor(attributeName);	
		}
		else
		{	
			// add new attribute 
			attributeDescriptor = new BuildUpAttributeDescriptor(attributeName);
			classDescriptor.addAttributeDescriptor(attributeDescriptor);
		}
		return attributeDescriptor;
	}

	private boolean doesClassDescriptorKnowsAttribute(BuildUpClassDescriptor classDescriptor, String attributeName)
	{
		for (final AttributeDescriptor attributeDescriptor : classDescriptor.getAttributeDescriptorList()) 
		{
			if (attributeDescriptor.getName().equals(attributeName))
			{
				return true;
			}
		}
		
		return false;
	}

	private List<String> getFirstRowData(final String[][] matrix)
	{
		final String[] firstRowData = matrix[0];
		final List<String> toReturn = new ArrayList<String>();
		
		for (int i = 1; i < firstRowData.length; i++) {
			toReturn.add(firstRowData[i]);
		}
		
		return toReturn;
	}
	
	private List<String> getFirstColumnData(final String[][] matrix)
	{
		final List<String> toReturn = new ArrayList<String>();
		
		for (int i = 1; i < matrix.length; i++) 
		{			
			final String[] rowData = matrix[i];
			toReturn.add(rowData[0]);
		}
		
		return toReturn;
	}	
	
	private List<String> getMetaInfoNames(final String[][] matrix, 
			                              final HashMap<String, HashMap<Integer, String>> renamingSettings,
			                              final String classname) throws MOGLiPluginException
	{
		final List<String> rawNames = getFirstRowData(matrix);

		final String errMsgTemplate;
		if (areMetaInfoDoublesAllowed())
		{					
			errMsgTemplate = null;
		}
		else
		{
			errMsgTemplate = "Name of MetaInfo <?> of class <" + classname 
                              + "> is not unique in its matrix!";
		}

		return doRenaming(rawNames, renamingSettings, errMsgTemplate);
	}

	private List<String> doRenaming(final List<String> rawNames,
			                        final HashMap<String, HashMap<Integer, String>> renamingSettings, 
			                        final String errMsgTemplate) throws MOGLiPluginException
	{
		final List<String> toReturn = new ArrayList<String>();
		final HashMap<String, Integer> occurrenceCounter = new HashMap<String, Integer>();
		
		for (String rawName : rawNames) 
		{
			rawName = removeCharsToIgnore(rawName);
			
			// count occurrence
			int currentOccurrence = 1;			
			if (occurrenceCounter.containsKey(rawName.toLowerCase()))
			{
				currentOccurrence = occurrenceCounter.get(rawName.toLowerCase()) + 1;
			}
			occurrenceCounter.put(rawName.toLowerCase(), currentOccurrence);
			
			// check renaming
			if (renamingSettings.containsKey(rawName.toLowerCase()))
			{
				final HashMap<Integer, String> hashMap = renamingSettings.get(rawName.toLowerCase());
				if (hashMap.containsKey(currentOccurrence))
				{
					rawName = hashMap.get(currentOccurrence);
					renamingCounter++;
				}
			}
			
			// ckeck for doubles
			if (toReturn.contains(rawName))
			{
				if (errMsgTemplate != null)
				{					
					throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX 
							+ errMsgTemplate.replace("?", rawName));
				}
			}
			
			toReturn.add(rawName);
		}
		
		return toReturn;
	}
	
	

	static String removeCharsToIgnore(String rawName)
	{
		for (final String toRemove : charsToIgnore) 
		{
			rawName = rawName.replaceAll(toRemove, "");
		}
		
		return rawName;
	}

	private List<String> getAttributeNames(final String[][] matrix,
			                              final HashMap<String, HashMap<Integer, String>> renamingSettings, 
			                              final String classname) throws MOGLiPluginException
	{
		final List<String> rawNames = getFirstColumnData(matrix);
		return doRenaming(rawNames, renamingSettings, "Name of Attribute <?> in class <" + classname 
                                                       + "> is not unique in its matrix!");
	}

	private boolean isMatrixToRotate()
	{
		final String rotationMode = (String) pluginProperties.get(modelName + ExcelStandardModelProviderStarter.ROTATION_MODE_IDENTIFIER);
		if (ExcelStandardModelProviderStarter.ROTATION_MODE_ATTRIBUTE_IN_COLUMS.equalsIgnoreCase(rotationMode))
		{
			return true;
		}
		
<<<<<<< HEAD
		// ROTATION_MODE_ATTRIBUTE_IN_ROWS (this is default)
=======
		// ROTATION MODE: ATTRIBUTE IN ROWS (this is default)
>>>>>>> 656c84c58ad794ed34c58c30ecc9bf656c921412
		return false;
	}

	private boolean areMetaInfoDoublesAllowed()
	{
		final String property = (String) pluginProperties.get(modelName + ExcelStandardModelProviderStarter.ALLOW_METAINFO_DOUBLES_IDENTIFIER);
		if ("true".equalsIgnoreCase(property))
		{
			return true;
		}
		return false;
	}
	
}
