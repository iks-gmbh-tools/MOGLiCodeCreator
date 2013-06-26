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

import com.iksgmbh.helper.AnnotationParser;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.utils.helper.PluginDataUnpacker;
import com.iksgmbh.moglicc.generator.utils.helper.PluginPackedData;
import com.iksgmbh.moglicc.plugin.MOGLiPlugin;
import com.iksgmbh.moglicc.plugin.type.basic.ModelProvider;
import com.iksgmbh.moglicc.provider.model.standard.exceptions.ModelParserException;
import com.iksgmbh.moglicc.provider.model.standard.impl.BuildUpModel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidatorVendor;
import com.iksgmbh.moglicc.provider.model.standard.parser.ModelParser;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.ImmutableUtil;

public class StandardModelProviderStarter implements ModelProvider, MOGLiPlugin {


	public static final String PLUGIN_ID = "StandardModelProvider";

	public static final String PLUGIN_PROPERTIES_FILE = "_model.properties";
	public static final String FILENAME_STATISTICS_FILE = "ModelStatistics.txt";
	public static final String FILENAME_STANDARD_MODEL_FILE = "MOGLiCC_JavaBeanModel.txt";
	private static final String FILENAME_NEW_PLUGIN_MODEL_FILE = "MOGLiCC_NewPluginModel.txt";

	private InfrastructureService infrastructure;
	private File modelFile;
	private BuildUpModel buildUpModel;

	private List<MetaInfoValidator> metaInfoValidatorList;
	final HashMap<String, List<String>> validationErrorMessages = new HashMap<String, List<String>>();  // message list per plugin

	private Properties pluginProperties;

	@Override
	public String getId() {
		return PLUGIN_ID;
	}

	@Override
	public PluginType getPluginType() {
		return MOGLiPlugin.PluginType.MODEL_PROVIDER;
	}

	InfrastructureService getInfrastructure() {
		return infrastructure;
	}

	@Override
	public void doYourJob() throws MOGLiPluginException {
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

		if (validationErrorMessages.size() > 0) {
			infrastructure.getPluginLogger().logInfo("Model breaks "
					+ validationErrorMessages.size() + " MetaInfoValidator settings!");
		}
		infrastructure.getPluginLogger().logInfo("Model is valid corresponding the MetaInfoValidators!");
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
									                    + " for attribute descriptor '" + attributeDescriptor.getName() + "'"
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
						final String errorMessage = metaInfoValidator.getValidationErrorMessage() + " for class descriptor '"
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
	public void setMOGLiInfrastructure(final InfrastructureService infrastructure) {
		this.infrastructure = infrastructure;
	}

	@Override
	public Model getModel(final String pluginId) throws MOGLiPluginException {
		if (buildUpModel == null) {
			throw new MOGLiPluginException(TEXT_NO_MODEL_FILE_LOADED);
		}

		final List<String> errorListForTheCallingPlugin = validationErrorMessages.get(pluginId);

		if (errorListForTheCallingPlugin != null) {
			throw new MOGLiPluginException(TEXT_MODEL_BREAKS_METAINFO_VALIDATORS);
		}
		return buildUpModel;
	}

	public BuildUpModel buildModel() throws MOGLiPluginException {
		modelFile = getModelFile();
		checkModelFile();

		final List<String> fileContentAsList = readModelFileContent();
	    final String braceSymbol = getMetaInfoBraceSymbol();
		infrastructure.getPluginLogger().logInfo("Brace symbol used: " + braceSymbol);


		try {
			buildUpModel = ModelParser.doYourJob(fileContentAsList, braceSymbol);
		} catch (ModelParserException e) {
			throw new MOGLiPluginException(TEXT_PARSE_ERROR_FOUND
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
			throw new MOGLiPluginException("Could not read file: "
					+ modelFile, e);
		}
		infrastructure.getPluginLogger().logInfo("Model file has been read!");
		return fileContentAsList;
	}

	private void checkModelFile() throws MOGLiPluginException {
		if (!modelFile.exists()) {
			throw new MOGLiPluginException(TEXT_MODEL_NOT_EXISTS + ":\n"
					+ modelFile.getAbsolutePath());
		}
		final String content;
		try {
			content = FileUtil.getFileContent(modelFile);
		} catch (IOException e) {
			throw new MOGLiPluginException("Could not read file: "
					+ modelFile.getAbsolutePath(), e);
		}
		if (content.trim().length() == 0) {
			throw new MOGLiPluginException("Unexpected empty file: "
					+ modelFile.getAbsolutePath());
		}
		infrastructure.getPluginLogger().logInfo("Model file found!");
	}

	File getModelFile() throws MOGLiPluginException {
		if (modelFile == null) {
			final String filename = getModelFileName();
			modelFile = new File(infrastructure.getPluginInputDir(), filename);
		}
		return modelFile;
	}

	private String getModelFileName() throws MOGLiPluginException {
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
			toReturn = files.get(0).getName();
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
	public InfrastructureService getMOGLiInfrastructure() {
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
