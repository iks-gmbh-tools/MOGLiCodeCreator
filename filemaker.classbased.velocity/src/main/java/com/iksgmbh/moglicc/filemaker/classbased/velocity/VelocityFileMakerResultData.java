package com.iksgmbh.moglicc.filemaker.classbased.velocity;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.GeneratorResultData;
import com.iksgmbh.moglicc.generator.classbased.velocity.BuildUpVelocityGeneratorResultData;

/**
 * Object to build a data structure with information needed to create a new target file.
 *
 * @author Reik Oberrath
 * @since 1.4.0
 */
public class VelocityFileMakerResultData extends BuildUpVelocityGeneratorResultData {

	public VelocityFileMakerResultData(final GeneratorResultData generatorResultData) {
		super(generatorResultData);
	}
	
	@Override
	public void validatePropertyKeys(final String artefact) throws MOGLiPluginException 
	{
		if (getTargetFileName() == null) {
			validationErrors.add(NO_TARGET_FILE_NAME);
		}
		
		super.validatePropertyKeys(artefact);
	}
	
}