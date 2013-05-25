
#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#if ( $attributeDescriptor.doesHaveAnyMetaInfosWithName("MinLength") )

		#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
		#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
		#set( $javaType = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor.getMetaInfoValueFor("JavaType") ) )
	
		'	
		'	@Test
		'	public void throwsExceptionIfField${AttributeName}UndercutsMinLength() {
		'		testData.set${AttributeName}("1");
		'		try {
		'			${classDescriptor.simpleName}Validator.doYourJob(testData);
		'		} catch (Exception e) {
		'			assertEquals("error message", "MinLengthValidator: Minimum length for field '$attributeName' (2)"   
		'										   + " not reached: "
		'                                          + testData.get${AttributeName}(), e.getMessage());
		'			return;
		'		}
		'		fail("Expected exception not thrown!");
		'	}
	
	#end
	
#end
