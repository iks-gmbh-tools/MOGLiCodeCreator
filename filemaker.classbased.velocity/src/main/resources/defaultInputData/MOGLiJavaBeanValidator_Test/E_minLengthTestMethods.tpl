
#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#if ( $attributeDescriptor.doesHaveAnyMetaInfosWithName("MinLength") )

		#set( $javaType = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor.getMetaInfoValueFor("JavaType") ) )
				
		#parse("commonSubtemplates/isFieldLengthRelevantForJavaType.tpl")
		
		#set( $MinLength = $attributeDescriptor.getMetaInfoValueFor("MinLength") )
		
		#if ( $classDescriptor.isValueNotAvailable($MinLength) )

			#set( $isFieldLengthRelevantForJavaType = "false" )
		
		#end						
		
		#if ( $isFieldLengthRelevantForJavaType == "true" )
		   	
			#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
			
			'	
			'	@Test
			'	public void throwsExceptionIfField${AttributeName}UndercutsMinLength() {
			
			#parse("commonSubtemplates/setContentSmallerThanMinLengthToField.tpl")
						
			'
			'		try {
			'			${classDescriptor.simpleName}Validator.doYourJob(testData);
			'		} catch (Exception e) {
			'			assertEquals("error message", "A validation error exists for '$classDescriptor.simpleName':" + System.getProperty("line.separator")   
			'			                               + "MinLengthValidator: Minimum length for field '$attributeName' ($MinLength)"   
			'										   + " is not reached by value '" + testData.get${AttributeName}() + "'", e.getMessage());
			'			return;
			'		}
			'		fail("Expected exception not thrown!");
			'	}

		#end
			
	#end
	
#end
