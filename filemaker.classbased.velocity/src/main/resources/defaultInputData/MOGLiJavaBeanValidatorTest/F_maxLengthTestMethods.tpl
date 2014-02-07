
#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#if ( $attributeDescriptor.doesHaveAnyMetaInfosWithName("MaxLength") )

		#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
		#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
		#set( $javaType = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor.getMetaInfoValueFor("JavaType") ) )
	
		'	
		'	@Test
		'	public void throwsExceptionIfField${AttributeName}ExceedsMaxLength() {
		'		testData.set${AttributeName}("1234567890123");
		'		try {
		'			${classDescriptor.simpleName}Validator.doYourJob(testData);
		'		} catch (Exception e) {
		'			assertEquals("error message", "MaxLengthValidator: Max Length for field '$attributeName' (10) exceeded: "
		'                                          + testData.get${AttributeName}().length(), e.getMessage());
		'			return;
		'		}
		'		fail("Expected exception not thrown!");
		'	}
	
	#end
	
#end

