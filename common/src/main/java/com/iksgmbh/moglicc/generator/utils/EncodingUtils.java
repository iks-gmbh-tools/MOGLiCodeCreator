package com.iksgmbh.moglicc.generator.utils;

import java.nio.charset.Charset;

import com.iksgmbh.moglicc.core.Logger;

public class EncodingUtils {

	private static final String STANDARD_OUTPUT_ENCODING_FORMAT = "UTF-8";

	public static String getValidOutputEncodingFormat(final String outputEncodingFormat,
			                                     final Logger logger) {
		if (outputEncodingFormat == null) {
			return STANDARD_OUTPUT_ENCODING_FORMAT;
		}

		try {
			Charset.forName(outputEncodingFormat);
		} catch (Exception e) {
			logger.logWarning("Invalid OutputEncodingFormat: " + outputEncodingFormat);
			return STANDARD_OUTPUT_ENCODING_FORMAT;
		}

		return outputEncodingFormat;
	}

}
