package com.iksgmbh.moglicc.provider.model.standard.parser;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException2;
import com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants;
import com.iksgmbh.moglicc.provider.model.standard.TextConstants;
import com.iksgmbh.moglicc.provider.model.standard.impl.BuildUpAttributeDescriptor;
import com.iksgmbh.data.Annotation;
import com.iksgmbh.helper.AnnotationParser;

public class AttributeDescriptorParser extends AnnotationParser {
	
	public AttributeDescriptorParser() {
		super(MetaModelConstants.ATTRIBUTE_IDENTIFIER + " ", AnnotationParser.DEFAULT_COMMENT_IDENTIFICATOR);
	}
	
	public BuildUpAttributeDescriptor parse(String line) throws MOGLiPluginException2 {
		final Annotation annotation = super.doYourJob(line);
		final BuildUpAttributeDescriptor toReturn = new BuildUpAttributeDescriptor(annotation.getName());
		if (annotation.getAdditionalInfo() != null) {
			throw new MOGLiPluginException2(TextConstants.INVALID_INFORMATION + " for " + annotation.getName());
		}
		return toReturn;
	}

}
