'	@Override
'	public String toString() 
'	{
'		return "${classDescriptor.simpleName} ["

#set( $useJavaBeanRegistry = $model.getMetaInfoValueFor("useJavaBeanRegistry") )

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
	#set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
	#set( $comma = ", " )
	
	#if ($velocityCount == $classDescriptor.attributeDescriptorList.size())
		#set( $comma = "" )
	#end

	#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
	#parse("commonSubtemplates/checkForJavaTypeListOfDomainObjects.tpl")

	#if ( $isJavaTypeDomainObject.equals( "true" ) && $useJavaBeanRegistry == "true")

		'				+ "$attributeName = " + ${attributeName}.getRegistryId() + "$comma"
	
	#elseif ( $isJavaTypeListOfDomainObjects == "true" && $useJavaBeanRegistry == "true")	
	
		'				+ "$attributeName = " + toStringForListOfDomainObjects(${attributeName}) + "$comma"
	
	#elseif ( $TemplateJavaUtility.isJavaMetaTypeArray($javaType) )

		'				+ "$attributeName = " + Arrays.toString($attributeName) + "$comma"
	
	#else
	
		'				+ "$attributeName = " + $attributeName + "$comma"
	
	#end

#end

#if ( $useJavaBeanRegistry == "true" )

		'				+ ", registryId = " + registryId

#end


'				+ "]";
'	}