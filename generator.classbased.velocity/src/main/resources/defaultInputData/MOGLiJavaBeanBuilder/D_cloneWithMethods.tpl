#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
	#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
	#set( $javaType = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor.getMetaInfoValueFor("JavaType") ) )
	
	'	public ${classDescriptor.simpleName}Builder cloneWith$AttributeName(final $javaType $attributeName) {
	'		final ${classDescriptor.simpleName}Builder clonedBuilder = new ${classDescriptor.simpleName}Builder(toBuild);
	'		return clonedBuilder.with$AttributeName($attributeName);
	'	}
	'
	
#end