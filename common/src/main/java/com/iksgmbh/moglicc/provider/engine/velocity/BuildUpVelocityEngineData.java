/*
 * Copyright 2016 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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