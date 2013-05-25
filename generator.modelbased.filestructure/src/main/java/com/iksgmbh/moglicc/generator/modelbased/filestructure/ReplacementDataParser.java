package com.iksgmbh.moglicc.generator.modelbased.filestructure;

import com.iksgmbh.data.Annotation;
import com.iksgmbh.helper.AnnotationParser;
import com.iksgmbh.helper.FolderContentBasedTextFileLineReplacer.ReplacementData;
import com.iksgmbh.moglicc.generator.modelbased.filestructure.TemplateProperties.KnownGeneratorPropertyNames;

public class ReplacementDataParser extends AnnotationParser {

	private static final String ANY = "*";

	public ReplacementDataParser() {
		this(AnnotationParser.DEFAULT_PART_BRACE_IDENTIFIER);
	}

	public ReplacementDataParser(final String braceSymbol) {
		super("@" + KnownGeneratorPropertyNames.ReplaceIn.name(), braceSymbol, AnnotationParser.DEFAULT_COMMENT_IDENTIFICATOR);
	}

	public ReplacementData parse(final String line) {
		final Annotation annotation = super.doYourJob(line);
		if (annotation.getName().startsWith(ERROR)) {
			throw new IllegalArgumentException(annotation.getName());
		}		
		if (annotation.getAdditionalInfo() == null) {
			throw new IllegalArgumentException("TextConstants.MISSING_VALUE");
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

		return new ReplacementData(oldString, newString, fileEndingPattern);
	}

}
