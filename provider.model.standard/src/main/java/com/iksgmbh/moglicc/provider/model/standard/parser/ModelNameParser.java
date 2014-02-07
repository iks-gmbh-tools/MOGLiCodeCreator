package com.iksgmbh.moglicc.provider.model.standard.parser;

import com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants;
import com.iksgmbh.data.Annotation;
import com.iksgmbh.helper.AnnotationParser;

public class ModelNameParser extends AnnotationParser {
	
	public ModelNameParser() {
		super(MetaModelConstants.MODEL_IDENTIFIER + " ", AnnotationParser.DEFAULT_PART_BRACE_IDENTIFIER, AnnotationParser.DEFAULT_COMMENT_IDENTIFICATOR);
	}
	
	public String parse(String line) {
		final Annotation annotation = super.doYourJob(line);
		return annotation.getFullInfo();
	}
}
