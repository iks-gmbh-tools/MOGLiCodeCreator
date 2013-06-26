#if ($classDescriptor.getMetaInfoValueFor("pluginType") == "GENERATOR")

'	@Override
'	public String getGenerationReport() {
'		return generationReport.toString().trim();
'	}
'
'	@Override
'	public int getNumberOfGenerations() {
'		return generationCounter;
'	}
'
'	@Override
'	public int getNumberOfArtefacts() {
'		return artefactCounter;
'	}
'
'	private void generateReportLines(final String artefact) {
'		artefactCounter++;
'		generationReport.append(FileUtil.getSystemLineSeparator());
'		generationReport.append("   Reports for artefact '");
'		generationReport.append(artefact);
'		generationReport.append("':");
'		generationReport.append(FileUtil.getSystemLineSeparator());
'	
'		// TODO add details here and call method from where it makes sense
'	}
'
'	List<String> getArtefactList() throws MOGLiPluginException {
'		final File generatorPropertiesFile = new File(infrastructure.getPluginInputDir(), PLUGIN_PROPERTIES_FILE);
'		return ArtefactListUtil.getArtefactListFrom(infrastructure.getPluginInputDir(), generatorPropertiesFile);
'	}
'
	
#end