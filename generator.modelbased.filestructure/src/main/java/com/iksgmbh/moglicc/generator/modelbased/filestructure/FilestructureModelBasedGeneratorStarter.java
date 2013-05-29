package com.iksgmbh.moglicc.generator.modelbased.filestructure;

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
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.utils.ArtefactListUtil;
import com.iksgmbh.moglicc.generator.utils.EncodingUtils;
import com.iksgmbh.moglicc.generator.utils.TemplateUtil;
import com.iksgmbh.moglicc.generator.utils.helper.PluginDataUnpacker;
import com.iksgmbh.moglicc.generator.utils.helper.PluginPackedData;
import com.iksgmbh.moglicc.plugin.type.basic.Generator;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidationUtil;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidatorVendor;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.FileUtil.FileCreationStatus;
import com.iksgmbh.utils.ImmutableUtil;
import com.iksgmbh.utils.StringUtil;

/**
* Generator of file structures to create a defined number of files within a specified folder structure.
* @author Reik Oberrath
* @since V1.3.0
*/
public class FilestructureModelBasedGeneratorStarter implements Generator, MetaInfoValidatorVendor {

	public static final String PLUGIN_ID = "FilestructureModelBasedGenerator";
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
	private TemplateProperties templateProperties;
	private Model model;

	@Override
	public void setMOGLiInfrastructure(final InfrastructureService infrastructure) {
		this.infrastructure = infrastructure;
	}

	@Override
	public void doYourJob() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("Doing my job...");

		model = infrastructure.getModelProvider(MODEL_PROVIDER_ID).getModel();

		final List<String> artefactList = getArtefactList();
		for (final String artefact : artefactList) {
			doYourJobFor(artefact);
			infrastructure.getPluginLogger().logInfo("Doing my job...");
		}

		infrastructure.getPluginLogger().logInfo("Done!");
	}


	private void doYourJobFor(final String artefact) throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("Creating artefact " + artefact + "...");
		final File sourceDir = new File(getMOGLiInfrastructure().getPluginInputDir(), artefact);
		templateProperties = readTemplateHeader(sourceDir, artefact);

		if (templateProperties.getNameOfValidModel() == null
			|| templateProperties.getNameOfValidModel().equals(model.getName()))
		{
			doYourJobWith(sourceDir, artefact, templateProperties);
		} else {
			infrastructure.getPluginLogger().logInfo("Artefact '" + artefact + "' has defined '"
			                                         + templateProperties.getNameOfValidModel() + "' as valid model.");
			infrastructure.getPluginLogger().logInfo("This artefact is not generated for current model '" + model.getName() + "'.");
		}
	}

	private void doYourJobWith(final File sourceDir, final String artefact,
			                   final TemplateProperties templateProperties) throws MOGLiPluginException
	{
		final List<String> problems = new ArrayList<String>();
		final List<String> filesToIgnore = templateProperties.getExcludes();
		filesToIgnore.add(FILENAME_ARTEFACT_PROPERTIES);

		// copy from inputDir to outputDir
		final FolderContentBasedFolderDuplicator inputFolderDuplicator = new FolderContentBasedFolderDuplicator(sourceDir, filesToIgnore);
		final File pluginOutputDir = new File(getMOGLiInfrastructure().getPluginOutputDir(), artefact);
		inputFolderDuplicator.duplicateTo(pluginOutputDir);

		// do replacements in files of outputDir before renaming with original file names
		final FolderContentBasedTextFileLineReplacer lineReplacer1 = new FolderContentBasedTextFileLineReplacer(pluginOutputDir, filesToIgnore);
		setEncodingHelper(lineReplacer1);
		lineReplacer1.doYourJob(templateProperties.getReplacements());
		problems.addAll(lineReplacer1.getErrorList());

		// do file renamings in outputDir
		final FolderContentBasedFileRenamer renamer = new FolderContentBasedFileRenamer(pluginOutputDir, filesToIgnore);
		renamer.doYourJob(templateProperties.getFileRenamings());
		problems.addAll(renamer.getErrorList());

		// do replacements in files of outputDir after renaming with renamed file names
		final FolderContentBasedTextFileLineReplacer lineReplacer2 = new FolderContentBasedTextFileLineReplacer(pluginOutputDir, filesToIgnore);
		setEncodingHelper(lineReplacer2);
		lineReplacer2.doYourJob(templateProperties.getReplacements());
		problems.addAll(lineReplacer2.getErrorList());

		// copy from outputDir to targetDir
		final FolderContentBasedFolderDuplicator outputFolderDuplicator = new FolderContentBasedFolderDuplicator(pluginOutputDir, filesToIgnore);
		final HashMap<String, FileCreationStatus> duplicationResults = outputFolderDuplicator.duplicateTo(getArtifactTopFolder(),
				                                                                                 templateProperties.isCreateNew());
		final FolderContent folderContent = outputFolderDuplicator.getFolderContent();

		if (problems.size() > 0) {
			throw new MOGLiPluginException("Following problem(s) exist for artefact '" + artefact + "': "
		                                   + FileUtil.getSystemLineSeparator()
		                                   + StringUtil.buildTextFromLines(problems));
		}

		generateReportLines(artefact, templateProperties.getReplacements(),
							templateProperties.getFileRenamings(),
							folderContent.getFolders(), duplicationResults);

	}

	private void setEncodingHelper(final FolderContentBasedTextFileLineReplacer lineReplacer) {
		final String encodingFormat = EncodingUtils.getValidOutputEncodingFormat(templateProperties.getOutputEncodingFormat(),
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
		boolean first = true;

		// report created subfolders
		for (final File file : generatedFolders) {
			if (first) {
				tmpReport.append("		File structure artefact generated in: " + getArtifactTopFolder());
				tmpReport.append(FileUtil.getSystemLineSeparator());
				tmpReport.append(FileUtil.getSystemLineSeparator());
				if (generatedFolders.size() > 1) {
					tmpReport.append("		Created subdirectories: ");
					tmpReport.append(FileUtil.getSystemLineSeparator());
				}
				first = false;
			} else {
				tmpReport.append("			" + file.getName());
				tmpReport.append(FileUtil.getSystemLineSeparator());
				generationCounter++;
			}
		}
		tmpReport.append(FileUtil.getSystemLineSeparator());

		// report created files
		final List<String> generatedFiles = StringUtil.asSortedList(duplicationResults.keySet());
		if (generatedFiles.size() > 0) {
			tmpReport.append("		Created files: ");
			tmpReport.append(FileUtil.getSystemLineSeparator());
		}
		final int lengthOfTargetDirPath = getArtifactTopFolder().getAbsolutePath().length();
		for (String filename : generatedFiles) {
			tmpReport.append("			" + filename.substring(lengthOfTargetDirPath + 1));
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

	private File getArtifactTopFolder() {
		String path = templateProperties.getTargetDir();
		path = path.replace(MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER, getMOGLiInfrastructure().getApplicationRootDir().getAbsolutePath());
		return new File(path, templateProperties.getRootName());
	}

	private TemplateProperties readTemplateHeader(final File sourceDir, final String artefact) throws MOGLiPluginException {
		final File templateFile = new File(sourceDir, TemplateUtil.findMainTemplate(sourceDir, FILENAME_ARTEFACT_PROPERTIES));
		return new TemplateProperties(templateFile , getMOGLiInfrastructure().getPluginLogger(), model, artefact);
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

		defaultData.addRootInputFile(PLUGIN_PROPERTIES_FILE);
		defaultData.addRootInputFile(MetaInfoValidationUtil.FILENAME_VALIDATION);

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
	public InfrastructureService getMOGLiInfrastructure() {
		return infrastructure;
	}

	@Override
	public boolean unpackPluginHelpFiles() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("unpackPluginHelpFiles");
		final PluginPackedData helpData = new PluginPackedData(this.getClass(), HELP_DATA_DIR, PLUGIN_ID);

		helpData.addFile(ARTEFACT_PROPERTIES_HELP_FILE);

		PluginDataUnpacker.doYourJob(helpData, infrastructure.getPluginHelpDir(), infrastructure.getPluginLogger());
		return true;
	}

	@Override
	public String getGenerationReport() {
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
	TemplateProperties getTemplateProperties() {
		return templateProperties;
	}


}