package com.iksgmbh.moglicc.provider.model.standard.exceptions;

import java.util.List;

public class ModelParserException extends Exception {

	private static final long serialVersionUID = -2227516656058989198L;
	
	private List<String> errorList;

	public ModelParserException(final List<String> errorList) {
		this.errorList = errorList;
	}

	public List<String> getErrorList() {
		return errorList;
	}
	
	public int getErrorNumber() {
		return errorList.size();
	}

	public String getParserErrors() {
		final StringBuffer sb = new StringBuffer();
		for (String error : errorList) {
			sb.append(error + "\n");
		}
		return sb.toString();
	}	
}
