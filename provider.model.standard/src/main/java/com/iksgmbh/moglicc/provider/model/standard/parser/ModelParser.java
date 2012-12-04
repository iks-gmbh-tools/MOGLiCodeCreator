package com.iksgmbh.moglicc.provider.model.standard.parser;

import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.ATTRIBUTE_WITHOUT_CLASS;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.DUPLICATE_MODEL_IDENTIFIER;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.EMPTY_CLASS_LIST;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.INVALID_INFORMATION;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.MODEL_IDENTIFIER_NOT_FOUND;
import static com.iksgmbh.moglicc.provider.model.standard.TextConstants.UNRELATED_METAINFO;

import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.TextConstants;
import com.iksgmbh.moglicc.provider.model.standard.exceptions.ModelParserException;
import com.iksgmbh.moglicc.provider.model.standard.impl.BuildUpAttributeDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.impl.BuildUpClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.impl.BuildUpMetaInfo;
import com.iksgmbh.moglicc.provider.model.standard.impl.BuildUpModel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;

public class ModelParser {
	
	private static final String COMMENT_PREFIX = "#";

	private final List<String> errorList = new ArrayList<String>();
	
	private final ModelNameParser modelNameParser = new ModelNameParser();
	private final ClassDescriptorParser classDescriptorParser = new ClassDescriptorParser();
	private final AttributeDescriptorParser attributeDescriptorParser = new AttributeDescriptorParser();
	private final MetaInfoParser metaInfoParser = new MetaInfoParser();
	
	private BuildUpModel buildUpModel;
	private BuildUpClassDescriptor buildUpClassDescriptor;
	private BuildUpAttributeDescriptor buildUpAttributeDescriptor;


	public static BuildUpModel doYourJob(final List<String> fileContentAsList) throws ModelParserException {
		final ModelParser modelParser = new ModelParser();
		return modelParser.parse(fileContentAsList);
	}

	BuildUpModel parse(List<String> fileContentAsList) throws ModelParserException {
		int lineCounter = 0;
		for (String line : fileContentAsList) {
			lineCounter++;
			line = line.trim();
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
			} else {
				errorList.add(INVALID_INFORMATION + " in line " + lineCounter + "!");
			}

		}
		checkForErrors();
		return buildUpModel;
	}

	private void parseMetaInfoLine(final int lineCounter, final String line) {
		try {
			final BuildUpMetaInfo buildUpMetaInfo;
			try {
				buildUpMetaInfo = metaInfoParser.parse(line);
			} catch (Exception e) {
				errorList.add(TextConstants.MISSING_NAME + " in line " + lineCounter + ": " + e.getMessage());
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

	private void parseAttributeLine(final int lineCounter, final String line) {
		try {
			buildUpAttributeDescriptor = attributeDescriptorParser.parse(line);
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
			buildUpClassDescriptor = classDescriptorParser.parse(line);
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
		final String modelName = modelNameParser.parse(line);
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
		} else {
			if (buildUpModel.getClassDescriptorList().isEmpty()) {
				errorList.add(EMPTY_CLASS_LIST);
			}
		}
		if (errorList.size() > 0) {
			throw new ModelParserException(errorList);
		}
	}
}
