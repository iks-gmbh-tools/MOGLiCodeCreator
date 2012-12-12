package com.iksgmbh.moglicc.generator.classbased.velocity;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.data.GeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;

/**
 * Object to build a data structure with information needed to create a result file
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class BuildUpVelocityGeneratorResultData extends BuildUpGeneratorResultData 
                                                implements VelocityGeneratorResultData {
		
	

	public BuildUpVelocityGeneratorResultData(final GeneratorResultData generatorResultData) {
		final BuildUpGeneratorResultData buildUpGeneratorResultData = (BuildUpGeneratorResultData) generatorResultData;
		properties = buildUpGeneratorResultData.getProperties();
		generatedContent = buildUpGeneratorResultData.getGeneratedContent();
	}
	
	@Override
	public String getTargetFileName() {
		return getProperty(KnownGeneratorPropertyNames.TargetFileName.name());
	}
	
	@Override
	public String getTargetDir() {
		return getProperty(KnownGeneratorPropertyNames.TargetDir.name());
	}
	
	@Override
	public String getNameOfValidModel() {
		return getProperty(KnownGeneratorPropertyNames.NameOfValidModel.name());
	}

	@Override
	public String getGeneratedContent() {
		return generatedContent;
	}
	
	@Override
	public boolean isTargetToBeCreatedNewly() {
		final String value = getProperty(KnownGeneratorPropertyNames.CreateNew.name());
		if (value == null) {
			return false;
		}
		return "true".equals(value.toLowerCase().trim());
	}
	
	@Override
	public void validate() throws MOGLiPluginException {
		if (getTargetFileName() == null) {
			validationErrors.add(NO_TARGET_FILE_NAME);
		}
		super.validate();
	}

	@Override
	public File getTargetDirAsFile(final String applicationRootDir, final String pathAdaption) 
	            throws MOGLiPluginException {
		String targetDir = getTargetDir();
		if (targetDir == null) {
			return null;
		}
		targetDir = replacePackageString(targetDir);
		targetDir = addParentDirAsString(applicationRootDir, pathAdaption, targetDir);
		final File targetDirAsFile = new File(targetDir);
		checkTargetDir(targetDirAsFile);
		return targetDirAsFile;
	}
	
	private void checkTargetDir(final File targetDirAsFile) throws MOGLiPluginException {
		if (isTargetToBeCreatedNewly()) {
			targetDirAsFile.mkdirs();
			return;
		}
		if (! targetDirAsFile.exists()) {
			throw new MOGLiPluginException(TEXT_TARGET_DIR_NOT_FOUND + "\n" + targetDirAsFile.getAbsolutePath());
		}
		if (! targetDirAsFile.isDirectory()) {
			throw new MOGLiPluginException(TEXT_TARGET_DIR_IS_A_FILE + "\n" + targetDirAsFile.getAbsolutePath());
		}		
	}
	
	@Override
	public File getTargetFile(final String applicationRootDir, final String pathAdaptation) throws MOGLiPluginException {
		final File targetDirAsFile = getTargetDirAsFile(applicationRootDir, pathAdaptation);
		if (targetDirAsFile == null) {
			return null;
		}
		final File toReturn = new File(targetDirAsFile, getTargetFileName());
		checkTargetFile(toReturn);
		return toReturn;
	}	

	protected void checkTargetFile(final File targetFile) throws MOGLiPluginException {
		if (isTargetToBeCreatedNewly()) {
			return;
		}
		if (targetFile.exists() && ! targetFile.isFile()) {
			throw new MOGLiPluginException(TEXT_TARGET_FILE_IS_A_DIRECTORY + "\n" + targetFile.getAbsolutePath());
		}		
	}


	private String replacePackageString(final String dir) throws MOGLiPluginException {
		String packageString = "";
		if (dir.endsWith(PACKAGE_IDENTIFIER)) {
			packageString = searchPackageInGeneratedContent().replace('.', '/');
			if (StringUtils.isBlank(packageString)) {
				throw new MOGLiPluginException(TEXT_PACKAGE_NOT_FOUND);
			}
		}
		String targetDir = getTargetDir().replace(PACKAGE_IDENTIFIER, packageString);
		return targetDir;
	}

	private String addParentDirAsString(final String applicationRootDir,
			final String pathAdaption, String targetDir) {
		if (targetDir.startsWith(ROOT_IDENTIFIER)) {
			if (applicationRootDir == null) {
				targetDir = pathAdaption + targetDir.substring(ROOT_IDENTIFIER.length() + 1);
			} else {
				targetDir = targetDir.replace(ROOT_IDENTIFIER, applicationRootDir);
			}
		} else {
			if (pathAdaption != null) {				
				targetDir = pathAdaption + targetDir;
			}
		}
		return targetDir;
	}

	private String searchPackageInGeneratedContent() {
		return searchTextInGeneratedContentBetween(PACKAGE, ";");
	}

}