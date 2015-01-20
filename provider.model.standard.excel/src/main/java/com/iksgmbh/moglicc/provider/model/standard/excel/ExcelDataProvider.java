package com.iksgmbh.moglicc.provider.model.standard.excel;

import static com.iksgmbh.moglicc.provider.model.standard.excel.ExcelStandardModelProviderStarter.FILENAME_IDENTIFIER;
import static com.iksgmbh.moglicc.provider.model.standard.excel.ExcelStandardModelProviderStarter.MATRIX_IDENTIFIER;
import static com.iksgmbh.moglicc.provider.model.standard.excel.ExcelStandardModelProviderStarter.MATRIX_PATTERN;
import static com.iksgmbh.moglicc.provider.model.standard.excel.ExcelStandardModelProviderStarter.MATRIX_PATTERN_EXTENDED;
import static com.iksgmbh.moglicc.provider.model.standard.excel.ExcelStandardModelProviderStarter.OCCURRENCE_RENAMING_PATTERN;
import static com.iksgmbh.moglicc.provider.model.standard.excel.ExcelStandardModelProviderStarter.PLUGIN_ID;
import static com.iksgmbh.moglicc.provider.model.standard.excel.ExcelStandardModelProviderStarter.PLUGIN_PROPERTIES_FILE;
import static com.iksgmbh.moglicc.provider.model.standard.excel.ExcelStandardModelProviderStarter.RENAME_ATTRIBUTE_METAINFO_OCCURRENCE;
import static com.iksgmbh.moglicc.provider.model.standard.excel.ExcelStandardModelProviderStarter.RENAME_ATTRIBUTE_NAME_OCCURRENCE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.excel.ExcelTableReader.AttributeSubset;
import com.iksgmbh.moglicc.provider.model.standard.excel.ExcelTableReader.Cell;

public class ExcelDataProvider
{
	private Properties pluginProperties;

	public static ExcelData doYourJob(final Properties pluginProperties,
			                             final String modelName, 
			                             final File pluginInputDir) throws MOGLiPluginException
	{
		final ExcelDataProvider excelDataProvider = new ExcelDataProvider(pluginProperties);
		final ExcelData toReturn = excelDataProvider.readExcelMetaDataFromPluginProperties(modelName, pluginInputDir);
		toReturn.matrixData = excelDataProvider.readExcelDataFromExcelFile(toReturn);
		toReturn.attributeRenamingSettings = excelDataProvider.readRenamingSettings(modelName, RENAME_ATTRIBUTE_NAME_OCCURRENCE);
		toReturn.metainfoRenamingSettings = excelDataProvider.readRenamingSettings(modelName, RENAME_ATTRIBUTE_METAINFO_OCCURRENCE);
		return toReturn;
	}
	
	ExcelDataProvider(final Properties pluginProperties) {
		this.pluginProperties = pluginProperties;
	}

	ExcelData readExcelMetaDataFromPluginProperties(final String modelName, final File pluginInputDir) throws MOGLiPluginException
	{
		final List<String> propertyKeys = findPropertKeysFor(modelName);
		if (propertyKeys.isEmpty()) {
			throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX + "Unkown model <" + modelName + ">. You can make it known in the '"
					+ PLUGIN_PROPERTIES_FILE + "' of the " + PLUGIN_ID + ".");
		}

		final ExcelData toReturn = new ExcelData();
		toReturn.excelFile = getExcelFile(modelName, pluginInputDir);

		try {
			toReturn.excelTableReader = new ExcelTableReader(toReturn.excelFile);
		} catch (IOException e) {
			throw new MOGLiPluginException(e);
		}

		readMatrices(toReturn, propertyKeys);

		return toReturn;
	}

	String[][][] readExcelDataFromExcelFile(final ExcelData excelMetaData) throws MOGLiPluginException
	{
		final String[][][] matrixData = new String[excelMetaData.sheetNames.size()][][];

		for (int i = 0; i < excelMetaData.sheetNumbers.size(); i++) {
			excelMetaData.excelTableReader.setSheet(excelMetaData.sheetNumbers.get(i));
			matrixData[i] = excelMetaData.excelTableReader.getMatrix(excelMetaData.firstCells.get(i + 1));

			if (matrixData[i].length < 2) 
			{
				if (matrixData[i][0].length < 2) 
				{
					final String matrixDef = excelMetaData.matrixDefinitions.get(i);
					throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX + 
							                       "No matrix data found for matrix <" + matrixDef + ">");
				} else {
					final String matrixDef = excelMetaData.matrixDefinitions.get(i);
					throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX + 
							                       "No data row found for matrix <" + matrixDef + ">");
				}

			}
			if (matrixData[i][0].length < 2) 
			{
				final String matrixDef = excelMetaData.matrixDefinitions.get(i);
				throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX + 
						                       "No data column found for matrix <" + matrixDef + ">");
			}

		}

		return matrixData;
	}

	private void readMatrices(final ExcelData excelMetaData, final List<String> propertyKeys) throws MOGLiPluginException
	{
		for (final String key : propertyKeys) 
		{
			if (key.contains(MATRIX_IDENTIFIER)) 
			{
				final String matrixProperty = pluginProperties.getProperty(key);
				excelMetaData.matrixDefinitions.add(matrixProperty);
				final String className = cutClassNameFromProprtyKey(key);
				
				if (excelMetaData.classNames.contains(className))
				{
					throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX + 
							                       "Class name is not unique <" + className + ">!");
				}
				excelMetaData.classNames.add(className);
				
				final String cellData = parseSheetData(excelMetaData);
				final String subsetData = parseCellData(cellData, excelMetaData);
				parseSubsetData(subsetData, excelMetaData);
			}
		}
	}

	private void parseSubsetData(final String subsetData, 
			                     final ExcelData excelMetaData) throws MOGLiPluginException
	{
		if (subsetData == null)
		{
			return;
		}
		
		final String[] splitResult = subsetData.split("-");
		final String s1;
		final String s2;
		
		if (splitResult.length == 2)
		{
			s1 = splitResult[0].trim();
			s2 = splitResult[1].trim();
		}
		else
		{
			s1 = splitResult[0].trim();
			s2 = splitResult[0].trim();
		}

		Integer min = null;
		Integer max = null;
		try {
			min = Integer.valueOf(s1);
			max = Integer.valueOf(s2);
		} catch (NumberFormatException e) {
			final String matrixDef = excelMetaData.matrixDefinitions.get(excelMetaData.getIndexOfLastMatrix());
			throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX + 
					                       "Invalid Excel Data. Expected something like " + MATRIX_PATTERN_EXTENDED + ". " + "Please correct " +
					                       "defined matrix <" + matrixDef + ">.");
		}
		
		excelMetaData.attributeSubset.put(excelMetaData.getIndexOfLastMatrix() + 1, new AttributeSubset(min, max));	
	}

	private String cutClassNameFromProprtyKey(final String key)
	{
		final int pos = key.lastIndexOf(MATRIX_IDENTIFIER) + MATRIX_IDENTIFIER.length();
		return key.substring(pos);
	}


	private String parseCellData(String cellData, final ExcelData excelMetaData) throws MOGLiPluginException
	{
		final String[] splitResult1 = cellData.split(">");
		String toReturn = null;
		
		if (splitResult1.length == 2)
		{
			cellData = splitResult1[0];
			toReturn = splitResult1[1];
		}
		
		final String[] splitResult2 = cellData.split(":");
		if (splitResult2.length != 2) {
			final String matrixDef = excelMetaData.matrixDefinitions.get(excelMetaData.getIndexOfLastMatrix());
			throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX + "Invalid Excel Data. Expected something like " + 
			                               MATRIX_PATTERN + ". " + "Please correct " + "defined matrix <" + matrixDef + ">.");
		}

		Integer colNo = null;
		Integer rowNo = null;
		try {
			colNo = Integer.valueOf(splitResult2[0].trim());
			rowNo = Integer.valueOf(splitResult2[1].trim());
		} catch (NumberFormatException e) {
			final String matrixDef = excelMetaData.matrixDefinitions.get(excelMetaData.getIndexOfLastMatrix());
			throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX + 
					                       "Invalid Excel Data. Expected something like " + MATRIX_PATTERN + ". " + "Please correct " +
					                       "defined matrix <" + matrixDef + ">.");
		}

		excelMetaData.firstCells.put(excelMetaData.getIndexOfLastMatrix() + 1, new Cell(colNo, rowNo));
		
		return toReturn;
	}

	private String parseSheetData(final ExcelData excelMetaData) throws MOGLiPluginException
	{
		final String matrixDef = excelMetaData.matrixDefinitions.get(excelMetaData.getIndexOfLastMatrix());
		final String[] splitResult = matrixDef.split("\\#");

		if (splitResult.length != 2) {
			throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX + 
					                      "Invalid Excel Data. Expected something like " + MATRIX_PATTERN + ". " + "Please correct " +
					                       "defined matrix <" + matrixDef + ">.");
		}

		final String sheet = splitResult[0];
		try {
			final Integer sheetNumber = Integer.valueOf(sheet);
			final String sheetName = excelMetaData.excelTableReader.getSheetNameForSheetNumber(sheetNumber);

			if (sheetName == null) {
				throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX + 
						                       "Invalid Excel Data: In ExcelFile '" + excelMetaData.excelFile.getAbsolutePath() + 
						                       "' are less than " + sheetNumber + " sheets. " + "Please correct defined matrix <" + matrixDef + ">.");
			}

			excelMetaData.sheetNumbers.add(sheetNumber);
			excelMetaData.sheetNames.add(sheetName);
		} catch (NumberFormatException e) {
			final String sheetName = sheet;
			final Integer sheetNumber = excelMetaData.excelTableReader.getSheetNumberForSheetName(sheetName);

			if (sheetNumber == null) {
				throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX + 
						"Invalid Excel Data: In ExcelFile '" + excelMetaData.excelFile.getAbsolutePath() + "' is no sheet with name '"
						+ sheetName + "'. " + "Please correct defined matrix <" + matrixDef + ">.");
			}

			excelMetaData.sheetNumbers.add(sheetNumber);
			excelMetaData.sheetNames.add(sheetName);
		}

		return splitResult[1];
	}

	private File getExcelFile(final String modelName, 
                              final File pluginInputDir) throws MOGLiPluginException
	{
		final String excelFileName = pluginProperties.getProperty(modelName + "." + FILENAME_IDENTIFIER);

		if (excelFileName == null) {
			throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX + 
					                       "No ExcelFilename defined in '" + PLUGIN_PROPERTIES_FILE + "'.");
		}

		final File excelFile = new File(pluginInputDir, excelFileName);

		if (!excelFile.exists()) {
			throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX + 
					                       "Defined ExcelFile '" + excelFile.getAbsolutePath() + "' does not exist.");
		}
		return excelFile;
	}

	private List<String> findPropertKeysFor(final String modelName) throws MOGLiPluginException
	{
		final List<String> toReturn = new ArrayList<String>();
		final Enumeration<Object> elements = pluginProperties.keys();

		while (elements.hasMoreElements()) 
		{
			final String key = (String) elements.nextElement();
			if (key.startsWith(modelName))
			{
				toReturn.add(key);
			}
			
		}

		Collections.sort(toReturn);
		return toReturn;
	}

	private HashMap<String, HashMap<Integer, String>> readRenamingSettings(final String modelName,
			                                                               final String renamingType) throws MOGLiPluginException
	{
		final HashMap<String, HashMap<Integer, String>> toReturn = new HashMap<String, HashMap<Integer, String>>();
		final Enumeration<Object> elements = pluginProperties.keys();
		
		while (elements.hasMoreElements()) 
		{
			final String key = (String) elements.nextElement();
			final String id = modelName + renamingType;
			if (key.startsWith(id))
			{
				final String newName = pluginProperties.getProperty(key);
				final String occurrenceData = key.substring(id.length());
				final String[] splitResult = occurrenceData.split("\\.");
				
				if (splitResult.length != 2)
				{
					final String pattern = OCCURRENCE_RENAMING_PATTERN.replace("?", renamingType); 
					throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX + 
							                       "Invalid renaming setting:" + System.getProperty("line.separator") + 
							                       "Expected something like '" + pattern  + "'" + System.getProperty("line.separator") + 
							                       "Actual: " + key + "=" + newName );
				}
				
				final String oldName = splitResult[0];
				final Integer occurrence; 

				try {
					occurrence = Integer.valueOf(splitResult[1]);
				} catch (Exception e) 
				{
					throw new MOGLiPluginException(ExcelStandardModelProviderStarter.ERROR_PREFIX +
							                       "Invalid renaming setting:" + System.getProperty("line.separator") + 
		                                           key + "=" + newName  + System.getProperty("line.separator") + 
					                               "Number of occurrence is no Integer value.");
				}
				final HashMap<Integer, String> hashMap;
				
				if (toReturn.get(oldName.toLowerCase()) != null)
				{
					hashMap = toReturn.get(oldName.toLowerCase());
				}
				else
				{
					
					hashMap = new HashMap<Integer, String>();
				}
				
				hashMap.put(occurrence, newName);
				toReturn.put(oldName.toLowerCase(), hashMap);
			}
		}

		return toReturn;
	}
	

	/**
	 * Container for all data and metadata about and from the excel file and from the plugin properties file.
	 * An index within the lists sheetNames, sheetNumbers, classNames, matrixDefinitions refers
	 * to the same matrix.
	 * 
	 * @author Reik Oberrrath
	 */
	public static class ExcelData
	{
		// data about the excel file
		public File excelFile;
		public ExcelTableReader excelTableReader;
		public String[][][] matrixData;
		public HashMap<String, HashMap<Integer, String>> metainfoRenamingSettings;
		public HashMap<String, HashMap<Integer, String>> attributeRenamingSettings;
		
		// data from excel file
		final List<String> sheetNames = new ArrayList<String>();
		final List<Integer> sheetNumbers = new ArrayList<Integer>();
		final HashMap<Integer, ExcelTableReader.Cell> firstCells = new HashMap<Integer, ExcelTableReader.Cell>();
		final HashMap<Integer, ExcelTableReader.AttributeSubset> attributeSubset = new HashMap<Integer, ExcelTableReader.AttributeSubset>();

		// from plugin properties file
		final List<String> classNames = new ArrayList<String>();             
		final List<String> matrixDefinitions = new ArrayList<String>();      

		public Integer getIndexOfLastMatrix()
		{
			return matrixDefinitions.size() - 1;
		} 
	}
}
