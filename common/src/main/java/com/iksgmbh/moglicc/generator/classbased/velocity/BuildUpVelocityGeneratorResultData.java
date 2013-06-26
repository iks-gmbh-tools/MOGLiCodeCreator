package com.iksgmbh.moglicc.generator.classbased.velocity;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.data.GeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoSupport;
import com.iksgmbh.utils.FileUtil;

/**
 * Object to build a data structure with information needed to create a result file
 *
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class BuildUpVelocityGeneratorResultData extends BuildUpGeneratorResultData
                                                implements VelocityGeneratorResultData 
{

	public static final String META_INFO_NOT_FOUND = "MetaInfo unkown to the model: ";

	private boolean existingTargetPreserved = false;  // default

	public BuildUpVelocityGeneratorResultData(final GeneratorResultData generatorResultData) {
		final BuildUpGeneratorResultData buildUpGeneratorResultData = (BuildUpGeneratorResultData) generatorResultData;
		propertyMap = buildUpGeneratorResultData.getPropertyMap();
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
	public boolean skipGeneration() {
		final String value = getProperty(KnownGeneratorPropertyNames.SkipGeneration.name());
		return doesStringRepresentBooleanTrue(value) || doesStringRepresentBooleanNotFalse(value);
	}

	@Override
	public String getNameOfValidModel() {
		return getProperty(KnownGeneratorPropertyNames.NameOfValidModel.name());
	}

	@Override
	public String getOutputEncodingFormat() {
		return getProperty(KnownGeneratorPropertyNames.OutputEncodingFormat.name());
	}

	@Override
	public String getGeneratedContent() {
		return generatedContent;
	}

	@Override
	public boolean isTargetToBeCreatedNewly() {
		final String value = getProperty(KnownGeneratorPropertyNames.CreateNew.name());
		return doesStringRepresentBooleanTrue(value);
	}

	private boolean doesStringRepresentBooleanTrue(final String value) {
		if (value == null) {
			return false;  // default
		}
		return "true".equals(value.toLowerCase().trim());
	}

	private boolean doesStringRepresentBooleanNotFalse(final String value) {
		if (value == null) {
			return false;  // default
		}
		return "not false".equals(value.toLowerCase().trim());
	}

	@Override
	public void validatePropertyKeys(final String artefact) throws MOGLiPluginException {
		if (getTargetFileName() == null) {
			validationErrors.add(NO_TARGET_FILE_NAME);
		}
		
		super.validatePropertyKeys(artefact);
	}
	
	@Override
	public void validatePropertyForMissingMetaInfoValues(final String artefact) throws MOGLiPluginException {
		checkForMissingMetaInfos();
		
		if (validationErrors.size() > 0) {
			throw new MOGLiPluginException(buildErrorString(artefact));
		}
	}

	
	private void checkForMissingMetaInfos() {
		final List<String> allPropertiesValues = getAllPropertiesValues();
		for (final String value : allPropertiesValues) {
			if (! isValueAvailable(value)) {
				validationErrors.add(META_INFO_NOT_FOUND + value);
			}
		}
	}

	private boolean isValueAvailable(final String metaInfoValue) {
		if (metaInfoValue == null) {
			return false;
		}
		return ! (metaInfoValue.startsWith(MetaInfoSupport.META_INFO_NOT_FOUND_START) 
				  && metaInfoValue.endsWith(MetaInfoSupport.META_INFO_NOT_FOUND_END));
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
			throw new MOGLiPluginException(TEXT_TARGET_DIR_NOT_FOUND + FileUtil.getSystemLineSeparator() 
					                      + targetDirAsFile.getAbsolutePath());
		}
		if (! targetDirAsFile.isDirectory()) {
			throw new MOGLiPluginException(TEXT_TARGET_DIR_IS_A_FILE + FileUtil.getSystemLineSeparator()
					                       + targetDirAsFile.getAbsolutePath());
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

	public boolean wasExistingTargetPreserved() {
		return existingTargetPreserved;
	}

	public void setExistingTargetPreserved(final boolean existingTargetPreserved) {
		this.existingTargetPreserved = existingTargetPreserved;
	}
}