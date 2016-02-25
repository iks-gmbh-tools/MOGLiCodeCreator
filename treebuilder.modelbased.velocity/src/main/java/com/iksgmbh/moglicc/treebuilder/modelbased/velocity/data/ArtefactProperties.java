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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.iksgmbh.helper.FolderContentBasedFileRenamer.RenamingData;
import com.iksgmbh.helper.FolderContentBasedTextFileLineReplacer.ReplacementData;
import com.iksgmbh.moglicc.MOGLiSystemConstants;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData.KnownGeneratorPropertyNames;
import com.iksgmbh.moglicc.treebuilder.modelbased.velocity.data.VelocityTreeBuilderResultData.KnownTreeBuilderPropertyNames;
import com.iksgmbh.moglicc.treebuilder.modelbased.velocity.helper.RenamingDataParser;
import com.iksgmbh.moglicc.treebuilder.modelbased.velocity.helper.ReplacementDataParser;
import com.iksgmbh.utils.StringUtil;

public class ArtefactProperties {

	private String artefact;

	// properties
	private String rootName;
	private String targetDir = MOGLiSystemConstants.APPLICATION_ROOT_IDENTIFIER;  // default value
	private String nameOfValidModel;
	private String excludeString;
	private String outputEncodingFormat;
	private boolean cleanTarget = false;
	private boolean preserveFiles = false;
	private List<ReplacementData> replacements = new ArrayList<ReplacementData>();
	private List<RenamingData> renamings = new ArrayList<RenamingData>();

	public ArtefactProperties(final VelocityTreeBuilderResultData velocityResult,
			                  final String artefact) throws MOGLiPluginException {
		this.artefact = artefact;
		this.rootName = artefact;  // set as default
		try {
			parseVelocityResult(velocityResult);
		} catch (IllegalArgumentException e) {
			throw new MOGLiPluginException("Error parsing properties for artefact '" + artefact + "': "
					                       + e.getMessage(), e);
		}
	}

	private void parseVelocityResult(final VelocityTreeBuilderResultData velocityResult) throws MOGLiPluginException
	{
		final List<String> keys = velocityResult.getAllKeys();
		for (final String key : keys) {
			final List<String> values = velocityResult.getAllPropertyValues(key);
			for (final String value : values) {
				parseProperty(key, value);
			}
		}
	}

	private void parseProperty(final String key, final String value) throws MOGLiPluginException
	{
		if (KnownGeneratorPropertyNames.TargetDir.name().equalsIgnoreCase(key)) {
			targetDir = value;
		} else if (KnownTreeBuilderPropertyNames.RootName.name().equalsIgnoreCase(key)) {
			rootName = value;
		} else if (KnownGeneratorPropertyNames.NameOfValidModel.name().equalsIgnoreCase(key)) {
			nameOfValidModel = value;
		} else if (KnownTreeBuilderPropertyNames.PreserveFiles.name().equalsIgnoreCase(key)) {
			preserveFiles = value.toLowerCase().equals("true");
		} else if (KnownGeneratorPropertyNames.CreateNew.name().equalsIgnoreCase(key)) {
			// do nothing - this flag is already handled - see: VelocityModelBasedTreeBuilderStarter.doYourJobFor
		} else if (KnownTreeBuilderPropertyNames.CleanTarget.name().equalsIgnoreCase(key)) {
			cleanTarget = value.toLowerCase().equals("true");
		} else if (KnownGeneratorPropertyNames.OutputEncodingFormat.name().equalsIgnoreCase(key)) {
			outputEncodingFormat = value;
		} else if (KnownTreeBuilderPropertyNames.Exclude.name().equalsIgnoreCase(key)) {
			excludeString = value;
		} else if (KnownTreeBuilderPropertyNames.ReplaceIn.name().equalsIgnoreCase(key)) {
			replacements.add(ReplacementDataParser.doYourJobFor(value));
		} else if (KnownTreeBuilderPropertyNames.RenameFile.name().equalsIgnoreCase(key)) {
			renamings.add(RenamingDataParser.doYourJobFor(value, false));
		} else if (KnownTreeBuilderPropertyNames.RenameDir.name().equalsIgnoreCase(key)) {
			renamings.add(RenamingDataParser.doYourJobFor(value, true));
		} else {
			throw new MOGLiPluginException("Unexpected property for artefact " + artefact + ": " + key);
		}
	}

	public String getRootName() {
		return rootName;
	}

	public boolean areExistingFilesToPreserve() {
		return preserveFiles;
	}

	public boolean isTargetToBeCleaned() {
		return cleanTarget;
	}

	public String getNameOfValidModel() {
		return nameOfValidModel;
	}

	public List<String> getExcludes() {
		final List<String> toReturn = new ArrayList<String>();
		if (excludeString == null) {
			return toReturn;
		}
		final String[] array = StringUtil.getListFromLineWithCommaSeparatedElements(excludeString);
		toReturn.addAll(Arrays.asList(array));
		return toReturn;
	}

	public List<ReplacementData> getReplacements() {
		return replacements;
	}

	public String getTargetDir() {
		return targetDir;
	}

	public String getOutputEncodingFormat() {
		return outputEncodingFormat;
	}

	public List<RenamingData> getFileRenamings() {
		return renamings;
	}
}