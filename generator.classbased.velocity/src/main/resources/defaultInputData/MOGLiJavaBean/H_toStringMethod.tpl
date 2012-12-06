'	@Override
'	public String toString() 
'	{
'		return "${classDescriptor.simpleName} ["

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
	#set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
	
	#if ( $TemplateJavaUtility.isJavaMetaTypeArray($javaType) )

		'				+ "$attributeName = " + Arrays.toString($attributeName)
	
	#else
	
		'				+ "$attributeName = " + $attributeName
	
	#end

#end

'				+ "]";
'	}