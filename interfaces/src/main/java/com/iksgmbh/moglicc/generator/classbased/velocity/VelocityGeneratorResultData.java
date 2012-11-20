package com.iksgmbh.moglicc.generator.classbased.velocity;

import java.io.File;

import com.iksgmbh.moglicc.data.GeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MogliPluginException;

/**
 * return Object for {@code VelocityEngineProviderStarter.startEngineWithClassList()}
 * 
 * @author Reik Oberrath
 */
public interface VelocityGeneratorResultData extends GeneratorResultData {
	
	static final String PACKAGE = "package";
	static final String PACKAGE_IDENTIFIER = "<" + PACKAGE + ">";
	static final String ROOT_IDENTIFIER = "<applicationRootDir>";
	
	static final String NO_TARGET_FILE_NAME = "TargetFileName not defined.";
	static final String NO_TARGET_DIR = "TargetDir not defined.";
	public static final String TEXT_TARGET_DIR_NOT_FOUND = "Defined target directory does not exist";
	public static final String TEXT_TARGET_DIR_IS_A_FILE = "Defined target directory is no directory";
	public static final String TEXT_TARGET_FILE_NOT_FOUND = "Defined target file does not exist";
	public static final String TEXT_TARGET_FILE_IS_A_DIRECTORY = "Defined target file is no file";
	public static final String TEXT_PACKAGE_NOT_FOUND = "'" + PACKAGE + "' not found in generated content!";
	
	enum KnownGeneratorPropertyNames { TargetDir, TargetFileName, CreateNew, NameOfValidModel };
	
	String getTargetDir();
	
	String getTargetFileName();
	
	/**
	 * Provides information whether target file has to be overwritten
	 * @return false if target file must remain untouched
	 */
	boolean isTargetToBeCreatedNewly();

	/**
	 * For null the underlying template is valid -> per default a template is applied to all models.
	 * For invalid templates this result will be ignored!
	 * @return name of model for which the underlying template is valid
	 */
	String getNameOfValidModel();

	/**
	 * Replaces dynamically PACKAGE_IDENTIFIER and ROOT_IDENTIFIER with real values
	 * and checks that it exists
	 * @param applicationRootDir
	 * @param pathAdaption neccassary for test purpose 
	 * @return null if not defined in main template  
	 * @throws MogliPluginException
	 */
	File getTargetDirAsFile(String applicationRootDir, String pathAdaption) throws MogliPluginException;
	
	/**
	 * Uses {@code getTargetDirAsFile} to create target file object and checks that it exists
	 * @param applicationRootDir
	 * @param pathAdaption neccassary for test purpose 
	 * @return null if not defined in main template  
	 * @throws MogliPluginException
	 */
	File getTargetFile(String applicationRootDir, String pathAdaptation) throws MogliPluginException;

	/**
	 * @throws MogliPluginException if conflicting or missing settings exist
	 */
	void validate() throws MogliPluginException;

}
