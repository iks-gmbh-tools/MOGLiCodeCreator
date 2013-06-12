package com.iksgmbh.moglicc.provider.engine.velocity.helper;

import java.util.List;

import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.generator.classbased.velocity.VelocityGeneratorResultData;
import com.iksgmbh.utils.StringUtil;

/**
 * Corrects one problem that appears to be a bug of Velocity:
 * A TargetFileName that is build by replacements, e.g.
 * @TargetFileName $TemplateStringUtility.getNowAsFormattedString("yyyyMMddHHmm")_$model.getMetaInfoValueFor("projectName")_system_init_ddl.sql
 * misses its file extension after Velocity has done its replacement.
 * 
 * Note: there is a Velocity workaround that solves the problem:
 * Use <${model.getMetaInfoValueFor("projectName")}> instead of <$model.getMetaInfoValueFor("projectName")>
 * 
 * @author Reik Oberrath
 */
public class VelocityBugCorrector {

	public static void doYourJob(final BuildUpGeneratorResultData buildUpGeneratorResultData,
			                     final List<String> mainTemplateFileContent) {
		
		addRemovedFileExtensionToTargetFilename(buildUpGeneratorResultData, mainTemplateFileContent);
	}

	private static void addRemovedFileExtensionToTargetFilename(final BuildUpGeneratorResultData buildUpGeneratorResultData,
			                                                    final List<String> mainTemplateFileContent) {
		final String targetFileNameId = VelocityGeneratorResultData.KnownGeneratorPropertyNames.TargetFileName.name();
		final String origLine = StringUtil.getStringFromStringListThatContainsSubstring(mainTemplateFileContent, targetFileNameId);
		if (origLine == null) {
			return; // targetFileName not defined
		}
		final String line = cutComment(origLine);
		final String fileExtension = cutFileExtension(line);
		
		if (fileExtension == null) {
			return;
		}
		
		final String targetFileName = buildUpGeneratorResultData.getProperty(targetFileNameId);
		if (targetFileName == null) {
			return; // targetFileName not defined
		}
		
		if (targetFileName.endsWith(fileExtension)) {
			return; // error did not occur - surprise!
		}
		
		// override with corrected value
		buildUpGeneratorResultData.addProperty(targetFileNameId, targetFileName + fileExtension);
	}
	
	
	private static String cutFileExtension(final String line) {
		final int pos = line.lastIndexOf(".");
		if (pos == -1) {
			return null;
		}
		return line.substring(pos).trim();
	}

	private static String cutComment(final String line) {
		final int pos = line.indexOf("#");
		if (pos == -1) {
			return line;
		}
		return line.substring(0, pos).trim();
	}
}
