package com.iksgmbh.moglicc.provider.model.standard.parser;

import com.iksgmbh.data.Annotation;
import com.iksgmbh.helper.AnnotationParser;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.MetaModelConstants;
import com.iksgmbh.moglicc.provider.model.standard.TextConstants;

public class VariableParser extends AnnotationParser {
	
	public VariableParser() {
		super(MetaModelConstants.VARIABLE_IDENTIFIER + " ", AnnotationParser.DEFAULT_PART_BRACE_IDENTIFIER, AnnotationParser.DEFAULT_COMMENT_IDENTIFICATOR);
	}
	
	public Annotation parse(String line) throws MOGLiPluginException 
	{
		final Annotation toReturn = super.doYourJob(line);
		if (toReturn.getAdditionalInfo() == null) {
			throw new MOGLiPluginException(TextConstants.INVALID_INFORMATION + "for variable '" + toReturn.getName() + "'");
		}
		
		String additionalInfo = toReturn.getAdditionalInfo();
		
		if (additionalInfo.startsWith(MetaModelConstants.UPPERCASE_IDENTIFIER))
		{
			int charsToCut = MetaModelConstants.UPPERCASE_IDENTIFIER.length();
			additionalInfo = additionalInfo.substring(charsToCut).toUpperCase();
			toReturn.setAdditionalInfo(additionalInfo);
		}

		if (additionalInfo.startsWith(MetaModelConstants.LOWERCASE_IDENTIFIER))
		{
			int charsToCut = MetaModelConstants.LOWERCASE_IDENTIFIER.length();
			additionalInfo = additionalInfo.substring(charsToCut).toLowerCase();
			toReturn.setAdditionalInfo(additionalInfo);
		}
		
		if (additionalInfo.startsWith("\"") && additionalInfo.endsWith("\""))
		{
			additionalInfo = additionalInfo.substring(1, additionalInfo.length()-1);
			toReturn.setAdditionalInfo(additionalInfo);
		}
		
		return toReturn;
	}

}