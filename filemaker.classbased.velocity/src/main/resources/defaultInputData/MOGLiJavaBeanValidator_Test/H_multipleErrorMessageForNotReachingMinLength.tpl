'	@Test
'	public void validatesWithMultipleErrorMessageForNotReachingMinLength() {
'		final ${classDescriptor.simpleName} instance = ${classDescriptor.simpleName}Factory.createInstanceWithAllSupportedFieldsNotReachingMinLength();
'

#set( $counter = 0 )

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

    #set( $MinLength = $attributeDescriptor.getMetaInfoValueFor("MinLength") )
    
    #if ( $classDescriptor.isValueNotAvailable($MinLength) )
    
    	# do nothing
    
    #else
    
		#set( $counter = $counter + 1 )
		
	#end


#end

#if ( $counter == 0 )

	// the meta model contains for class '${classDescriptor.simpleName}' no attribute with metainfo 'MinLength', therefore is no test in this method possible 
	
#else

	'		try {
	'			${classDescriptor.simpleName}Validator.doYourJob(instance);
	'			fail("Expected exception not thrown!");
	'		} catch (Exception e) {
	'			System.err.println(e.getMessage());
	
				#if ( $counter == 1 )
	
					'			assertTrue("unexpected error message", e.getMessage().startsWith("A validation error exists for '$classDescriptor.simpleName'"));
				
				#else
				
					'			assertTrue("unexpected error message", e.getMessage().startsWith("$counter validation errors exist for '$classDescriptor.simpleName'"));
					
				#end
				
	'			return;
	'		}

#end

'
'	}
