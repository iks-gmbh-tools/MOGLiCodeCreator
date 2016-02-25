/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iksgmbh.moglicc.generator.classbased.velocity;

import java.io.File;
import java.util.List;

import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.GeneratorResultData;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfoSupport;
import com.iksgmbh.utils.FileUtil;

/**
 * Object to build a data structure with information needed to create a result file.
 * This class adds functionality to the {@link BuildUpGeneratorResultData} class
 * that is common to all velocity generators and is used by Velocity Engine Provider.
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
	public boolean isGenerationToSkip() {
		final String value = getProperty(KnownGeneratorPropertyNames.SkipGeneration.name());
		return isStringRepresentBooleanTrue(value) 
			   || isStringRepresentBooleanNotFalse(value)
			   || isTrueComparisonExpression(value);
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
	public String getNumberSignReplacement() {
		return getProperty(KnownGeneratorPropertyNames.ReplaceToNumberSign.name());
	}
	
	@Override
	public boolean isTargetToBeCreatedNewly() {
		final String value = getProperty(KnownGeneratorPropertyNames.CreateNew.name());
		return isStringRepresentBooleanTrue(value);
	}

	private boolean isStringRepresentBooleanTrue(final String value) {
		if (value == null) {
			return false;  // default
		}
		return "true".equals(value.toLowerCase().trim());
	}

	private boolean isStringRepresentBooleanNotFalse(final String value) {
		if (value == null) {
			return false;  // default
		}
		return "not false".equals(value.toLowerCase().trim());
	}
	
	private boolean isTrueComparisonExpression(final String value) 
	{
		if (value == null) {
			return false;  // default
		}
		
		if (value.contains("=="))
		{
			final String[] result = value.split("==");
			final String expression1 = result[0].trim();
			final String expression2 = result[1].trim();
			return expression1.equals(expression2);
		}

		if (value.contains("!="))
		{
			final String[] result = value.split("!=");
			final String expression1 = result[0].trim();
			final String expression2 = result[1].trim();
			return ! expression1.equals(expression2);
		}
		
		return false;
	}

	

	@Override
	public void validatePropertyKeys(final String artefact) throws MOGLiPluginException {
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
	public File getTargetDirAsFile(final String applicationRootDir, 
			                       final String pathAdaption)
			                       throws MOGLiPluginException 
	{
		String targetDir = getTargetDir();
		if (targetDir == null) {
			return null;
		}
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
	public File getTargetFile(final String applicationRootDir, final String pathAdaptation) throws MOGLiPluginException 
	{
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

	public boolean wasExistingTargetPreserved() {
		return existingTargetPreserved;
	}

	public void setExistingTargetPreserved(final boolean existingTargetPreserved) {
		this.existingTargetPreserved = existingTargetPreserved;
	}
		
}