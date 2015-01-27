'	@Override
'	public int hashCode() {
'		final int prime = 31;
'		int result = 1;
'		
#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
	#set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
	
	#if ( $TemplateJavaUtility.isJavaMetaTypeArray($javaType) )

'				 result = prime * result + Arrays.hashCode($attributeName);
	
	#elseif ( $TemplateJavaUtility.isJavaMetaTypePrimitive($javaType) )
	
		#parse("J2_hashCodePrimitiveTypes.tpl")
	
	#else
	
'                result = prime * result + (($attributeName == null) ? 0 : $attributeName.hashCode());

	#end

#end

'
'		return result;
'	}
