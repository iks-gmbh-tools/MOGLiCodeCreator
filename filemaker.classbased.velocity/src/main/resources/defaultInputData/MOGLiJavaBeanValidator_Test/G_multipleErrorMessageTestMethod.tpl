'	@Test
'	public void validatesWithMultipleValidationErrors() {


#set( $counter = 0 )

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
	#set( $javaType = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor.getMetaInfoValueFor("JavaType") ) )
	
	#if ( $TemplateJavaUtility.isPrimitiveType($javaType) )
	
		# do nothing
		
	#elseif ( $attributeDescriptor.doesHaveMetaInfo("Mandatory", "true") )
	
		'		testData.set${AttributeName}(null);

		#set( $counter = $counter + 1 )
	
	#elseif ( $attributeDescriptor.doesHaveAnyMetaInfosWithName("MinLength") )
	
		#set( $MinLength = $attributeDescriptor.getMetaInfoValueFor("MinLength") )
		
		#parse("commonSubtemplates/setContentSmallerThanMinLengthToField.tpl")
		
		#set( $counter = $counter + 1 )
	
	#elseif ( $attributeDescriptor.doesHaveAnyMetaInfosWithName("MaxLength") )
	
		#set( $MaxLength = $attributeDescriptor.getMetaInfoValueFor("MaxLength") )
		
		#parse("commonSubtemplates/setContentLargerThanMaxLengthToField.tpl")
		
		#set( $counter = $counter + 1 )
	
	#end

	#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )

#end


#if ( $counter == 0 )

	'		// there is no validation defined for attributes in class '${classDescriptor.simpleName}'

#else

	'
	'		try {
	'			${classDescriptor.simpleName}Validator.doYourJob(testData);
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
	'		fail("Expected exception not thrown!");

#end

'	}
