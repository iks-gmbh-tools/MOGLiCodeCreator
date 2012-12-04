package com.iksgmbh.moglicc.provider.model.standard.parser;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException2;
import com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants;
import com.iksgmbh.moglicc.provider.model.standard.TextConstants;
import com.iksgmbh.moglicc.provider.model.standard.impl.BuildUpClassDescriptor;
import com.iksgmbh.data.Annotation;
import com.iksgmbh.data.ClassNameData;
import com.iksgmbh.helper.AnnotationParser;

public class ClassDescriptorParser extends AnnotationParser {
	
	public ClassDescriptorParser() {
		super(MetaModelConstants.CLASS_IDENTIFIER + " ", AnnotationParser.DEFAULT_COMMENT_IDENTIFICATOR);
	}
	
	public BuildUpClassDescriptor parse(String line) throws MOGLiPluginException2 {
		final Annotation annotation = super.doYourJob(line);
		final ClassNameData classnameData = new ClassNameData(annotation.getName());
		final BuildUpClassDescriptor toReturn = new BuildUpClassDescriptor(classnameData);
		if (annotation.getAdditionalInfo() != null) {
			throw new MOGLiPluginException2(TextConstants.INVALID_INFORMATION + " for " + annotation.getName());
		}
		return toReturn;
	}

}
