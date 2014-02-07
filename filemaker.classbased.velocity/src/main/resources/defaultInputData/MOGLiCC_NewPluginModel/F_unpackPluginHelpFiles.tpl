'	@Override
'	public boolean unpackPluginHelpFiles() throws MOGLiPluginException {

	#if ($classDescriptor.getMetaInfoValueFor("hasHelpDataToUnpack") == "true")

'		infrastructure.getPluginLogger().logInfo("unpackPluginHelpFiles");
'		final PluginPackedData helpData = new PluginPackedData(this.getClass(), HELP_DATA_DIR);
'
'		//  TODO: add help files here
'
'		PluginDataUnpacker.doYourJob(helpData, infrastructure.getPluginHelpDir(), infrastructure.getPluginLogger());
'		return true;

	#else

'		return false;

	#end
'	}