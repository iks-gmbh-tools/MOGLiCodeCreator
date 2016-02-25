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
package com.iksgmbh.moglicc.filemaker.classbased.velocity;

import static com.iksgmbh.moglicc.generator.utils.GeneratorReportUtil.REPORT_TAB;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.helper.IOEncodingHelper;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.GeneratorResultData;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData;
import com.iksgmbh.moglicc.generator.utils.ArtefactListUtil;
import com.iksgmbh.moglicc.generator.utils.EncodingUtils;
import com.iksgmbh.moglicc.generator.utils.GeneratorReportUtil;
import com.iksgmbh.moglicc.generator.utils.GeneratorReportUtil.GeneratorStandardReportData;
import com.iksgmbh.moglicc.generator.utils.ModelMatcherGeneratorUtil;
import com.iksgmbh.moglicc.generator.utils.TemplateUtil;
import com.iksgmbh.moglicc.generator.utils.helper.PluginDataUnpacker;
import com.iksgmbh.moglicc.generator.utils.helper.PluginPackedData;
import com.iksgmbh.moglicc.plugin.subtypes.GeneratorPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ClassBasedEngineProvider;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ModelProvider;
import com.iksgmbh.moglicc.provider.engine.velocity.BuildUpVelocityEngineData;
import com.iksgmbh.moglicc.provider.engine.velocity.VelocityEngineData.ExecutionMode;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidatorVendor;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validation.MetaInfoValidationUtil;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validator.ConditionalMetaInfoValidator;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.ImmutableUtil;

public class VelocityClassBasedFileMakerStarter implements GeneratorPlugin, MetaInfoValidatorVendor {

	public static final String PLUGIN_ID = "VelocityClassBasedFileMaker";

	public static final String MODEL_PROVIDER_ID = "StandardModelProvider";
	public static final String ENGINE_PROVIDER_ID = "VelocityEngineProvider";
	public static final String MAIN_TEMPLATE_IDENTIFIER = "Main";
	public static final String PLUGIN_PROPERTIES_FILE = "generator.properties";

	public static final String ARTEFACT_COMMON = "commonSubtemplates";
	public static final String ARTEFACT_JAVABEAN = "MOGLiJavaBean";
	public static final String ARTEFACT_JAVABEAN_TEST = "MOGLiJavaBean_Test";
	public static final String ARTEFACT_JAVABEAN_BUILDER = "MOGLiJavaBeanBuilder";
	public static final String ARTEFACT_JAVABEAN_BUILDER_TEST = "MOGLiJavaBeanBuilder_Test";
	public static final String ARTEFACT_JAVABEAN_VALIDATOR = "MOGLiJavaBeanValidator";
	public static final String ARTEFACT_JAVABEAN_VALIDATOR_TEST = "MOGLiJavaBeanValidator_Test";
	public static final String ARTEFACT_JAVABEAN_FACTORY = "MOGLiJavaBeanFactory";	
	public static final String ARTEFACT_JAVABEAN_FACTORY_TEST = "MOGLiJavaBeanFactory_Test";
	public static final String ARTEFACT_MOGLICC_NEW_PLUGIN = "MOGLiCC_NewPluginModel";
	public static final String ARTEFACT_CONSOLE_COMICS_TRIP_DIR = "ConsoleComicStrip";

	private static final String LOGFILE_LINE_SEPARATOR = "-----";

	final static String[] javabeanTemplates = {
		    "A_MainTemplate.tpl",         "B_ClassJavaDoc.tpl",            "C_ClassDefinitionLine.tpl",
			"D_Serializable.tpl",         "E_Variables.tpl",               "F_SetterMethods.tpl",
			"G_GetterMethods.tpl",        "H_toStringMethod.tpl",          "I_equalsMethod.tpl",         
			"I1_equalsArrayTypes.tpl",    "I2_equalsPrimitiveTypes.tpl",   "I3_equalsStandardTypes.tpl",      			
			"J_hashCodeMethod.tpl",       "J2_hashCodePrimitiveTypes.tpl", "K_cloneMethod.tpl",      
			"K1_cloneArrayType.tpl",	  "K2_cloneCollectionType.tpl",    "K3_cloneStandardType.tpl" };

	final static String[] javabeanTestTemplates = {"A_MainTemplate.tpl", "C_setupMethod.tpl",
                                                   "D_testEqualsMethods.tpl", "E_testHashcodeMethods.tpl",
                                                   "F_createsNewInstanceUsing_CloneWith_MethodOfBuilder.tpl",
                                                   "modifyValueOfPrimitiveType.tpl"};

	final static String[] javabeanBuilderTemplates = {"A_MainTemplate.tpl", "C_withMethods.tpl",
		                                              "D_cloneWithMethods.tpl", "E_cloneDataObjectMethod.tpl"};

	final static String[] javabeanBuilderTestTemplates = {"A_MainTemplate.tpl", "C_buildEmptyInstanceMethod.tpl"};

	final static String[] javabeanValidatorTemplates = {"A_MainTemplate.tpl", "C_Constructor.tpl",
                                                        "D_validateMethod.tpl"};

	final static String[] javabeanValidatorTestTemplates = {"A_MainTemplate.tpl", "C_setupMethod.tpl",
		                                                    "D_mandatoryTestMethods.tpl", "E_minLengthTestMethods.tpl",
		                                                    "F_maxLengthTestMethods.tpl", "G_multipleErrorMessageTestMethod.tpl",
		                                                    "H_multipleErrorMessageForNotReachingMinLength.tpl",
		                                                    "I_multipleErrorMessageForExceedingMaxLength.tpl",
		                                                    "K_InvalidCharTestMethods.tpl"};

	final static String[] javabeanCommonSubtempates = {"importDomainModelClasses.tpl", "isJavaTypeDomainObject.tpl", 
		                                               "isFieldLengthRelevantForJavaType.tpl", "setContentLargerThanMaxLengthToField.tpl", 
		                                               "setContentSmallerThanMinLengthToField.tpl", "generateListOfDomainObjectsFromExampleDataOrDataPool.tpl",
		                                               "checkForJavaTypeListOfDomainObjects.tpl" };

	final static String[] javabeanFactoryTestTempates = {"A_MainTemplate.tpl", "B_buildReturnsFirstMethod.tpl", "C_buildReturnsAllMethod.tpl", 
		                                                 "D_buildReturnsInstanceWithAllFieldsAtMaxLength.tpl",
		                                                 "E_buildReturnsInstanceWithAllFieldsAtMinLength.tpl",
													     "G_buildReturnsInstanceWithAllSupportedFieldsExceedingMaxLength.tpl",
													     "H_buildReturnsInstanceWithAllSupportedFieldsNotReachingMinLength.tpl",
													     "I_buildReturnsExampleDataInstance.tpl"};
	
	final static String[] javabeanFactoryTempates = {"A_MainTemplate.tpl", "B1_createInstanceWithAllFieldsAtMaxLength.tpl", 
		                                             "B2_createInstanceWithAllFieldsAtMinLength.tpl",
		                                             "C_buildObjectMethod.tpl", "C_buildObjectMethodWithRegistry.tpl", "D_buildDataPool.tpl",
		                                             "E_buildCutFieldContentMethod.tpl", "F_buildAddToFieldContentMethod.tpl",
		                                             "G_buildCreateInstanceWithAllSupportedFieldsExceedingMaxLength.tpl",
		                                             "H_buildCreateInstanceWithAllSupportedFieldsNotReachingMinLength.tpl",
		                                             "I_buildCreateExampleInstanceMethod.tpl"};
	
	final static String[] MOGLiCCNewPluginSubtempates = {"A_MainTemplate.tpl", "B_ClassDefinitionLine.tpl", "C_generatorVariables.tpl",
		                                                 "D_unpackDefaultInputData.tpl", "E_getMetaInfoValidatorList.tpl",
		                                                 "F_unpackPluginHelpFiles.tpl", "G_getModel.tpl",
		                                                 "H_engineMethods.tpl", "I_generatorMethods.tpl"};

	final static String[] consoleComicStripTemplates = {"A_MainTemplate.tpl", "buildLineToDraw.tpl", "drawFrame.tpl",
        										"drawImageLineIntoFrame.tpl", "drawStaticFooter.tpl", "drawStaticHeader.tpl", 
        										"echoFrameLine.tpl", "handleTransparancy.tpl", "initBackgroundLines.tpl",
	                                            "initGlobalVariables.tpl", "initImageRotationCounter.tpl", "initVariables.tpl",
												"readLinesOfCurrentImage.tpl", "validateVariables.tpl"};

	private InfrastructureService infrastructure;
	private IOEncodingHelper encodingHelper;
	private String testDir = "";
	final GeneratorStandardReportData standardReportData = new GeneratorStandardReportData();
	private int generationCounter = 0;
	private StringBuffer generationReport = new StringBuffer();
	private List<VelocityFileMakerResultData> preparationResultList;

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
	public void setInfrastructure(InfrastructureService infrastructure) {
		this.infrastructure = infrastructure;
	}

	@Override
	public void doYourJob() throws MOGLiPluginException {
		standardReportData.jobStarted = true;
		infrastructure.getPluginLogger().logInfo("Doing my job...");
		encodingHelper = null;
		final List<String> artefactList = getArtefactList();  // read before possible exception for model access may be thrown
		standardReportData.numberOfAllInputArtefacts = artefactList.size();
		if (standardReportData.numberOfAllInputArtefacts == 0) {
			infrastructure.getPluginLogger().logInfo("No input artefacts . Nothing to do.");
			return;
		}

		try
		{
			standardReportData.model = getModel();
		} catch (MOGLiPluginException e)
		{
			standardReportData.modelError = e.getMessage();
			throw e;
		}
		
		if (standardReportData.model.getSize() == 0) {
			infrastructure.getPluginLogger().logInfo(standardReportData.numberOfAllInputArtefacts + " input artifact(s), but no class in model file found.");
			return;
		}

		for (final String artefact : artefactList) {
			applyModelToArtefactTemplates(standardReportData.model, artefact);
			infrastructure.getPluginLogger().logInfo(LOGFILE_LINE_SEPARATOR);
		}

		infrastructure.getPluginLogger().logInfo("Done!");
	}

	private Model getModel() throws MOGLiPluginException {
		final ModelProvider modelProvider = (ModelProvider) infrastructure.getProvider(MODEL_PROVIDER_ID);
		final Model model = modelProvider.getModel(PLUGIN_ID);
		int numberOfClasses = 0;
		if (standardReportData.model != null) {
			numberOfClasses = standardReportData.model.getSize();
		}
		infrastructure.getPluginLogger().logInfo("Model '" + model.getName() + "' with "
		                                         + numberOfClasses + " class(es) retrieved from "
				                                 + MODEL_PROVIDER_ID);
		infrastructure.getPluginLogger().logInfo(LOGFILE_LINE_SEPARATOR);
		return model;
	}

	private void applyModelToArtefactTemplates(final Model model, final String artefact) throws MOGLiPluginException 
	{
		final BuildUpVelocityEngineData engineData = new BuildUpVelocityEngineData(artefact, model, PLUGIN_ID);
		boolean doFullGeneration = doesModelAndTemplateMatch(model, artefact, engineData);
		
		if (! doFullGeneration) {
			return;
		}

		validateResult(artefact, preparationResultList);
		final List<VelocityFileMakerResultData> resultList = doFullGeneration(artefact, engineData);
		final List<VelocityFileMakerResultData> artefactsToCreate = removeSkippedClassesFromList(resultList, artefact);
		handleNumberSignReplacements(artefactsToCreate);
		generateOutput(artefact, resultList, artefactsToCreate);
		generateReportLines(artefactsToCreate, artefact);
		
		infrastructure.getPluginLogger().logInfo(artefactsToCreate.size() + " files for artefact '" + artefact + "' created!");
	}

	private void handleNumberSignReplacements(final	List<VelocityFileMakerResultData> artefactsToCreate)
	{
		for (VelocityFileMakerResultData resultData : artefactsToCreate)
		{
			String numberSignReplacement = resultData.getNumberSignReplacement();
			if (numberSignReplacement != null)
			{
				String generatedContent = resultData.getGeneratedContent();
				generatedContent = StringUtils.replace(generatedContent, numberSignReplacement, "#");
				resultData.setGeneratedContent(generatedContent);			}
		}
	}
	
	private void generateOutput(final String artefact,
							    final List<VelocityFileMakerResultData> resultList,
			                    final List<VelocityFileMakerResultData> artefactsToCreate)
			                    throws MOGLiPluginException 
	{
		encodingHelper = IOEncodingHelper.getInstance(EncodingUtils.getValidOutputEncodingFormat(
				                                      resultList.get(0).getOutputEncodingFormat(),
				                                      infrastructure.getPluginLogger()));
		try
		{
			writeFilesIntoPluginOutputDir(artefactsToCreate, artefact);
			writeFilesIntoTargetDirReadFromTemplateFile(artefactsToCreate, artefact);
		} catch (MOGLiPluginException e)
		{
			standardReportData.invalidInputArtefacts.add("Cannot write output artefact for input artefact '" + artefact + "': " + e.getMessage());
			throw e;
		}
	}

	private List<VelocityFileMakerResultData> doFullGeneration(final String artefact, 
			                                                   final BuildUpVelocityEngineData engineData)
			                                                   throws MOGLiPluginException 
	{
		standardReportData.numberOfModelMatchingInputArtefacts++;

		try
		{
			engineData.setExecutionMode(ExecutionMode.FULL_GENERATION);
			return generate(engineData);
		} 
		catch (MOGLiPluginException e)
		{
			standardReportData.invalidInputArtefacts.add("Error parsing artefact properties for artefact '" + artefact + "': " + e.getMessage());
			throw e;
		}
	}

	private boolean doesModelAndTemplateMatch(final Model model,
										      final String artefact, 
										      final BuildUpVelocityEngineData engineData)
										      throws MOGLiPluginException 
	{
		try
		{
			engineData.setExecutionMode(ExecutionMode.ONLY_PREPARATION);
			preparationResultList = generate(engineData);
		} 
		catch (MOGLiPluginException e)
		{
			standardReportData.invalidInputArtefacts.add("Error parsing artefact properties for artefact '" + artefact + "': " + e.getMessage());
			throw e;
		}

		final VelocityGeneratorResultData firstResultData = preparationResultList.get(0);
		if (! ModelMatcherGeneratorUtil.doesItMatch(firstResultData, model.getName())) 
		{
			infrastructure.getPluginLogger().logInfo("Artefact '" + artefact + "' has defined '" + preparationResultList.get(0).getNameOfValidModel()
					                                 + "' as valid model.");
			infrastructure.getPluginLogger().logInfo("This artefact is not generated for current model '" + model.getName() + "'.");
			standardReportData.nonModelMatchingInputArtefacts.add(artefact);
			return false;
		}
		
		return true;
	}

	private void validateResult(final String artefact,
			                    final List<VelocityFileMakerResultData> resultList)
			                    throws MOGLiPluginException 
	{
		try
		{
			validatePropertyKeys(resultList, artefact);
		} catch (MOGLiPluginException e)
		{
			standardReportData.invalidInputArtefacts.add(artefact + " has invalid property keys: " + e.getMessage());
			throw e;
		}
		
		try
		{
			validatePropertyForMissingMetaInfoValues(resultList, artefact);
		} catch (MOGLiPluginException e)
		{
			standardReportData.invalidInputArtefacts.add(artefact + " uses invalid metainfos: " + e.getMessage());
			throw e;
		}
	}

	private List<VelocityFileMakerResultData> removeSkippedClassesFromList(final List<VelocityFileMakerResultData> resultList,
			                                                               final String artefact)
    {
		final List<VelocityFileMakerResultData> toReturn = new ArrayList<VelocityFileMakerResultData>();
		for (final VelocityFileMakerResultData resultData : resultList) {
			if (resultData.isGenerationToSkip()) {
				infrastructure.getPluginLogger().logInfo("Generation of file '" + resultData.getTargetFileName()
						                                  + "' was skipped as configured for artefact " + artefact + ".");
				standardReportData.skippedArtefacts.add(resultData.getTargetFileName());
			} else {
				toReturn.add(resultData);
			}
		}
		return toReturn;
	}

	private void generateReportLines(final List<VelocityFileMakerResultData> resultList, final String artefact) {
		generationReport.append(FileUtil.getSystemLineSeparator());
		generationReport.append(GeneratorReportUtil.getArtefactReportLine(artefact));
		generationReport.append(FileUtil.getSystemLineSeparator());

		for (final VelocityFileMakerResultData resultData : resultList) {
			standardReportData.numberOfOutputArtefacts++;
			generationCounter++;
			if (resultData.wasExistingTargetPreserved()) {
				generationReport.append("      ");
				generationReport.append(resultData.getTargetFileName());
				generationReport.append(" did already exist and was NOT overwritten in ");
				generationReport.append(resultData.getTargetDir());
				generationReport.append(FileUtil.getSystemLineSeparator());
			} else {
				generationReport.append("      ");
				generationReport.append(resultData.getTargetFileName());
				generationReport.append(" was created in ");
				generationReport.append(GeneratorReportUtil.getTargetDirToDisplay(infrastructure, resultData.getTargetDir()));
				generationReport.append(FileUtil.getSystemLineSeparator());
			}
		}
	}

	private void validatePropertyKeys(final List<VelocityFileMakerResultData> resultList,
			              final String artefact) throws MOGLiPluginException
	{
		for (final VelocityFileMakerResultData resultData : resultList) {
			resultData.validatePropertyKeys(artefact);
		}
	}

	private void validatePropertyForMissingMetaInfoValues(final List<VelocityFileMakerResultData> resultList,
			                          final String artefact) throws MOGLiPluginException
	{
		for (final VelocityFileMakerResultData resultData : resultList) {
			resultData.validatePropertyForMissingMetaInfoValues(artefact);
		}
	}

	List<String> getArtefactList() throws MOGLiPluginException {
		final File generatorPropertiesFile = new File(infrastructure.getPluginInputDir(), PLUGIN_PROPERTIES_FILE);
		return ArtefactListUtil.getArtefactListFrom(infrastructure.getPluginInputDir(), generatorPropertiesFile);
	}

	void writeFilesIntoTargetDirReadFromTemplateFile(final List<VelocityFileMakerResultData> resultList,
			                                         final String artefact) throws MOGLiPluginException {
		for (final VelocityFileMakerResultData resultData : resultList) 
		{
			final File outputFile;
			try
			{
				outputFile = resultData.getTargetFile(infrastructure.getApplicationRootDir().getAbsolutePath(), getTestPathPrefix(true));
			} catch (MOGLiPluginException e)
			{
				standardReportData.invalidInputArtefacts.add("Error for artefact '" + artefact 
						                                      + "': " + e.getMessage());
				throw e;
			}
			 
			if (outputFile == null) {
				return; // @TargetDir not defined in template -> do nothing here
			}
			
			if (resultData.isTargetToBeCreatedNewly()
				|| ! outputFile.exists()) {
				try {
					FileUtil.createNewFileWithContent(encodingHelper, outputFile, resultData.getGeneratedContent());
				} catch (Exception e) {
					throw new MOGLiPluginException("Error creating file\n" + outputFile.getAbsolutePath(), e);
				}
			} else {
				infrastructure.getPluginLogger().logWarning("Target file " + outputFile.getAbsolutePath() + " exists and will not overwritten!");
				resultData.setExistingTargetPreserved(true);
			}
		}
	}

	private void writeFilesIntoPluginOutputDir(final List<VelocityFileMakerResultData> resultList, final String subDir)
	                                           throws MOGLiPluginException {
		final File targetdir = new File(infrastructure.getPluginOutputDir(), subDir);
		targetdir.mkdirs();
		for (final VelocityFileMakerResultData resultData : resultList) {
			if (resultData.getTargetDir() == null) {

			}
			final File outputFile = new File(targetdir, resultData.getTargetFileName());
			try {
				FileUtil.createNewFileWithContent(encodingHelper, outputFile, resultData.getGeneratedContent());
			} catch (Exception e) {
				throw new MOGLiPluginException("Error creating file\n" + outputFile.getAbsolutePath(), e);
			}
		}
	}

	List<VelocityFileMakerResultData> generate(final BuildUpVelocityEngineData engineData) throws MOGLiPluginException {
		final File templateDir = new File(infrastructure.getPluginInputDir(), engineData.getArtefactType());
		engineData.setTemplateDir(templateDir);
		engineData.setTemplateFileName(findMainTemplate(templateDir));

		final ClassBasedEngineProvider velocityEngineProvider =
			       (ClassBasedEngineProvider) infrastructure.getProvider(ENGINE_PROVIDER_ID);

		if (engineData.isExecutionModeOnlyPreparation()) 
		{
			infrastructure.getPluginLogger().logInfo("Starting velocity engine for preparation of artefact '"
					+ engineData.getArtefactType() + " and with template '"
					+ engineData.getMainTemplateSimpleFileName() + "'...");
		}
		else
		{
			infrastructure.getPluginLogger().logInfo("Starting velocity engine for full generation of artefact '"
					+ engineData.getArtefactType() + " and with template '"
					+ engineData.getMainTemplateSimpleFileName() + "'...");
		}

		velocityEngineProvider.setEngineData(engineData);
		
		
		final List<GeneratorResultData> generatorResultDataList;
		try
		{
			generatorResultDataList = velocityEngineProvider.startEngineWithClassList();
		} catch (MOGLiPluginException e)
		{
			standardReportData.invalidInputArtefacts.add("Velocity Engine Provider Error:" + e.getMessage());
			throw e;
		}
		
		return buildVelocityResultData(generatorResultDataList);
	}

	private List<VelocityFileMakerResultData> buildVelocityResultData(final List<GeneratorResultData>
	                                                                   generatorResultDataList) {
		final List<VelocityFileMakerResultData> velocityGeneratorResultData = new ArrayList<VelocityFileMakerResultData>();
		for (final GeneratorResultData generatorResultData : generatorResultDataList) {
			velocityGeneratorResultData.add(new VelocityFileMakerResultData(generatorResultData));
		}
		return velocityGeneratorResultData;
	}

	String findMainTemplate(final File templateDir) throws MOGLiPluginException {
		return TemplateUtil.findMainTemplate(templateDir, MAIN_TEMPLATE_IDENTIFIER);
	}

	@Override
	public boolean unpackDefaultInputData() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("initDefaultInputData");

		final PluginPackedData defaultData = new PluginPackedData(this.getClass(), DEFAULT_DATA_DIR, PLUGIN_ID);

		defaultData.addFlatFolder(ARTEFACT_COMMON, javabeanCommonSubtempates);
		defaultData.addFlatFolder(ARTEFACT_JAVABEAN, javabeanTemplates);
		defaultData.addFlatFolder(ARTEFACT_JAVABEAN_TEST, javabeanTestTemplates);
		defaultData.addFlatFolder(ARTEFACT_JAVABEAN_BUILDER, javabeanBuilderTemplates);
		defaultData.addFlatFolder(ARTEFACT_JAVABEAN_BUILDER_TEST, javabeanBuilderTestTemplates);
		defaultData.addFlatFolder(ARTEFACT_JAVABEAN_VALIDATOR, javabeanValidatorTemplates);
		defaultData.addFlatFolder(ARTEFACT_JAVABEAN_VALIDATOR_TEST, javabeanValidatorTestTemplates);
		defaultData.addFlatFolder(ARTEFACT_JAVABEAN_FACTORY, javabeanFactoryTempates);
		defaultData.addFlatFolder(ARTEFACT_JAVABEAN_FACTORY_TEST, javabeanFactoryTestTempates);		
		defaultData.addFlatFolder(ARTEFACT_MOGLICC_NEW_PLUGIN, MOGLiCCNewPluginSubtempates);
		defaultData.addFlatFolder(ARTEFACT_CONSOLE_COMICS_TRIP_DIR, consoleComicStripTemplates);		
		
		defaultData.addRootFile(PLUGIN_PROPERTIES_FILE);
		defaultData.addRootFile(MetaInfoValidationUtil.FILENAME_VALIDATION);

		PluginDataUnpacker.doYourJob(defaultData, infrastructure.getPluginInputDir(), infrastructure.getPluginLogger());
		return true;
	}

	private String getTestPathPrefix(final boolean withTestDir) {
		String prefix = "";
		if (withTestDir && ! StringUtils.isEmpty(testDir)) {
			prefix = testDir + "/";
		}
		return prefix;
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
			if (! conditionInputFile.exists()) {
				throw new MOGLiPluginException("Expected condition file does not exist: " + conditionInputFile.getAbsolutePath());
			}
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
		return generationCounter;
	}

	@Override
	public String getShortReport() 
	{
		final String standardReport = GeneratorReportUtil.getShortReport(standardReportData);

		if (! StringUtils.isEmpty(standardReport)) {
			return standardReport.trim();
		}	

		return "From " + standardReportData.numberOfAllInputArtefacts + " input artefact(s), have been " 
		        + standardReportData.numberOfOutputArtefacts + " used to make " + generationCounter + " files.";
	}
	
	@Override
	public int getNumberOfGeneratedArtefacts() {
		return standardReportData.numberOfOutputArtefacts;
	}
	
	@Override
	public int getSuggestedPositionInExecutionOrder()
	{
		return 400;
	}
	
	/**
	 * FOR TEST PURPOSE ONLY
	 */
	
	public void setTestDir(final String testDir) {
		this.testDir = testDir;
	}

	public IOEncodingHelper getEncodingHelper() {
		return encodingHelper;
	}

}