'	@Override
'	public int hashCode() {
'		final int prime = 31;
'		int result = 1;
'		

#set( $useJavaBeanRegistry = $model.getMetaInfoValueFor("useJavaBeanRegistry") )

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
	#set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
	#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
	#parse("commonSubtemplates/checkForJavaTypeListOfDomainObjects.tpl")

	#if ( $isJavaTypeDomainObject.equals( "true" ) && $useJavaBeanRegistry == "true")
	
'		result = prime * result + (($attributeName == null) ? 0 : ${attributeName}.getRegistryId().hashCode());
		
	#elseif ( $TemplateJavaUtility.isJavaMetaTypeArray($javaType) )

'		result = prime * result + Arrays.hashCode($attributeName);
	
	#elseif ( $TemplateJavaUtility.isJavaMetaTypePrimitive($javaType) )
	
		#parse("J2_hashCodePrimitiveTypes.tpl")
		
	#elseif ( $isJavaTypeListOfDomainObjects == "true" && $useJavaBeanRegistry == "true" )

'		result = prime * result + (($attributeName == null) ? 0 : hashCodeOfListOfDomainObjects(${attributeName}));
	
	#else
	
'		result = prime * result + (($attributeName == null) ? 0 : ${attributeName}.hashCode());

	#end

#end

#if ( $useJavaBeanRegistry == "true" )

'		result = prime * result + ((registryId == null) ? 0 : registryId.hashCode());

#end

'
'		return result;
'	}


