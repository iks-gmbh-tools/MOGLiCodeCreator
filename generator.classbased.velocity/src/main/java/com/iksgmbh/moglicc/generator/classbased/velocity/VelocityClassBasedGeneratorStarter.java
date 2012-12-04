package com.iksgmbh.moglicc.generator.classbased.velocity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.data.GeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.utils.ArtefactListUtil;
import com.iksgmbh.moglicc.generator.utils.MetaInfoValidationUtil;
import com.iksgmbh.moglicc.generator.utils.ModelValidationGeneratorUtil;
import com.iksgmbh.moglicc.generator.utils.TemplateUtil;
import com.iksgmbh.moglicc.generator.utils.helper.PluginDataUnpacker;
import com.iksgmbh.moglicc.generator.utils.helper.PluginPackedData;
import com.iksgmbh.moglicc.plugin.type.ClassBasedEngineProvider;
import com.iksgmbh.moglicc.plugin.type.basic.Generator;
import com.iksgmbh.moglicc.provider.engine.velocity.BuildUpVelocityEngineData;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidatorVendor;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.ImmutableUtil;

public class VelocityClassBasedGeneratorStarter implements Generator, MetaInfoValidatorVendor {
	
	public static final String PLUGIN_ID = "VelocityClassBasedGenerator";
	public static final String MODEL_PROVIDER_ID = "StandardModelProvider";
	public static final String ENGINE_PROVIDER_ID = "VelocityEngineProvider";
	public static final String MAIN_TEMPLATE_IDENTIFIER = "Main";
	public static final String PLUGIN_PROPERTIES_FILE = "generator.properties";

	public static final String ARTEFACT_COMMON = "commonSubtemplates";
	public static final String ARTEFACT_JAVABEAN = "MOGLiJavaBean";
	public static final String ARTEFACT_JAVABEAN_BUILDER = "MOGLiJavaBeanBuilder";
	public static final String ARTEFACT_JAVABEAN_VALIDATOR = "MOGLiJavaBeanValidator";
	public static final String ARTEFACT_JAVABEAN_VALIDATOR_TEST = "MOGLiJavaBeanValidatorTest";
	
	final static String[] javabeanTemplates = {"A_MainTemplate.tpl", "E_Variables.tpl", 
		    "G_GetterMethods.tpl", "F_SetterMethods.tpl", "C_ClassDefinitionLine.tpl",
			"D_Serializable.tpl", "H_toStringMethod.tpl", "J_hashCodeMethod.tpl",
			"I_equalsMethod.tpl", "I1_equalsArrayTypes.tpl", "I2_equalsPrimitiveTypes.tpl", 
			"I3_equalsStandardTypes.tpl", "J_hashCodeMethod.tpl", "J2_hashCodePrimitiveTypes.tpl", 
			"K_cloneMethod.tpl", "K1_cloneArrayType.tpl", "K2_cloneCollectionType.tpl", 
			"K3_cloneStandardType.tpl"};

	final static String[] javabeanBuilderTemplates = {"A_MainTemplate.tpl", "C_withMethods.tpl", 
		                                              "D_cloneWithMethods.tpl", "E_cloneDataObjectMethod.tpl"};

	final static String[] javabeanValidatorTemplates = {"A_MainTemplate.tpl", "C_Constructor.tpl", 
                                                        "D_validateMethod.tpl"};

	final static String[] javabeanValidatorTestTemplates = {"A_MainTemplate.tpl", "C_setupMethod.tpl", 
		                                                    "D_mandatoryTestMethods.tpl", "E_minLengthTestMethods.tpl", 
		                                                    "F_maxLengthTestMethods.tpl"};
	
	final static String[] javabeanCommonSubtempates = {"B_ImportStatements.tpl"};

	private InfrastructureService infrastructure;
	private String testDir = "";
	
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
		
		final Model model = infrastructure.getModelProvider(MODEL_PROVIDER_ID).getModel();
		infrastructure.getPluginLogger().logInfo("Model '" + model.getName() + "' retrieved from " + MODEL_PROVIDER_ID);		
		
		final List<String> list = getArtefactList();
		for (final String artefact : list) {
			applyModelToArtefactTemplates(model, artefact);
		}
		
		infrastructure.getPluginLogger().logInfo("Done!");
	}

	private void applyModelToArtefactTemplates(final Model model, final String artefact) throws MOGLiPluginException {
		final BuildUpVelocityEngineData engineData = new BuildUpVelocityEngineData(artefact, model, PLUGIN_ID);
		final List<VelocityGeneratorResultData> resultList = generate(engineData);
		if (! ModelValidationGeneratorUtil.validateModel(resultList.get(0).getNameOfValidModel(), model.getName())) {
			infrastructure.getPluginLogger().logInfo("Artefact '" + artefact + "' not generated, because only model '"
	                + resultList.get(0).getNameOfValidModel() + "' is valid for this artefact, " 
	                + "but not the current model '" + model.getName() + "'.");
			
			return;
		}
		validate(resultList);
		writeFilesIntoPluginOutputDir(resultList, artefact);
		writeFilesIntoTemplateTargetDir(resultList);
		infrastructure.getPluginLogger().logInfo(resultList.size() + " files for artefact '" + artefact + "' created!");
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

	private void writeFilesIntoTemplateTargetDir(final List<VelocityGeneratorResultData> resultList) throws MOGLiPluginException {
		for (final VelocityGeneratorResultData resultData : resultList) {
			final File outputFile = resultData.getTargetFile(infrastructure.getApplicationRootDir().getAbsolutePath(), 
					                                             getTestPathPrefix(true));
			if (outputFile == null) {
				return; // @TargetDir not defined in template -> do nothing here
			}
			if (resultData.isTargetToBeCreatedNewly()
				|| ! outputFile.exists()) {
				try {
					FileUtil.createFileWithContent(outputFile, resultData.getGeneratedContent());
				} catch (Exception e) {
					throw new MOGLiPluginException("Error creating file\n" + outputFile.getAbsolutePath(), e);
				}
			} else {
				infrastructure.getPluginLogger().logWarning("Target file " + outputFile.getAbsolutePath() + " exists and will not overwritten!");
			}
		} 

	}

	private void writeFilesIntoPluginOutputDir(final List<VelocityGeneratorResultData> resultList, final String subDir) 
	                                           throws MOGLiPluginException {
		final File targetdir = new File(infrastructure.getPluginOutputDir(), subDir);
		targetdir.mkdirs();
		for (final VelocityGeneratorResultData resultData : resultList) {
			final File outputFile = new File(targetdir, resultData.getTargetFileName());
			try {
				FileUtil.createFileWithContent(outputFile, resultData.getGeneratedContent());
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
		final PluginPackedData defaultData = new PluginPackedData(this.getClass(), DEFAULT_DATA_DIR);
		defaultData.addDirectory(ARTEFACT_COMMON, javabeanCommonSubtempates);
		defaultData.addDirectory(ARTEFACT_JAVABEAN, javabeanTemplates);
		defaultData.addDirectory(ARTEFACT_JAVABEAN_BUILDER, javabeanBuilderTemplates);
		defaultData.addDirectory(ARTEFACT_JAVABEAN_VALIDATOR, javabeanValidatorTemplates);
		defaultData.addDirectory(ARTEFACT_JAVABEAN_VALIDATOR_TEST, javabeanValidatorTestTemplates);
		defaultData.addFile(MetaInfoValidationUtil.FILENAME_VALIDATION);
		defaultData.addFile(PLUGIN_PROPERTIES_FILE);
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
		final File validationInputFile = new File(infrastructure.getPluginInputDir(), 
				                                  MetaInfoValidationUtil.FILENAME_VALIDATION);
		final List<MetaInfoValidator> metaInfoValidatorList = 
			               MetaInfoValidationUtil.getMetaInfoValidatorList(validationInputFile, getId());
		infrastructure.getPluginLogger().logInfo(metaInfoValidatorList.size() + " MetaInfoValidators found.");
		return metaInfoValidatorList;
	}

	@Override
	public InfrastructureService getMOGLiInfrastructure() {
		return infrastructure;
	}
		
	@Override
	public boolean unpackPluginHelpFiles() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("unpackPluginHelpFiles");
		final PluginPackedData helpData = new PluginPackedData(this.getClass(), HELP_DATA_DIR);
		helpData.addFile("TemplateFileHeaderAttributes.htm");
		PluginDataUnpacker.doYourJob(helpData, infrastructure.getPluginHelpDir(), infrastructure.getPluginLogger());
		return true;
	}

	/**
	 * FOR TEST PURPOSE ONLY
	 */
	public void setTestDir(String testDir) {
		this.testDir = testDir;
	}
	
}
