package com.iksgmbh.moglicc.generator.modelbased.filestructure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.data.Annotation;
import com.iksgmbh.helper.AnnotationParser;
import com.iksgmbh.helper.FolderContentBasedFileRenamer.RenamingData;
import com.iksgmbh.helper.FolderContentBasedTextFileLineReplacer.ReplacementData;
import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.core.Logger;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoSupport;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.StringUtil;

public class TemplateProperties {
	private static final String MODELMETAINFO_REPLACE_START_INDICATOR = "${";
	private static final String MODELMETAINFO_REPLACE_END_INDICATOR = "}";

	enum KnownGeneratorPropertyNames { TargetDir, RootName, CreateNew, NameOfValidModel, OutputEncodingFormat, 
		                               Exclude, ReplaceIn, RenameFile };
	
	private static final AnnotationParser annotationParser = AnnotationParser.getInstance();

	private static final Object MODEL_META_INFO = "ModelMetaInfo";

	private File templateFile;
	private Logger logger;
	private Model model;
	
	// properties
	private String rootName;
	private String targetDir = MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER;  // default value
	private String nameOfValidModel;
	private String excludeString;
	private String outputEncodingFormat;
	private boolean createNew = false;
	private List<ReplacementData> replacements = new ArrayList<ReplacementData>();
	private List<RenamingData> renamings = new ArrayList<RenamingData>();


	TemplateProperties(final File templateFile, final Logger logger, 
			           final Model model, final String artefact) throws MOGLiPluginException {
		this.templateFile = templateFile;
		this.logger = logger;
		this.model = model;
		this.rootName = artefact;  // set as default
		parseTemplateFile();
	}

	private void parseTemplateFile() throws MOGLiPluginException {
		final List<String> content;
		try {
			content = FileUtil.getFileContentAsList(templateFile);
		} catch (IOException e) {
			throw new MOGLiPluginException("Error reading template file: " + templateFile.getAbsolutePath() + " " + e.getMessage());
		}
		for (final String line : content) {
			if (annotationParser.hasCorrectPrefix(line)) {
				parseLine(line);
			} else if (StringUtils.isEmpty(line)) {
				// ignore this line
				
			} else if (annotationParser.isCommentLine(line)) {
				// ignore this line
			}
			else {
				logger.logWarning("Unexpected line in " + templateFile.getAbsolutePath() + ": " + line);
			}
		}
	}
	

	private void parseLine(final String line) throws MOGLiPluginException {
		final String preparsedLine = replaceModelMetaInfoReferences(line);
		final Annotation annotation = annotationParser.doYourJob(preparsedLine);
		
		if (KnownGeneratorPropertyNames.TargetDir.name().equalsIgnoreCase(annotation.getName())) {
			targetDir = annotationParser.removePartBraceIdentifier(annotation.getAdditionalInfo());
		} else if (KnownGeneratorPropertyNames.RootName.name().equalsIgnoreCase(annotation.getName())) {
			rootName = annotationParser.removePartBraceIdentifier(annotation.getAdditionalInfo());
		} else if (KnownGeneratorPropertyNames.NameOfValidModel.name().equalsIgnoreCase(annotation.getName())) {
			nameOfValidModel = annotationParser.removePartBraceIdentifier(annotation.getAdditionalInfo());
		} else if (KnownGeneratorPropertyNames.CreateNew.name().equalsIgnoreCase(annotation.getName())) {
			createNew = annotation.getAdditionalInfo().equals("true");
		} else if (KnownGeneratorPropertyNames.OutputEncodingFormat.name().equalsIgnoreCase(annotation.getName())) {
			outputEncodingFormat = annotationParser.removePartBraceIdentifier(annotation.getAdditionalInfo());
		} else if (KnownGeneratorPropertyNames.Exclude.name().equalsIgnoreCase(annotation.getName())) {
			excludeString = annotationParser.removePartBraceIdentifier(annotation.getAdditionalInfo());
		} else if (KnownGeneratorPropertyNames.ReplaceIn.name().equalsIgnoreCase(annotation.getName())) {
			replacements.add(parseReplacementData(preparsedLine));
		} else if (KnownGeneratorPropertyNames.RenameFile.name().equalsIgnoreCase(annotation.getName())) {
			renamings.add(parseFileRenamingData(annotation, line));
		} else {
			throw new MOGLiPluginException("Unexpected annotation in " + templateFile.getAbsolutePath() + ": " + line);
		}
	}

	private RenamingData parseFileRenamingData(final Annotation annotation, final String line) throws MOGLiPluginException {
		final String additionalInfo = annotation.getAdditionalInfo().trim();
		final int pos = additionalInfo.indexOf(" ");
		if (pos == -1) {
			throw new MOGLiPluginException("Missing name2 in " + templateFile.getAbsolutePath() + ": " + line);
		}
		final String name1 = additionalInfo.substring(0, pos).trim();
		final String name2 = additionalInfo.substring(pos).trim();
		if (name2.contains(" ")) {
			throw new MOGLiPluginException("Name2 must not contain spaces in " + templateFile.getAbsolutePath() + ": " + line);
		}
		return new RenamingData(name1, name2);
	}

	private String replaceModelMetaInfoReferences(final String line) throws MOGLiPluginException {
		if (nameOfValidModel != null 
			&& ! nameOfValidModel.equals(model.getName())) 
		{
			return line; // model is not valid for this artifact, thus replacement does not make sense
		}

		final String metaInfo = StringUtil.substringBetween(line.trim(), MODELMETAINFO_REPLACE_START_INDICATOR, MODELMETAINFO_REPLACE_END_INDICATOR);
		if (metaInfo == null) {
			return line;  // nothing to replace
		}
		final String[] splitResult = StringUtils.splitByWholeSeparator(metaInfo, "=");
		if (splitResult.length != 2) {
			throw new MOGLiPluginException("Wrong usage of '" + MODEL_META_INFO + "' in template file "
                                           + FileUtil.getSystemLineSeparator() + templateFile.getAbsolutePath()
                                           + FileUtil.getSystemLineSeparator() + "Use: "
                                           + MODELMETAINFO_REPLACE_START_INDICATOR + MODEL_META_INFO + "=<MetaInfoName>"
                                           + MODELMETAINFO_REPLACE_END_INDICATOR);
		}
		if (splitResult[0].equals(MODEL_META_INFO)) {
			final String substringToReplace = MODELMETAINFO_REPLACE_START_INDICATOR 
					                          + metaInfo
					                          + MODELMETAINFO_REPLACE_END_INDICATOR;
			final String replacement = getMetaInfoValueFor(splitResult[1]);
			return line.replace(substringToReplace, replacement);
		}
		throw new MOGLiPluginException("Unkown placeholder '" + splitResult[0] + "' in template file: " 
		                                 + FileUtil.getSystemLineSeparator() + templateFile.getAbsolutePath());
	}

	private String getMetaInfoValueFor(String metaInfoName) throws MOGLiPluginException {
		metaInfoName = StringUtil.firstToUpperCase(metaInfoName);
		String metaInfoValue = model.getMetaInfoValueFor(metaInfoName);
		if (model.isValueAvailable(metaInfoValue)) {
			return metaInfoValue;
		}
		metaInfoName = StringUtil.firstToLowerCase(metaInfoName);
		metaInfoValue = model.getMetaInfoValueFor(metaInfoName);
		if (metaInfoValue == null) {
			metaInfoValue = MetaInfoSupport.META_INFO_NOT_FOUND.replaceFirst("#", metaInfoName);
		}
		if (! model.isValueAvailable(metaInfoValue)) {
			throw new MOGLiPluginException("MetaInfo '" + metaInfoName 
					                       + "' is unknown in hierarchy level 'model' of model '"
					                       + model.getName() + "'.");
		}
		return metaInfoValue;
	}

	private ReplacementData parseReplacementData(final String line) {
		return new ReplacementDataParser().parse(line);
	}

	public String getRootName() {
		return rootName;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public String getNameOfValidModel() {
		return nameOfValidModel;
	}

	public List<String> getExcludes() {
		final List<String> toReturn = new ArrayList<String>();
		if (excludeString == null) {
			return toReturn;
		}
		final String[] array = StringUtil.getListFromLineWithCommaSeparatedElements(excludeString);
		toReturn.addAll(Arrays.asList(array));
		return toReturn;
	}
	
	public List<ReplacementData> getReplacements() {
		return replacements;
	}

	public String getTargetDir() {
		return targetDir;
	}

	public String getOutputEncodingFormat() {
		return outputEncodingFormat;
	}

	public List<RenamingData> getFileRenamings() {
		return renamings;
	}
}
