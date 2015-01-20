#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
	#set( $ValidChars = $attributeDescriptor.getMetaInfoValueFor("ValidChars") )
	#set( $InvalidChars = $attributeDescriptor.getMetaInfoValueFor("InvalidChars") )
	#set( $generateMethodForCurrentAttribute = "false")
	#set( $Validator = "")
	
	#if ( $attributeDescriptor.isValueAvailable($ValidChars) )
		#set( $Validator = "ValidCharFieldValidator")
		#set( $generateMethodForCurrentAttribute = "true")
	#end
	
	#if ( $attributeDescriptor.isValueAvailable($InvalidChars) )
		#set( $Validator = "InvalidCharFieldValidator")
		#set( $generateMethodForCurrentAttribute = "true")
	#end
	
	#if ( $generateMethodForCurrentAttribute == "true" )
	
	
		'	@Test
		'	public void throwsExceptionForInvalidCharInField_$AttributeName() {
		'		testData.set${AttributeName}("123 ° 567");

		#set( $counter = $counter + 1 )
	
		'
		'		try {
		'			${classDescriptor.simpleName}Validator.doYourJob(testData);
		'		} catch (Exception e) {
		'			assertEquals("error message", "A validation error exists for '$classDescriptor.simpleName':" + System.getProperty("line.separator")
		'                                         + "${Validator}: Field '$attributeDescriptor.name' contains invalid char(s): [°]", e.getMessage());
		'		return;
		'		}
		'
		'		fail("Expected exception not thrown!");
		'	}
	
	#end
		
#end
