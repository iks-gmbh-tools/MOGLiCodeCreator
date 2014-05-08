@TargetFileName ${classDescriptor.simpleName}.java # Name of output file with extension but without path
@TargetDir $model.getMetaInfoValueFor("eclipseProjectDir")/$model.getMetaInfoValueFor("projectName")/src/main/java/<package>
@NameOfValidModel MOGLiCC_NewPluginModel
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
import com.iksgmbh.moglicc.provider.model.standard.Model;
import com.iksgmbh.moglicc.provider.model.standard.metainfo.*;
import com.iksgmbh.utils.ImmutableUtil;
import com.iksgmbh.moglicc.generator.utils.helper.*;
import com.iksgmbh.utils.FileUtil;
'

#parse("B_ClassDefinitionLine.tpl")

#parse("C_generatorVariables.tpl")


'
'	public static final String PLUGIN_ID = "$classDescriptor.getMetaInfoValueFor("pluginID")";
'	public static final String MODEL_PROVIDER_ID = "StandardModelProvider";
'	public static final String ENGINE_PROVIDER_ID = "VelocityEngineProvider";
'
'	public static final String PLUGIN_PROPERTIES_FILE = "generator.properties";
'	public static final String MAIN_TEMPLATE_IDENTIFIER = "Main";
'
'	private InfrastructureService infrastructure;
#if ($classDescriptor.getMetaInfoValueFor("pluginType") == "ENGINE_PROVIDER")
'	private Object engineData;
#end
'
'	@Override
'	public void setMOGLiInfrastructure(final InfrastructureService infrastructure) {
'		this.infrastructure = infrastructure;
'	}
'
'	@Override
'	public void doYourJob() throws MOGLiPluginException {

	#if ($classDescriptor.getMetaInfoValueFor("pluginType") == "ENGINE_PROVIDER")

'		// engine providers have nothing to do here (see startEngine)

	#else

'		infrastructure.getPluginLogger().logInfo("Doing my job...");
'
'		//  TODO: implement here the plugin's job
'
'		infrastructure.getPluginLogger().logInfo("Done!");

	#end

'	}
'
'

	#if ($classDescriptor.getMetaInfoValueFor("pluginType") == "GENERATOR")
	'	List<String> getArtefactList() throws MOGLiPluginException {
	'		final File generatorPropertiesFile = new File(infrastructure.getPluginInputDir(), PLUGIN_PROPERTIES_FILE);
	'		return ArtefactListUtil.getArtefactListFrom(infrastructure.getPluginInputDir(), generatorPropertiesFile);
	'	}
	'
	#end

'	String findMainTemplate(final File templateDir) throws MOGLiPluginException {
'		return TemplateUtil.findMainTemplate(templateDir, MAIN_TEMPLATE_IDENTIFIER);
'	}
'

#parse("D_unpackDefaultInputData.tpl")

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
#parse("E_getMetaInfoValidatorList.tpl")

'
'	@Override
'	public InfrastructureService getMOGLiInfrastructure() {
'		return infrastructure;
'	}
'

#parse("F_unpackPluginHelpFiles.tpl")

'

#parse("G_getModel.tpl")

#parse("H_engineMethods.tpl")

#parse("I_generatorMethods.tpl")

'
}
