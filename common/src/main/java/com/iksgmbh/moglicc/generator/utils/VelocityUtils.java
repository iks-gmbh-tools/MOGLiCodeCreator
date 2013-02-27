package com.iksgmbh.moglicc.generator.utils;

import java.nio.charset.Charset;

import com.iksgmbh.moglicc.core.Logger;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData;

public class VelocityUtils {

	private static final String STANDARD_OUTPUT_ENCODING_FORMAT = "UTF-8";

	public static String getOutputEncodingFormat(final VelocityGeneratorResultData resultData,
			                                     final Logger logger) {
		if (resultData.getOutputEncodingFormat() == null) {
			return STANDARD_OUTPUT_ENCODING_FORMAT;
		}

		try {
			Charset.forName(resultData.getOutputEncodingFormat());
		} catch (Exception e) {
			logger.logWarning("Invalid OutputEncodingFormat: " + resultData.getOutputEncodingFormat());
			return STANDARD_OUTPUT_ENCODING_FORMAT;
		}

		return resultData.getOutputEncodingFormat();
	}

}
