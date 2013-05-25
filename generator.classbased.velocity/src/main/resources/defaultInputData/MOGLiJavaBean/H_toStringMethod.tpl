'	@Override
'	public String toString() 
'	{
'		return "${classDescriptor.simpleName} ["

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
	#set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
	#set( $comma = ", " )

	
	#if ($velocityCount == $classDescriptor.attributeDescriptorList.size())
		#set( $comma = "" )
	#end
	
	#if ( $TemplateJavaUtility.isJavaMetaTypeArray($javaType) )

		'				+ "$attributeName = " + Arrays.toString($attributeName) + "$comma"
	
	#else
	
		'				+ "$attributeName = " + $attributeName + "$comma"
	
	#end

#end

'				+ "]";
'	}