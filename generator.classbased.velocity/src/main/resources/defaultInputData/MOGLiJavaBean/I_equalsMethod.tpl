'	@Override
'	public boolean equals(Object obj) {
'		if (this == obj)
'			return true;
'		if (obj == null)
'			return false;
'		if (getClass() != obj.getClass())
'			return false;
'			
'		final ${classDescriptor.simpleName} other = (${classDescriptor.simpleName}) obj;
'		
#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
	#set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
	
	#if ( $TemplateJavaUtility.isJavaMetaTypeArray($javaType) )

		#parse("I1_equalsArrayTypes.tpl")
	
	#elseif ( $TemplateJavaUtility.isJavaMetaTypePrimitive($javaType) )
	
		#parse("I2_equalsPrimitiveTypes.tpl")
	
	#else
	
		#parse("I3_equalsStandardTypes.tpl")	

	#end

#end

'		return true;
'	}
