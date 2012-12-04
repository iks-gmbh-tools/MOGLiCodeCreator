
#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
	#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
	#set( $javaType = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor.getMetaInfoValueFor("JavaType") ) )

	#if ( $TemplateJavaUtility.isJavaMetaTypePrimitive($javaType) )
		
		'
		'	// The field '$attributeName' has a primitive JavaType and cannot be empty.
		'	// Therefore there has not been generated a method testing the mandatory attribute!
		
	#else
	
		#if ( $attributeDescriptor.doesHaveMetaInfo("Mandatory", "true") )
		
			'
			'	@Test
			'	public void throwsExceptionIfMandatoryField${AttributeName}IsNotSet() {
			'		testData.set$AttributeName(null);
			'		try {
			'			${classDescriptor.simpleName}Validator.doYourJob(testData);
			'		} catch (Exception e) {
			'			assertEquals("error message", "MandatoryFieldValidator: Mandatory field '$attributeName' is empty.", e.getMessage());
			'			return;
			'		}
			'		fail("Expected exception not thrown!");
			'	}
		
		#else
		
			'
			'	@Test
			'	public void validatesWithoutErrorIfOptionalField${AttributeName}IsNotSet() {
			'		testData.set$AttributeName(null);
			'		${classDescriptor.simpleName}Validator.doYourJob(testData);
			'	}
			
		#end
	
	#end

#end