'	@Test
'	public void validatesWithMultipleErrorMessageForExceedingMaxLength() {
'		final ${classDescriptor.simpleName} instance = ${classDescriptor.simpleName}Factory.createInstanceWithAllSupportedFieldsExceedingMaxLength();
'

#set( $counter = 0 )

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

    #set( $MaxLength = $attributeDescriptor.getMetaInfoValueFor("MaxLength") )
    
    #if ( $classDescriptor.isValueNotAvailable($MaxLength) )
    
    	# do nothing
    
    #else
    
		#set( $counter = $counter + 1 )
		
	#end


#end


#if ( $counter == 0 )

	// the meta model contains for class '${classDescriptor.simpleName}' no attribute with metainfo 'MaxLength', therefore is no test in this method possible 
	
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
