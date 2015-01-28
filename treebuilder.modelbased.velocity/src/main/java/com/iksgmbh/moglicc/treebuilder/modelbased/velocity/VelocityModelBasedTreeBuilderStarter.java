package com.iksgmbh.moglicc.treebuilder.modelbased.velocity;

import static com.iksgmbh.moglicc.generator.utils.GeneratorReportUtil.REPORT_TAB;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.data.FolderContent;
import com.iksgmbh.helper.FolderContentBasedFileRenamer;
import com.iksgmbh.helper.FolderContentBasedFileRenamer.RenamingData;
import com.iksgmbh.helper.FolderContentBasedFolderDuplicator;
import com.iksgmbh.helper.FolderContentBasedTextFileLineReplacer;
import com.iksgmbh.helper.FolderContentBasedTextFileLineReplacer.ReplacementData;
import com.iksgmbh.helper.IOEncodingHelper;
import com.iksgmbh.moglicc.MOGLiSystemConstants;
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
import com.iksgmbh.moglicc.plugin.subtypes.GeneratorPlugin;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ModelBasedEngineProvider;
import com.iksgmbh.moglicc.plugin.subtypes.providers.ModelProvider;
import com.iksgmbh.moglicc.provider.engine.velocity.BuildUpVelocityEngineData;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidatorVendor;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.validation.MetaInfoValidationUtil;
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
public class VelocityModelBasedTreeBuilderStarter implements GeneratorPlugin, MetaInfoValidatorVendor 
{
	public static final String PLUGIN_ID = "VelocityModelBasedTreeBuilder";
	public static final String MODEL_PROVIDER_ID = "StandardModelProvider";
	public static final String ENGINE_PROVIDER_ID = "VelocityEngineProvider";

	public static final String PLUGIN_PROPERTIES_FILE = "generator.properties";
	public static final String MOGLICC_NEW_PLUGIN_PROJECT = "MOGLiCC_NewPluginProject";
	public static final String MOGLICC_JAVA_BEAN_PROJECT = "MOGLiCC_JavaBeanProject";
	public static final String DEMO_SOURCE_DIR = MOGLICC_JAVA_BEAN_PROJECT + "/src/main/java/domainPathToReplace";
	public static final String VALIDATOR_TYPES_SOURCE_DIR = MOGLICC_JAVA_BEAN_PROJECT + "/src/main/java/domainPathToReplace/validator/types";
	public static final String VALIDATOR_HELPER_SOURCE_DIR = MOGLICC_JAVA_BEAN_PROJECT + "/src/main/java/domainPathToReplace/validator/helper";
	public static final String FILENAME_ARTEFACT_PROPERTIES = "artefact.properties";
	public static final String MAIN_TEMPLATE_IDENTIFIER = "Main";

	private static final String[] childrenMOGLiCCNewPluginProject = {"pom.xml", "artefact.properties", "readme.md"};
	private static final String[] childrenMOGLiCCJavaBeanProject = {"pom.xml", "artefact.properties"};

	private static final String[] children_UTILS_SOURCE_DIR = {"ClassOverviewPrinter.java", "MOGLiFactoryUtils.java", "CollectionsStringUtils.java"};

	private static final String[] children_VALIDATOR_TYPES_SOURCE_DIR = {"MandatoryFieldValidator.java",
                                                                         "InvalidCharFieldValidator.java",
                                                                         "ValidCharFieldValidator.java",
		                                                                 "MaxLengthValidator.java",
		                                                                 "MinLengthValidator.java"};
	private static final String[] children_VALIDATOR_SOURCE_DIR = {"FieldValidationException.java",
		                                                           "FieldValidationResult.java",
		                                                           "FieldValidator.java",
		                                                           "JavaBeanValidator.java"};

	private int fileGenerationCounter = 0;
	private int folderGenerationCounter = 0;
	private int fileModificationCounter = 0;
	private StringBuffer generationReport = new StringBuffer();
	private InfrastructureService infrastructure;
	private ModelBasedEngineProvider velocityEngineProvider;
	private ArtefactProperties artefactProperties;
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
		final List<String> artefactList = getArtefactList(); // read before possible exception for model access may be thrown
		standardReportData.numberOfAllInputArtefacts = artefactList.size();
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

		velocityEngineProvider = (ModelBasedEngineProvider) infrastructure.getProvider(ENGINE_PROVIDER_ID);

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
		try
		{
			velocityResult.validatePropertyKeys(artefact);
		} catch (MOGLiPluginException e)
		{
			standardReportData.invalidInputArtefacts.add(artefact + " has invalid property keys: " + e.getMessage());
			throw e;
		}
		
		if (ModelMatcherGeneratorUtil.doesItMatch(velocityResult, standardReportData.model.getName()))
		{
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
				standardReportData.skippedArtefacts.add(artefact);
				infrastructure.getPluginLogger().logInfo("Generation of file '" + velocityResult.getTargetFileName()
                        + "' was skipped as configured for artefact " + artefact + ".");
				return;
			}

			try
			{
				artefactProperties = new ArtefactProperties(velocityResult, artefact);
			} catch (MOGLiPluginException e)
			{
				standardReportData.invalidInputArtefacts.add("Error parsing artefact properties for artefact '" + artefact + "': " + e.getMessage());
				throw e;
			}

			if (artefactProperties.isTargetToBeCleaned()) {
				final File artifactTopFolder = getArtifactTopFolder();
				if (artifactTopFolder != null) {					
					FileUtil.deleteDirWithContent(artifactTopFolder);
				}
			}

			doYourJobWith(sourceDir, artefact);
		} else {
			infrastructure.getPluginLogger().logInfo("Artefact '" + artefact + "' has defined '"
			                                         + velocityResult.getNameOfValidModel() + "' as valid model.");
			infrastructure.getPluginLogger().logInfo("This artefact is not generated for current model '" + standardReportData.model.getName() + "'.");
			standardReportData.nonModelMatchingInputArtefacts.add(artefact);
		}
	}

	private VelocityTreeBuilderResultData doInsertsByCallingVelocityEngineProvider(final String artefact, final File templateDir,
			                                                                    final String mainTemplate) throws MOGLiPluginException
	{
		final BuildUpVelocityEngineData engineData = new BuildUpVelocityEngineData(artefact, standardReportData.model, PLUGIN_ID);
		engineData.setTemplateDir(templateDir);
		engineData.setTemplateFileName(mainTemplate);

		infrastructure.getPluginLogger().logInfo("Starting velocity engine for artefact '"
													+ engineData.getArtefactType() + " and with '"
													+ engineData.getMainTemplateSimpleFileName() + "'...");

		velocityEngineProvider.setEngineData(engineData);
		
		final GeneratorResultData generatorResultData;
		try
		{
			generatorResultData = velocityEngineProvider.startEngineWithModel(); 
		} catch (MOGLiPluginException e)
		{
			standardReportData.invalidInputArtefacts.add("Velocity Engine Provider Error:" + e.getMessage());
			throw e;
		}

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
		final HashMap<String, FileCreationStatus> duplicationResultsOutputDir = inputFolderDuplicator.duplicateTo(pluginOutputDir);

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
		final File artifactTopFolder = getArtifactTopFolder();
		HashMap<String, FileCreationStatus> duplicationResultsTargetDir = null;
		if (artifactTopFolder != null) {
			// create target dir only when defined
			duplicationResultsTargetDir = outputFolderDuplicator.duplicateTo(artifactTopFolder, artefactProperties.isCreateNew());
		}
		final FolderContent folderContent = outputFolderDuplicator.getFolderContent();

		if (problems.size() > 0) {
			throw new MOGLiPluginException("Following problem(s) exist for artefact '" + artefact + "': "
		                                   + FileUtil.getSystemLineSeparator()
		                                   + StringUtil.buildTextFromLines(problems));
		}

		final HashMap<String, FileCreationStatus> duplicationResults;
		if (duplicationResultsTargetDir != null) {
			duplicationResults = duplicationResultsTargetDir;
		} else {
			duplicationResults = duplicationResultsOutputDir; // fall back if targetDir is not defined
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
			                         final HashMap<String, FileCreationStatus> duplicationResults) 
	{
		standardReportData.numberOfOutputArtefacts++;
		generationReport.append(FileUtil.getSystemLineSeparator());
		generationReport.append(GeneratorReportUtil.getArtefactReportLine(artefact));
		generationReport.append(FileUtil.getSystemLineSeparator());
		generationReport.append(FileUtil.getSystemLineSeparator());
		
		final StringBuffer artefactDetailReport = new StringBuffer();
		final StringBuffer artefactHeaderReport = new StringBuffer();
		
		File artifactTopFolder = getArtifactTopFolder();
		if (artifactTopFolder == null) {
			artifactTopFolder = new File(infrastructure.getPluginOutputDir(), artefact);
			artefactHeaderReport.append(REPORT_TAB + "No targetFile has beed defined. Therefore, tree artefact is generated only in output directory!");
		}
		
		// ARTEFACT DETAIL REPORT
		
		// report created subfolders
		if (generatedFolders.size() > 1) {
			artefactDetailReport.append("   Created subdirectories: ");
			artefactDetailReport.append(FileUtil.getSystemLineSeparator());
			for (final File folder : generatedFolders) {
				if (FileUtil.isTip(folder)) {
					artefactDetailReport.append(REPORT_TAB + REPORT_TAB + cutRootPath(folder.getAbsolutePath(), artefact));
					artefactDetailReport.append(FileUtil.getSystemLineSeparator());
					folderGenerationCounter++;
				}
			}
		}
		artefactDetailReport.append(FileUtil.getSystemLineSeparator());

		// report created files
		final List<String> generatedFiles = StringUtil.asSortedList(duplicationResults.keySet());
		if (generatedFiles.size() > 0) {
			artefactDetailReport.append(REPORT_TAB + "Created files: ");
			artefactDetailReport.append(FileUtil.getSystemLineSeparator());
		}
		for (String filename : generatedFiles) {
			artefactDetailReport.append(REPORT_TAB + REPORT_TAB + cutRootPath(filename, artifactTopFolder.getAbsolutePath()));
			final FileCreationStatus fileStatus = duplicationResults.get(filename);
			if (fileStatus == FileCreationStatus.EXISTING_FILE_OVERWRITTEN) {
				//tmpReport.append("  (an existing file has been overwritten)"); this info is not given by the other generators - for similarity reason this output is commented out.
			} else if (fileStatus == FileCreationStatus.EXISTING_FILE_PRESERVED) {
				artefactDetailReport.append("  (file existed already in the targetDir and was preserved - the generated file is available in the plugin's output dir)");
			}

			artefactDetailReport.append(FileUtil.getSystemLineSeparator());
			fileGenerationCounter++;
		}
		artefactDetailReport.append(FileUtil.getSystemLineSeparator());

		// report replacements
		if (replacements.size() > 0) {
			artefactDetailReport.append(REPORT_TAB + "Performed replacements:");
			artefactDetailReport.append(FileUtil.getSystemLineSeparator());
		}
		for (final ReplacementData replacementData : replacements) {
			final List<File> matchingFiles = replacementData.getMatchingFiles();
			artefactDetailReport.append(REPORT_TAB + REPORT_TAB + "Placeholder '" + replacementData.getOldString() + "' replaced by '" 
			                            + replacementData.getNewString() + "' in file '");
			for (final File file : matchingFiles) {
				artefactDetailReport.append(file.getName());
				artefactDetailReport.append("'");
				fileModificationCounter++;
			}
			artefactDetailReport.append(FileUtil.getSystemLineSeparator());
		}
		artefactDetailReport.append(FileUtil.getSystemLineSeparator());

		// report file renamings
		if (fileRenamings.size() > 0) {
			artefactDetailReport.append(REPORT_TAB + "Performed file renamings:");
			artefactDetailReport.append(FileUtil.getSystemLineSeparator());
		}
		for (final RenamingData renaming : fileRenamings) {
			final List<String> results = renaming.getRenamingResults();
			for (String result : results) {
				artefactDetailReport.append(REPORT_TAB + REPORT_TAB +  result);
				artefactDetailReport.append(FileUtil.getSystemLineSeparator());
				fileModificationCounter++;
			}
		}

		// ARTEFACT HEADER REPORT
		
		artefactHeaderReport.append(REPORT_TAB + "Tree artefact generated in: " + artifactTopFolder);
		artefactHeaderReport.append(FileUtil.getSystemLineSeparator());
		artefactHeaderReport.append(REPORT_TAB + folderGenerationCounter + " folders have been created.");
		artefactHeaderReport.append(FileUtil.getSystemLineSeparator());
		artefactHeaderReport.append(REPORT_TAB + fileGenerationCounter + " files have been created.");
		artefactHeaderReport.append(FileUtil.getSystemLineSeparator());
		artefactHeaderReport.append(REPORT_TAB + fileModificationCounter + " file modifications were performed.");

		generationReport.append(artefactHeaderReport);
		generationReport.append(FileUtil.getSystemLineSeparator());
		generationReport.append(FileUtil.getSystemLineSeparator());
		generationReport.append(artefactDetailReport);
		infrastructure.getPluginLogger().logInfo("----------------------------------------------"
				                                 + FileUtil.getSystemLineSeparator()
		                                         + artefactDetailReport.toString()
				                                 + "----------------------------------------------");
	}

	private String cutRootPath(final String path, final String rootPath) {
		final int pos = path.indexOf(rootPath);
		return path.substring(pos + rootPath.length());
	}

	private File getArtifactTopFolder() {
		String path = artefactProperties.getTargetDir();
		if (path == null) {
			return null;
		}
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
		defaultData.addFlatFolder(VALIDATOR_HELPER_SOURCE_DIR, children_VALIDATOR_SOURCE_DIR);
		defaultData.addFlatFolder(DEMO_SOURCE_DIR + "/utils", children_UTILS_SOURCE_DIR);

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
	public String getGeneratorReport() 
	{
		standardReportData.additionalReport = FileUtil.getSystemLineSeparator() 
				                             + REPORT_TAB + generationReport.toString().trim();
		return GeneratorReportUtil.getReport(standardReportData); 
	}

	@Override
	public int getNumberOfGenerations() {
		return folderGenerationCounter + fileGenerationCounter + fileModificationCounter;
	}

	@Override
	public int getNumberOfGeneratedArtefacts() {
		return standardReportData.numberOfOutputArtefacts;
	}

	@Override
	public List<MetaInfoValidator> getMetaInfoValidatorList() throws MOGLiPluginException {
		final File validationInputFile = new File(infrastructure.getPluginInputDir(), MetaInfoValidationUtil.FILENAME_VALIDATION);
		final List<MetaInfoValidator> metaInfoValidatorList = MetaInfoValidationUtil.getMetaInfoValidatorList(validationInputFile, getId());
		infrastructure.getPluginLogger().logInfo(metaInfoValidatorList.size() + " MetaInfoValidators found.");
		return metaInfoValidatorList;
	}

	@Override
	public String getShortReport() 
	{
		final String standardReport = GeneratorReportUtil.getShortReport(standardReportData);

		if (! StringUtils.isEmpty(standardReport)) {
			return standardReport;
		}
		
		return "From " + standardReportData.numberOfAllInputArtefacts + " input artefact(s), have been " 
		        + standardReportData.numberOfOutputArtefacts + " used to perform " 
		        + getNumberOfGenerations() + " generation events.";
	}
	
	@Override
	public int getSuggestedPositionInExecutionOrder()
	{
		return 300;
	}	
	
	/**
	 * for test purpose only
	 */
	ArtefactProperties getArtefactProperties() {
		return artefactProperties;
	}


}