package com.iksgmbh.moglicc.provider.model.standard;

import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.MODELFILE_PROPERTY;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.TEXT_METAINFO_VALIDATION_ERROR_OCCURRED;
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
import java.util.List;
import java.util.Properties;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MetaInfoValidatorException;
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
	public static final String FILENAME_STANDARD_MODEL_TEXTFILE = "DemoModel.txt";

	private InfrastructureService infrastructure;
	private File modelFile;
	private BuildUpModel buildUpModel;

	private List<MetaInfoValidator> metaInfoValidatorList;

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
		buildUpModel = buildModel();
		validateMetaInfos();
		StatisticsFileCreator.doYourJob(this); // must be called after validateMetaInfos() !
		infrastructure.getPluginLogger().logInfo("Done!");
	}

	void validateMetaInfos() throws MOGLiPluginException {
		final List<MetaInfoValidator> metaInfoValidatorList = getAllMetaInfoValidators();
		
		boolean validationErrorOccurredOnModelLevel = validateModelMetaInfos(metaInfoValidatorList);
		boolean validationErrorOccurredOnClassLevel = validateClassMetaInfos(metaInfoValidatorList);
		boolean validationErrorOccurredOnAttributeLevel = validateAttributeMetaInfos(metaInfoValidatorList);
		
		if (validationErrorOccurredOnModelLevel 
			|| validationErrorOccurredOnClassLevel
			|| validationErrorOccurredOnAttributeLevel) {
			throw new MetaInfoValidatorException(TEXT_METAINFO_VALIDATION_ERROR_OCCURRED 
					                            + infrastructure.getPluginLogFile().getName());
		}
	}

	private boolean validateAttributeMetaInfos(final List<MetaInfoValidator> allMetaInfoValidators) {
		boolean validationErrorOccurred = false;
		final List<ClassDescriptor> classDescriptorList = buildUpModel.getClassDescriptorList();
		for (final ClassDescriptor classDescriptor : classDescriptorList) {
			final List<AttributeDescriptor> attributeDescriptorList = classDescriptor.getAttributeDescriptorList();
			for (final AttributeDescriptor attributeDescriptor : attributeDescriptorList) {				
				for (final MetaInfoValidator metaInfoValidator : allMetaInfoValidators) {
					boolean validationOk = metaInfoValidator.validate(attributeDescriptor.getMetaInfoList(), HierarchyLevel.Attribute);
					if (! validationOk) {
						validationErrorOccurred = true;
						infrastructure.getPluginLogger().logError(metaInfoValidator.getValidationErrorMessage() 
								+ "for attributeDescriptor '" + attributeDescriptor.getName() + "'");
					}
				}
			}
		}
		return validationErrorOccurred;
	}

	private boolean validateClassMetaInfos(final List<MetaInfoValidator> allMetaInfoValidators) {
		boolean validationErrorOccurred = false;
		final List<ClassDescriptor> classDescriptorList = buildUpModel.getClassDescriptorList();
		for (final ClassDescriptor classDescriptor : classDescriptorList) {
			for (final MetaInfoValidator metaInfoValidator : allMetaInfoValidators) {
				boolean validationOk = metaInfoValidator.validate(classDescriptor.getMetaInfoList(), HierarchyLevel.Class);
				if (! validationOk) {
					validationErrorOccurred = true;
					infrastructure.getPluginLogger().logError(metaInfoValidator.getValidationErrorMessage() 
							                                  + "for classDescriptor '" + classDescriptor.getSimpleName() + "'");
				}
			}
		}
		return validationErrorOccurred;
	}

	private boolean validateModelMetaInfos(final List<MetaInfoValidator> allMetaInfoValidators) {
		boolean validationErrorOccurred = false;
		for (final MetaInfoValidator metaInfoValidator : allMetaInfoValidators) {
			boolean validationOk = metaInfoValidator.validate(buildUpModel.getMetaInfoList(), HierarchyLevel.Model);
			if (! validationOk) {
				validationErrorOccurred = true;
				infrastructure.getPluginLogger().logError(metaInfoValidator.getValidationErrorMessage()
						                                   + "for model '" + buildUpModel.getName() + "'");
			}
		}
		return validationErrorOccurred;
	}
	
	public List<MetaInfoValidator> getAllMetaInfoValidators() throws MOGLiPluginException {
		if (metaInfoValidatorList == null) {
			metaInfoValidatorList = collectMetaInfoValidatorsFromVendors();
		}
		return metaInfoValidatorList;
	}

	private List<MetaInfoValidator> collectMetaInfoValidatorsFromVendors() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("Collecting MetaInfoValidators from vendors: ");
		final List<MetaInfoValidator> toReturn = new ArrayList<MetaInfoValidator>();
		final List<MetaInfoValidatorVendor> vendors = 
			   infrastructure.getPluginsOfType(MetaInfoValidatorVendor.class);
		
		if (vendors.isEmpty()) {
			infrastructure.getPluginLogger().logInfo("No vendor for MetaInfoValidators found.");
		}
		
		for (final MetaInfoValidatorVendor metaInfoValidatorVendor : vendors) {
			final MOGLiPlugin vendorPlugin = (MOGLiPlugin) metaInfoValidatorVendor;
			int counter = 0;
			final List<MetaInfoValidator> metaInfoValidatorList = metaInfoValidatorVendor.getMetaInfoValidatorList();
			for (final MetaInfoValidator metaInfoValidator : metaInfoValidatorList) {
				if (metaInfoValidator.getNameOfValidModel() == null
					|| metaInfoValidator.getNameOfValidModel().equals(buildUpModel.getName())) {
					toReturn.add(metaInfoValidator);
					counter++;
				}
			}
			infrastructure.getPluginLogger().logInfo("Found MetaInfoValidatorVendor '" + vendorPlugin.getId() + "'" +
					" providing " + counter + " MetaInfoValidators to model '" + buildUpModel.getName() + "'.");
		}
		
		return toReturn;
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
	public Model getModel() throws MOGLiPluginException {
		if (buildUpModel == null) {
			throw new MOGLiPluginException(TEXT_NO_MODEL_FILE_LOADED);
		}
		return buildUpModel;
	}

	public BuildUpModel buildModel() throws MOGLiPluginException {
		modelFile = getModelFile();
		checkModelFile();

		final List<String> fileContentAsList = readModelFileContent();

		try {
			buildUpModel = ModelParser.doYourJob(fileContentAsList);
		} catch (ModelParserException e) {
			throw new MOGLiPluginException(TEXT_PARSE_ERROR_FOUND
					+ e.getParserErrors());
		}

		infrastructure.getPluginLogger().logInfo(
				buildUpModel.getSize() + " classes read from "
						+ modelFile.getAbsolutePath());

		return buildUpModel;
	}

	private List<String> readModelFileContent() throws MOGLiPluginException {
		final List<String> fileContentAsList;
		try {
			fileContentAsList = FileUtil.getFileContentAsList(modelFile);
		} catch (IOException e) {
			throw new MOGLiPluginException("Could not read file: "
					+ modelFile, e);
		}
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
	}

	File getModelFile() throws MOGLiPluginException {
		if (modelFile == null) {
			final String filename = getModelFileName();
			if (filename ==  null) {
				throw new MOGLiPluginException(TEXT_NO_MODELFILE_FOUND);
			}
			modelFile = new File(infrastructure.getPluginInputDir(), filename);
		}
		return modelFile;
	}

	private String getModelFileName() {
		String toReturn = readFilenameFromPropertiesFile();
		
		if (toReturn == null) {
			toReturn = readModelFileNameFromFileSystem();
		}
		
		return toReturn;
	}
	
	private String readModelFileNameFromFileSystem() {
		String toReturn = null;
		final List<File> files = FileUtil.getOnlyFileChildren(infrastructure.getPluginInputDir());
		if (files.size() == 1 && ! files.get(0).getName().equals(PLUGIN_PROPERTIES_FILE)) {
			toReturn = files.get(0).getName();
		}
		return toReturn;
	}

	private String readFilenameFromPropertiesFile() {
		String toReturn = null;
		final File propertiesFile = new File(infrastructure.getPluginInputDir(), PLUGIN_PROPERTIES_FILE);
		if (propertiesFile.exists()) {
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(propertiesFile);
				final Properties pluginProperties = new Properties();
				pluginProperties.load(fileInputStream);
				toReturn = pluginProperties.getProperty(MODELFILE_PROPERTY);
				if (toReturn == null) {
					infrastructure.getPluginLogger().logWarning(TEXT_NO_MODELFILE_DEFINED_IN_PROPERTIES_FILE);
				}
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
		return toReturn;
	}

	@Override
	public boolean unpackDefaultInputData() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("unpackDefaultInputData");
		final PluginPackedData defaultData = new PluginPackedData(this.getClass(), DEFAULT_DATA_DIR);
		defaultData.addFile(FILENAME_STANDARD_MODEL_TEXTFILE);
		defaultData.addFile(PLUGIN_PROPERTIES_FILE);
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
		final PluginPackedData helpData = new PluginPackedData(this.getClass(), HELP_DATA_DIR);
		helpData.addFile("AttributeDescriptor.htm");
		helpData.addFile("ClassDescriptor.htm");
		helpData.addFile("Model.htm");
		helpData.addFile("MetaModel.htm");
		PluginDataUnpacker.doYourJob(helpData, infrastructure.getPluginHelpDir(), infrastructure.getPluginLogger());
		return true;
	}

}
