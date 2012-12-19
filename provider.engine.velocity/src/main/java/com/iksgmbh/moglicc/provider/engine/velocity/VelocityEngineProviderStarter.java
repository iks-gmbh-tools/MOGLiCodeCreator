package com.iksgmbh.moglicc.provider.engine.velocity;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;

import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.data.BuildUpGeneratorResultData;
import com.iksgmbh.moglicc.data.GeneratorResultData;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.utils.helper.PluginDataUnpacker;
import com.iksgmbh.moglicc.generator.utils.helper.PluginPackedData;
import com.iksgmbh.moglicc.plugin.type.ClassBasedEngineProvider;
import com.iksgmbh.moglicc.plugin.type.ModelBasedEngineProvider;
import com.iksgmbh.moglicc.provider.engine.velocity.helper.MergeResultAnalyser;
import com.iksgmbh.moglicc.provider.model.standard.ClassDescriptor;
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.utils.ImmutableUtil;

public class VelocityEngineProviderStarter implements ClassBasedEngineProvider, ModelBasedEngineProvider {

	public static final String ENGINE_STARTED_WITHOUT_DATA = "Velocity Engine started without VelocityEngineData set.";
	public static final String TEMPLATE_REFERENCE_MODEL = "model";
	public static final String TEMPLATE_REFERENCE_CLASS_DESCRIPTOR = "classDescriptor";
	public static final String PLUGIN_ID = "VelocityEngineProvider";
	public static final String DEFAULT_FILE_EXTENSION = ".txt";

	private InfrastructureService infrastructure;
	private VelocityEngineData velocityEngineData;
	private Model model;

	@Override
	public PluginType getPluginType() {
		return PluginType.ENGINE_PROVIDER;
	}

	@Override
	public String getId() {
		return PLUGIN_ID;
	}

	@Override
	public List<String> getDependencies() {
		return ImmutableUtil.getImmutableListOf();
	}

	@Override
	public void setMOGLiInfrastructure(InfrastructureService infrastructure) {
		this.infrastructure = infrastructure;
	}

	@Override
	public Object startEngine() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("startEngine called");
		return startEngineWithClassList();
	}

	@Override
	public void doYourJob() throws MOGLiPluginException {
		// engine providers have nothing to do here (see startEngine)
	}

	@Override
	public GeneratorResultData startEngineWithModel() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("startEngineAllClassesIntoSingleTargetFile called");
		prepareStart();

		final VelocityContext context = getVelocityContextWith(model);
		final String mergeResult = mergeTemplateWith(context);
		final BuildUpGeneratorResultData buildUpGeneratorResultData = MergeResultAnalyser.doYourJob(mergeResult,
				                                                          getVelocityEngineData().getArtefactType());
        infrastructure.getPluginLogger().logInfo("GeneratorResultData created for model merged with template '"
        		                                  + velocityEngineData.getMainTemplateSimpleFileName() + "'");

		infrastructure.getPluginLogger().logInfo("-----");
		return buildUpGeneratorResultData;
	}

	@Override
	public List<GeneratorResultData> startEngineWithClassList() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("startEngineEachClassIntoSeparateTargetFiles called");
		prepareStart();

		final List<GeneratorResultData> toReturn = new ArrayList<GeneratorResultData>();
		for (int i = 0; i < model.getSize(); i++) {
			final ClassDescriptor clDescr = (ClassDescriptor) model.getClassDescriptorList().get(i);
			final VelocityContext context = getVelocityContextWith(clDescr, model);
			final String mergeResult = mergeTemplateWith(context);
			final BuildUpGeneratorResultData velocityResultData = MergeResultAnalyser.doYourJob(mergeResult,
					                                                 getVelocityEngineData().getArtefactType());
	        infrastructure.getPluginLogger().logInfo("GeneratorResultData created for merging class '"
	        		 + clDescr.getSimpleName() + "' with template '"
	        		 + velocityEngineData.getMainTemplateSimpleFileName() + "'");
	        toReturn.add(velocityResultData);
		}
		infrastructure.getPluginLogger().logInfo("-----");
		return toReturn;
	}

	private void prepareStart() throws MOGLiPluginException {
		if (velocityEngineData == null) {
			throw new MOGLiPluginException(ENGINE_STARTED_WITHOUT_DATA);
		}

		infrastructure.getPluginLogger().logInfo("-");
		infrastructure.getPluginLogger().logInfo("Engine started from " + velocityEngineData.getGeneratorPluginId());
		infrastructure.getPluginLogger().logInfo("MainTemplate: " + velocityEngineData.getTemplateDir().getAbsolutePath()
				+ "/" + velocityEngineData.getMainTemplateSimpleFileName());

		model = velocityEngineData.getModel();
		infrastructure.getPluginLogger().logInfo("Model " + model.getName() + " received from "
				                                 + velocityEngineData.getGeneratorPluginId());
	}

	VelocityContext getVelocityContextWith(final Object... objectsToAdd) {
		final VelocityContext context = new VelocityContext();
		for (final Object object : objectsToAdd) {
			if (object instanceof Model) {
				context.put(TEMPLATE_REFERENCE_MODEL, object);
			} else {
				context.put(TEMPLATE_REFERENCE_CLASS_DESCRIPTOR, object);
			}
		}
        context.put("TemplateStringUtility", new TemplateStringUtility());
        context.put("TemplateJavaUtility", new TemplateJavaUtility());
		return context;
	}

	String mergeTemplateWith(final VelocityContext context) throws MOGLiPluginException {
        final VelocityEngine engine = getVelocityEngine();
        final StringWriter writer = new StringWriter();
        final Template template = createVelocityTemplate(engine);
		template.merge(context, writer); // create new content from template and class from model
        writer.flush();
        return writer.toString();
	}

	private Template createVelocityTemplate(final VelocityEngine engine) throws MOGLiPluginException {
		final Template template;
		try {
        	 template = engine.getTemplate(velocityEngineData.getMainTemplateSimpleFileName(), "UTF-8");
		} catch (ResourceNotFoundException e) {
			final String templateDirAsString = velocityEngineData.getTemplateDir().getAbsolutePath();
			throw new MOGLiPluginException("Error finding template file:\n"
					+ velocityEngineData.getMainTemplateSimpleFileName()
					+ "\nRootDir: " + templateDirAsString, e);
		}
		return template;
	}


	private VelocityEngine getVelocityEngine() {
		final String templateDirAsString = velocityEngineData.getTemplateDir().getAbsolutePath();
		final String templateParentDirAsString = velocityEngineData.getTemplateDir().getParentFile().getAbsolutePath();
		final Properties velocityEngineProperties = new Properties();
        velocityEngineProperties.setProperty("output.encoding", "UTF-8");
        velocityEngineProperties.setProperty("input.encoding", "UTF-8");
        velocityEngineProperties.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
        velocityEngineProperties.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
        									 templateDirAsString + ", " + templateParentDirAsString);

        final VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init(velocityEngineProperties);

		return velocityEngine;
	}

	@Override
	public boolean unpackDefaultInputData() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("Nothing to do in initDefaultInputData!");
		return false;
	}

	@Override
	public void setEngineData(final Object engineData) throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("-----");
		if (engineData == null) {
			throw new MOGLiPluginException("Parameter 'engineData' must not be null!");
		}
		final VelocityEngineData velocityEngineData;

		if (engineData instanceof VelocityEngineData) {
			velocityEngineData = (VelocityEngineData) engineData;
		} else {
			throw new MOGLiPluginException("VelocityEngineData expected! Wrong engine data set: " + engineData.getClass().getName());
		}

		if (velocityEngineData.getModel() == null) {
			throw new MOGLiPluginException("Model not set!");
		}

		if (velocityEngineData.getGeneratorPluginId() == null) {
			throw new MOGLiPluginException("GeneratorPluginId not set!");
		}

		if (infrastructure.getGenerator(velocityEngineData.getGeneratorPluginId()) == null) {
			throw new MOGLiPluginException("Unknown GeneratorPlugin!");
		}

		if (StringUtils.isEmpty(velocityEngineData.getArtefactType())) {
			throw new MOGLiPluginException("ArtefactType not set!");
		}

		if (velocityEngineData.getMainTemplateSimpleFileName() == null) {
			throw new MOGLiPluginException("MainTemplateName not set!");
		}

		if (velocityEngineData.getTemplateDir() == null) {
			throw new MOGLiPluginException("TemplateDir not set!");
		}

		final File templateDir = velocityEngineData.getTemplateDir();
		if (! templateDir.exists()) {
			throw new MOGLiPluginException("TemplateDir does not exist:\n"
					                            + templateDir.getAbsolutePath());
		}

		final File mainTemplateFile = new File(templateDir,
				                               velocityEngineData.getMainTemplateSimpleFileName());
		if (! mainTemplateFile.exists()) {
			throw new MOGLiPluginException("Main Template File does not exist:\n"
					   + mainTemplateFile.getAbsolutePath());
		}

		this.velocityEngineData = velocityEngineData;
		infrastructure.getPluginLogger().logInfo("Setting velocity engine data:\n '"
				+ velocityEngineData + "'...");

	}

	@Override
	public InfrastructureService getMOGLiInfrastructure() {
		return infrastructure;
	}

	@Override
	public boolean unpackPluginHelpFiles() throws MOGLiPluginException {
		infrastructure.getPluginLogger().logInfo("unpackPluginHelpFiles");
		final PluginPackedData helpData = new PluginPackedData(this.getClass(), HELP_DATA_DIR);
		helpData.addFile("_TemplateUtilities.htm");
		helpData.addFile("TemplateStringUtility.htm");
		helpData.addFile("TemplateJavaUtility.htm");
		PluginDataUnpacker.doYourJob(helpData, infrastructure.getPluginHelpDir(), infrastructure.getPluginLogger());
		return true;
	}

	/**
	 * FOR TEST PURPOSE ONLY
	 */
	VelocityEngineData getVelocityEngineData() {
		return velocityEngineData;
	}

}
