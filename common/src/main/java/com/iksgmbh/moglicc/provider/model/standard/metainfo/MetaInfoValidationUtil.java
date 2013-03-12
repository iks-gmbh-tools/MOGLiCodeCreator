package com.iksgmbh.moglicc.provider.model.standard.metainfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.data.Annotation;
import com.iksgmbh.helper.AnnotationParser;
import com.iksgmbh.helper.AnnotationParser.AnnotationContentParts;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator.ValidationType;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.StringUtil;

/**
 * Common helper for plugins to instantiate their MetaInfoValidator objects.
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class MetaInfoValidationUtil {

	public static final String FILENAME_VALIDATION = "MetaInfo.validation";
	public static final String VALIDATOR_IDENTIFIER = "validator";
	public static final String COMMENT_IDENTIFICATOR = "#";

	// identifier for modern validation DSL of
	public static final String METAINFO_IDENTIFIER = "|MetaInfo|";
	public static final String IF_METAINFO_IDENTIFIER = "|if MetaInfo|";
	public static final String WITHVALUE_IDENTIFIER = "|with value|";  // used for validator line AND condition line
	public static final String EXISTS_IDENTIFIER = "|exists.|";
	public static final String DOESNOTEXIST_IDENTIFIER = "|does not exist.|";
	public static final String IS_IDENTIFIER = "|is|";
	public static final String FOR_IDENTIFIER = "|for|";
	public static final String TIMES_FOR_IDENTIFIER = "|time(s) for|";
	public static final String IN_IDENTIFIER = "|in|";
	public static final String OCCURS_IDENTIFIER = "|is valid to occur|";
	public static final String IF_IDENTIFIER = "|if|";
	public static final String TRUE_IDENTIFIER = "|is true.|";
	public static final String DOT_IDENTIFIER = "|.|";
	public static final String CONDITION_BLOCK_SEPARATOR = "OR";

	private static final Class<?>[] constructorTypesClassic = {String.class, HierarchyLevel.class, String.class};
	private static final Class<?>[] constructorTypesConditional = {MetaInfoValidationData.class};
	private static final AnnotationParser annotationParserClassic = AnnotationParser.getInstance(VALIDATOR_IDENTIFIER + " ");	private static final MetaInfoValidationCondition newBlockIndentifier = new MetaInfoValidationCondition();

	public static List<MetaInfoValidator> getMetaInfoValidatorList(final File inputFile, final String vendorPluginId) throws MOGLiPluginException {
		final List<MetaInfoValidator> toReturn = new ArrayList<MetaInfoValidator>();
		if (inputFile != null && inputFile.exists()) {
			final List<String> fileContentAsList;
			try {
				fileContentAsList = FileUtil.getFileContentAsList(inputFile);
			} catch (IOException e) {
				throw new MOGLiPluginException("Error reading file\n" + inputFile.getAbsolutePath(), e);
			}
			for (String line : fileContentAsList) {
				line = cutComment(line);
				final MetaInfoValidatorParent metaInfoValidatorParent = parseValidatorLine(line);
				if (metaInfoValidatorParent != null) {
					metaInfoValidatorParent.setVendorPluginId(vendorPluginId);
					toReturn.add(metaInfoValidatorParent);
				}
			}
		}
		return toReturn;
	}


	/**
	 * @param inputFile
	 * @return list of blocks of conditions (condition block = inner list)
	 * @throws MOGLiPluginException
	 */
	public static List<List<MetaInfoValidationCondition>> getConditionList(final File inputFile) throws MOGLiPluginException {
		final List<List<MetaInfoValidationCondition>> toReturn = new ArrayList<List<MetaInfoValidationCondition>>();
		if (inputFile != null && inputFile.exists()) {
			final List<String> fileContentAsList;
			try {
				fileContentAsList = FileUtil.getFileContentAsList(inputFile);
			} catch (IOException e) {
				throw new MOGLiPluginException("Error reading file\n" + inputFile.getAbsolutePath(), e);
			}

			List<MetaInfoValidationCondition> innerList = new ArrayList<MetaInfoValidationCondition>();
			toReturn.add(innerList);

			for (String line : fileContentAsList) {
				line = cutComment(line);
				final MetaInfoValidationCondition validationCondition = parseConditionLine(line);

				if (newBlockIndentifier == validationCondition) {
					innerList = new ArrayList<MetaInfoValidationCondition>();
					toReturn.add(innerList);
				} else if (validationCondition != null) {
					innerList.add(validationCondition);
				}
			}
		}
		return toReturn;
	}

	static String cutComment(final String line) {
		final int pos = line.indexOf(COMMENT_IDENTIFICATOR);
		if (pos == -1) {
			return line;
		}
		return line.substring(0, pos).trim();
	}

	static MetaInfoValidatorParent parseValidatorLine(String line) throws MOGLiPluginException {
		if (StringUtils.isEmpty(line)) {
			return null; // ignore this line
		}

		line = line.trim();
		if (annotationParserClassic.hasCorrectPrefix(line)) {
			final Annotation annotation = annotationParserClassic.doYourJob(line);
			final MetaInfoValidatorParent metaInfoValidator = createValidatorInstanceClassic(annotation);
			return metaInfoValidator;
		} else if (annotationParserClassic.isCommentLine(line)) {
			return null; // ignore this line
		} else if (line.startsWith(METAINFO_IDENTIFIER)) {
			final MetaInfoValidatorParent metaInfoValidator = createValidatorInstanceModern(line);
			return metaInfoValidator;
		}
		throw new MOGLiPluginException("Line not parsable as MetaInfoValidator: <" + line + ">");
	}

	private static MetaInfoValidatorParent createValidatorInstanceClassic(final Annotation annotation) throws MOGLiPluginException {
		for (final MetaInfoValidator.ValidationType validationType : MetaInfoValidator.ValidationType.values()) {
			if (validationType.name().toLowerCase().equals(annotation.getName().toLowerCase())) {
				final Object[] initArgs = getArgs(annotation);
				return getValidatorInstance(constructorTypesClassic, validationType, initArgs, annotation.toString());
			}
		}

		throw new MOGLiPluginException("Unknown ValidationType <" + annotation.getName() + ">.");
	}

	private static MetaInfoValidatorParent getValidatorInstance(final Class<?>[] constructorTypes,
			                                                    final MetaInfoValidator.ValidationType validationType,
			                                                    final Object[] initArgs,
			                                                    final String line) throws MOGLiPluginException
	{
		final String className = "com.iksgmbh.moglicc.provider.model.standard.metainfo.validator."
			                      + validationType.name() + "MetaInfoValidator";
		try {
			return (MetaInfoValidatorParent) Class.forName(className).getConstructor(constructorTypes).newInstance(initArgs);
		} catch (Exception e) {
			throw new MOGLiPluginException("Error creating instance of MetaInfoValidator from <" + line + ">.", e);
		}
	}

	private static MetaInfoValidatorParent createValidatorInstanceModern(final String line) throws MOGLiPluginException {
		final String metaInfoName;
		final String validationTypeAsString;
		final String hierarchyLevelDescriptor;
		final String modelName;

		String additionalInfo = StringUtil.removePrefixIfExisting(line, METAINFO_IDENTIFIER).trim();

		if (additionalInfo.contains(IS_IDENTIFIER)) {
			final ParseResult parseResult = parseNextInfoItemFromLine(additionalInfo, IS_IDENTIFIER, "MetaInfoName", line );
			metaInfoName = parseResult.infoItem;
			additionalInfo = parseResult.additionalInformation;
		} else {
			if (additionalInfo.contains(WITHVALUE_IDENTIFIER)
			    || additionalInfo.contains(OCCURS_IDENTIFIER)) {
				return createNumOccurenceInstance(line);
			}
			throw new MOGLiPluginException("Cannot parse ValidationType/Occurrence for MetaInfoValidator <" + line + ">");
		}

		ParseResult parseResult = parseNextInfoItemFromLine(additionalInfo, FOR_IDENTIFIER, "Validation Type", line );
		validationTypeAsString = parseResult.infoItem;
		additionalInfo = parseResult.additionalInformation;

		parseResult = parseNextInfoItemFromLine(additionalInfo, IN_IDENTIFIER, "Hierarchy Level", line );
		hierarchyLevelDescriptor = parseResult.infoItem;
		additionalInfo = parseResult.additionalInformation;

		if (additionalInfo.endsWith(DOT_IDENTIFIER)) {
			modelName = StringUtil.removeSuffixIfExisting(additionalInfo.trim(), DOT_IDENTIFIER).trim();
			if (StringUtils.isEmpty(modelName)) {
				final String errorMessage = "No Model Name defined for MetaInfoValidator <" + line + ">";
				throw new MOGLiPluginException(errorMessage);
			}
		} else {
			throw new MOGLiPluginException("Mandatory and Optional MetaInfoValidator must end with " + DOT_IDENTIFIER + ": <" + line + ">");
		}

		for (final MetaInfoValidator.ValidationType validationType : MetaInfoValidator.ValidationType.values()) {
			if (validationType.name().toLowerCase().equals(validationTypeAsString.toLowerCase())) {
				final Object[] initArgs = {metaInfoName, getHierarchyLevelFromDescriptor(hierarchyLevelDescriptor), modelName};
				return getValidatorInstance(constructorTypesClassic, validationType, initArgs, line);
			}
		}

		throw new MOGLiPluginException("Unknown ValidationType <" + line + ">.");
	}

	private static MetaInfoValidatorParent createNumOccurenceInstance(final String line) throws MOGLiPluginException {
		final ValidationType validationType;
		MetaInfoValidationData metaInfoValidationData = new MetaInfoValidationData();
		String additionalInfo = StringUtil.removePrefixIfExisting(line, METAINFO_IDENTIFIER).trim();
		ParseResult parseResult;

		if (additionalInfo.contains(WITHVALUE_IDENTIFIER)) {
			parseResult = parseNextInfoItemFromLine(additionalInfo, WITHVALUE_IDENTIFIER, "MetaInfoValue", line );
			metaInfoValidationData.withMetaInfoName(parseResult.infoItem);
			additionalInfo = parseResult.additionalInformation;

			parseResult = parseNextInfoItemFromLine(additionalInfo, OCCURS_IDENTIFIER, "MetaInfoValue", line );
			metaInfoValidationData.withMetaInfoValue(parseResult.infoItem);
			additionalInfo = parseResult.additionalInformation;
		} else {
			parseResult = parseNextInfoItemFromLine(additionalInfo, OCCURS_IDENTIFIER, "MetaInfoName", line );
			metaInfoValidationData.withMetaInfoName(parseResult.infoItem);
			additionalInfo = parseResult.additionalInformation;
		}

		parseResult = parseNextInfoItemFromLine(additionalInfo, TIMES_FOR_IDENTIFIER, "occurence", line );
		metaInfoValidationData.withOccurrence(parseResult.infoItem);
		additionalInfo = parseResult.additionalInformation;

		parseResult = parseNextInfoItemFromLine(additionalInfo, IN_IDENTIFIER, "HierarchyLevel", line );
		final String hierarchyLevelDescriptor = StringUtil.firstToUpperCase(parseResult.infoItem.toLowerCase());
		metaInfoValidationData = metaInfoValidationData.withHierarchyLevel(getHierarchyLevelFromDescriptor(hierarchyLevelDescriptor));
		additionalInfo = parseResult.additionalInformation;

		if (additionalInfo.contains(IF_IDENTIFIER)) {
			parseResult = parseNextInfoItemFromLine(additionalInfo, IF_IDENTIFIER, "model name", line );
			metaInfoValidationData.withNameOfValidModel(parseResult.infoItem);
			additionalInfo = parseResult.additionalInformation;

			if (additionalInfo.endsWith(TRUE_IDENTIFIER)) {
				metaInfoValidationData.withConditionFilename(StringUtil.removeSuffixIfExisting(additionalInfo.trim(), TRUE_IDENTIFIER).trim());
			} else {
				throw new MOGLiPluginException("Condition of MetaInfoValidator must end with " + TRUE_IDENTIFIER + ": <" + line + ">");
			}
			validationType = ValidationType.Conditional;
		} else {
			if (additionalInfo.endsWith(DOT_IDENTIFIER)) {
				final String modelName = StringUtil.removeSuffixIfExisting(additionalInfo.trim(), DOT_IDENTIFIER).trim();
				if (StringUtils.isEmpty(modelName)) {
					final String errorMessage = "No Model Name defined for MetaInfoValidator <" + line + ">";
					throw new MOGLiPluginException(errorMessage);
				}
				metaInfoValidationData.withNameOfValidModel(modelName);
			} else {
				throw new MOGLiPluginException("Either |if|-Identifier is wrong or MetaInfoValidator without condition does not end with " + DOT_IDENTIFIER + ": <" + line + ">");
			}
			validationType = ValidationType.NumOccur;
		}

		final Object[] initArgs = { metaInfoValidationData };
		return getValidatorInstance(constructorTypesConditional, validationType, initArgs, line);
	}


	private static ParseResult parseNextInfoItemFromLine(final String additionalInfo,
			                                     final String separator,
			                                     final String infoItemName,
			                                     final String line) throws MOGLiPluginException
	{
		if (additionalInfo.contains(separator)) {
			final ParseResult toReturn = new ParseResult();
			final String[] result = StringUtils.splitByWholeSeparator(additionalInfo, separator);
			if (result.length != 2) {
				final String errorMessage = "No " + infoItemName + " defined for MetaInfoValidator <" + line + ">";
				throw new MOGLiPluginException(errorMessage);
			}
			toReturn.infoItem = result[0].trim();
			toReturn.additionalInformation = result[1].trim();
			return toReturn;

		} else {
			final String errorMessage = "Cannot parse " + infoItemName + " for MetaInfoValidator <" + line + ">";
			throw new MOGLiPluginException(errorMessage);
		}
	}

	private static Object[] getArgs(final Annotation annotation) throws MOGLiPluginException {
		if (annotation.getAdditionalInfo() == null) {
			throw new MOGLiPluginException("Missing information parsing " + annotation);
		}

		final AnnotationContentParts annotationContentParts1 =
			                         annotationParserClassic.getAnnotationContentParts(annotation.getAdditionalInfo());
		if (annotationContentParts1.getSecondPart() == null) {
			throw new MOGLiPluginException("MetaInfoName or MetaInfoHierarchyLevel is missing.\n"
					 + "Error parsing " + annotation);
		}

		final AnnotationContentParts annotationContentParts2 =
            annotationParserClassic.getAnnotationContentParts(annotationContentParts1.getSecondPart());

		Object[] toReturn = { annotationContentParts1.getFirstPart(),
				              getHierarchyLevel(annotationContentParts2.getFirstPart()),
				              annotationContentParts2.getSecondPart() };
		return toReturn;
	}

	public static HierarchyLevel getHierarchyLevel(final String metaInfoHierarchyLevel) throws MOGLiPluginException {
		final HierarchyLevel[] values = HierarchyLevel.values();
		for (final HierarchyLevel hierarchyLevel : values) {
			if (hierarchyLevel.name().toLowerCase().equals(metaInfoHierarchyLevel.toLowerCase())) {
				return hierarchyLevel;
			}
		}
		throw new MOGLiPluginException("Unknown MetaInfoHierarchyLevel <" + metaInfoHierarchyLevel + ">.");
	}

	/**
	 * Converts describtor string value into a HierarchyLevel enum object by building singular from Plural.
	 * @param descriptor e.g. Classes
	 * @return HierarchyLevel e.g. Class
	 * @throws MOGLiPluginException
	 */
	public static HierarchyLevel getHierarchyLevelFromDescriptor(final String descriptor) throws MOGLiPluginException {

		String tmpDescriptor = descriptor;

		try {
			return getHierarchyLevel(tmpDescriptor);
		} catch (Exception e) {
			tmpDescriptor = StringUtil.removeSuffixByLength(descriptor, 1);
		}

		try {
			return getHierarchyLevel(tmpDescriptor);
		} catch (Exception e) {
			tmpDescriptor = StringUtil.removeSuffixByLength(tmpDescriptor, 1);
		}

		try {
			return getHierarchyLevel(tmpDescriptor);
		} catch (Exception e) {
			throw new MOGLiPluginException("Unknown Descriptor for MetaInfoHierarchyLevel <" + descriptor + ">.");
		}
	}

	static MetaInfoValidationCondition parseConditionLine(String line) throws MOGLiPluginException {
		line = line.trim();

		if (StringUtils.isEmpty(line)) {
			return null; // ignore this line
		}

		if (line.startsWith(IF_METAINFO_IDENTIFIER)) {
			final MetaInfoValidationCondition condition = createConditionInstance(line);
			return condition;
		} else if (line.equals(CONDITION_BLOCK_SEPARATOR)) {
			return newBlockIndentifier;
		}
		throw new MOGLiPluginException("Line not parsable as Condition: <" + line + ">");
	}


	private static MetaInfoValidationCondition createConditionInstance(final String line) throws MOGLiPluginException {
		final String otherMetaInfoName;
		final String otherMetaInfoValue;
		final Boolean mustNotHaveotherMetaInfo;

		String additionalInfo = StringUtil.removePrefixIfExisting(line, IF_METAINFO_IDENTIFIER).trim();

		if (additionalInfo.endsWith(EXISTS_IDENTIFIER)) {
			mustNotHaveotherMetaInfo = Boolean.FALSE;
			additionalInfo = StringUtil.removeSuffixIfExisting(additionalInfo, EXISTS_IDENTIFIER);
		} else if (additionalInfo.endsWith(DOESNOTEXIST_IDENTIFIER)) {
			mustNotHaveotherMetaInfo = Boolean.TRUE;
			additionalInfo = StringUtil.removeSuffixIfExisting(additionalInfo, DOESNOTEXIST_IDENTIFIER);
		} else {
			throw new MOGLiPluginException("MetaInfoValidationCondition line must end with " + EXISTS_IDENTIFIER
					+ " or " + DOESNOTEXIST_IDENTIFIER + ": <" + line + ">.");
		}

		if (additionalInfo.contains(WITHVALUE_IDENTIFIER)) {
			final String[] result = StringUtils.splitByWholeSeparator(additionalInfo, WITHVALUE_IDENTIFIER);
			otherMetaInfoName = result[0].trim();
			otherMetaInfoValue = result[1].trim();
		} else {
			otherMetaInfoName = additionalInfo.trim();
			otherMetaInfoValue = null;
		}

		return new MetaInfoValidationCondition(mustNotHaveotherMetaInfo.booleanValue(), otherMetaInfoName, otherMetaInfoValue);
	}

	static class ParseResult  {
		String infoItem;
		String additionalInformation;
	}

}