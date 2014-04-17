#if ($classDescriptor.getMetaInfoValueFor("isMetaInfoValidatorVendor") == "true")

'	@Override
'	public List<MetaInfoValidator> getMetaInfoValidatorList() throws MOGLiPluginException {
'		final File validationInputFile = new File(infrastructure.getPluginInputDir(),
'				                                  MetaInfoValidationUtil.FILENAME_VALIDATION);
'		return MetaInfoValidationUtil.getMetaInfoValidatorList(validationInputFile, getId());
'	}

#end