package com.iksgmbh.moglicc.generator.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.utils.FileUtil;


public class GeneratorReportUtil
{
	public static final String REPORT_TAB = "   ";

	public static String getShortReport(final GeneratorStandardReportData standardReportData) 
	{
		if (! standardReportData.jobStarted) {
			return "not yet executed";
		}
		
		if (standardReportData.numberOfAllInputArtefacts == 0) {
			return "No input artefact found. Nothing to do.";
		}
		
		if (standardReportData.modelError != null) {
			return "Model Error: " + standardReportData.modelError + " No generation done.";
		}
		
		
		if (standardReportData.model == null || StringUtils.isEmpty(standardReportData.model.getName())) {
			return "No model available. No generation done.";
		}

		if (standardReportData.numberOfModelMatchingInputArtefacts 
			+ standardReportData.invalidInputArtefacts.size() == 0) {
			return "No model matching input artefact found. Nothing to do.";
		}
		
		if (standardReportData.invalidInputArtefacts.size() == 0) {
			if (standardReportData.numberOfOutputArtefacts == 0) {
				return standardReportData.numberOfAllInputArtefacts + " input artefact(s) found, but no generation done.";
			}
		} else {
			String invalidArtefacts = "";
			for (final String artefact : standardReportData.invalidInputArtefacts)
			{
				invalidArtefacts = artefact + FileUtil.getSystemLineSeparator();
			}
			return invalidArtefacts;
		}
		
		return "";
	}
	
	public static String getNonGeneratedArtefactReport(final GeneratorStandardReportData standardReportData) 
	{
		final StringBuffer toReturn = new StringBuffer();
		
		if (standardReportData.nonModelMatchingInputArtefacts.size() > 0) {
			toReturn.append(FileUtil.getSystemLineSeparator());
			toReturn.append(FileUtil.getSystemLineSeparator());
			toReturn.append("Following " + standardReportData.nonModelMatchingInputArtefacts.size() + " artefact(s) did not match the model used:");
			toReturn.append(FileUtil.getSystemLineSeparator());
			for (final String plugin : standardReportData.nonModelMatchingInputArtefacts)
			{					
				toReturn.append(plugin);
				toReturn.append(FileUtil.getSystemLineSeparator());
			}
		}
			
		if (standardReportData.skippedArtefacts.size() > 0) {
			toReturn.append(FileUtil.getSystemLineSeparator());
			toReturn.append(FileUtil.getSystemLineSeparator());
			toReturn.append("Following " + standardReportData.skippedArtefacts.size() + " artefact(s) are configured to skip generation:");
			toReturn.append(FileUtil.getSystemLineSeparator());
			for (final String plugin : standardReportData.skippedArtefacts)
			{					
				toReturn.append(plugin);
				toReturn.append(FileUtil.getSystemLineSeparator());
			}
		}
		
		if (standardReportData.invalidInputArtefacts.size() > 0) {
			toReturn.append(FileUtil.getSystemLineSeparator());
			toReturn.append(FileUtil.getSystemLineSeparator());
			toReturn.append("Following " + standardReportData.invalidInputArtefacts.size() + " artefact(s) are invalid:");
			toReturn.append(FileUtil.getSystemLineSeparator());
			for (final String plugin : standardReportData.invalidInputArtefacts)
			{					
				toReturn.append(plugin);
				toReturn.append(FileUtil.getSystemLineSeparator());
			}
		}

		return toReturn.toString();
	}

	public static String getReport(final GeneratorStandardReportData standardReportData) 
	{
		if (standardReportData.model != null && standardReportData.model.getSize() == 0) {
			return standardReportData.numberOfAllInputArtefacts + " input artefact(s) found. No classes in model. Nothing to do.";
		}

		final StringBuffer toReturn = new StringBuffer(GeneratorReportUtil.getShortReport(standardReportData));

		toReturn.append(GeneratorReportUtil.getNonGeneratedArtefactReport(standardReportData));		

		if (standardReportData.numberOfOutputArtefacts > 0) {
			toReturn.append(standardReportData.additionalReport);
		}

		return toReturn.toString().trim();

	}

	public static String getArtefactReportLine(final String artefactName) {
		return REPORT_TAB + "Report for input artefact '" + artefactName + "':";
	}
	
	public static class GeneratorStandardReportData 
	{		
		public String additionalReport = "";  // generator specific report part 

		public boolean jobStarted = false;
		
		public Model model = null;
		public String modelError = null;  // TODO testfall 
		
		public int numberOfAllInputArtefacts = 0;
		public int numberOfModelMatchingInputArtefacts = 0;
		public int numberOfOutputArtefacts = 0;
		
		public List<String> nonModelMatchingInputArtefacts = new ArrayList<String>();
		public List<String> skippedArtefacts = new ArrayList<String>();  // for model based generators input artefact, for class based output artefacts
		public List<String> invalidInputArtefacts = new ArrayList<String>();  // TODO testfaelle für drei varianten

	}
}
