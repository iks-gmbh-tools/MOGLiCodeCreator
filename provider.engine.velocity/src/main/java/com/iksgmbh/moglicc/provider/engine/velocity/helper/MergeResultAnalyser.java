package com.iksgmbh.moglicc.provider.engine.velocity.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.data.Annotation;
import com.iksgmbh.helper.AnnotationParser;
import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.utils.StringUtil;

public class MergeResultAnalyser {

	public static final String IDENTIFICATOR_WHITESPACE_MARKER = "'";
	private static final AnnotationParser annotationParser = AnnotationParser.getInstance();
	private static final String commentSymbolLineIdentifier = annotationParser.getCommentIdentificator() +
			                                                  annotationParser.getCommentIdentificator() +
			                                                  annotationParser.getCommentIdentificator();

	private BuildUpGeneratorResultData velocityResultData;
	private String artefactType;
	private boolean headerEndReached = false;

	public static BuildUpGeneratorResultData doYourJob(final String mergeResult,
			                                           final String artefactType) throws MOGLiPluginException {
		final MergeResultAnalyser analyser = new MergeResultAnalyser(artefactType);
		try {
			return analyser.analyseGeneratorResult(mergeResult);
		} catch (IOException e) {
			throw new MOGLiPluginException(e);
		}
	}

	private MergeResultAnalyser(final String artefactType) {
		this.artefactType = artefactType;
	}

	private BuildUpGeneratorResultData analyseGeneratorResult(final String mergeResult)
	         throws IOException, MOGLiPluginException {
		velocityResultData = new BuildUpGeneratorResultData();
		final String resultFileContent = parseMergeResult(mergeResult);
		velocityResultData.setGeneratedContent(resultFileContent);
		return velocityResultData;
	}

	protected void removeTrailingEmptyLines(final List<String> newLines) {
		final List<Integer> list = new ArrayList<Integer>();
		for (int i = newLines.size()-1; i == 0; i--) {
			if (! StringUtils.isEmpty(newLines.get(i))) {
				break;
			} else {
				list.add(i);
			}
		}
		for (Integer index : list) {
			newLines.remove(index);
		}
	}

	protected String removeWhitespaceMarker(final String line) {
		if (line.startsWith(IDENTIFICATOR_WHITESPACE_MARKER)) {
			return line.substring(IDENTIFICATOR_WHITESPACE_MARKER.length());
		}
		return line;
	}

	/**
	 * extracts template properties via  annotations
	 * @param originalFileContent
	 * @return filecontent without annotation lines
	 * @throws MOGLiPluginException
	 */
	protected String parseMergeResult(final String originalFileContent) throws MOGLiPluginException {

//		The following implementation does not work on all machines.
//		In case of error FileUtil.getSystemLineSeparator() is \r\n, but originalFileContent uses \n.
//		Reason unkown. Probably a hidden line.separator property used by velocity.
//		final String[] oldLines = originalFileContent.split(FileUtil.getSystemLineSeparator());
		final String[] oldLines = originalFileContent.replaceAll("\r", "").split("\n");

		final List<String> newLines = new ArrayList<String>();
		for (int i = 0; i < oldLines.length; i++) {
			final String line = oldLines[i].trim();
			parseLine(newLines, line, i+1);
		}
		removeTrailingEmptyLines(newLines);
		return StringUtil.concat(newLines);
	}

	protected void parseLine(final List<String> newLines, final String line, final int lineNo) throws MOGLiPluginException {
		if (! headerEndReached && annotationParser.hasCorrectPrefix(line)) {
			final Annotation annotation = annotationParser.doYourJob(line);
			if (annotation.getName() == null) {
				throw new MOGLiPluginException("Header attribute '" + line + "' in line " + lineNo
						                        + " of main template of artefact '" + artefactType
						                        + "' is not parseble!");
			}
			if (annotation.getAdditionalInfo() == null) {
				throw new MOGLiPluginException("Header attribute '" + annotation.getName() + "' in line " + lineNo
						                       + " of main template of artefact '" + artefactType
						                       + "' needs additional information.");
			}
			velocityResultData.addProperty(annotation.getName(), annotation.getAdditionalInfo());
		} else if (annotationParser.isCommentLine(line)) {
			if (line.startsWith(commentSymbolLineIdentifier)) {
				// do not remove whole line that consists of separator symbols
				newLines.add(removeWhitespaceMarker(line));
			}
			// ignore this line
		} else {
			if (line.trim().length() > 0) {
				String s = removeWhitespaceMarker(line);
				s = StringUtil.cutUnwantedLeadingControlChars(s);
				newLines.add(s);
				headerEndReached = true;
			}
		}
	}

}
