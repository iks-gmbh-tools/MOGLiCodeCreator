'	@Test
'	public void returnsInstanceWithAllSupportedFieldsNotReachingMinLength()
'	{
'		final ${classDescriptor.simpleName} toReturn = ${classDescriptor.simpleName}Factory.createInstanceWithAllSupportedFieldsNotReachingMinLength();
'

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

    #set( $MinLength = $attributeDescriptor.getMetaInfoValueFor("MinLength") )
    
	
    #if ( $classDescriptor.isValueNotAvailable($MinLength) )
    
	    	# do nothing
    
    #else
    
		#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) ) 
	    #set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
    
		#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
		
		#if ( $isJavaTypeDomainObject.equals( "true" ) )
	    
	    	# do nothing
			
	    #elseif ($javaType == "BigDecimal" || $javaType == "java.math.BigDecimal")
	    
			'		System.out.println("${AttributeName}: Is " + ${classDescriptor.simpleName}Factory.getMinLength("$AttributeName") + " > " + toReturn.get${AttributeName}().toPlainString().length()  + "  ?");
			'		assertTrue("content of field '$AttributeName' too long", ${classDescriptor.simpleName}Factory.getMinLength("$AttributeName") > toReturn.get${AttributeName}().toPlainString().length());
			
	    #elseif ($javaType == "String")
	    
			'		System.out.println("${AttributeName}: Is " + ${classDescriptor.simpleName}Factory.getMinLength("$AttributeName") + " > " + toReturn.get${AttributeName}().length()  + "  ?");
			'		assertTrue("content of field '$AttributeName' too long", ${classDescriptor.simpleName}Factory.getMinLength("$AttributeName") > toReturn.get${AttributeName}().length());
		#else 
		    
			'		System.out.println("${AttributeName}: Is " + ${classDescriptor.simpleName}Factory.getMinLength("$AttributeName") + " > " + ("" + toReturn.get${AttributeName}()).length() + "  ?");
			'		assertTrue("content of field '$AttributeName' too long", ${classDescriptor.simpleName}Factory.getMinLength("$AttributeName") > ("" + toReturn.get${AttributeName}()).length());				
		
		#end			
		
    #end
    	
#end

'	}
