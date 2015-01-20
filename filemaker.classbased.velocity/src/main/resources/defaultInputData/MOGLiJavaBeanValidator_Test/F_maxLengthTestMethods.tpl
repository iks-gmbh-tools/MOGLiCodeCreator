
#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#if ( $attributeDescriptor.doesHaveAnyMetaInfosWithName("MaxLength") )

		#set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
		#set( $MaxLength = $attributeDescriptor.getMetaInfoValueFor("MaxLength") )
		
		#parse("commonSubtemplates/isFieldLengthRelevantForJavaType.tpl")		
		
		#if ( $classDescriptor.isValueNotAvailable($MaxLength) )

			#set( $isFieldLengthRelevantForJavaType = "false" )
		
		#end						
		
		#if ( $isFieldLengthRelevantForJavaType == "true" )

			#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )	
	
			'	
			'	@Test
			'	public void throwsExceptionIfField${AttributeName}ExceedsMaxLength() {
		
			#parse("commonSubtemplates/setContentLargerThanMaxLengthToField.tpl")
		
			'		
			'		try {
			'			${classDescriptor.simpleName}Validator.doYourJob(testData);
			'		} catch (Exception e) {
			'			assertEquals("error message", "A validation error exists for '$classDescriptor.simpleName':" + System.getProperty("line.separator")   
			'                                          + "MaxLengthValidator: Max Length for field '$attributeName' ($MaxLength) is exceeded by current value '"
			'                                          + testData.get${AttributeName}() + "'.", e.getMessage());
			'			return;
			'		}
			'		fail("Expected exception not thrown!");
			'	}

		#end
			
	#end
	
#end

