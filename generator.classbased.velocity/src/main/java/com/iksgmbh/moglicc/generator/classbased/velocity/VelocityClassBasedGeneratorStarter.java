package com.iksgmbh.moglicc.generator.classbased.velocity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.helper.IOEncodingHelper;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.data.GeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.utils.ArtefactListUtil;
import com.iksgmbh.moglicc.generator.utils.ModelValidationGeneratorUtil;
import com.iksgmbh.moglicc.generator.utils.TemplateUtil;
import com.iksgmbh.moglicc.generator.utils.EncodingUtils;
import com.iksgmbh.moglicc.generator.utils.helper.PluginDataUnpacker;
import com.iksgmbh.moglicc.generator.utils.helper.PluginPackedData;
import com.iksgmbh.moglicc.plugin.type.ClassBasedEngineProvider;
import com.iksgmbh.moglicc.plugin.type.basic.Generator;
import com.iksgmbh.moglicc.provider.engine.velocity.BuildUpVelocityEngineData;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidationUtil;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidatorVendor;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validator.ConditionalMetaInfoValidator;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.ImmutableUtil;

public class VelocityClassBasedGeneratorStarter implements Generator, MetaInfoValidatorVendor {

	private static final String LOGFILE_LINE_SEPARATOR = "-----";
	public static final String PLUGIN_ID = "VelocityClassBasedGenerator";
	public static final String MODEL_PROVIDER_ID = "StandardModelProvider";
	public static final String ENGINE_PROVIDER_ID = "VelocityEngineProvider";
	public static final String MAIN_TEMPLATE_IDENTIFIER = "Main";
	public static final String PLUGIN_PROPERTIES_FILE = "generator.properties";

	public static final String ARTEFACT_COMMON = "commonSubtemplates";
	public static final String ARTEFACT_JAVABEAN = "MOGLiJavaBean";
	public static final String ARTEFACT_JAVABEAN_TEST = "MOGLiJavaBeanTest";
	public static final String ARTEFACT_JAVABEAN_BUILDER = "MOGLiJavaBeanBuilder";
	public static final String ARTEFACT_JAVABEAN_VALIDATOR = "MOGLiJavaBeanValidator";
	public static final String ARTEFACT_JAVABEAN_VALIDATOR_TEST = "MOGLiJavaBeanValidatorTest";
	public static final String ARTEFACT_MOGLICC_NEW_PLUGIN = "MOGLiCC_NewPluginModel";

	final static String[] javabeanTemplates = {
		    "A_MainTemplate.tpl",         "E_Variables.tpl",
		    "G_GetterMethods.tpl",        "F_SetterMethods.tpl",     "C_ClassDefinitionLine.tpl",
			"D_Serializable.tpl",         "H_toStringMethod.tpl",    "J_hashCodeMethod.tpl",
			"I_equalsMethod.tpl",         "I1_equalsArrayTypes.tpl", "I2_equalsPrimitiveTypes.tpl",
			"I3_equalsStandardTypes.tpl", "J_hashCodeMethod.tpl",    "J2_hashCodePrimitiveTypes.tpl",
			"K_cloneMethod.tpl",          "K1_cloneArrayType.tpl",   "K2_cloneCollectionType.tpl",
			"K3_cloneStandardType.tpl"};

	final static String[] javabeanTestTemplates = {"A_MainTemplate.tpl", "C_setupMethod.tpl",
                                                   "D_testEqualsMethods.tpl", "E_testHashcodeMethods.tpl",
                                                   "F_createsNewInstanceUsing_CloneWith_MethodOfBuilder.tpl",
                                                   "modifyValueOfPrimitiveType.tpl"};

	final static String[] javabeanBuilderTemplates = {"A_MainTemplate.tpl", "C_withMethods.tpl",
		                                              "D_cloneWithMethods.tpl", "E_cloneDataObjectMethod.tpl"};

	final static String[] javabeanValidatorTemplates = {"A_MainTemplate.tpl", "C_Constructor.tpl",
                                                        "D_validateMethod.tpl"};

	final static String[] javabeanValidatorTestTemplates = {"A_MainTemplate.tpl", "C_setupMethod.tpl",
		                                                    "D_mandatoryTestMethods.tpl", "E_minLengthTestMethods.tpl",
		                                                    "F_maxLengthTestMethods.tpl"};

	final static String[] javabeanCommonSubtempates = {"B_ImportStatements.tpl"};

	final static String[] MOGLiCCNewPluginSubtempates = {"A_MainTemplate.tpl", "B_ClassDefinitionLine.tpl", "C_generatorVariables.tpl",
		                                                 "D_unpackDefaultInputData.tpl", "E_getMetaInfoValidatorList.tpl",
		                                                 "F_unpackPluginHelpFiles.tpl", "G_getModel.tpl",
		                                                 "H_engineMethods.tpl", "I_generatorMethods.tpl"};

	private InfrastructureService infrastructure;
	private IOEncodingHelper encodingHelper;
	private String testDir = "";
	private int generationCounter = 0;
	private int artefactCounter;
	private StringBuffer generationReport = new StringBuffer();

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
	public void setMOGLiInfrastructure(InfrastructureService infrastructure) {
		this.infrastructure = infrastructure;
	}

	@Override
	public void doYourJob() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("Doing my job...");
		encodingHelper = null;

		final Model model = infrastructure.getModelProvider(MODEL_PROVIDER_ID).getModel(PLUGIN_ID);
		infrastructure.getPluginLogger().logInfo("Model '" + model.getName() + "' retrieved from " + MODEL_PROVIDER_ID);
		infrastructure.getPluginLogger().logInfo(LOGFILE_LINE_SEPARATOR);

		final List<String> list = getArtefactList();
		for (final String artefact : list) {
			applyModelToArtefactTemplates(model, artefact);
			infrastructure.getPluginLogger().logInfo(LOGFILE_LINE_SEPARATOR);
		}

		infrastructure.getPluginLogger().logInfo("Done!");
	}

	private void applyModelToArtefactTemplates(final Model model, final String artefact) throws MOGLiPluginException {
		final BuildUpVelocityEngineData engineData = new BuildUpVelocityEngineData(artefact, model, PLUGIN_ID);
		final List<VelocityGeneratorResultData> resultList = generate(engineData);

		if (! ModelValidationGeneratorUtil.validateModel(resultList.get(0).getNameOfValidModel(), model.getName())) {
			infrastructure.getPluginLogger().logInfo("Artefact '" + artefact + "' has defined '" + resultList.get(0).getNameOfValidModel()
					                                 + "' as valid model.");
			infrastructure.getPluginLogger().logInfo("This artefact is not generated for current model '" + model.getName() + "'.");
			return;
		}

		validate(resultList);
		final List<VelocityGeneratorResultData> artefactsToCreate = removeSkippedClassesFromList(resultList, artefact);

		encodingHelper = IOEncodingHelper.getInstance(EncodingUtils.getValidOutputEncodingFormat(resultList.get(0).getOutputEncodingFormat(),
				                                      infrastructure.getPluginLogger()));
		writeFilesIntoPluginOutputDir(artefactsToCreate, artefact);
		writeFilesIntoTargetDirReadFromTemplateFile(artefactsToCreate);
		generateReportLines(artefactsToCreate, artefact);
		infrastructure.getPluginLogger().logInfo(artefactsToCreate.size() + " files for artefact '" + artefact + "' created!");
	}

	private List<VelocityGeneratorResultData> removeSkippedClassesFromList(final List<VelocityGeneratorResultData> resultList,
			                                                               final String artefact)
    {
		final List<VelocityGeneratorResultData> toReturn = new ArrayList<VelocityGeneratorResultData>();
		for (final VelocityGeneratorResultData resultData : resultList) {
			if (resultData.skipGeneration()) {
				infrastructure.getPluginLogger().logInfo("Generation of file '" + resultData.getTargetFileName()
						                                  + "' was skipped as configured for artefact " + artefact + ".");
			} else {
				toReturn.add(resultData);
			}
		}
		return toReturn;
	}

	private void generateReportLines(final List<VelocityGeneratorResultData> resultList, final String artefact) {
		artefactCounter++;
		generationReport.append(FileUtil.getSystemLineSeparator());
		generationReport.append("   Reports for artefact '");
		generationReport.append(artefact);
		generationReport.append("':");
		generationReport.append(FileUtil.getSystemLineSeparator());

		for (final VelocityGeneratorResultData resultData : resultList) {
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
				generationReport.append(resultData.getTargetDir());
				generationReport.append(FileUtil.getSystemLineSeparator());
			}
		}
	}

	private void validate(final List<VelocityGeneratorResultData> resultList) throws MOGLiPluginException {
		for (final VelocityGeneratorResultData resultData : resultList) {
			resultData.validate();
		}
	}

	List<String> getArtefactList() throws MOGLiPluginException {
		final File generatorPropertiesFile = new File(infrastructure.getPluginInputDir(), PLUGIN_PROPERTIES_FILE);
		return ArtefactListUtil.getArtefactListFrom(infrastructure.getPluginInputDir(), generatorPropertiesFile);
	}

	void writeFilesIntoTargetDirReadFromTemplateFile(final List<VelocityGeneratorResultData> resultList) throws MOGLiPluginException {
		for (final VelocityGeneratorResultData resultData : resultList) {
			final File outputFile = resultData.getTargetFile(infrastructure.getApplicationRootDir().getAbsolutePath(),
					                                             getTestPathPrefix(true));
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

	private void writeFilesIntoPluginOutputDir(final List<VelocityGeneratorResultData> resultList, final String subDir)
	                                           throws MOGLiPluginException {
		final File targetdir = new File(infrastructure.getPluginOutputDir(), subDir);
		targetdir.mkdirs();
		for (final VelocityGeneratorResultData resultData : resultList) {
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

	List<VelocityGeneratorResultData> generate(final BuildUpVelocityEngineData engineData) throws MOGLiPluginException {
		final File templateDir = new File(infrastructure.getPluginInputDir(), engineData.getArtefactType());
		engineData.setTemplateDir(templateDir);
		engineData.setTemplateFileName(findMainTemplate(templateDir));

		final ClassBasedEngineProvider velocityEngineProvider =
			       (ClassBasedEngineProvider) infrastructure.getEngineProvider(ENGINE_PROVIDER_ID);

		infrastructure.getPluginLogger().logInfo("Starting velocity engine for artefact '"
				+ engineData.getArtefactType() + " and with template '"
				+ engineData.getMainTemplateSimpleFileName() + "'...");

		velocityEngineProvider.setEngineData(engineData);
		final List<GeneratorResultData> generatorResultDataList = velocityEngineProvider.startEngineWithClassList();
		return buildVelocityResultData(generatorResultDataList);
	}

	private List<VelocityGeneratorResultData> buildVelocityResultData(final List<GeneratorResultData>
	                                                                   generatorResultDataList) {
		final List<VelocityGeneratorResultData> velocityGeneratorResultData = new ArrayList<VelocityGeneratorResultData>();
		for (final GeneratorResultData generatorResultData : generatorResultDataList) {
			velocityGeneratorResultData.add(new BuildUpVelocityGeneratorResultData(generatorResultData));
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
		defaultData.addFlatFolder(ARTEFACT_JAVABEAN_VALIDATOR, javabeanValidatorTemplates);
		defaultData.addFlatFolder(ARTEFACT_JAVABEAN_VALIDATOR_TEST, javabeanValidatorTestTemplates);
		defaultData.addFlatFolder(ARTEFACT_MOGLICC_NEW_PLUGIN, MOGLiCCNewPluginSubtempates);

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
	public InfrastructureService getMOGLiInfrastructure() {
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
	public String getGenerationReport() {
		final StringBuffer toReturn = new StringBuffer(PLUGIN_ID
		        + " has done work for following artefacts:"
		        + FileUtil.getSystemLineSeparator());

		toReturn.append(generationReport);

		return toReturn.toString().trim();
	}

	@Override
	public int getNumberOfGenerations() {
		return generationCounter;
	}

	/**
	 * FOR TEST PURPOSE ONLY
	 */
	public void setTestDir(final String testDir) {
		this.testDir = testDir;
	}

	/**
	 * FOR TEST PURPOSE ONLY
	 */
	public IOEncodingHelper getEncodingHelper() {
		return encodingHelper;
	}

	@Override
	public int getNumberOfArtefacts() {
		return artefactCounter;
	}

}
