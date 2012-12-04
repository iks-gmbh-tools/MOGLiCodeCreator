package com.iksgmbh.moglicc.inserter.modelbased.velocity;

import static com.iksgmbh.moglicc.inserter.modelbased.velocity.TextConstants.TEXT_END_REPLACE_INDICATOR_NOT_FOUND;
import static com.iksgmbh.moglicc.inserter.modelbased.velocity.TextConstants.TEXT_START_REPLACE_INDICATOR_NOT_FOUND;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.data.GeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.utils.ArtefactListUtil;
import com.iksgmbh.moglicc.generator.utils.MetaInfoValidationUtil;
import com.iksgmbh.moglicc.generator.utils.ModelValidationGeneratorUtil;
import com.iksgmbh.moglicc.generator.utils.TemplateUtil;
import com.iksgmbh.moglicc.generator.utils.helper.PluginDataUnpacker;
import com.iksgmbh.moglicc.generator.utils.helper.PluginPackedData;
import com.iksgmbh.moglicc.plugin.type.Inserter;
import com.iksgmbh.moglicc.plugin.type.ModelBasedEngineProvider;
import com.iksgmbh.moglicc.provider.engine.velocity.BuildUpVelocityEngineData;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidatorVendor;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.ImmutableUtil;

public class VelocityModelBasedInserterStarter implements Inserter, MetaInfoValidatorVendor {

	public static final String PLUGIN_ID = "VelocityModelBasedInserter";
	public static final String MODEL_PROVIDER_ID = "StandardModelProvider";
	public static final String ENGINE_PROVIDER_ID = "VelocityEngineProvider";
	public static final String MAIN_TEMPLATE_IDENTIFIER = "Main";	
	public static final String PLUGIN_PROPERTIES_FILE = "generator.properties";
	
	private InfrastructureService infrastructure;

	@Override
	public void setMOGLiInfrastructure(final InfrastructureService infrastructure) {
		this.infrastructure = infrastructure;
	}

	@Override
	public void doYourJob() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("Doing my job...");
		
		final Model model = infrastructure.getModelProvider(MODEL_PROVIDER_ID).getModel();
		infrastructure.getPluginLogger().logInfo("Model '" + model.getName() + "' retrieved from " + MODEL_PROVIDER_ID);		
		
		final List<String> list = getArtefactList();
		for (final String artefact : list) {
			infrastructure.getPluginLogger().logInfo("-");
			applyModelToArtefactTemplate(model, artefact);
		}
		infrastructure.getPluginLogger().logInfo("-");
		
		infrastructure.getPluginLogger().logInfo("Done!");
	}

	private void applyModelToArtefactTemplate(final Model model, final String artefact) throws MOGLiPluginException {
		final BuildUpVelocityEngineData engineData = new BuildUpVelocityEngineData(artefact, model, PLUGIN_ID);
		final VelocityInserterResultData result = insert(engineData);
		if (! ModelValidationGeneratorUtil.validateModel(result.getNameOfValidModel(), model.getName())) {
			infrastructure.getPluginLogger().logInfo("Artefact '" + artefact + "' not generated, because only model '"
	                + result.getNameOfValidModel() + "' is valid for this artefact, " 
	                + "but not the current model '" + model.getName() + "'.");
			
			return;
		}
		result.validate();
		writeResultIntoPluginOutputDir(result, artefact);
		writeResultIntoTargetDefinedInTemplate(result);
		infrastructure.getPluginLogger().logInfo("Generated content for artefact '" + artefact 
				+ "' inserted into " + result.getTargetDir() + "/" + result.getTargetFileName());
	}

	private void writeResultIntoPluginOutputDir(final VelocityInserterResultData resultData, final String subDir) 
	             throws MOGLiPluginException {
		final File targetdir = new File(infrastructure.getPluginOutputDir(), subDir);
		targetdir.mkdirs();
		final File outputFile = new File(targetdir, resultData.getTargetFileName());
		try {
			FileUtil.createFileWithContent(outputFile, resultData.getGeneratedContent());
		} catch (Exception e) {
			throw new MOGLiPluginException("Error creating file\n" + outputFile.getAbsolutePath());
		}
	}

	private void writeResultIntoTargetDefinedInTemplate(final VelocityInserterResultData resultData) 
	             throws MOGLiPluginException {
		final File outputFile = resultData.getTargetFile(infrastructure.getApplicationRootDir().getAbsolutePath(), null);
		infrastructure.getPluginLogger().logInfo("Creating file: " + outputFile.getAbsolutePath());
		final String buildOutputFileContent = buildOutputFileContent(outputFile, resultData);
		if (buildOutputFileContent !=  null) {
			try {
				FileUtil.createFileWithContent(outputFile, buildOutputFileContent);
			} catch (Exception e) {
				throw new MOGLiPluginException("Error creating file\n" + outputFile.getAbsolutePath(), e);
			}
		}
	}

	private String buildOutputFileContent(final File outputFile, final VelocityInserterResultData resultData) 
	               throws MOGLiPluginException {
		
		if (resultData.isTargetToBeCreatedNewly()) {
			if (outputFile.exists()) {
				infrastructure.getPluginLogger().logInfo("Old output file will be overwritten:\n" 
						+ outputFile.getAbsolutePath());
			} else {
				infrastructure.getPluginLogger().logInfo("Output file will be created:\n" 
						+ outputFile.getAbsolutePath());
			}
			return resultData.getGeneratedContent();
		}	
		
		if (resultData.mustGeneratedContentBeMergedWithExistingTargetFile()) {
			if (! outputFile.exists()) {
				throw new MOGLiPluginException("File does not exist:\n" + outputFile.getAbsolutePath());
			}
			infrastructure.getPluginLogger().logInfo("Generated content merged with content of\n" 
					                                 + outputFile.getAbsolutePath());
			return mergeOldAndNewContent(outputFile, resultData);
		}	

		if (! outputFile.exists()) {
			infrastructure.getPluginLogger().logInfo("An old output file does not exist:\n" 
					+ outputFile.getAbsolutePath());
			return resultData.getGeneratedContent();
		}

		infrastructure.getPluginLogger().logWarning("Output file already exists and will not be overwritten:\n" 
                + outputFile.getAbsolutePath());
		return null;
	}

	private String mergeOldAndNewContent(final File outputFile, 
			                           final VelocityInserterResultData resultData) throws MOGLiPluginException {
		final List<String> oldContent;
		try {
			oldContent = FileUtil.getFileContentAsList(outputFile);
		} catch (IOException e) {
			throw new MOGLiPluginException("Error creating file\n" + outputFile.getAbsolutePath(), e);
		}
		
		String generatedContent = resultData.getGeneratedContent();
		final String InsertAboveIndicator = resultData.getInsertAboveIndicator();
		if (InsertAboveIndicator != null) {
			generatedContent = InsertAbove(oldContent, generatedContent, InsertAboveIndicator);
			infrastructure.getPluginLogger().logInfo("Generated Content inserted above '" 
		            + InsertAboveIndicator + "' in\n" + outputFile.getAbsolutePath());
			return generatedContent;
		}
		
		final String InsertBelowIndicator = resultData.getInsertBelowIndicator();
		if (InsertBelowIndicator != null) {
			generatedContent = InsertBelow(oldContent, generatedContent, InsertBelowIndicator);
			infrastructure.getPluginLogger().logInfo("Generated Content inserted below '" 
					                                 + InsertBelowIndicator + "' in\n" + outputFile.getAbsolutePath());
			return generatedContent;
		}
		
		final String ReplaceStartIndicator = resultData.getReplaceStartIndicator();
		if (ReplaceStartIndicator != null) {
			generatedContent = replace(oldContent, generatedContent, ReplaceStartIndicator, resultData.getReplaceEndIndicator());
			infrastructure.getPluginLogger().logInfo("Generated Content replaced in\n" + outputFile.getAbsolutePath());
			return generatedContent;
		}
		
		throw new MOGLiPluginException("Invalid VelocityInserterResultData Setting");
	}

	private String replace(final List<String> oldContent, final String contentToInsert,
			final String ReplaceStartIndicator, final String ReplaceEndIndicator) throws MOGLiPluginException {
		final StringBuffer sb = new StringBuffer();
		boolean lineMustBeReplaced = false;
		boolean ReplaceStartFound = false;
		boolean ReplaceEndFound = false;
		for (final String line : oldContent) {
			if (! lineMustBeReplaced) {
				sb.append(line);
				sb.append(FileUtil.getSystemLineSeparator());
			}
			if (line.contains(ReplaceStartIndicator)) {
				lineMustBeReplaced = true;
				sb.append(contentToInsert);
				sb.append(FileUtil.getSystemLineSeparator());
				ReplaceStartFound = true;
			}
			if (line.contains(ReplaceEndIndicator)) {
				lineMustBeReplaced = false;
				sb.append(line);
				sb.append(FileUtil.getSystemLineSeparator());
				ReplaceEndFound = true;
			}
		}
		if (! ReplaceStartFound) {
			throw new MOGLiPluginException(TEXT_START_REPLACE_INDICATOR_NOT_FOUND + ReplaceStartIndicator);
		}
		if (! ReplaceEndFound) {
			throw new MOGLiPluginException(TEXT_END_REPLACE_INDICATOR_NOT_FOUND + ReplaceStartIndicator);
		}
		
		return sb.toString();
	}

	private String InsertBelow(final List<String> oldContent, final String contentToInsert,
			                   final String InsertBelowIndicator) throws MOGLiPluginException {
		final StringBuffer sb = new StringBuffer();
		boolean indicatorFound = false;
		for (final String line : oldContent) {
			sb.append(line);
			sb.append(FileUtil.getSystemLineSeparator());
			if (line.contains(InsertBelowIndicator)) {
				sb.append(contentToInsert);
				sb.append(FileUtil.getSystemLineSeparator());
				indicatorFound = true;
			}
		}
		if (! indicatorFound) {
			throw new MOGLiPluginException(TextConstants.TEXT_INSERT_BELOW_INDICATOR_NOT_FOUND + InsertBelowIndicator);
		}
		return sb.toString();
	}

	private String InsertAbove(final List<String> oldContent, final String contentToInsert,
			                   final String InsertAboveIndicator) throws MOGLiPluginException {
		final StringBuffer sb = new StringBuffer();
		boolean indicatorFound = false;
		for (final String line : oldContent) {
			if (line.contains(InsertAboveIndicator)) {
				sb.append(contentToInsert);
				sb.append(FileUtil.getSystemLineSeparator());
				indicatorFound = true;
			}
			sb.append(line);
			sb.append(FileUtil.getSystemLineSeparator());
		}
		if (! indicatorFound) {
			throw new MOGLiPluginException(TextConstants.TEXT_INSERT_ABOVE_INDICATOR_NOT_FOUND + InsertAboveIndicator);
		}
		return sb.toString();
	}

	List<String> getArtefactList() throws MOGLiPluginException {
		final File generatorPropertiesFile = new File(infrastructure.getPluginInputDir(), PLUGIN_PROPERTIES_FILE);
		return ArtefactListUtil.getArtefactListFrom(infrastructure.getPluginInputDir(), generatorPropertiesFile);
	}
	
	VelocityInserterResultData insert(final BuildUpVelocityEngineData engineData) throws MOGLiPluginException {
		final File templateDir = new File(infrastructure.getPluginInputDir(), engineData.getArtefactType());
		engineData.setTemplateDir(templateDir);
		engineData.setTemplateFileName(findMainTemplate(templateDir));
		
		final ModelBasedEngineProvider velocityEngineProvider = 
		       (ModelBasedEngineProvider) infrastructure.getEngineProvider(ENGINE_PROVIDER_ID);
		
		infrastructure.getPluginLogger().logInfo("Starting velocity engine for artefact '" 
				+ engineData.getArtefactType()  + " and with template '" 
				+ engineData.getMainTemplateSimpleFileName() + "'...");

		velocityEngineProvider.setEngineData(engineData);
		final GeneratorResultData generatorResultData = velocityEngineProvider.startEngineWithModel();
		return new BuildUpVelocityInserterResultData(generatorResultData);
	}

	String findMainTemplate(final File templateDir) throws MOGLiPluginException {
		return TemplateUtil.findMainTemplate(templateDir, MAIN_TEMPLATE_IDENTIFIER);
	}


	@Override
	public boolean unpackDefaultInputData() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("initDefaultInputData");
		
		
		final PluginPackedData defaultData = new PluginPackedData(this.getClass(), DEFAULT_DATA_DIR);
		final String[] beanFactoryClass = {"BeanFactoryClass.tpl"};
		defaultData.addDirectory("BeanFactoryClass", beanFactoryClass);
		final String[] beanFactoryReplaceTemplate = {"BeanFactoryReplaceTemplate.tpl"};
		defaultData.addDirectory("BeanFactoryReplaceTemplate", beanFactoryReplaceTemplate);
		final String[] beanFactoryInsertAboveTemplate = {"BeanFactoryInsertAboveTemplate.tpl"};
		defaultData.addDirectory("BeanFactoryInsertAboveTemplate", beanFactoryInsertAboveTemplate);
		final String[] beanFactoryInsertBelowTemplate = {"BeanFactoryInsertBelowTemplate.tpl"};
		defaultData.addDirectory("BeanFactoryInsertBelowTemplate", beanFactoryInsertBelowTemplate);
		defaultData.addFile(PLUGIN_PROPERTIES_FILE);
		
		PluginDataUnpacker.doYourJob(defaultData, infrastructure.getPluginInputDir(), infrastructure.getPluginLogger());
		return true;
	}

	@Override
	public PluginType getPluginType() {
		return PluginType.GENERATOR;
	}

	@Override
	public String getId() {
		return PLUGIN_ID;
	}

	@Override
	public List<String> getDependencies() {
		return ImmutableUtil.getImmutableListOf(MODEL_PROVIDER_ID, ENGINE_PROVIDER_ID);
	}
	
	@Override
	public List<MetaInfoValidator> getMetaInfoValidatorList() throws MOGLiPluginException {
		final File validationInputFile = new File(infrastructure.getPluginInputDir(), 
				                                  MetaInfoValidationUtil.FILENAME_VALIDATION);
		return MetaInfoValidationUtil.getMetaInfoValidatorList(validationInputFile, getId());
	}

	@Override
	public InfrastructureService getMOGLiInfrastructure() {
		return infrastructure;
	}

	@Override
	public boolean unpackPluginHelpFiles() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("unpackPluginHelpFiles");
		final PluginPackedData helpData = new PluginPackedData(this.getClass(), HELP_DATA_DIR);
		helpData.addFile("TemplateFileHeaderInserterAttributes.htm");
		PluginDataUnpacker.doYourJob(helpData, infrastructure.getPluginHelpDir(), infrastructure.getPluginLogger());
		return true;
	}

}
