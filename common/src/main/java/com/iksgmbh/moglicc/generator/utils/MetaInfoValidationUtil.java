package com.iksgmbh.moglicc.generator.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo.HierarchyLevel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidator;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoValidatorParent;
import com.iksgmbh.data.Annotation;
import com.iksgmbh.helper.AnnotationParser;
import com.iksgmbh.helper.AnnotationParser.AnnotationContentParts;
import com.iksgmbh.utils.FileUtil;

/**
 * Common helper for plugins to instantiate their MetaInfoValidator objects.
 * @author Reik Oberrath
 */
public class MetaInfoValidationUtil {
	
	public static final String FILENAME_VALIDATION = "MetaInfo.validation";
	public static final String VALIDATOR_IDENTIFIER = "validator";
	
	private static final Class<?>[] constructorTypes = {String.class, HierarchyLevel.class, String.class};
	private static final AnnotationParser annotationParser = AnnotationParser.getInstance(VALIDATOR_IDENTIFIER + " ");


	public static List<MetaInfoValidator> getMetaInfoValidatorList(final File inputFile, 
								final String vendorPluginId) throws MOGLiPluginException {
		final List<MetaInfoValidator> toReturn = new ArrayList<MetaInfoValidator>();
		if (inputFile != null && inputFile.exists()) {
			final List<String> fileContentAsList;
			try {
				fileContentAsList = FileUtil.getFileContentAsList(inputFile);
			} catch (IOException e) {
				throw new MOGLiPluginException("Error reading file\n" + inputFile.getAbsolutePath(), e);
			}
			for (final String line : fileContentAsList) {
				final MetaInfoValidatorParent metaInfoValidatorParent = parseLine(line);
				if (metaInfoValidatorParent != null) {
					metaInfoValidatorParent.setVendorPluginId(vendorPluginId);
					toReturn.add(metaInfoValidatorParent);
				}
			}
		}
		return toReturn;
	}

	static MetaInfoValidatorParent parseLine(String line) throws MOGLiPluginException {
		if (StringUtils.isEmpty(line)) {
			return null; // ignore this line
		}
		
		if (annotationParser.hasCorrectPrefix(line)) {
			final Annotation annotation = annotationParser.doYourJob(line);
			final MetaInfoValidatorParent metaInfoValidator = createInstance(annotation);
			return metaInfoValidator;
		} else if (annotationParser.isCommentLine(line)) {
			return null; // ignore this line
		}
		throw new MOGLiPluginException("Line not parsable as MetaInfoValidator: " + line);
	}

	private static MetaInfoValidatorParent createInstance(final Annotation annotation) throws MOGLiPluginException {
		for (final MetaInfoValidator.ValidationType validationType : MetaInfoValidator.ValidationType.values()) {
			if (validationType.name().toLowerCase().equals(annotation.getName().toLowerCase())) {
				final String className = "com.iksgmbh.moglicc.provider.model.standard.metainfo." 
					                      + validationType.name() + "MetaInfoValidator";

				final Object[] initArgs = getArgs(annotation);
				try {
					return (MetaInfoValidatorParent) Class.forName(className).
					                                 getConstructor(constructorTypes).newInstance(initArgs);
				} catch (Exception e) {
					throw new MOGLiPluginException("Error creating instance of MetaInfoValidator from <" 
							                            + annotation + ">.", e);
				}
			}
		}
		
		throw new MOGLiPluginException("Unknown ValidationType <" + annotation.getName() + ">.");
	}

	private static Object[] getArgs(final Annotation annotation) throws MOGLiPluginException {
		if (annotation.getAdditionalInfo() == null) {
			throw new MOGLiPluginException("Missing information parsing " + annotation);
		}
		
		final AnnotationContentParts annotationContentParts1 = 
			                         annotationParser.getAnnotationContentParts(annotation.getAdditionalInfo());
		if (annotationContentParts1.getSecondPart() == null) {
			throw new MOGLiPluginException("MetaInfoName or MetaInfoHierarchyLevel is missing.\n"
					 + "Error parsing " + annotation);
		}

		final AnnotationContentParts annotationContentParts2 = 
            annotationParser.getAnnotationContentParts(annotationContentParts1.getSecondPart());
		
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

}
