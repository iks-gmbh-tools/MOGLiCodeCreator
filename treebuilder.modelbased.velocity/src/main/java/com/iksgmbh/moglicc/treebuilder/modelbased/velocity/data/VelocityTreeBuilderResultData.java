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
package com.iksgmbh.moglicc.treebuilder.modelbased.velocity.data;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.GeneratorResultData;
import com.iksgmbh.moglicc.generator.classbased.velocity.BuildUpVelocityGeneratorResultData;
import com.iksgmbh.utils.StringUtil;

/**
 * Object to build a data structure with information needed to build a folder tree.
 *
 * @author Reik Oberrath
 * @since 1.3.0
 */
public class VelocityTreeBuilderResultData extends BuildUpVelocityGeneratorResultData
{
	public enum KnownTreeBuilderPropertyNames { RootName, Exclude, ReplaceIn, RenameFile, RenameDir, CleanTarget, PreserveFiles };
	
	public static final String CREATE_NEW_MIXED_CONFIGURATION = "CleanTarget property and PreserverFiles property cannot be true simultaneously.";

	public VelocityTreeBuilderResultData(final GeneratorResultData generatorResultData) {
		super(generatorResultData);
	}

	public void checkTargetDir(final File targetDir) throws MOGLiPluginException 
	{
		if (targetDir.isFile()) {
			throw new MOGLiPluginException(TEXT_TARGET_DIR_IS_A_FILE + "\n" + targetDir.getAbsolutePath());
		}
		
		if (isTargetToBeCreatedNewly()) {
			return;
		}
		
		if ( ! targetDir.exists() ) {
			throw new MOGLiPluginException(TEXT_TARGET_DIR_NOT_FOUND + "\n" + targetDir.getAbsolutePath());
		}
	}


	@Override
	public void validatePropertyKeys(final String artefact) throws MOGLiPluginException {
		if ("true".equals(getCleanTarget()) && "true".equals(getPreserveFiles())) {
			validationErrors.add(CREATE_NEW_MIXED_CONFIGURATION);
		}

		super.validatePropertyKeys(artefact);
	}

	public String getRootName() {
		return getProperty(KnownTreeBuilderPropertyNames.RootName.name());
	}

	public String getCleanTarget() {
		return getProperty(KnownTreeBuilderPropertyNames.CleanTarget.name());
	}

	public List<String> getExludes() {
		final String line = getProperty(KnownTreeBuilderPropertyNames.Exclude.name());
		final String[] elements = StringUtil.getListFromLineWithCommaSeparatedElements(line);
		return Arrays.asList(elements);
	}

	public String getPreserveFiles() {
		return getProperty(KnownTreeBuilderPropertyNames.PreserveFiles.name());
	}
	
	public String getReplaceIn() {
		return getProperty(KnownTreeBuilderPropertyNames.ReplaceIn.name());
	}

	public String getRenameFile() {
		return getProperty(KnownTreeBuilderPropertyNames.RenameFile.name());
	}

	public String getRenameDir() {
		return getProperty(KnownTreeBuilderPropertyNames.RenameDir.name());
	}

	@Override
	public String getGeneratedContent() {
		return ""; // for tree builder no content exists!
	}

}