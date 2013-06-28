package com.iksgmbh.moglicc.treebuilder.modelbased.velocity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.iksgmbh.data.FolderContent;
import com.iksgmbh.helper.FolderContentBasedFileRenamer;
import com.iksgmbh.helper.FolderContentBasedFileRenamer.RenamingData;
import com.iksgmbh.helper.FolderContentBasedFolderDuplicator;
import com.iksgmbh.helper.FolderContentBasedTextFileLineReplacer;
import com.iksgmbh.helper.FolderContentBasedTextFileLineReplacer.ReplacementData;
import com.iksgmbh.helper.IOEncodingHelper;
import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.data.GeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.utils.ArtefactListUtil;
import com.iksgmbh.moglicc.generator.utils.EncodingUtils;
import com.iksgmbh.moglicc.generator.utils.ModelValidationGeneratorUtil;
import com.iksgmbh.moglicc.generator.utils.TemplateUtil;
import com.iksgmbh.moglicc.generator.utils.helper.PluginDataUnpacker;
import com.iksgmbh.moglicc.generator.utils.helper.PluginPackedData;
import com.iksgmbh.moglicc.plugin.type.ModelBasedEngineProvider;
import com.iksgmbh.moglicc.plugin.type.basic.Generator;
import com.iksgmbh.moglicc.provider.engine.velocity.BuildUpVelocityEngineData;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidationUtil;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidatorVendor;
import com.iksgmbh.moglicc.treebuilder.modelbased.velocity.data.ArtefactProperties;
import com.iksgmbh.moglicc.treebuilder.modelbased.velocity.data.VelocityTreeBuilderResultData;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.FileUtil.FileCreationStatus;
import com.iksgmbh.utils.ImmutableUtil;
import com.iksgmbh.utils.StringUtil;

/**
* Generator of file structures to create a defined number of files within a specified folder structure.
* @author Reik Oberrath
* @since V1.3.0
*/
public class VelocityModelBasedTreeBuilderStarter implements Generator, MetaInfoValidatorVendor {

	public static final String PLUGIN_ID = "VelocityModelBasedTreeBuilder";
	public static final String MODEL_PROVIDER_ID = "StandardModelProvider";
	public static final String ENGINE_PROVIDER_ID = "VelocityEngineProvider";

	public static final String PLUGIN_PROPERTIES_FILE = "generator.properties";
	public static final String MOGLICC_NEW_PLUGIN_PROJECT = "MOGLiCC_NewPluginProject";
	public static final String MOGLICC_JAVA_BEAN_PROJECT = "MOGLiCC_JavaBeanProject";
	public static final String DEMO_SOURCE_DIR = MOGLICC_JAVA_BEAN_PROJECT
                                                 + "/src/main/java/com/iksgmbh/moglicc/demo";
	public static final String VALIDATOR_TYPES_SOURCE_DIR = MOGLICC_JAVA_BEAN_PROJECT
			                                                + "/src/main/java/com/iksgmbh/moglicc/demo/validator/types";
	public static final String VALIDATOR_SOURCE_DIR = MOGLICC_JAVA_BEAN_PROJECT
                                                      + "/src/main/java/com/iksgmbh/moglicc/demo/validator";
	public static final String FILENAME_ARTEFACT_PROPERTIES = "artefact.properties";
	public static final String MAIN_TEMPLATE_IDENTIFIER = "Main";

	private static final String[] childrenMOGLiCCNewPluginProject = {"pom.xml", "artefact.properties", "readme.md"};
	private static final String[] childrenMOGLiCCJavaBeanProject = {"pom.xml", "artefact.properties"};

	private static final String[] children_MAIN_SOURCE_DIR = {"main.java"};

	private static final String[] children_VALIDATOR_TYPES_SOURCE_DIR = {"MandatoryFieldValidator.java",
		                                                                 "MaxLengthValidator.java",
		                                                                 "MinLengthValidator.java"};
	private static final String[] children_VALIDATOR_SOURCE_DIR = {"FieldValidationException.java",
		                                                           "FieldValidationResult.java",
		                                                           "FieldValidator.java",
		                                                           "JavaBeanValidator.java"};

	private int generationCounter = 0;
	private int artefactCounter = 0;
	private StringBuffer generationReport = new StringBuffer();
	private InfrastructureService infrastructure;
	private Model model;
	private ModelBasedEngineProvider velocityEngineProvider;
	private ArtefactProperties artefactProperties;

	@Override
	public void setInfrastructure(final InfrastructureService infrastructure) {
		this.infrastructure = infrastructure;
	}

	@Override
	public void doYourJob() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("Doing my job...");

		model = infrastructure.getModelProvider(MODEL_PROVIDER_ID).getModel(PLUGIN_ID);
		velocityEngineProvider = (ModelBasedEngineProvider) infrastructure.getEngineProvider(ENGINE_PROVIDER_ID);

		final List<String> artefactList = getArtefactList();
		for (final String artefact : artefactList) {
			doYourJobFor(artefact);
			infrastructure.getPluginLogger().logInfo("Doing my job...");
		}

		infrastructure.getPluginLogger().logInfo("Done!");
	}


	private void doYourJobFor(final String artefact) throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("Creating artefact " + artefact + "...");
		final File sourceDir = new File(getInfrastructure().getPluginInputDir(), artefact);
		final VelocityTreeBuilderResultData velocityResult = doInsertsByCallingVelocityEngineProvider(
				                                               artefact, sourceDir, FILENAME_ARTEFACT_PROPERTIES);
		velocityResult.validatePropertyKeys(artefact);
		if (ModelValidationGeneratorUtil.validateModel(velocityResult, model.getName()))
		{
			velocityResult.validatePropertyForMissingMetaInfoValues(artefact);

			if (velocityResult.skipGeneration()) {
				infrastructure.getPluginLogger().logInfo("Generation of file '" + velocityResult.getTargetFileName()
                        + "' was skipped as configured for artefact " + artefact + ".");
				return;
			}

			artefactProperties = new ArtefactProperties(velocityResult, artefact);

			if (artefactProperties.isTargetToBeCleaned()) {
				FileUtil.deleteDirWithContent(getArtifactTopFolder());
			}

			doYourJobWith(sourceDir, artefact);
		} else {
			infrastructure.getPluginLogger().logInfo("Artefact '" + artefact + "' has defined '"
			                                         + velocityResult.getNameOfValidModel() + "' as valid model.");
			infrastructure.getPluginLogger().logInfo("This artefact is not generated for current model '" + model.getName() + "'.");
		}
	}

	private VelocityTreeBuilderResultData doInsertsByCallingVelocityEngineProvider(final String artefact, final File templateDir,
			                                                                    final String mainTemplate) throws MOGLiPluginException
	{
		final BuildUpVelocityEngineData engineData = new BuildUpVelocityEngineData(artefact, model, PLUGIN_ID);
		engineData.setTemplateDir(templateDir);
		engineData.setTemplateFileName(mainTemplate);

		infrastructure.getPluginLogger().logInfo("Starting velocity engine for artefact '"
													+ engineData.getArtefactType() + " and with '"
													+ engineData.getMainTemplateSimpleFileName() + "'...");

		velocityEngineProvider.setEngineData(engineData);
		final GeneratorResultData generatorResultData = velocityEngineProvider.startEngineWithModel(); // this does the actual insert job

		return new VelocityTreeBuilderResultData(generatorResultData);
	}

	private void doYourJobWith(final File sourceDir, final String artefact) throws MOGLiPluginException
	{
		final List<String> problems = new ArrayList<String>();
		final List<String> filesToIgnore = artefactProperties.getExcludes();
		filesToIgnore.add(FILENAME_ARTEFACT_PROPERTIES);

		// copy from inputDir to outputDir
		final FolderContentBasedFolderDuplicator inputFolderDuplicator = new FolderContentBasedFolderDuplicator(sourceDir, filesToIgnore);
		final File pluginOutputDir = new File(getInfrastructure().getPluginOutputDir(), artefact);
		inputFolderDuplicator.duplicateTo(pluginOutputDir);

		// do replacements in files of outputDir before renaming with original file names
		final FolderContentBasedTextFileLineReplacer lineReplacer1 = new FolderContentBasedTextFileLineReplacer(pluginOutputDir, filesToIgnore);
		setEncodingHelper(lineReplacer1);
		lineReplacer1.doYourJob(artefactProperties.getReplacements());
		problems.addAll(lineReplacer1.getErrorList());

		// do file renamings in outputDir
		final FolderContentBasedFileRenamer renamer = new FolderContentBasedFileRenamer(pluginOutputDir, filesToIgnore);
		renamer.doYourJob(artefactProperties.getFileRenamings());
		problems.addAll(renamer.getErrorList());

		// do replacements in files of outputDir after renaming with renamed file names
		final FolderContentBasedTextFileLineReplacer lineReplacer2 = new FolderContentBasedTextFileLineReplacer(pluginOutputDir, filesToIgnore);
		setEncodingHelper(lineReplacer2);
		lineReplacer2.doYourJob(artefactProperties.getReplacements());
		problems.addAll(lineReplacer2.getErrorList());

		// copy from outputDir to targetDir
		final List<String> takeAllFiles = null; // here is no need for ignoring files -> outputFolder contains wanted files only!
		final FolderContentBasedFolderDuplicator outputFolderDuplicator = new FolderContentBasedFolderDuplicator(pluginOutputDir, takeAllFiles);
		final HashMap<String, FileCreationStatus> duplicationResults = outputFolderDuplicator.duplicateTo(getArtifactTopFolder(),
				                                                                                 artefactProperties.isCreateNew());
		final FolderContent folderContent = outputFolderDuplicator.getFolderContent();

		if (problems.size() > 0) {
			throw new MOGLiPluginException("Following problem(s) exist for artefact '" + artefact + "': "
		                                   + FileUtil.getSystemLineSeparator()
		                                   + StringUtil.buildTextFromLines(problems));
		}

		generateReportLines(artefact, artefactProperties.getReplacements(),
							artefactProperties.getFileRenamings(),
							folderContent.getFolders(), duplicationResults);

	}

	private void setEncodingHelper(final FolderContentBasedTextFileLineReplacer lineReplacer) {
		final String encodingFormat = EncodingUtils.getValidOutputEncodingFormat(artefactProperties.getOutputEncodingFormat(),
                																 infrastructure.getPluginLogger());
		final IOEncodingHelper encodingHelper = IOEncodingHelper.getInstance(encodingFormat);
		lineReplacer.setEncodingHelper(encodingHelper);
	}

	private void generateReportLines(final String artefact, final List<ReplacementData> replacements,
			                         final List<RenamingData> fileRenamings, final List<File> generatedFolders,
			                         final HashMap<String, FileCreationStatus> duplicationResults) {
		artefactCounter++;
		generationReport.append(FileUtil.getSystemLineSeparator());
		generationReport.append("   Reports for artefact '");
		generationReport.append(artefact);
		generationReport.append("':");
		generationReport.append(FileUtil.getSystemLineSeparator());
		generationReport.append(FileUtil.getSystemLineSeparator());

		final StringBuffer tmpReport = new StringBuffer();

		// report created subfolders
		tmpReport.append("		Tree artefact generated in: " + getArtifactTopFolder());
		tmpReport.append(FileUtil.getSystemLineSeparator());
		tmpReport.append(FileUtil.getSystemLineSeparator());
		if (generatedFolders.size() > 1) {
			tmpReport.append("		Created subdirectories: ");
			tmpReport.append(FileUtil.getSystemLineSeparator());
			for (final File folder : generatedFolders) {
				if (FileUtil.isTip(folder)) {
					tmpReport.append("			" + cutRootPath(folder.getAbsolutePath(), artefact));
					tmpReport.append(FileUtil.getSystemLineSeparator());
					generationCounter++;
				}
			}
		}
		tmpReport.append(FileUtil.getSystemLineSeparator());

		// report created files
		final List<String> generatedFiles = StringUtil.asSortedList(duplicationResults.keySet());
		if (generatedFiles.size() > 0) {
			tmpReport.append("		Created files: ");
			tmpReport.append(FileUtil.getSystemLineSeparator());
		}
		for (String filename : generatedFiles) {
			tmpReport.append("			" + cutRootPath(filename, getArtifactTopFolder().getAbsolutePath()));
			final FileCreationStatus fileStatus = duplicationResults.get(filename);
			if (fileStatus == FileCreationStatus.EXISTING_FILE_OVERWRITTEN) {
				//tmpReport.append("  (an existing file has been overwritten)"); this info is not given by the other generators - for similarity reason this output is commented out.
			} else if (fileStatus == FileCreationStatus.EXISTING_FILE_PRESERVED) {
				tmpReport.append("  (file existed already in the targetDir and was preserved - the generated file is available in the plugin's output dir)");
			}

			tmpReport.append(FileUtil.getSystemLineSeparator());
			generationCounter++;
		}
		tmpReport.append(FileUtil.getSystemLineSeparator());

		// report replacements
		if (replacements.size() > 0) {
			tmpReport.append("		Performed replacements:");
			tmpReport.append(FileUtil.getSystemLineSeparator());
		}
		for (final ReplacementData replacementData : replacements) {
			final List<File> matchingFiles = replacementData.getMatchingFiles();
			tmpReport.append("			Placeholder '" + replacementData.getOldString() + "' replaced by '" + replacementData.getNewString() + "' in file '");
			for (final File file : matchingFiles) {
				tmpReport.append(file.getName());
				tmpReport.append("'");
				generationCounter++;
			}
			tmpReport.append(FileUtil.getSystemLineSeparator());
		}
		tmpReport.append(FileUtil.getSystemLineSeparator());

		// report file renamings
		if (fileRenamings.size() > 0) {
			tmpReport.append("		Performed file renamings:");
			tmpReport.append(FileUtil.getSystemLineSeparator());
		}
		for (final RenamingData renaming : fileRenamings) {
			final List<String> results = renaming.getRenamingResults();
			for (String result : results) {
				tmpReport.append("			" + result);
				tmpReport.append(FileUtil.getSystemLineSeparator());
				generationCounter++;
			}
		}

		generationReport.append(tmpReport);
		infrastructure.getPluginLogger().logInfo("----------------------------------------------"
				                                 + FileUtil.getSystemLineSeparator()
		                                         + tmpReport.toString()
				                                 + "----------------------------------------------");
	}

	private String cutRootPath(final String path, final String rootPath) {
		final int pos = path.indexOf(rootPath);
		return path.substring(pos + rootPath.length());
	}

	private File getArtifactTopFolder() {
		String path = artefactProperties.getTargetDir();
		path = path.replace(MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER, getInfrastructure().getApplicationRootDir().getAbsolutePath());
		return new File(path, artefactProperties.getRootName());
	}

	List<String> getArtefactList() throws MOGLiPluginException {
		final File generatorPropertiesFile = new File(infrastructure.getPluginInputDir(), PLUGIN_PROPERTIES_FILE);
		return ArtefactListUtil.getArtefactListFrom(infrastructure.getPluginInputDir(), generatorPropertiesFile);
	}

	String findMainTemplate(final File templateDir) throws MOGLiPluginException {
		return TemplateUtil.findMainTemplate(templateDir, MAIN_TEMPLATE_IDENTIFIER);
	}

	@Override
	public boolean unpackDefaultInputData() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("initDefaultInputData");
		final PluginPackedData defaultData = new PluginPackedData(this.getClass(), DEFAULT_DATA_DIR, PLUGIN_ID);

		defaultData.addFlatFolder(MOGLICC_NEW_PLUGIN_PROJECT, childrenMOGLiCCNewPluginProject);
		defaultData.addFlatFolder(MOGLICC_JAVA_BEAN_PROJECT, childrenMOGLiCCJavaBeanProject);
		defaultData.addFlatFolder(VALIDATOR_TYPES_SOURCE_DIR, children_VALIDATOR_TYPES_SOURCE_DIR);
		defaultData.addFlatFolder(VALIDATOR_SOURCE_DIR, children_VALIDATOR_SOURCE_DIR);
		defaultData.addFlatFolder(DEMO_SOURCE_DIR + "/main", children_MAIN_SOURCE_DIR);

		defaultData.addSubDir(MOGLICC_JAVA_BEAN_PROJECT + "/src/main/java");
		defaultData.addSubDir(MOGLICC_JAVA_BEAN_PROJECT + "/src/main/resources");
		defaultData.addSubDir(MOGLICC_JAVA_BEAN_PROJECT + "/src/test/java");
		defaultData.addSubDir(MOGLICC_JAVA_BEAN_PROJECT + "/src/test/resources");

		defaultData.addSubDir(MOGLICC_NEW_PLUGIN_PROJECT + "/src/main/java");
		defaultData.addSubDir(MOGLICC_NEW_PLUGIN_PROJECT + "/src/main/resources");
		defaultData.addSubDir(MOGLICC_NEW_PLUGIN_PROJECT + "/src/test/java");
		defaultData.addSubDir(MOGLICC_NEW_PLUGIN_PROJECT + "/src/test/resources");

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
	public String getGenerationReport() {
		if (artefactCounter == 0) {
			return PLUGIN_ID + " have had nothing to do. No artefact found.";
		}

		final StringBuffer toReturn = new StringBuffer(PLUGIN_ID
		        + " has done work for following artefacts:"
		        + FileUtil.getSystemLineSeparator());

		toReturn.append(FileUtil.getSystemLineSeparator());
		toReturn.append(artefactCounter + " artefact(s) have been generated and " + generationCounter + " generation event(s) have been performed.");
		toReturn.append(FileUtil.getSystemLineSeparator());
		toReturn.append(generationReport);

		return toReturn.toString().trim();
	}

	@Override
	public int getNumberOfGenerations() {
		return generationCounter;
	}

	@Override
	public int getNumberOfArtefacts() {
		return artefactCounter;
	}

	@Override
	public List<MetaInfoValidator> getMetaInfoValidatorList() throws MOGLiPluginException {
		final File validationInputFile = new File(infrastructure.getPluginInputDir(), MetaInfoValidationUtil.FILENAME_VALIDATION);
		final List<MetaInfoValidator> metaInfoValidatorList = MetaInfoValidationUtil.getMetaInfoValidatorList(validationInputFile, getId());
		infrastructure.getPluginLogger().logInfo(metaInfoValidatorList.size() + " MetaInfoValidators found.");
		return metaInfoValidatorList;
	}

	/**
	 * for test purpose only
	 */
	ArtefactProperties getArtefactProperties() {
		return artefactProperties;
	}


}