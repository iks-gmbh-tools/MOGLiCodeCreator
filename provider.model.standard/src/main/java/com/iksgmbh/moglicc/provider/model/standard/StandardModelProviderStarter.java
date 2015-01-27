package com.iksgmbh.moglicc.provider.model.standard;

import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.BRACE_SYMBOL_PROPERTY;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.MODELFILE_PROPERTY;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.TEXT_MODEL_BREAKS_METAINFO_VALIDATORS;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.TEXT_MODEL_NOT_EXISTS;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.TEXT_NO_MODELFILE_DEFINED_IN_PROPERTIES_FILE;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.TEXT_NO_MODELFILE_FOUND;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.TEXT_NO_MODEL_FILE_LOADED;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.TEXT_NO_PROPERTIESFILE_FOUND;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.TEXT_PARSE_ERROR_FOUND;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.TEXT_PROPERTIES_FILE_NOT_LOADED;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.iksgmbh.helper.AnnotationParser;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.utils.helper.PluginDataUnpacker;
import com.iksgmbh.moglicc.generator.utils.helper.PluginPackedData;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.ProviderPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ModelProvider;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpModel;
import com.iksgmbh.moglicc.provider.model.standard.exceptions.ModelParserException;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidatorVendor;
import com.iksgmbh.moglicc.provider.model.standard.parser.ModelParser;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.ImmutableUtil;


/**
* Starter class of the StandardModelProvider plugin.
* It is instanciated by the core's PluginLoader via reflection.
* @author Reik Oberrath
*/
public class StandardModelProviderStarter implements ModelProvider {

	public static final String PLUGIN_ID = "StandardModelProvider";

	public static final String PLUGIN_PROPERTIES_FILE = "_model.properties";
	public static final String FILENAME_STATISTICS_FILE = "ModelStatistics.txt";
	public static final String FILENAME_STANDARD_MODEL_FILE = "MOGLiCC_JavaBeanModel.txt";
	public static final String USE_EXTENSION_PLUGIN_ID = "useExtensionPlugin";
	private static final String FILENAME_NEW_PLUGIN_MODEL_FILE = "MOGLiCC_NewPluginModel.txt";

	private InfrastructureService infrastructure;
	private boolean jobStarted = false;
	private String modelBuildFailure;
	private int callCounts = 0;
	private File modelFile;
	private String modelFileName;
	private BuildUpModel buildUpModel;

	private List<MetaInfoValidator> metaInfoValidatorList;
	final HashMap<String, List<String>> validationErrorMessages = new HashMap<String, List<String>>();  // message list per plugin
	final List<String> callingPlugins = new ArrayList<String>();

	private Properties pluginProperties;

	private List<String> warnMessages = new ArrayList<String>();

	@Override
	public String getId() {
		return PLUGIN_ID;
	}

	@Override
	public PluginType getPluginType() {
		return MOGLiPlugin.PluginType.PROVIDER;
	}

	@Override
	public void doYourJob() throws MOGLiPluginException {
		jobStarted = true;
		infrastructure.getPluginLogger().logInfo("Doing my job...");
		readPluginProperties();
		buildUpModel = buildModel();
		validateMetaInfos();
		StatisticsFileCreator.doYourJob(this); // must be called after validateMetaInfos() !
		infrastructure.getPluginLogger().logInfo("Done!");
	}

	void validateMetaInfos() throws MOGLiPluginException {
		validationErrorMessages.clear();
		collectMetaInfoValidatorsFromVendors();

		validateModelMetaInfos(metaInfoValidatorList);
		validateClassMetaInfos(metaInfoValidatorList);
		validateAttributeMetaInfos(metaInfoValidatorList);

		if (validationErrorMessages.size() > 0) 
		{
			infrastructure.getPluginLogger().logInfo("Error: Model breaks " + validationErrorMessages.size() 
					                                  + " MetaInfoValidator settings!");
		}
		else
		{			
			infrastructure.getPluginLogger().logInfo("Model is valid corresponding the MetaInfoValidators!");
		}
	}

	private boolean validateAttributeMetaInfos(final List<MetaInfoValidator> allMetaInfoValidators) {
		boolean validationErrorOccurred = false;
		final List<ClassDescriptor> classDescriptorList = buildUpModel.getClassDescriptorList();

		for (final ClassDescriptor classDescriptor : classDescriptorList) {
			final List<AttributeDescriptor> attributeDescriptorList = classDescriptor.getAttributeDescriptorList();
			for (final AttributeDescriptor attributeDescriptor : attributeDescriptorList) {
				for (final MetaInfoValidator metaInfoValidator : allMetaInfoValidators) {
					if (metaInfoValidator.isValidatorValidForHierarchyLevel(HierarchyLevel.Attribute))  {
						boolean validationOk = metaInfoValidator.validate(attributeDescriptor.getMetaInfoList());
						if (! validationOk) {
							validationErrorOccurred = true;
							final String errorMessage = metaInfoValidator.getValidationErrorMessage()
									                    + " for attribute '" + attributeDescriptor.getName() + "'"
									                    + " of class '" + classDescriptor.getSimpleName() + "'"
									                    + getModelString(metaInfoValidator);
							infrastructure.getPluginLogger().logWarning(errorMessage);
							addValidationErrorMessage(metaInfoValidator.getVendorPluginId(), errorMessage);
						}
					}
				}
			}
		}
		return validationErrorOccurred;
	}

	private void addValidationErrorMessage(final String pluginID, final String message) {
		List<String> list = validationErrorMessages.get(pluginID);
		if (list == null) {
			list = new ArrayList<String>();
			validationErrorMessages.put(pluginID, list);
		}
		list.add(message);
	}

	private String getModelString(final MetaInfoValidator metaInfoValidator) {
		String toReturn = "";
		if (metaInfoValidator.getNameOfValidModel() != null) {
			toReturn = " in model '" + metaInfoValidator.getNameOfValidModel() + "'";
		}
		return toReturn;
	}

	private boolean validateClassMetaInfos(final List<MetaInfoValidator> allMetaInfoValidators) {
		boolean validationErrorOccurred = false;
		final List<ClassDescriptor> classDescriptorList = buildUpModel.getClassDescriptorList();
		for (final ClassDescriptor classDescriptor : classDescriptorList) {
			for (final MetaInfoValidator metaInfoValidator : allMetaInfoValidators) {
				if (metaInfoValidator.isValidatorValidForHierarchyLevel(HierarchyLevel.Class))  {
					boolean validationOk = metaInfoValidator.validate(classDescriptor.getMetaInfoList());
					if (! validationOk) {
						validationErrorOccurred = true;
						final String errorMessage = metaInfoValidator.getValidationErrorMessage() + " for class '"
								+ classDescriptor.getSimpleName() + "'"
								+ getModelString(metaInfoValidator);
						infrastructure.getPluginLogger().logWarning(errorMessage);
						addValidationErrorMessage(metaInfoValidator.getVendorPluginId(), errorMessage);
					}
				}

			}
		}
		return validationErrorOccurred;
	}

	private void validateModelMetaInfos(final List<MetaInfoValidator> allMetaInfoValidators) {
		for (final MetaInfoValidator metaInfoValidator : allMetaInfoValidators) {
			if (metaInfoValidator.isValidatorValidForHierarchyLevel(HierarchyLevel.Model))  {
				boolean validationOk = metaInfoValidator.validate(buildUpModel.getMetaInfoList());
				if (! validationOk) {
					final String errorMessage = metaInfoValidator.getValidationErrorMessage() + " for model '"
				                                + buildUpModel.getName() + "'"
				                                + getModelString(metaInfoValidator);
					infrastructure.getPluginLogger().logWarning(errorMessage);
					addValidationErrorMessage(metaInfoValidator.getVendorPluginId(), errorMessage);
				}
			}
		}
	}

	public List<MetaInfoValidator> getAllMetaInfoValidators() throws MOGLiPluginException {
		return metaInfoValidatorList;
	}

	private void collectMetaInfoValidatorsFromVendors() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("Collecting MetaInfoValidators from vendors: ");
		final List<MetaInfoValidator> toReturn = new ArrayList<MetaInfoValidator>();
		final List<MetaInfoValidatorVendor> vendors = infrastructure.getPluginsOfType(MetaInfoValidatorVendor.class);

		if (vendors.isEmpty()) {
			infrastructure.getPluginLogger().logInfo("No vendor for MetaInfoValidators found.");
		}

		for (final MetaInfoValidatorVendor metaInfoValidatorVendor : vendors) {
			final MOGLiPlugin vendorPlugin = (MOGLiPlugin) metaInfoValidatorVendor;
			int counter = 0;
			final List<MetaInfoValidator> metaInfoValidatorList = metaInfoValidatorVendor.getMetaInfoValidatorList();
			for (final MetaInfoValidator metaInfoValidator : metaInfoValidatorList) {
				if (metaInfoValidator.isValidatorValidForModel(buildUpModel.getName()))  {
					toReturn.add(metaInfoValidator);
					counter++;
				}
			}
			infrastructure.getPluginLogger().logInfo("Found MetaInfoValidatorVendor '" + vendorPlugin.getId() + "'" +
					" providing " + counter + " MetaInfoValidators to model '" + buildUpModel.getName() + "'.");
		}

		metaInfoValidatorList = toReturn;
	}


	@Override
	public List<String> getDependencies() {
		return ImmutableUtil.getImmutableListOf();
	}

	@Override
	public void setInfrastructure(final InfrastructureService infrastructure) {
		this.infrastructure = infrastructure;
	}

	@Override
	public Model getModel(final String pluginId) throws MOGLiPluginException 
	{
		callCounts++;
		callingPlugins.add(pluginId);
		
		if (buildUpModel == null) {
			throw new MOGLiPluginException(TEXT_NO_MODEL_FILE_LOADED);
		}

		final List<String> errorListForTheCallingPlugin = validationErrorMessages.get(pluginId);

		if (errorListForTheCallingPlugin != null) {
			throw new MOGLiPluginException(TEXT_MODEL_BREAKS_METAINFO_VALIDATORS);
		}
		
		return buildUpModel;
	}

	private void extendModelDataIfNeccessary() throws MOGLiPluginException
	{
		if (buildUpModel.doesHaveAnyMetaInfosWithName(USE_EXTENSION_PLUGIN_ID)) 
		{
			final String extensionPluginId = buildUpModel.getMetaInfoValueFor(USE_EXTENSION_PLUGIN_ID);
			final ProviderPlugin extensionPlugin = infrastructure.getProvider(extensionPluginId);
			
			if (extensionPlugin != null)
			{
				if (extensionPlugin instanceof ModelProvider)
				{
					final ModelProvider modelProvider =  (ModelProvider) extensionPlugin;
					buildUpModel = (BuildUpModel) modelProvider.getModel(PLUGIN_ID, buildUpModel);					
				}
				else
				{
					final String warnMsg = "Model MetaInfo '" + USE_EXTENSION_PLUGIN_ID + "' does not call a ModelProvider plugin <" + extensionPluginId + ">!";
					infrastructure.getPluginLogger().logWarning(warnMsg);
					warnMessages.add(warnMsg);
				}
			} 
			else 
			{
				final String warnMsg = "Model MetaInfo '" + USE_EXTENSION_PLUGIN_ID + "' calls a unkown provider plugin <" + extensionPluginId + ">!";
				infrastructure.getPluginLogger().logWarning(warnMsg);
				warnMessages.add(warnMsg);
			}
		}
	}

	@Override
	public Model getModel(final String pluginId, final Object inputData) throws MOGLiPluginException 
	{
		return getModel(pluginId); // inputData is not needed for this model provider
	}
	
	public BuildUpModel buildModel() throws MOGLiPluginException {
		modelFile = getModelFile();
		checkModelFile();

		final List<String> fileContentAsList = readModelFileContent();
	    final String braceSymbol = getMetaInfoBraceSymbol();
		infrastructure.getPluginLogger().logInfo("Brace symbol used: " + braceSymbol);


		try 
		{
			buildUpModel = ModelParser.doYourJob(fileContentAsList, braceSymbol);
			extendModelDataIfNeccessary();
		} 
		catch (ModelParserException e) 
		{
			modelBuildFailure = TEXT_PARSE_ERROR_FOUND + ": " + modelFile.getName();
			throw new MOGLiPluginException(TEXT_PARSE_ERROR_FOUND + " '" + modelFile.getName() + "':\n"
					+ e.getParserErrors());
		}

		infrastructure.getPluginLogger().logInfo("Model file: " + modelFile.getAbsolutePath());
		infrastructure.getPluginLogger().logInfo(buildUpModel.getSize() + " classes read from model file.");

		return buildUpModel;
	}

	private String getMetaInfoBraceSymbol() {
		String toReturn;
		if (pluginProperties == null) {
			toReturn = AnnotationParser.DEFAULT_PART_BRACE_IDENTIFIER;
		} else {
			toReturn = pluginProperties.getProperty(BRACE_SYMBOL_PROPERTY);
			if (toReturn == null || toReturn.length() == 0) {
				toReturn = AnnotationParser.DEFAULT_PART_BRACE_IDENTIFIER;
			}
		}
		return toReturn;
	}

	List<String> readModelFileContent() throws MOGLiPluginException {
		final List<String> fileContentAsList;
		try {
			fileContentAsList = FileUtil.getFileContentAsList(modelFile);
		} catch (IOException e) {
			modelBuildFailure = "Could not read file: " + modelFile;
			throw new MOGLiPluginException(modelBuildFailure);
		}
		infrastructure.getPluginLogger().logInfo("Model file has been read!");
		return fileContentAsList;
	}

	private void checkModelFile() throws MOGLiPluginException {
		if (!modelFile.exists()) {
			modelBuildFailure = TEXT_MODEL_NOT_EXISTS + ": " + modelFile.getAbsolutePath();
			throw new MOGLiPluginException(modelBuildFailure);
		}
		final String content;
		try {
			content = FileUtil.getFileContent(modelFile);
		} catch (IOException e) {
			modelBuildFailure = "Could not read file: " + modelFile.getAbsolutePath();
			throw new MOGLiPluginException(modelBuildFailure, e);
		}
		if (content.trim().length() == 0) {
			modelBuildFailure = "Unexpected empty file: " + modelFile.getAbsolutePath();
			throw new MOGLiPluginException(modelBuildFailure);
		}
		infrastructure.getPluginLogger().logInfo("Model file found!");
	}

	File getModelFile() throws MOGLiPluginException {
		if (modelFile == null) {
			modelFileName = readModelFileName();
			modelFile = new File(infrastructure.getPluginInputDir(), modelFileName);
		}
		return modelFile;
	}

	@Override
	public String getModelFileName() {
		return modelFileName;
	}

	private String readModelFileName() throws MOGLiPluginException {
		String toReturn = null;

		if (pluginProperties != null) {
			toReturn = pluginProperties.getProperty(MODELFILE_PROPERTY);
			if (toReturn == null) {
				infrastructure.getPluginLogger().logInfo("Model file name read from property file: " + toReturn);
			}
		}

		if (toReturn == null) {
			infrastructure.getPluginLogger().logWarning(TEXT_NO_MODELFILE_DEFINED_IN_PROPERTIES_FILE);
			toReturn = readModelFileNameFromFileSystem();
			if (toReturn == null) {
				infrastructure.getPluginLogger().logInfo("Model file name found in input directory: " + toReturn);
			}
		}

		if (toReturn == null) {
			modelBuildFailure = TEXT_NO_MODELFILE_FOUND;
			throw new MOGLiPluginException(TEXT_NO_MODELFILE_FOUND);
		}

		return toReturn;
	}

	private String readModelFileNameFromFileSystem() {
		String toReturn = null;
		final List<File> files = FileUtil.getOnlyFileChildren(infrastructure.getPluginInputDir());
		final List<File> list = new ArrayList<File>();

		for (final File file : files) {
			if (! file.getName().equals(PLUGIN_PROPERTIES_FILE)) {
				list.add(file);
			}
		}

		if (list.size() == 1) {
			toReturn = list.get(0).getName();
		}

		return toReturn;
	}

	void readPluginProperties() {
		if (pluginProperties == null) {
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
		}
	}

	@Override
	public boolean unpackDefaultInputData() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("unpackDefaultInputData");
		final PluginPackedData defaultData = new PluginPackedData(this.getClass(), DEFAULT_DATA_DIR, PLUGIN_ID);
		
		defaultData.addFile(PLUGIN_PROPERTIES_FILE);
		defaultData.addFile(FILENAME_STANDARD_MODEL_FILE);
		defaultData.addFile(FILENAME_NEW_PLUGIN_MODEL_FILE);
		
		PluginDataUnpacker.doYourJob(defaultData, infrastructure.getPluginInputDir(), infrastructure.getPluginLogger());
		return true;
	}

	@Override
	public InfrastructureService getInfrastructure() {
		return infrastructure;
	}

	@Override
	public boolean unpackPluginHelpFiles() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("unpackPluginHelpFiles");
		final PluginPackedData helpData = new PluginPackedData(this.getClass(), HELP_DATA_DIR, PLUGIN_ID);
		helpData.addFile("AttributeDescriptor.htm");
		helpData.addFile("ClassDescriptor.htm");
		helpData.addFile("Model.htm");
		helpData.addFile("_MetaModel.htm");
		PluginDataUnpacker.doYourJob(helpData, infrastructure.getPluginHelpDir(), infrastructure.getPluginLogger());
		return true;
	}

	@Override
	public String getShortReport()
	{
		if (! jobStarted) {
			return "not yet executed";
		}
		else if (modelBuildFailure != null) {
			return modelBuildFailure;
		}
			
		return "Model " + buildUpModel.getName() + " with " + buildUpModel.getSize() 
					   + " classes has been called " + getNumberOfCalls() + " times."; 
	}
	
	@Override
	public String getProviderReport()
	{
		final StringBuffer sb = new StringBuffer(getShortReport());
		
		if (buildUpModel != null) {
			sb.append(FileUtil.getSystemLineSeparator());
			sb.append(FileUtil.getSystemLineSeparator());
			
			sb.append("Classes in model:");
			sb.append(FileUtil.getSystemLineSeparator());
			
			final List<ClassDescriptor> classDescriptorList = buildUpModel.getClassDescriptorList();
			for (final ClassDescriptor classDescriptor : classDescriptorList)
			{
				sb.append(classDescriptor.getFullyQualifiedName());
				sb.append(FileUtil.getSystemLineSeparator());
			}

			sb.append(FileUtil.getSystemLineSeparator());
			
			sb.append("Plugins called for the model:");
			sb.append(FileUtil.getSystemLineSeparator());
			
			for (final String pluginId : callingPlugins)
			{
				sb.append(pluginId);
				sb.append(FileUtil.getSystemLineSeparator());
			}
			
			for (final String msg : warnMessages) {
				sb.append("Warning: ");
				sb.append(msg);
				sb.append(FileUtil.getSystemLineSeparator());	
			}
			
		}
		
		if (! validationErrorMessages.isEmpty())
		{
			sb.append(FileUtil.getSystemLineSeparator());
			sb.append("Following validation error(s) occurred:");
			sb.append(FileUtil.getSystemLineSeparator());
			
			final Set<String> keySet = validationErrorMessages.keySet();
			for (String pluginId : keySet) 
			{
				final List<String> list = validationErrorMessages.get(pluginId);
				
				sb.append("Vendor of validation rule: " + pluginId);
				sb.append(FileUtil.getSystemLineSeparator());
				
				for (String errorMessage : list) {
					sb.append("   " + errorMessage);
					sb.append(FileUtil.getSystemLineSeparator());
				}
			}
			
		}
		
		return sb.toString();
	}

	@Override
	public int getNumberOfCalls() {
		return callCounts;
	}

	@Override
	public int getSuggestedPositionInExecutionOrder()
	{
		return 100;
	}
	

	/**
	 * FOR TEST PURPOSE ONLY
	 */
	void setModelFile(final File file) {
		modelFile = file;
	}

	@Override
	public String getModelName() {
		if (buildUpModel == null) {
			return "";
		}
		return buildUpModel.getName();
	}

	public BuildUpModel getUnvalidatedModel() {
		return buildUpModel;
	}
}
