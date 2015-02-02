package com.iksgmbh.moglicc.provider.model.standard.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.utils.helper.PluginDataUnpacker;
import com.iksgmbh.moglicc.generator.utils.helper.PluginPackedData;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ModelProvider;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpModel;
import com.iksgmbh.moglicc.provider.model.standard.excel.ExcelDataProvider.ExcelData;
import com.iksgmbh.utils.ImmutableUtil;

/**
* Starter class of the ExcelStandardModelProvider plugin.
* It is instanciated by the core's PluginLoader via reflection.
* @author Reik Oberrath
*/
public class ExcelStandardModelProviderStarter implements ModelProvider {

	public static final String PLUGIN_ID = "ExcelStandardModelProvider";
	public static final String PLUGIN_PROPERTIES_FILE = "_excel.properties";
	public static final String FILENAME_STANDARD_EXCEL_FILE = "MOGLiCC_JavaBeanModel_Testdaten.xlsx";
	public static final String STANDARD_MODEL_PROVIDER_ID = "StandardModelProvider";

	public static final String TEXT_PROPERTIES_FILE_NOT_LOADED = PLUGIN_PROPERTIES_FILE + " not loaded: ";
	public static final String TEXT_NO_PROPERTIESFILE_FOUND = "File '" + PLUGIN_PROPERTIES_FILE + "' not found.";
	public static final String FILENAME_IDENTIFIER = "filename";
	public static final String FIRST_CELL_USAGE_IDENTIFIER = ".useFirstCellContentAsMClassMetaInfo";
	public static final String MATRIX_IDENTIFIER = ".matrix.";
	public static final String ROTATION_MODE_IDENTIFIER = ".ROTATION.MODE";
	public static final String ALLOW_METAINFO_DOUBLES_IDENTIFIER = ".AllowMetaInfoDoubles";
	public static final String ROTATION_MODE_ATTRIBUTE_IN_ROWS = "Attributes in rows";
	public static final String ROTATION_MODE_ATTRIBUTE_IN_COLUMS = "Attributes in columns";
	public static final String RENAME_ATTRIBUTE_METAINFO_OCCURRENCE = ".rename.AttributeMetaInfo.Occurrence.";
	public static final String RENAME_ATTRIBUTE_NAME_OCCURRENCE = ".rename.AttributeName.Occurrence.";
	public static final String ERROR_PREFIX = "Problem with model read from " + PLUGIN_ID + ": ";
	public static final String MATRIX_PATTERN = "<modelName>.matrix.1=<sheet>#<colNumber>:<rowNumber>";
	public static final String MATRIX_PATTERN_EXTENDED = "<modelName>.matrix.1=<sheet>#<colNumber>:<rowNumber> > <minAttributeIndex>-<maxAttributeIndex>";
	public static final String OCCURRENCE_RENAMING_PATTERN = "<modelName>?<oldname>.<numberOfOccurrence>=<newName>"; 
	public static final String OCCURRENCE_METAINFO_RENAMING_PATTERN = OCCURRENCE_RENAMING_PATTERN.replace("?", RENAME_ATTRIBUTE_METAINFO_OCCURRENCE); 
	public static final String OCCURRENCE_ATTRIBUTE_RENAMING_PATTERN = OCCURRENCE_RENAMING_PATTERN.replace("?", RENAME_ATTRIBUTE_NAME_OCCURRENCE);

	private InfrastructureService infrastructure;
	private Properties pluginProperties;
	private StringBuffer providerReport = new StringBuffer();
	private BuildUpModel buildUpModel;
	private int callCounts = 0;
	private String excelFilename;
	
	@Override
	public void setInfrastructure(final InfrastructureService infrastructure) {
		this.infrastructure = infrastructure;
	}

	@Override
	public void doYourJob() throws MOGLiPluginException 
	{
		infrastructure.getPluginLogger().logInfo("DoYourJob: Reading plugin properties...");
		readPluginProperties();
		// main work of this plugin is done in the getModel methods
		infrastructure.getPluginLogger().logInfo("Done!");
	}

	Properties readPluginProperties() 
	{
		final File propertiesFile = new File(infrastructure.getPluginInputDir(), PLUGIN_PROPERTIES_FILE);
		if (propertiesFile.exists()) {
			infrastructure.getPluginLogger().logInfo("Property file '" + PLUGIN_PROPERTIES_FILE +  "' found.");
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(propertiesFile);
				pluginProperties = new Properties();
				pluginProperties.load(fileInputStream);
				infrastructure.getPluginLogger().logInfo("Property file '" + PLUGIN_PROPERTIES_FILE +  "' read.");
			} catch (IOException e) {
				infrastructure.getPluginLogger().logError(TEXT_PROPERTIES_FILE_NOT_LOADED + e.getMessage());
			} finally {
				if (fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException e) {
						infrastructure.getPluginLogger().logError("Error reading file\n" + propertiesFile.getAbsolutePath());
					}
				}
			}
		} else {
			infrastructure.getPluginLogger().logWarning(TEXT_NO_PROPERTIESFILE_FOUND);
		}
		return pluginProperties;
	}

	@Override
	public boolean unpackDefaultInputData() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("initDefaultInputData");
		final PluginPackedData defaultData = new PluginPackedData(this.getClass(), DEFAULT_DATA_DIR, PLUGIN_ID);

		defaultData.addFile(PLUGIN_PROPERTIES_FILE);
		defaultData.addFile(FILENAME_STANDARD_EXCEL_FILE);

		PluginDataUnpacker.doYourJob(defaultData, infrastructure.getPluginInputDir(), infrastructure.getPluginLogger());
		return true;
	}

	@Override
	public PluginType getPluginType() {
		return PluginType.PROVIDER;
	}

	@Override
	public String getId() {
		return PLUGIN_ID;
	}

	@Override
	public List<String> getDependencies() {
		return ImmutableUtil.getImmutableListOf();  // do not set StandardModelProvider as dependency -> it would cause a dependency cycle !
	}

	@Override
	public boolean unpackPluginHelpFiles() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("unpackPluginHelpFiles");
		final PluginPackedData helpData = new PluginPackedData(this.getClass(), HELP_DATA_DIR, PLUGIN_ID);

		helpData.addFile("ExcelModelDefinition.htm");

		PluginDataUnpacker.doYourJob(helpData, infrastructure.getPluginHelpDir(), infrastructure.getPluginLogger());
		return true;
	}

	/**
	 * To be called directly by a generator plugin. 
	 */
	@Override
	public Model getModel(final String pluginId) throws MOGLiPluginException 
	{
		final ModelProvider standardModelProvider = (ModelProvider) infrastructure.getProvider(STANDARD_MODEL_PROVIDER_ID);
		final Model model = standardModelProvider.getModel(PLUGIN_ID);
		return getModel(pluginId, model);
	}
	
	/**
	 * To be called by the StandardModelProvider which is called by a generator plugin 
	 */
	@Override
	public Model getModel(final String pluginId, final Object inputData) throws MOGLiPluginException
	{
		infrastructure.getPluginLogger().logInfo("Building model...");
		callCounts++;
		
		try {
			if (inputData instanceof Model)
			{
				buildUpModel = (BuildUpModel) inputData;
				providerReport.append(System.getProperty("line.separator") + System.getProperty("line.separator"));
				
				final ExcelData excelData = ExcelDataProvider.doYourJob(getPluginProperties(buildUpModel.getVariables()), 
						                                                buildUpModel.getName(),
						                                                infrastructure.getPluginInputDir());
				excelFilename = excelData.excelFile.getName();
				final StringBuffer providerReportLines = ModelEnricher.doYourJob(pluginProperties, excelData, buildUpModel);
				this.providerReport.append(providerReportLines);
				
				infrastructure.getPluginLogger().logInfo("Done.");
				return buildUpModel;
			}
			
			throw new MOGLiPluginException("Error executing ExcelStandardModelProviderStarter.getModel(pluginId, inputData): inputData is of unexpected type or null!");
		} catch (MOGLiPluginException mpe) {
			infrastructure.getPluginLogger().logError("MOGLiPluginException: " + mpe.getMessage());
			throw mpe;
		} catch (Exception e) {
			infrastructure.getPluginLogger().logError(e.getClass().getName() + ": " + e.getMessage());
			throw new MOGLiPluginException(e);
		}
	}


	private Properties getPluginProperties(final HashMap<String, String> variables)
	{
		if (pluginProperties == null)
		{
			readPluginProperties();
		}
		
		doVariableReplacements(variables);
		
		return pluginProperties;
	}

	Properties doVariableReplacements(final HashMap<String, String> variables)
	{
		if (variables  != null)  
		{
			final Set<String> variableKeys = variables.keySet();
			final Properties replacedProperties = new Properties();
			final Enumeration<Object> elements = pluginProperties.keys();
			
			while (elements.hasMoreElements())
			{
				final String origPropertyKey = (String) elements.nextElement();
				String replacedPropertyKey = origPropertyKey;
				
				for (String variableKey : variableKeys) {
					replacedPropertyKey = replacedPropertyKey.replace(variableKey, variables.get(variableKey));
				}
				
				replacedProperties.put(replacedPropertyKey, pluginProperties.get(origPropertyKey));
			}
			
			pluginProperties = replacedProperties;
		}
		
		return pluginProperties;  // for test purpose
	}

	@Override
	public String getProviderReport()
	{
		final StringBuffer sb = new StringBuffer(getShortReport());
		sb.append(System.getProperty("line.separator"));
		sb.append(providerReport);
		return sb.toString().trim();
	}

	@Override
	public int getNumberOfCalls()
	{
		return 	callCounts++;
	}

	@Override
	public InfrastructureService getInfrastructure()
	{
		return infrastructure;
	}

	@Override
	public String getShortReport()
	{
		if (buildUpModel == null)
		{
			return "not used for code generation"; 
		}
			
		return "Model " + buildUpModel.getName() + " with " + buildUpModel.getSize() 
					   + " classes has been called " + getNumberOfCalls() + " times."; 
	}

	@Override
	public int getSuggestedPositionInExecutionOrder()
	{
		return 50;
	}

	@Override
	public String getModelFileName()
	{
		return excelFilename;
	}

	@Override
	public String getModelName()
	{
		return buildUpModel.getName();
	}
		
}