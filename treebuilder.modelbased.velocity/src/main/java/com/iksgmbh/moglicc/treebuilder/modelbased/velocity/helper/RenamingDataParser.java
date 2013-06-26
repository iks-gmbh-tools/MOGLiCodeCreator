package com.iksgmbh.moglicc.treebuilder.modelbased.velocity.helper;

import com.iksgmbh.data.Annotation;
import com.iksgmbh.helper.AnnotationParser;
import com.iksgmbh.helper.FolderContentBasedFileRenamer.RenamingData;
import com.iksgmbh.moglicc.treebuilder.modelbased.velocity.data.VelocityTreeBuilderResultData.KnownTreeBuilderPropertyNames;
import com.iksgmbh.utils.FileUtil;

public class RenamingDataParser extends AnnotationParser {

	private RenamingDataParser(final String annotationPrefixIdentificator,
			                     final String partBraceIdentifier, 
			                     final String commentIdentificator) {
		super(annotationPrefixIdentificator, partBraceIdentifier, commentIdentificator);
	}
	
	private RenamingDataParser() {
		this(AnnotationParser.DEFAULT_ANNOTATION_PREFIX_IDENTIFICATOR, 
		     AnnotationParser.DEFAULT_PART_BRACE_IDENTIFIER, 
		     AnnotationParser.DEFAULT_COMMENT_IDENTIFICATOR);
	}

	private static RenamingDataParser instance;

	public static RenamingData doYourJobFor(final String dataAsString, final boolean isDir) {
		if (instance == null) {
			instance = new RenamingDataParser();
		}
		return instance.parse(buildParserLine(dataAsString, isDir), isDir); 
	}

	private RenamingData parse(final String line, final boolean isDir) 
	{
		final Annotation annotation = super.doYourJob(line);
		final String additionalInfo = annotation.getAdditionalInfo().trim();
		final int pos = additionalInfo.indexOf(" ");
		if (pos == -1) {
			throw new IllegalArgumentException("Missing name2 in line: " 
                                          + FileUtil.getSystemLineSeparator() + line);
		}
		final String name1 = additionalInfo.substring(0, pos).trim();
		final String name2 = additionalInfo.substring(pos).trim();
		if (name2.contains(" ")) {
			throw new IllegalArgumentException("Name2 must not contain spaces in line: " 
		                                    + FileUtil.getSystemLineSeparator() + line);
		}
		return new RenamingData(name1, name2, isDir);
			
	}

	/**
	 * The original property name was removed by the VelocityEngineProvider.
	 * To use the Annotation parser to build Replacement Data objects,
	 * the original property name is added here.
	 * @param isDir 
	 */
	private static String buildParserLine(final String line, boolean isDir) {
		if (isDir) {
			return "@" + KnownTreeBuilderPropertyNames.RenameDir.name() + " " + line;
		}
		return "@" + KnownTreeBuilderPropertyNames.RenameFile.name() + " " + line;
	}

}
