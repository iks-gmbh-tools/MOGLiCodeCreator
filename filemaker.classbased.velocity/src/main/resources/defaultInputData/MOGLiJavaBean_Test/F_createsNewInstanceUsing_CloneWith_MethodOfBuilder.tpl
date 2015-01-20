
#if ($classDescriptor.attributeDescriptorList.size() > 1)

	#set($Q = '"')
	
	#set( $attributeDescriptor1 = $classDescriptor.attributeDescriptorList.get(0) )	
	#set( $field1 = $TemplateStringUtility.firstToUpperCase($attributeDescriptor1.name) )
	#set( $javaType1 = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor1.getMetaInfoValueFor("JavaType") ) )
	#set( $ExampleData1 = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor1.getMetaInfoValueFor("ExampleData") ) )	
    #set( $reason1 = "" )
	
	#set( $attributeDescriptor2 = $classDescriptor.attributeDescriptorList.get(1) )
	#set( $field2 = $TemplateStringUtility.firstToUpperCase($attributeDescriptor2.name) )
	#set( $javaType2 = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor2.getMetaInfoValueFor("JavaType") ) )	
	#set( $ExampleData2 = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor2.getMetaInfoValueFor("ExampleData") ) )	
    #set( $reason2 = "" )

	#if ($javaType1 == "String")
        #set( $replacement1 = "New Content for Attribute "+ $attributeDescriptor1.name )
		#set( $replacement1 = $Q + $replacement1 + $Q )
	#elseif ($javaType1 == "int" || $javaType1 == "Integer")
		#set( $replacement1 = "9" + $ExampleData1 )
	#elseif ($javaType1 == "long" || $javaType1 == "Long")
		#set( $replacement1 = "9" + $ExampleData1 + "L" )
	#elseif ($javaType1 == "boolean" || $javaType1 == "Boolean")
		#if ($ExampleData1 == "true")
			#set( $replacement1 = "false" )
		#else
			#set( $replacement1 = "true" )
		#end
	#else
		#set( $reason1 = "javaType of first field not supported for this test" )
	#end

	#if ($javaType2 == "String")
		#set( $replacement2 = "New Content for Attribute "+ $attributeDescriptor2.name )
		#set( $replacement2 = $Q + $replacement2 + $Q )
	#elseif ($javaType2 == "int" || $javaType2 == "Integer")
		#set( $replacement2 = "9" + $ExampleData2 )
	#elseif ($javaType2 == "long" || $javaType2 == "Long")
		#set( $replacement2 = "9" + $ExampleData2 + "L" )
	#elseif ($javaType2 == "boolean" || $javaType2 == "Boolean")
		#if ($ExampleData2 == "true")
			#set( $replacement2 = "false" )
		#else
			#set( $replacement2 = "true" )
		#end
	#else
		#set( $reason2 = "javaType of second field not supported for this test" )
	#end
	
	#if ($attributeDescriptor1.isValueAvailable($replacement1) && $attributeDescriptor2.isValueAvailable($replacement2))
	
		'	@Test
		'	public void createsNew${classDescriptor.simpleName}InstancesUsing_CloneWith_MethodOfBuilder() {
		'		// arrange
		'		final ${classDescriptor.simpleName}Builder builder0 = new ${classDescriptor.simpleName}Builder( ${className}1 );
		'
		'		// act 1
		'		final ${classDescriptor.simpleName}Builder builder1 = builder0.cloneWith$field1($replacement1); 
		'		final ${classDescriptor.simpleName} clone1 = builder1.build(); // create clone modified in field1
		'		final ${classDescriptor.simpleName}Builder builder2 = builder0.cloneWith$field2($replacement2);  
		'		final ${classDescriptor.simpleName} clone2 = builder2.build(); // create clone modified in field2
		'		
		'		// assert 1a: clone of builder1 differs from ${className}1 only in field 1
		'		final String expected1 = "" + $replacement1 + " / " +  ${className}1.get$field2();
		'		final String actual1 =  "" + clone1.get$field1() + " / " + clone1.get$field2();
		'		
		'		System.out.print("Is '" + expected1 + "'   ==   '" + actual1 + "'   ?");
		'		assertEquals(expected1, actual1);
		'		System.out.println(" - YES!");
		'		
		'		// assert 1b: clone of builder2 differs from ${className}1 only in field 2
		'		final String expected2 =  "" + ${className}1.get$field1()  + " / " + $replacement2;
		'		final String actual2 =  "" + clone2.get$field1() + " / " + clone2.get$field2();
		'
		'		System.out.print("Is '" + expected2 + "'   ==   '" + actual2 + "'   ?");
		'		assertEquals(expected2, actual2);
		'		System.out.println(" - YES!");
		'
		'		// act 2
		'		final String stringRepresentation1 = builder1.cloneWith$field2($replacement2).build().toString();
		'		final String stringRepresentation2 = builder2.cloneWith$field1($replacement1).build().toString();
		'		
		'		// assert 2: two clones have identical values 
		'		System.out.print("Is '" + stringRepresentation1  + "'   ==   '" + stringRepresentation2 + "'   ?");
		'		assertEquals(stringRepresentation1, stringRepresentation2);
		'		System.out.println(" - YES!");
		'	}
	
	#else
	
		'	// method 'createsNew${classDescriptor.simpleName}InstancesUsing_CloneWith_MethodOfBuilder()' could not be created, because
		'	// $reason1	
		'	// $reason2	
	#end
#else 
	'	// method 'createsNew${classDescriptor.simpleName}InstancesUsing_CloneWith_MethodOfBuilder()' could not be created, because
	'	// only one attribute exists for class '${classDescriptor.simpleName}'
#end