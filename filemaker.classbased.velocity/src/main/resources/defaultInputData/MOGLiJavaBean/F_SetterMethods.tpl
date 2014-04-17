'	// ===============  setter methods  ===============
'

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
#set( $javaType = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor.getMetaInfoValueFor("JavaType") ) )
#set( $methodName = "set" + $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )

'	public void $methodName(final $javaType $attributeName)
'	{
'		this.$attributeName = $attributeName;
'	}
'
#end

