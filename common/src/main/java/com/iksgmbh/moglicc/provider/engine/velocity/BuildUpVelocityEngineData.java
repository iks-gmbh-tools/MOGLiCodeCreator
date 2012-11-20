package com.iksgmbh.moglicc.provider.engine.velocity;

import java.io.File;

import com.iksgmbh.moglicc.provider.engine.velocity.VelocityEngineData;
import com.iksgmbh.moglicc.provider.model.standard.Model;

public class BuildUpVelocityEngineData implements VelocityEngineData {

	private String artefactType;
	private File templateDir;
	private String templateFileName;
	private Model model;
	private String generatorPluginId;

	@Override
	public String toString() {
		return "VelocityEngineDataImpl [artefactType=" + artefactType
				+ ", templateDir=" + templateDir + ", templateFileName="
				+ templateFileName + ", model=" + model
				+ ", generatorPluginId=" + generatorPluginId + "]";
	}

	public BuildUpVelocityEngineData(final String artefactType, final Model model,
			                      final String generatorPluginId) {
		this.artefactType = artefactType;
		this.model = model;
		this.generatorPluginId = generatorPluginId;
	}

	public void setTemplateDir(final File templateDir) {
		this.templateDir = templateDir;
	}

	public void setTemplateFileName(final String templateFileName) {
		this.templateFileName = templateFileName;
	}

	@Override
	public String getGeneratorPluginId() {
		return generatorPluginId;
	}

	@Override
	public String getArtefactType() {
		return artefactType;
	}

	@Override
	public File getTemplateDir() {
		return templateDir;
	}

	@Override
	public String getMainTemplateSimpleFileName() {
		return templateFileName;
	}

	@Override
	public Model getModel() {
		return model;
	}
}
