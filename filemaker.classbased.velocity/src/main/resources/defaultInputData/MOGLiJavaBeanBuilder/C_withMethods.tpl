#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
	#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
	#set( $javaType = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor.getMetaInfoValueFor("JavaType") ) )
	
	'	public ${classDescriptor.simpleName}Builder with$AttributeName(final $javaType $attributeName) {
	'		toBuild.set$AttributeName($attributeName);
	'		return this;
	'	}
	'
	
#end