'	@Override
'	public boolean unpackDefaultInputData() throws MOGLiPluginException {

	#if ($classDescriptor.getMetaInfoValueFor("hasDefaultDataToUnpack") == "true")

'		infrastructure.getPluginLogger().logInfo("initDefaultInputData");
'		final PluginPackedData defaultData = new PluginPackedData(this.getClass(), DEFAULT_DATA_DIR);
'
'		//  TODO: add defaultData files here
'
'		PluginDataUnpacker.doYourJob(defaultData, infrastructure.getPluginInputDir(), infrastructure.getPluginLogger());
'		return true;

	#else

'		return false;

	#end
'	}