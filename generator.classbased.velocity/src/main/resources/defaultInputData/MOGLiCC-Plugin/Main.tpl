@TargetFileName ${classDescriptor.simpleName}.java # Name of output file with extension but without path
@TargetDir $model.getMetaInfoValueFor("eclipseWorkspaceProject")/src/main/java/<package>
@NameOfValidModel MOGLiCC-Plugin
@CreateNew true # creates target dir if not existing and overwrites target file if existing

package ${classDescriptor.package};
'
import java.io.File;
import java.util.List;
'
import com.iksgmbh.moglicc.core.InfrastructureService;
import com.iksgmbh.moglicc.exceptions.MOGLiPluginException;
import com.iksgmbh.moglicc.generator.utils.*;
import com.iksgmbh.moglicc.plugin.type.*;
import com.iksgmbh.moglicc.plugin.type.basic.*;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.*;
import com.iksgmbh.utils.ImmutableUtil;
'
#if ($classDescriptor.getMetaInfoValueFor("isMetaInfoValidatorVendor") == "true")
public class ${classDescriptor.simpleName} implements $classDescriptor.getMetaInfoValueFor("pluginType"), MetaInfoValidatorVendor {

#else

public class ${classDescriptor.simpleName} implements $classDescriptor.getMetaInfoValueFor("pluginType") {

#end

'
'	public static final String PLUGIN_ID = "$classDescriptor.getMetaInfoValueFor("pluginID")";
'	public static final String MODEL_PROVIDER_ID = "StandardModelProvider";
'	public static final String ENGINE_PROVIDER_ID = "VelocityEngineProvider";
'
'	public static final String PLUGIN_PROPERTIES_FILE = "generator.properties";
'	public static final String MAIN_TEMPLATE_IDENTIFIER = "Main";
'
'	private InfrastructureService infrastructure;
'
'	@Override
'	public void setMOGLiInfrastructure(final InfrastructureService infrastructure) {
'		this.infrastructure = infrastructure;
'	}
'
'	@Override
'	public void doYourJob() throws MOGLiPluginException {
'		infrastructure.getPluginLogger().logInfo("Doing my job...");
'
'		infrastructure.getPluginLogger().logInfo("Done!");
'	}
'
'
'	List<String> getArtefactList() throws MOGLiPluginException {
'		final File generatorPropertiesFile = new File(infrastructure.getPluginInputDir(), PLUGIN_PROPERTIES_FILE);
'		return ArtefactListUtil.getArtefactListFrom(infrastructure.getPluginInputDir(), generatorPropertiesFile);
'	}
'
'	String findMainTemplate(final File templateDir) throws MOGLiPluginException {
'		return TemplateUtil.findMainTemplate(templateDir, MAIN_TEMPLATE_IDENTIFIER);
'	}
'
'
'	@Override
'	public boolean unpackDefaultInputData() throws MOGLiPluginException {
'		/*
'		infrastructure.getPluginLogger().logInfo("initDefaultInputData");
'		final PluginPackedData defaultData = new PluginPackedData(this.getClass(), DEFAULT_DATA_DIR);
'
'		TODo : add help files here
'
'		PluginDataUnpacker.doYourJob(defaultData, infrastructure.getPluginInputDir(), infrastructure.getPluginLogger());
'		*/
'		return false;  // set to true if
'	}
'
'	@Override
'	public PluginType getPluginType() {
'		return PluginType.$classDescriptor.getMetaInfoValueFor("pluginType");
'	}
'
'	@Override
'	public String getId() {
'		return PLUGIN_ID;
'	}
'
'	@Override
'	public List<String> getDependencies() {
'		return ImmutableUtil.getImmutableListOf($classDescriptor.getCommaSeparatedListOfAllMetaInfoValuesFor("pluginDependency"));
'	}
'

#if ($classDescriptor.getMetaInfoValueFor("isMetaInfoValidatorVendor") == "true")

'	@Override
'	public List<MetaInfoValidator> getMetaInfoValidatorList() throws MOGLiPluginException {
'		final File validationInputFile = new File(infrastructure.getPluginInputDir(),
'				                                  MetaInfoValidationUtil.FILENAME_VALIDATION);
'		return MetaInfoValidationUtil.getMetaInfoValidatorList(validationInputFile, getId());
'	}

#end

'
'	@Override
'	public InfrastructureService getMOGLiInfrastructure() {
'		return infrastructure;
'	}
'
'	@Override
'	public boolean unpackPluginHelpFiles() throws MOGLiPluginException {
'		/*
'		infrastructure.getPluginLogger().logInfo("unpackPluginHelpFiles");
'		final PluginPackedData helpData = new PluginPackedData(this.getClass(), HELP_DATA_DIR);
'
'		TO Do: add help files here
'
'		PluginDataUnpacker.doYourJob(helpData, infrastructure.getPluginHelpDir(), infrastructure.getPluginLogger());
'		*/
'		return false;
'	}
'
}
