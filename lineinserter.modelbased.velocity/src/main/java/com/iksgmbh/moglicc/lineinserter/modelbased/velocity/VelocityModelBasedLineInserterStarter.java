package com.iksgmbh.moglicc.lineinserter.modelbased.velocity;

import static com.iksgmbh.moglicc.generator.utils.GeneratorReportUtil.*;

import static com.iksgmbh.moglicc.lineinserter.modelbased.velocity.TextConstants.TEXT_END_REPLACE_INDICATOR_NOT_FOUND;
import static com.iksgmbh.moglicc.lineinserter.modelbased.velocity.TextConstants.TEXT_START_REPLACE_INDICATOR_NOT_FOUND;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.helper.IOEncodingHelper;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.GeneratorResultData;
import com.iksgmbh.moglicc.generator.utils.ArtefactListUtil;
import com.iksgmbh.moglicc.generator.utils.EncodingUtils;
import com.iksgmbh.moglicc.generator.utils.GeneratorReportUtil;
import com.iksgmbh.moglicc.generator.utils.GeneratorReportUtil.GeneratorStandardReportData;
import com.iksgmbh.moglicc.generator.utils.ModelMatcherGeneratorUtil;
import com.iksgmbh.moglicc.generator.utils.TemplateUtil;
import com.iksgmbh.moglicc.generator.utils.helper.PluginDataUnpacker;
import com.iksgmbh.moglicc.generator.utils.helper.PluginPackedData;
import com.iksgmbh.moglicc.plugin.subtypes.generators.Inserter;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ModelBasedEngineProvider;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ModelProvider;
import com.iksgmbh.moglicc.provider.engine.velocity.BuildUpVelocityEngineData;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidationUtil;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidatorVendor;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validator.ConditionalMetaInfoValidator;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.ImmutableUtil;
import com.iksgmbh.utils.StringUtil;

public class VelocityModelBasedLineInserterStarter implements Inserter, MetaInfoValidatorVendor {

	public static final String BEAN_FACTORY_DIR = "BeanFactory";
	public static final String PLUGIN_ID = "VelocityModelBasedLineInserter";
	public static final String MODEL_PROVIDER_ID = "StandardModelProvider";
	public static final String ENGINE_PROVIDER_ID = "VelocityEngineProvider";
	public static final String MAIN_TEMPLATE_IDENTIFIER = "Main";
	public static final String PLUGIN_PROPERTIES_FILE = "generator.properties";

	private InfrastructureService infrastructure;
	private ModelBasedEngineProvider velocityEngineProvider;
	private IOEncodingHelper encodingHelper;
	private String targetFileOfCurrentArtefact;
	private int insertCounter = 0;
	private int createdCounter = 0;
	private StringBuffer reportForCurrentArtefact = new StringBuffer();
	private StringBuffer generationReport = new StringBuffer();
	final GeneratorStandardReportData standardReportData = new GeneratorStandardReportData();

	@Override
	public void setInfrastructure(final InfrastructureService infrastructure) {
		this.infrastructure = infrastructure;
	}

	@Override
	public void doYourJob() throws MOGLiPluginException 
	{
		standardReportData.jobStarted = true;
		infrastructure.getPluginLogger().logInfo("Doing my job...");
		velocityEngineProvider = (ModelBasedEngineProvider) infrastructure.getProvider(ENGINE_PROVIDER_ID);
		encodingHelper = null;
		final List<String> list = getArtefactList();  // read before possible exception for model access may be thrown
		standardReportData.numberOfAllInputArtefacts = list.size();
		
		if (standardReportData.numberOfAllInputArtefacts == 0) {
			infrastructure.getPluginLogger().logInfo("No input artefacts . Nothing to do.");
			return;
		}
		
		final ModelProvider modelProvider = (ModelProvider) infrastructure.getProvider(MODEL_PROVIDER_ID);
		
		try
		{
			standardReportData.model = modelProvider.getModel(PLUGIN_ID);
		} catch (MOGLiPluginException e)
		{
			standardReportData.modelError = e.getMessage();
			throw e;
		}		
		
		infrastructure.getPluginLogger().logInfo("Model '" + standardReportData.model.getName() + "' retrieved from " + MODEL_PROVIDER_ID);

		for (final String artefact : list) 
		{
			reportForCurrentArtefact = new StringBuffer(); 
			generateArtefactReportHeader(artefact);
			final File templateDir = new File(infrastructure.getPluginInputDir(), artefact);
			final List<String> mainTemplates = findMainTemplates(templateDir);
			targetFileOfCurrentArtefact = null;
			for (final String mainTemplate : mainTemplates) {
				infrastructure.getPluginLogger().logInfo("-");
				final String targetFileReadFromMainTemplate = applyModelToArtefactTemplate(artefact, templateDir, mainTemplate);
				validateTargetFileOfCurrentArtefact(artefact, targetFileReadFromMainTemplate);
			}
			generationReport.append(reportForCurrentArtefact);
			infrastructure.getPluginLogger().logInfo("Doing my job...");
		}

		infrastructure.getPluginLogger().logInfo("Done!");
	}

	protected void validateTargetFileOfCurrentArtefact(final String artefact,
			                                           final String targetFileReadFromMainTemplate)
			                                           throws MOGLiPluginException {
		if (targetFileReadFromMainTemplate != null) {
			if (targetFileOfCurrentArtefact == null) {
				targetFileOfCurrentArtefact = targetFileReadFromMainTemplate; // init for first main template
			} else {
				if (! targetFileOfCurrentArtefact.equals(targetFileReadFromMainTemplate)) {
					final String errorMessage = "There are main templates for artefact '" + artefact +
		                                        "' that differ in there targetFileName or targetDir!";
					standardReportData.invalidInputArtefacts.add("Error: " + errorMessage);
					throw new MOGLiPluginException(errorMessage);
				}
			}
		}
	}

	private void generateArtefactReportHeader(final String artefact) 
	{
		standardReportData.numberOfOutputArtefacts++;
		reportForCurrentArtefact.append(FileUtil.getSystemLineSeparator());
		reportForCurrentArtefact.append(GeneratorReportUtil.getArtefactReportLine(artefact));
		reportForCurrentArtefact.append(FileUtil.getSystemLineSeparator());
	}

	private String applyModelToArtefactTemplate(final String artefact, final File templateDir,
			                                    final String mainTemplate) throws MOGLiPluginException
	{
		final VelocityLineInserterResultData velocityResult;
		try
		{
			velocityResult = doInsertsByCallingVelocityEngineProvider(artefact, templateDir, mainTemplate);
		} catch (MOGLiPluginException e)
		{
			standardReportData.invalidInputArtefacts.add("Error parsing artefact properties for artefact '" + artefact + "': " + e.getMessage());
			throw e;
		}		
		
		try
		{
			velocityResult.validatePropertyKeys(artefact);
		} catch (MOGLiPluginException e)
		{
			standardReportData.invalidInputArtefacts.add(artefact + " has invalid property keys: " + e.getMessage());
			throw e;
		}


		if (! ModelMatcherGeneratorUtil.doesItMatch(velocityResult, standardReportData.model.getName())) 
		{
			infrastructure.getPluginLogger().logInfo("Artefact '" + artefact + "' has defined '"
                                                    + velocityResult.getNameOfValidModel() + "' as valid model.");
			infrastructure.getPluginLogger().logInfo("This artefact is not generated for current model '" + standardReportData.model.getName() + "'.");

			return null;
		}
		
		standardReportData.numberOfModelMatchingInputArtefacts++;

		try
		{
			velocityResult.validatePropertyForMissingMetaInfoValues(artefact);
		} catch (MOGLiPluginException e)
		{
			standardReportData.invalidInputArtefacts.add(artefact + " uses invalid metainfos: " + e.getMessage());
			throw e;
		}


		if (velocityResult.skipGeneration()) {
			infrastructure.getPluginLogger().logInfo("Generation of file '" + velocityResult.getTargetFileName()
							                         + "' was skipped as configured for artefact " + artefact + ".");
			standardReportData.skippedArtefacts.add(velocityResult.getTargetFileName());
			reportForCurrentArtefact= new StringBuffer();
			return null;
		}

		encodingHelper = IOEncodingHelper.getInstance(EncodingUtils.getValidOutputEncodingFormat(velocityResult.getOutputEncodingFormat(),
				                                      infrastructure.getPluginLogger()));
		try
		{
			writeResultIntoPluginOutputDir(velocityResult, artefact);
			writeResultIntoTargetDefinedInTemplate(velocityResult);
		} catch (MOGLiPluginException e)
		{
			standardReportData.invalidInputArtefacts.add("Cannot write output artefact for input artefact '" + artefact + "': " + e.getMessage());
			throw e;
		}
		
		generateReportForResult(velocityResult);
		infrastructure.getPluginLogger().logInfo("Generated content for artefact '" + artefact
				+ "' inserted into " + velocityResult.getTargetDir() + "/" + velocityResult.getTargetFileName());
		return velocityResult.getTargetDir() + "/" + velocityResult.getTargetFileName();
	}

	private VelocityLineInserterResultData doInsertsByCallingVelocityEngineProvider(final String artefact, final File templateDir,
			                                                                    final String mainTemplate) throws MOGLiPluginException {
		final BuildUpVelocityEngineData engineData = new BuildUpVelocityEngineData(artefact, standardReportData.model, PLUGIN_ID);
		engineData.setTemplateDir(templateDir);
		engineData.setTemplateFileName(mainTemplate);

		final VelocityLineInserterResultData result = insert(engineData);
		return result;
	}

	private void generateReportForResult(final VelocityLineInserterResultData resultData) 
	{
		final String generatedContent = resultData.getGeneratedContent().trim();
		final int numLines = StringUtil.getLinesFromText(generatedContent).size();

		if (resultData.isTargetToBeCreatedNewly()) {
			createdCounter++;
			reportForCurrentArtefact.append("      ");
			reportForCurrentArtefact.append(resultData.getTargetFileName());
			reportForCurrentArtefact.append(" was created in ");
			reportForCurrentArtefact.append(resultData.getTargetDir());
			reportForCurrentArtefact.append(FileUtil.getSystemLineSeparator());
			return;
		} else if (resultData.wasExistingTargetPreserved()) {
			reportForCurrentArtefact.append("      ");
			reportForCurrentArtefact.append(resultData.getTargetFileName());
			reportForCurrentArtefact.append(" did already exist and was NOT overwritten in ");
			reportForCurrentArtefact.append(resultData.getTargetDir());
			reportForCurrentArtefact.append(FileUtil.getSystemLineSeparator());
			return;
		} else if (resultData.getInsertAboveIndicator() != null) {
			insertCounter++;
			reportForCurrentArtefact.append("      " + numLines + " line(s) inserted by a above-instruction into "); 
		} else if (resultData.getInsertBelowIndicator() != null) {
			insertCounter++;
			reportForCurrentArtefact.append("      " + numLines + " line(s) inserted by a below-instruction into ");
		} else if (resultData.getReplaceStartIndicator() != null) {
			insertCounter++;
			reportForCurrentArtefact.append("      " + numLines + " line(s) inserted by a replace-instruction into "); 
		}
		reportForCurrentArtefact.append(resultData.getTargetFileName());
		reportForCurrentArtefact.append(" in directory ");
		reportForCurrentArtefact.append(resultData.getTargetDir());
		reportForCurrentArtefact.append(FileUtil.getSystemLineSeparator());
	}

	private void writeResultIntoPluginOutputDir(final VelocityLineInserterResultData resultData, final String subDir)
	             throws MOGLiPluginException {
		final File targetdir = new File(infrastructure.getPluginOutputDir(), subDir);
		targetdir.mkdirs();
		final File outputFile = new File(targetdir, resultData.getTargetFileName());
		try {
			FileUtil.createNewFileWithContent(encodingHelper, outputFile, resultData.getGeneratedContent());
		} catch (Exception e) {
			throw new MOGLiPluginException("Error creating file\n" + outputFile.getAbsolutePath());
		}
	}

	private void writeResultIntoTargetDefinedInTemplate(final VelocityLineInserterResultData resultData)
	             throws MOGLiPluginException {
		final File outputFile = resultData.getTargetFile(infrastructure.getApplicationRootDir().getAbsolutePath(), null);
		infrastructure.getPluginLogger().logInfo("Creating file: " + outputFile.getAbsolutePath());
		final String buildOutputFileContent = buildOutputFileContent(outputFile, resultData);
		if (buildOutputFileContent !=  null) {
			try {
				FileUtil.createNewFileWithContent(encodingHelper, outputFile, buildOutputFileContent);
			} catch (Exception e) {
				throw new MOGLiPluginException("Error creating file\n" + outputFile.getAbsolutePath(), e);
			}
		}
	}

	private String buildOutputFileContent(final File outputFile, final VelocityLineInserterResultData resultData)
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
		resultData.setExistingTargetPreserved(true);
		return null;
	}

	private String mergeOldAndNewContent(final File outputFile,
			                           final VelocityLineInserterResultData resultData) throws MOGLiPluginException {
		final List<String> oldContent;
		try {
			oldContent = FileUtil.getFileContentAsList(outputFile);
		} catch (IOException e) {
			throw new MOGLiPluginException("Error creating file\n" + outputFile.getAbsolutePath(), e);
		}

		String generatedContent = resultData.getGeneratedContent();
		final String insertAboveIndicator = resultData.getInsertAboveIndicator();
		if (insertAboveIndicator != null) {
			generatedContent = InsertAbove(oldContent, generatedContent, insertAboveIndicator);
			infrastructure.getPluginLogger().logInfo("Generated Content inserted above '"
		            + insertAboveIndicator + "' in\n" + outputFile.getAbsolutePath());
			return generatedContent;
		}

		final String insertBelowIndicator = resultData.getInsertBelowIndicator();
		if (insertBelowIndicator != null) {
			generatedContent = InsertBelow(oldContent, generatedContent, insertBelowIndicator);
			infrastructure.getPluginLogger().logInfo("Generated Content inserted below '"
					                                 + insertBelowIndicator + "' in\n" + outputFile.getAbsolutePath());
			return generatedContent;
		}

		final String replaceStartIndicator = resultData.getReplaceStartIndicator();
		if (replaceStartIndicator != null) {
			generatedContent = replace(oldContent, generatedContent, replaceStartIndicator, resultData.getReplaceEndIndicator());
			infrastructure.getPluginLogger().logInfo("Generated Content replaced in\n" + outputFile.getAbsolutePath());
			return generatedContent;
		}

		throw new MOGLiPluginException("Invalid VelocityLineInserterResultData Setting");
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
			                   final String insertAboveIndicator) throws MOGLiPluginException {
		final StringBuffer sb = new StringBuffer();
		boolean indicatorFound = false;
		for (final String line : oldContent) {
			if (line.contains(insertAboveIndicator)) {
				sb.append(contentToInsert);
				sb.append(FileUtil.getSystemLineSeparator());
				indicatorFound = true;
			}
			sb.append(line);
			sb.append(FileUtil.getSystemLineSeparator());
		}
		if (! indicatorFound) {
			throw new MOGLiPluginException(TextConstants.TEXT_INSERT_ABOVE_INDICATOR_NOT_FOUND + insertAboveIndicator);
		}
		return sb.toString();
	}

	List<String> getArtefactList() throws MOGLiPluginException {
		final File generatorPropertiesFile = new File(infrastructure.getPluginInputDir(), PLUGIN_PROPERTIES_FILE);
		return ArtefactListUtil.getArtefactListFrom(infrastructure.getPluginInputDir(), generatorPropertiesFile);
	}

	VelocityLineInserterResultData insert(final BuildUpVelocityEngineData engineData) throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("Starting velocity engine for artefact '"
				+ engineData.getArtefactType()  + " and with template '"
				+ engineData.getMainTemplateSimpleFileName() + "'...");

		velocityEngineProvider.setEngineData(engineData);
		
		final GeneratorResultData generatorResultData;
		try
		{
			generatorResultData = velocityEngineProvider.startEngineWithModel(); // this does the actual insert job
		} catch (MOGLiPluginException e)
		{
			standardReportData.invalidInputArtefacts.add("Velocity Engine Provider Error:" + e.getMessage());
			throw e;
		}
				
		return new VelocityLineInserterResultData(generatorResultData);
	}

	List<String> findMainTemplates(final File templateDir) throws MOGLiPluginException {
		return TemplateUtil.findMainTemplates(templateDir, MAIN_TEMPLATE_IDENTIFIER);
	}


	@Override
	public boolean unpackDefaultInputData() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("initDefaultInputData");


		final PluginPackedData defaultData = new PluginPackedData(this.getClass(), DEFAULT_DATA_DIR, PLUGIN_ID);

		final String[] templates = {"BeanFactoryClassMain.tpl",               "BeanFactoryReplaceTemplateMain.tpl",
				                    "BeanFactoryInsertAboveTemplateMain.tpl", "BeanFactoryInsertBelowTemplateMain.tpl"};
		defaultData.addFlatFolder(BEAN_FACTORY_DIR, templates);

		defaultData.addRootFile(PLUGIN_PROPERTIES_FILE);
		defaultData.addRootFile(MetaInfoValidationUtil.FILENAME_VALIDATION);

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
		final File validationInputFile = new File(infrastructure.getPluginInputDir(), MetaInfoValidationUtil.FILENAME_VALIDATION);
		final List<MetaInfoValidator> metaInfoValidatorList = MetaInfoValidationUtil.getMetaInfoValidatorList(validationInputFile, getId());

		for (final MetaInfoValidator metaInfoValidator : metaInfoValidatorList) {
			if (metaInfoValidator instanceof ConditionalMetaInfoValidator) {
				readConditionFileIfNecessary(metaInfoValidator);
			}
		}

		infrastructure.getPluginLogger().logInfo(metaInfoValidatorList.size() + " MetaInfoValidators found.");
		return metaInfoValidatorList;
	}

	private void readConditionFileIfNecessary(final MetaInfoValidator metaInfoValidator) throws MOGLiPluginException {
		final ConditionalMetaInfoValidator conditionalMetaInfoValidator = (ConditionalMetaInfoValidator) metaInfoValidator;
		if (conditionalMetaInfoValidator.getConditionFilename() != null) {
			final File conditionInputFile = new File(infrastructure.getPluginInputDir(), conditionalMetaInfoValidator.getConditionFilename());
			conditionalMetaInfoValidator.setConditionList(MetaInfoValidationUtil.getConditionList(conditionInputFile));
		}
	}

	@Override
	public InfrastructureService getInfrastructure() {
		return infrastructure;
	}

	@Override
	public boolean unpackPluginHelpFiles() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("unpackPluginHelpFiles");
		final PluginPackedData helpData = new PluginPackedData(this.getClass(), HELP_DATA_DIR, PLUGIN_ID);

		helpData.addRootFile(ARTEFACT_PROPERTIES_HELP_FILE);

		PluginDataUnpacker.doYourJob(helpData, infrastructure.getPluginHelpDir(), infrastructure.getPluginLogger());
		return true;
	}

	@Override
	public String getGeneratorReport() 
	{
		standardReportData.additionalReport = FileUtil.getSystemLineSeparator() 
				                              + REPORT_TAB + generationReport.toString().trim();
		return GeneratorReportUtil.getReport(standardReportData); 
	}

	@Override
	public int getNumberOfGenerations() {
		return insertCounter + createdCounter;
	}

	@Override
	public int getNumberOfGeneratedArtefacts() {
		return standardReportData.numberOfOutputArtefacts;
	}
	
	@Override
	public String getShortReport()
	{
		final String standardReport = GeneratorReportUtil.getShortReport(standardReportData);

		if (! StringUtils.isEmpty(standardReport)) {
			return standardReport;
		}
		
		return "From " + standardReportData.numberOfAllInputArtefacts + " input artefact(s), have been " 
		        + standardReportData.numberOfOutputArtefacts + " used to create " + createdCounter + " file(s) and to perform " 
		        + insertCounter + " insertion(s).";
	}

	@Override
	public int getSuggestedPositionInExecutionOrder()
	{
		return 500;
	}
}
