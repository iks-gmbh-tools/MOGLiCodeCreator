package com.iksgmbh.moglicc.provider.model.standard;

public class TextConstants {
	
	
	public static final String MODEL_IDENTIFIER_NOT_FOUND = "MODEL_IDENTIFIER '" + MetaModelConstants.MODEL_IDENTIFIER + "' not found!";
	public static final String DUPLICATE_MODEL_IDENTIFIER = "Duplicate MODEL_IDENTIFIER '" + MetaModelConstants.MODEL_IDENTIFIER + "' found ";
	public static final String EMPTY_CLASS_LIST = "No valid '" + MetaModelConstants.CLASS_IDENTIFIER + "' entry found ";
	public static final String INVALID_INFORMATION = "Invalid information ";
	public static final String ATTRIBUTE_WITHOUT_CLASS = "Attribute without class ";
	public static final String UNRELATED_METAINFO = "Unrelated MetaInfo";
	public static final String MISSING_NAME = "Missing name ";
	public static final String MISSING_VALUE = "Missing value ";
	public static final String DUPLICATE_CLASS_NAME = "Identical class name";
	public static final String DUPLICATE_ATTRIBUTE_NAME = "Identical attribute name";
	public static final String MODELFILE_PROPERTY = "modelfile";
	
	public static final String TEXT_PROPERTIES_FILE_NOT_LOADED = StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE + " not loaded: ";
	public static final String TEXT_NO_MODELFILE_DEFINED_IN_PROPERTIES_FILE = StandardModelProviderStarter.PLUGIN_PROPERTIES_FILE 
	                                                                          + " does not contain a '" + MODELFILE_PROPERTY + "' entry.";
	public static final String TEXT_NO_MODELFILE_FOUND = "No modelfile found";
	public static final String TEXT_NO_MODEL_FILE_LOADED = "No model file loaded!";
	public static final String TEXT_PARSE_ERROR_FOUND = "Error parsing model file:\n";
	public static final String TEXT_METAINFO_VALIDATION_ERROR_OCCURRED = "Error validating MetaInfos. For more information see ";
	
	public static final String TEXT_MODEL_NOT_EXISTS = "Model file does not exist";
}
