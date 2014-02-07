
'	@Before
'	public void setup() {
'		final ${classDescriptor.simpleName}Builder builder = new ${classDescriptor.simpleName}Builder();
'		testData = builder

		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
			#set( $exampleData = $attributeDescriptor.getMetaInfoValueFor("ExampleData") )
	
			#if ( $attributeDescriptor.doesHaveMetaInfo("JavaType", "String") )
	
				'		          .with$AttributeName("$exampleData")
	
			#else
	
				'		          .with$AttributeName($exampleData)
	
			#end
			
		#end
		
				'		          .build();
'	}
