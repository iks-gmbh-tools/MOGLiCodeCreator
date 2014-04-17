package com.iksgmbh.moglicc.exceptions;

import static com.iksgmbh.moglicc.MOGLiTextConstants.TEXT_DUPLICATE_PLUGINIDS;

public class DuplicatePluginIdException extends MOGLiCoreException {

	private static final long serialVersionUID = -7861226890315250149L;
	
	private String pluginId;

	public DuplicatePluginIdException(String pluginId) {
		super(TEXT_DUPLICATE_PLUGINIDS + pluginId);
		this.pluginId = pluginId;
	}

	public String getPluginId() {
		return pluginId;
	}
	
}
