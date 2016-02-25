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
package com.iksgmbh.moglicc.lineinserter.modelbased.velocity;

import java.io.File;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.GeneratorResultData;
import com.iksgmbh.moglicc.generator.classbased.velocity.BuildUpVelocityGeneratorResultData;

/**
 * Object to build a data structure with information needed to create or
 * insert content into a target file.
 *
 * @author Reik Oberrath
 * @since 1.3.0
 */
public class VelocityLineInserterResultData extends BuildUpVelocityGeneratorResultData {

	enum KnownInserterPropertyNames { ReplaceStart, ReplaceEnd, InsertBelow, InsertAbove };
	
	public static final String MISSING_REPLACE_CONFIGURATION = "Either property '" +
								   KnownInserterPropertyNames.ReplaceStart.name()
	                               + "' or '" +
	                               KnownInserterPropertyNames.ReplaceEnd.name()
	                               + "' is missing in template file.";

	public static final String INVALID_INSERT_CONFIGURATION = "Properties '" +
								   KnownInserterPropertyNames.InsertBelow.name()
                                   + "' and '" +
                                   KnownInserterPropertyNames.InsertAbove.name()
                                   + "' cannot be used simultanuously.";

	public static final String INVALID_MIXED_CONFIGURATION = "Insert and replace properties '"
	   														  + "' cannot be used simultanuously.";

	public static final String CREATE_NEW_MIXED_CONFIGURATION = "CreateNew property with value 'true'"
			                                                      + " cannot be used with Insert or Replace properties.";


	public VelocityLineInserterResultData(final GeneratorResultData generatorResultData) {
		super(generatorResultData);
	}

	public String getReplaceStartIndicator() {
		return removeQuotes(getProperty(KnownInserterPropertyNames.ReplaceStart.name()));
	}

	public String getReplaceEndIndicator() {
		return removeQuotes(getProperty(KnownInserterPropertyNames.ReplaceEnd.name()));
	}

	public String getInsertBelowIndicator() {
		return removeQuotes(getProperty(KnownInserterPropertyNames.InsertBelow.name()));
	}

	public String getInsertAboveIndicator() {
		return removeQuotes(getProperty(KnownInserterPropertyNames.InsertAbove.name()));
	}
	
	private String removeQuotes(final String s) {
		if (s == null || s.length() <3)
		{
			return null;
		}
		if ( (s.startsWith("'") && s.endsWith("'"))
			 || 
			 (s.startsWith("\"") && s.endsWith("\"")))
		{
			return s.substring(1, s.length()-1);
		}
		return s;
	}

	@Override
	protected void checkTargetFile(final File targetFile) throws MOGLiPluginException {
		if (isTargetToBeCreatedNewly()) {
			return;
		}
		if (mustGeneratedContentBeMergedWithExistingTargetFile() && ! targetFile.exists()) {
			throw new MOGLiPluginException(TEXT_TARGET_FILE_NOT_FOUND + "\n" + targetFile.getAbsolutePath());
		}
		if (targetFile.exists() && ! targetFile.isFile()) {
			throw new MOGLiPluginException(TEXT_TARGET_FILE_IS_A_DIRECTORY + "\n" + targetFile.getAbsolutePath());
		}
	}

	public boolean mustGeneratedContentBeMergedWithExistingTargetFile() {
		return isReplaceIndicatorDefined() || isInsertIndicatorDefined();
	}


	private boolean isReplaceIndicatorDefined() {
		return getReplaceEndIndicator() != null || getReplaceStartIndicator() != null;
	}

	private boolean isInsertIndicatorDefined() {
		return getInsertAboveIndicator() != null || getInsertBelowIndicator() != null;
	}

	@Override
	public void validatePropertyKeys(final String artefact) throws MOGLiPluginException 
	{
		if (getTargetFileName() == null) {
			validationErrors.add(NO_TARGET_FILE_NAME);
		}
		if (getTargetDir() == null) {
			validationErrors.add(NO_TARGET_DIR);
		}
		
		if (getReplaceEndIndicator() == null && getReplaceStartIndicator() != null) {
			validationErrors.add(MISSING_REPLACE_CONFIGURATION);
		}
		if (getReplaceEndIndicator() != null && getReplaceStartIndicator() == null) {
			validationErrors.add(MISSING_REPLACE_CONFIGURATION);
		}
		if (getInsertBelowIndicator() != null && getInsertAboveIndicator() != null) {
			validationErrors.add(INVALID_INSERT_CONFIGURATION);
		}
		if (getReplaceEndIndicator() != null && getInsertAboveIndicator() != null) {
			validationErrors.add(INVALID_MIXED_CONFIGURATION);
		}
		if (getReplaceEndIndicator() != null && getInsertBelowIndicator() != null) {
			validationErrors.add(INVALID_MIXED_CONFIGURATION);
		}
		if (getReplaceEndIndicator() != null && isTargetToBeCreatedNewly()) {
			validationErrors.add(CREATE_NEW_MIXED_CONFIGURATION);
		}
		if (getInsertBelowIndicator() != null && isTargetToBeCreatedNewly()) {
			validationErrors.add(CREATE_NEW_MIXED_CONFIGURATION);
		}
		if (getInsertAboveIndicator() != null && isTargetToBeCreatedNewly()) {
			validationErrors.add(CREATE_NEW_MIXED_CONFIGURATION);
		}
		
		super.validatePropertyKeys(artefact);
	}

}