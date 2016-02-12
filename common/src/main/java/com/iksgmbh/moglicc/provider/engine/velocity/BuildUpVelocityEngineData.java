package com.iksgmbh.moglicc.provider.engine.velocity;

import java.io.File;
import java.util.List;

import com.iksgmbh.moglicc.provider.model.standard.ClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.buildup.BuildUpModel;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.MetaInfo;

public class BuildUpVelocityEngineData implements VelocityEngineData {

	private String artefactType;
	private File templateDir;
	private String templateFileName;
	private Model model;
	private String generatorPluginId;
	private ExecutionMode executionMode;

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
		this.executionMode = ExecutionMode.ONLY_PREPARATION;
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
		if (executionMode == ExecutionMode.FULL_GENERATION)
		{
			return model;
		}
		return createPreparationModel();
	}

	private Model createPreparationModel() 
	{
		final List<ClassDescriptor> classDescriptorList = model.getClassDescriptorList();
		final BuildUpModel preparationModel = new BuildUpModel(model.getName());
		final List<MetaInfo> metaInfoList = model.getMetaInfoList();
		
		for (MetaInfo metaInfo : metaInfoList) {
			preparationModel.addMetaInfo(metaInfo);
		}
		
		if ( ! classDescriptorList.isEmpty() )
		{
			preparationModel.addClassDescriptor(classDescriptorList.get(0));
		}
		
		return preparationModel;
	}
	
	public ExecutionMode getExecutionMode() {
		return executionMode;
	}
	
	public boolean isExecutionModeOnlyPreparation() {
		return executionMode == ExecutionMode.ONLY_PREPARATION;
	}
	

	public void setExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
	}
	
}