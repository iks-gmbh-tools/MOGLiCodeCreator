package com.iksgmbh.moglicc.inserter.modelbased.velocity;

import java.io.File;

import com.iksgmbh.moglicc.data.GeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.BuildUpVelocityGeneratorResultData;

/**
 * Object to build a data structure with information needed to create or 
 * insert content into a target file.
 * 
 * @author Reik Oberrath
 * @since 1.0.0
 */
public class BuildUpVelocityInserterResultData extends BuildUpVelocityGeneratorResultData 
                                               implements VelocityInserterResultData {
	
	public static final String MISSING_REPLACE_CONFIGURATION = "Either annotation '" + 
								   VelocityInserterResultData.KnownInserterPropertyNames.ReplaceStart.name()
	                               + "' or '" + 
	                               VelocityInserterResultData.KnownInserterPropertyNames.ReplaceEnd.name()
	                               + "' is missing in template file.";

	public static final String INVALID_INSERT_CONFIGURATION = "Annotations '" + 
								   VelocityInserterResultData.KnownInserterPropertyNames.InsertBelow.name()
                                   + "' and '" + 
                                   VelocityInserterResultData.KnownInserterPropertyNames.InsertAbove.name()
                                   + "' cannot be used simultanuously.";
	
	public static final String INVALID_MIXED_CONFIGURATION = "Insert and replace annotations '" 
	   														  + "' cannot be used simultanuously.";
	
	public static final String CREATE_NEW_MIXED_CONFIGURATION = "CreateNew annotation with value 'true'" 
			                                                      + " cannot be used with Insert or replace annotations.";


	public BuildUpVelocityInserterResultData(final GeneratorResultData generatorResultData) {
		super(generatorResultData);
	}
	
	@Override
	public String getReplaceStartIndicator() {
		return getProperty(KnownInserterPropertyNames.ReplaceStart.name());
	}

	@Override
	public String getReplaceEndIndicator() {
		return getProperty(KnownInserterPropertyNames.ReplaceEnd.name());
	}

	@Override
	public String getInsertBelowIndicator() {
		return getProperty(KnownInserterPropertyNames.InsertBelow.name());
	}

	@Override
	public String getInsertAboveIndicator() {
		return getProperty(KnownInserterPropertyNames.InsertAbove.name());
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
	
	@Override
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
	public void validatePropertyKeys(final String artefact) throws MOGLiPluginException {
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
		if (getTargetDir() == null) {
			validationErrors.add(NO_TARGET_DIR);
		}
		super.validatePropertyKeys(artefact);
	}

}