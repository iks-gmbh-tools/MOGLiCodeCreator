'	@Test
'	public void returnsInstanceWithAllSupportedFieldsExceedingMaxLength()
'	{
'		final ${classDescriptor.simpleName} toReturn = ${classDescriptor.simpleName}Factory.createInstanceWithAllSupportedFieldsExceedingMaxLength();
'

#set( $counter = 0 )    

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

    #set( $MaxLength = $attributeDescriptor.getMetaInfoValueFor("MaxLength") )

    #if ( $classDescriptor.isValueNotAvailable($MaxLength) )
	    
	    	# do nothing
	
    #else
	
		#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) ) 
		#set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
		#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
		
		#if ( $isJavaTypeDomainObject.equals( "true" ) )
		
			# do nothing
			
		#elseif ($javaType == "BigDecimal" || $javaType == "java.math.BigDecimal")
		
			'		System.out.println("${AttributeName}: Is " + ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName") + " < " + toReturn.get${AttributeName}().toPlainString().length()  + "  ?");
			'		assertTrue("content of field '$AttributeName' too short", ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName") < toReturn.get${AttributeName}().toPlainString().length());	
			#set( $counter = $counter + 1 )		

		#elseif ($javaType == "String")
		
			'		System.out.println("${AttributeName}: Is " + ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName") + " < " + toReturn.get${AttributeName}().length()  + "  ?");
			'		assertTrue("content of field '$AttributeName' too short", ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName") < toReturn.get${AttributeName}().length());	
			#set( $counter = $counter + 1 )		
			
		#else 
			
			'		System.out.println("${AttributeName}: Is " + ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName") + " < " + ("" + toReturn.get${AttributeName}()).length()  + "  ?");
			'		assertTrue("content of field '$AttributeName' too short", ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName") < ("" + toReturn.get${AttributeName}()).length());
			#set( $counter = $counter + 1 )		
			
		#end			
	
	
	#end    

#end

#if ( $counter == 0 )

	'		// there is no MetaInfo "MaxLength" defined for attributes of class '${classDescriptor.simpleName}'
#end

'		
}
