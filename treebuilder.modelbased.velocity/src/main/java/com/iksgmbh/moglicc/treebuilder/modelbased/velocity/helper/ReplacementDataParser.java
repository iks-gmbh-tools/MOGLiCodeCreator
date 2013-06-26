package com.iksgmbh.moglicc.treebuilder.modelbased.velocity.helper;

import com.iksgmbh.data.Annotation;
import com.iksgmbh.helper.AnnotationParser;
import com.iksgmbh.helper.FolderContentBasedTextFileLineReplacer.ReplacementData;
import com.iksgmbh.moglicc.treebuilder.modelbased.velocity.data.VelocityTreeBuilderResultData.KnownTreeBuilderPropertyNames;
import com.iksgmbh.utils.FileUtil;

public class ReplacementDataParser extends AnnotationParser {

	private static final String ANY = "*";

	private static ReplacementDataParser instance;

	public static ReplacementData doYourJobFor(final String dataAsString) {
		if (instance == null) {
			instance = new ReplacementDataParser();
		}
		return instance.parse(buildParserLine(dataAsString)); 
	}
	
	private ReplacementDataParser() {
		this(AnnotationParser.DEFAULT_PART_BRACE_IDENTIFIER);
	}

	private ReplacementDataParser(final String braceSymbol) {
		super("@" + KnownTreeBuilderPropertyNames.ReplaceIn.name(), braceSymbol, AnnotationParser.DEFAULT_COMMENT_IDENTIFICATOR);
	}

	public ReplacementData parse(final String line) {
		final Annotation annotation = super.doYourJob(line);
		if (annotation.getName().startsWith(ERROR)) {
			throw new IllegalArgumentException(annotation.getName());
		}		
		if (annotation.getAdditionalInfo() == null) {
			throw new IllegalArgumentException("Values are missing for line: " 
		                  + FileUtil.getSystemLineSeparator() + line);
		}
	
		final String fileEndingPattern;
		if (ANY.equals(annotation.getName())) {
			fileEndingPattern = null;  // that means: take all
		} else {
			fileEndingPattern = annotation.getName();
		}
		final AnnotationContentParts annotationContentParts = getAnnotationContentParts(annotation.getAdditionalInfo());
		final String oldString = annotationContentParts.getFirstPart();
		final String newString = removePartBraceIdentifier(annotationContentParts.getSecondPart());
		if (newString == null) {
			throw new IllegalArgumentException("Missing value in line: " + FileUtil.getSystemLineSeparator() + line);
		}
	
		return new ReplacementData(oldString, newString, fileEndingPattern);
	}

	/**
	 * The original property name was removed by the VelocityEngineProvider.
	 * To use the Annotation parser to build Replacement Data objects,
	 * the original property name is added here.
	 */
	private static String buildParserLine(final String line) {
		return "@" + KnownTreeBuilderPropertyNames.ReplaceIn.name() + " " + line;
	}

}
