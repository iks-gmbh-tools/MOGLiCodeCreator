'	// ===============  getter methods  ===============
'

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
#set( $javaType = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor.getMetaInfoValueFor("JavaType") ) )
#set( $methodName = "get" + $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )

'	public $javaType $methodName() 
'	{
'		return $attributeName;
'	}
'
#end

