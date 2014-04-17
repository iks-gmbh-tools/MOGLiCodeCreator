#if ($classDescriptor.getMetaInfoValueFor("pluginType") == "ENGINE_PROVIDER")

'	@Override
'	public void setEngineData(Object engineData) throws MOGLiPluginException {
'
'		if (engineData == null) {
'			throw new MOGLiPluginException("Parameter 'engineData' must not be null!");
'		}
'
'		// TODO add further validations of enginaData here
'
'		this.engineData = engineData;
'		infrastructure.getPluginLogger().logInfo("Setting engine data:\n '" + engineData + "'...");
'	}
'
'	@Override
'	public Object startEngine() throws MOGLiPluginException {
'		// TODO implement the engine's job here
'		return null;
'	}

#end