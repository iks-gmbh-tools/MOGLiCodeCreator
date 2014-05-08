
#if ($classDescriptor.attributeDescriptorList.size() > 1)

	#set($Q = '"')
	
	#set( $attributeDescriptor1 = $classDescriptor.attributeDescriptorList.get(0) )	
	#set( $field1 = $TemplateStringUtility.firstToUpperCase($attributeDescriptor1.name) )
	#set( $javaType1 = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor1.getMetaInfoValueFor("JavaType") ) )
	#set( $replacement1 = $attributeDescriptor1.getMetaInfoValueFor("TestExampleData") )
	
	#set( $attributeDescriptor2 = $classDescriptor.attributeDescriptorList.get(1) )
	#set( $field2 = $TemplateStringUtility.firstToUpperCase($attributeDescriptor2.name) )
	#set( $javaType2 = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor2.getMetaInfoValueFor("JavaType") ) )	
	#set( $replacement2 = $attributeDescriptor2.getMetaInfoValueFor("TestExampleData") )
	
	#if ($attributeDescriptor1.isValueAvailable($replacement1) && $attributeDescriptor2.isValueAvailable($replacement2))
	
		#if ($javaType1 == "String")
			#set( $replacement1 = $Q + $replacement1 + $Q )
		#end

		#if ($javaType2 == "String")
			#set( $replacement2 = $Q + $replacement2 + $Q )
		#end
	
	
		'	@Test
		'	public void createsNew${classDescriptor.simpleName}InstancesUsing_CloneWith_MethodOfBuilder() {
		'		final ${classDescriptor.simpleName}Builder builder0 = new ${classDescriptor.simpleName}Builder( ${className}1);
		'		final ${classDescriptor.simpleName}Builder builder1 = builder0.cloneWith$field1($replacement1); 
		'		final ${classDescriptor.simpleName}Builder builder2 = builder0.cloneWith$field2($replacement2);  
		'		final ${classDescriptor.simpleName} buildResult1 = builder1.build();
		'		final ${classDescriptor.simpleName} buildResult2 = builder2.build();
		'		
		'		final String expected1 = "" + $replacement1 +  ${className}1.get$field2();
		'		final String actual1 =  "" + buildResult1.get$field1() + buildResult1.get$field2();
		'		
		'		System.out.print("Is '" + expected1 + "'   ==   '" + actual1 + "'   ?");
		'		assertEquals(expected1, actual1);
		'		System.out.println(" - YES!");
		'		
		'		final String expected2 =  "" + ${className}1.get$field1() + $replacement2;
		'		final String actual2 =  "" + buildResult2.get$field1() + buildResult2.get$field2();
		'
		'		System.out.print("Is '" + expected2 + "'   ==   '" + actual2 + "'   ?");
		'		assertEquals(expected2, actual2);
		'		System.out.println(" - YES!");
		'
		'		final String stringRepresentation1 = builder1.cloneWith$field2($replacement2).build().toString();
		'		final String stringRepresentation2 = builder2.cloneWith$field1($replacement1).build().toString();
		'		
		'		System.out.print("Is '" + stringRepresentation1  + "'   ==   '" + stringRepresentation2 + "'   ?");
		'		assertEquals(stringRepresentation1, stringRepresentation2);
		'		System.out.println(" - YES!");
		'	}
	
	#end
#end