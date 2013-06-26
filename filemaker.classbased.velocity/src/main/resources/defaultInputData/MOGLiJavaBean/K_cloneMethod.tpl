'	@Override
'	public Object clone() 
'	{

'		final ${classDescriptor.simpleName} clone;
'		try {
'			clone = (${classDescriptor.simpleName}) super.clone();
'		} catch (Exception e) {
'			throw new AssertionError("Unexpected error cloning " + this);
'		}
'
#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
	#set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
	
	#if ( $TemplateJavaUtility.isJavaMetaTypeArray($javaType) )

		#parse("K1_cloneArrayType.tpl")
	
	#elseif ( $TemplateJavaUtility.isJavaMetaTypeCollection($javaType) )
	
		#parse("K2_cloneCollectionType.tpl")
	
	#elseif ( $TemplateJavaUtility.isJavaMetaTypePrimitive($javaType) )
	
'		clone.$attributeName = this.${attributeName};
	
	#else
	
		#parse("K3_cloneStandardType.tpl")	

	#end
#end
'
'		return clone;
'	}
