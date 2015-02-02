package com.iksgmbh.moglicc.provider.model.standard.parser;

import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.ATTRIBUTE_WITHOUT_CLASS;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.DUPLICATE_MODEL_IDENTIFIER;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.INVALID_INFORMATION;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.MODEL_IDENTIFIER_NOT_FOUND;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.UNKOWN_VARIABLE;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.UNRELATED_METAINFO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.iksgmbh.data.Annotation;
import com.iksgmbh.helper.AnnotationParser;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.TextConstants;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpAttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpMetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpModel;
import com.iksgmbh.moglicc.provider.model.standard.exceptions.ModelParserException;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;
import com.iksgmbh.utils.StringUtil;

public class ModelParser {

	public static final String VARIABLE_START_INDICATOR = "<<";
	public static final String VARIABLE_END_INDICATOR = ">>";
	private static final String COMMENT_PREFIX = "#";

	private final List<String> errorList = new ArrayList<String>();

	private final ModelNameParser modelNameParser = new ModelNameParser();
	private final ClassDescriptorParser classDescriptorParser = new ClassDescriptorParser();
	private final AttributeDescriptorParser attributeDescriptorParser = new AttributeDescriptorParser();
	private final VariableParser variableParser = new VariableParser();
	private final MetaInfoParser metaInfoParser;

	private BuildUpModel buildUpModel;
	private BuildUpClassDescriptor buildUpClassDescriptor;
	private BuildUpAttributeDescriptor buildUpAttributeDescriptor;
	private HashMap<String, String> variableMap = new HashMap<String, String>();


	private ModelParser(final String braceSymbol) {
		metaInfoParser = new MetaInfoParser(braceSymbol);
	}

	public ModelParser() {
		metaInfoParser = new MetaInfoParser(AnnotationParser.DEFAULT_PART_BRACE_IDENTIFIER);
	}

	public static BuildUpModel doYourJob(final List<String> fileContentAsList,
			                             final String braceSymbol) throws ModelParserException {
		final ModelParser modelParser = new ModelParser(braceSymbol);
		return modelParser.parse(fileContentAsList);
	}

	BuildUpModel parse(List<String> fileContentAsList) throws ModelParserException {
		int lineCounter = 0;
		for (String line : fileContentAsList) {
			lineCounter++;
			line = StringUtil.cutUnwantedLeadingControlChars(line).trim();
			if (line.length() == 0 || line.startsWith(COMMENT_PREFIX)) {
				continue;
			}
			
			if (metaInfoParser.hasCorrectPrefix(line)) {
				parseMetaInfoLine(lineCounter, line);
			} else if (attributeDescriptorParser.hasCorrectPrefix(line)) {
				parseAttributeLine(lineCounter, line);
			} else if (classDescriptorParser.hasCorrectPrefix(line)) {
				parseClassLine(lineCounter, line);
			} else if (modelNameParser.hasCorrectPrefix(line)) {
				parseModelLine(lineCounter, line);
			} else if (variableParser.hasCorrectPrefix(line)) {
				parseVariableLine(lineCounter, line);
			} else {
				errorList.add(INVALID_INFORMATION.trim() + " in line " + lineCounter + "!");
			}

		}
		
		checkForErrors();
		
		buildUpModel.setVariables(variableMap);
		return buildUpModel;
	}
	
	private void parseVariableLine(final int lineCounter, final String line) {
		try
		{
			final String replacedLine = doVariableReplacement(line, lineCounter);
			final Annotation variableData = variableParser.parse(replacedLine);
			final String placeholder = VARIABLE_START_INDICATOR + variableData.getName() + VARIABLE_END_INDICATOR;
			variableMap.put(placeholder, variableData.getAdditionalInfo());
		} catch (MOGLiPluginException e)
		{
			errorList.add("Problem in line " + lineCounter + ": " + e.getMessage());
			return;
		}
	}

	private void parseMetaInfoLine(final int lineCounter, final String line) {
		try {
			final BuildUpMetaInfo buildUpMetaInfo;
			try {
				final String replacedLine = doVariableReplacement(line, lineCounter);
				buildUpMetaInfo = metaInfoParser.parse(replacedLine);
			} catch (Exception e) {
				errorList.add("Problem in line " + lineCounter + ": " + e.getMessage());
				return;
			}
			if (buildUpAttributeDescriptor != null) {
				buildUpMetaInfo.setLevel(MetaInfo.HierarchyLevel.Attribute);
				buildUpAttributeDescriptor.addMetaInfo(buildUpMetaInfo);
			} else if (buildUpClassDescriptor != null) {
				buildUpMetaInfo.setLevel(MetaInfo.HierarchyLevel.Class);
				buildUpClassDescriptor.addMetaInfo(buildUpMetaInfo);
			} else if (buildUpModel != null) {
				buildUpMetaInfo.setLevel(MetaInfo.HierarchyLevel.Model);
				buildUpModel.addMetaInfo(buildUpMetaInfo);
			} else {
				throw new MOGLiPluginException(UNRELATED_METAINFO);
			}
		} catch (MOGLiPluginException e) {
			errorList.add(e.getMessage() + " in line " + lineCounter + "!");
		}
	}

	private String doVariableReplacement(String line, final int lineCounter) {
		final Set<String> keySet = variableMap.keySet();
		for (final String key : keySet)
		{
			line = line.replace(key, variableMap.get(key));
		}
		
		verifyReplacement(line, lineCounter);
		return line;
	}

	private void verifyReplacement(String line, final int lineCounter) {
		final int pos1 = line.indexOf(VARIABLE_START_INDICATOR);
		final int pos2 = line.indexOf(VARIABLE_END_INDICATOR);
		if (pos1 > -1 && pos2 > 0 && pos1 < pos2) {
			final String variable = line.substring(pos1 + VARIABLE_START_INDICATOR.length(), pos2);
			errorList.add(UNKOWN_VARIABLE + VARIABLE_START_INDICATOR + variable + VARIABLE_END_INDICATOR 
					      + " in line " + lineCounter + "!");			
		}
	}

	private void parseAttributeLine(final int lineCounter, final String line) {
		try {
			final String replacedLine = doVariableReplacement(line, lineCounter);
			buildUpAttributeDescriptor = attributeDescriptorParser.parse(replacedLine);
			if (buildUpClassDescriptor == null) {
				throw new MOGLiPluginException(ATTRIBUTE_WITHOUT_CLASS);
			} else {
				if (buildUpClassDescriptor.hasAttributeDescriptorAreadyInList(buildUpAttributeDescriptor.getName())) {
					errorList.add(TextConstants.DUPLICATE_ATTRIBUTE_NAME + " in line " + lineCounter + ".");
				} else {
					buildUpClassDescriptor.addAttributeDescriptor(buildUpAttributeDescriptor);
				}
			}
		} catch (MOGLiPluginException e) {
			buildUpAttributeDescriptor = null;
			errorList.add(e.getMessage() + " in line " + lineCounter + "!");
		}
	}

	private void parseClassLine(final int lineCounter, final String line) {
		if (buildUpModel == null) {
			errorList.add(MODEL_IDENTIFIER_NOT_FOUND);
			buildUpModel = new BuildUpModel("DefaultModelName");
		}
		try {
			final String replacedLine = doVariableReplacement(line, lineCounter);
			buildUpClassDescriptor = classDescriptorParser.parse(replacedLine);
			buildUpAttributeDescriptor = null;
			if (buildUpModel.hasClassDescriptorAreadyInList(buildUpClassDescriptor.getFullyQualifiedName())) {
				errorList.add(TextConstants.DUPLICATE_CLASS_NAME + " in line " + lineCounter + ".");
			} else {
				buildUpModel.addClassDescriptor(buildUpClassDescriptor);
			}
		} catch (IllegalArgumentException e) {
			errorList.add(TextConstants.MISSING_NAME + " in line " + lineCounter + ": " + e.getMessage());
		} catch (Exception e) {
			buildUpClassDescriptor = null;
			errorList.add(e.getMessage() + " in line " + lineCounter + "!");
		}
	}

	private void parseModelLine(final int lineCounter, final String line) {
		final String replacedLine = doVariableReplacement(line, lineCounter);
		final String modelName = modelNameParser.parse(replacedLine);
		if (buildUpModel != null) {
			errorList.add(DUPLICATE_MODEL_IDENTIFIER + " in line " + lineCounter + "!");
		} else {
			try {
				buildUpModel = new BuildUpModel(modelName);
			} catch (IllegalArgumentException e) {
				errorList.add(TextConstants.MISSING_NAME + " in line " + lineCounter + ": " + e.getMessage());
			}
		}
	}

	private void checkForErrors() throws ModelParserException {
		if (buildUpModel == null) {
			errorList.add(MODEL_IDENTIFIER_NOT_FOUND);
		} 
		
		if (errorList.size() > 0) {
			throw new ModelParserException(errorList);
		}
	}
}
