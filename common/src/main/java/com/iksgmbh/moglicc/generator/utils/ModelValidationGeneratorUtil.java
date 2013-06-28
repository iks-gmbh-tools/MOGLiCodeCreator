package com.iksgmbh.moglicc.generator.utils;

import java.util.List;

import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData;

public class ModelValidationGeneratorUtil {

	public static boolean validateModel(final VelocityGeneratorResultData resultData,
			                            final String modelName) throws MOGLiPluginException
	{
		final String key = VelocityGeneratorResultData.KnownGeneratorPropertyNames.NameOfValidModel.name();
		final List<String> namesOfValidModels = resultData.getAllPropertyValues(key);

		if (namesOfValidModels == null || namesOfValidModels.isEmpty()) {
			return true; // ok, template has to be applied to all models
		}

		for (final String nameOfValidModel : namesOfValidModels) {
			if (nameOfValidModel.equals(modelName)) {
				return true; // ok, template has to be applied to this model
			}
		}

		return false;
	}

}
