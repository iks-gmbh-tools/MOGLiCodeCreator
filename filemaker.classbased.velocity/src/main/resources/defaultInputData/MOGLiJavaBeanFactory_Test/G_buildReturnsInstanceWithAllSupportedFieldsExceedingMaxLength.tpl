'	@Test
'	public void returnsInstanceWithAllSupportedFieldsExceedingMaxLength()
'	{
'		final ${classDescriptor.simpleName} toReturn = ${classDescriptor.simpleName}Factory.createInstanceWithAllSupportedFieldsExceedingMaxLength();
'

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) ) 
    #set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
	#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
	
	#if ( $isJavaTypeDomainObject.equals( "true" ) )
    
    	# do nothing
		
    #elseif ($javaType == "BigDecimal" || $javaType == "java.math.BigDecimal")
    
		'		System.out.println("${AttributeName}: Is " + ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName") + " < " + toReturn.get${AttributeName}().toPlainString().length()  + "  ?");
		'		assertTrue("content of field '$AttributeName' too short", ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName") < toReturn.get${AttributeName}().toPlainString().length());	

    #elseif ($javaType == "String")
    
		'		System.out.println("${AttributeName}: Is " + ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName") + " < " + toReturn.get${AttributeName}().length()  + "  ?");
		'		assertTrue("content of field '$AttributeName' too short", ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName") < toReturn.get${AttributeName}().length());	
		
	#else 

	    #set( $MaxLength = $attributeDescriptor.getMetaInfoValueFor("MaxLength") )
	    
	    #if ( $classDescriptor.isValueNotAvailable($MaxLength) )
	    
	    	# do nothing
	    
	    #else
	    
			'		System.out.println("${AttributeName}: Is " + ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName") + " < " + ("" + toReturn.get${AttributeName}()).length()  + "  ?");
			'		assertTrue("content of field '$AttributeName' too short", ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName") < ("" + toReturn.get${AttributeName}()).length());	
	
		#end
		
	#end			
    

#end

'		
}
