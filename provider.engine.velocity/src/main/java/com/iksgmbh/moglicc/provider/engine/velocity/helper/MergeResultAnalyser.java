package com.iksgmbh.moglicc.provider.engine.velocity.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException2;
import com.iksgmbh.data.Annotation;
import com.iksgmbh.helper.AnnotationParser;
import com.iksgmbh.utils.FileUtil;
import com.iksgmbh.utils.StringUtil;

public class MergeResultAnalyser {
	
	public static final String IDENTIFICATOR_WHITESPACE_MARKER = "'";
	
	private BuildUpGeneratorResultData velocityResultData;
	private AnnotationParser annotationParser = AnnotationParser.getInstance();
	
	

	public static BuildUpGeneratorResultData doYourJob(final String mergeResult) throws MOGLiPluginException2 {
		final MergeResultAnalyser analyser = new MergeResultAnalyser();
		try {
			return analyser.analyseGeneratorResult(mergeResult);
		} catch (IOException e) {
			throw new MOGLiPluginException2(e);
		}
	}
	
	private BuildUpGeneratorResultData analyseGeneratorResult(final String mergeResult) throws IOException {
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
	 */
	protected String parseMergeResult(final String originalFileContent) {
		final String[] oldLines = originalFileContent.split(FileUtil.getSystemLineSeparator());
		final List<String> newLines = new ArrayList<String>();
		for (int i = 0; i < oldLines.length; i++) {
			final String line = oldLines[i].trim();
			parseLine(newLines, line);
		}
		removeTrailingEmptyLines(newLines);
		return StringUtil.concat(newLines);
	}

	protected void parseLine(final List<String> newLines, final String line) {
		if (annotationParser.hasCorrectPrefix(line)) {
			final Annotation annotation = annotationParser.doYourJob(line);
			velocityResultData.addProperty(annotation.getName(), annotation.getAdditionalInfo());
		} else if (annotationParser.isCommentLine(line)) {
			// ignore this line
		} else {
			if (line.trim().length() > 0) {
				newLines.add(removeWhitespaceMarker(line));
			}
		}
	}

}
