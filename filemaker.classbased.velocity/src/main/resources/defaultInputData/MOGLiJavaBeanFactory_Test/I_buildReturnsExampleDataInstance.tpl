'	@Test
'	public void buildsReturnsExampleDataInstance() {
'		final ${classDescriptor.simpleName} instance = ${classDescriptor.simpleName}Factory.createInstanceWithExampleData();
'
		#set( $useExampleData = "true" )
		#parse("commonSubtemplates/generateListOfDomainObjectsFromExampleDataOrDataPool.tpl")
		
		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
			#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
			#set( $ExampleData = $attributeDescriptor.getMetaInfoValueFor("ExampleData") )
			#set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
			#set( $enumList = $classDescriptor.getAllMetaInfoValuesFor("Enum") )
			#parse("commonSubtemplates/checkForJavaTypeListOfDomainObjects.tpl")

			#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
			
		    #if ( $TemplateStringUtility.contains($enumList, $javaType) )
			
				'		assertEquals("$attributeName", "$ExampleData", "${javaType}." + instance.get${AttributeName}());
			
			#elseif ( $isJavaTypeDomainObject.equals( "true" ) )
			
				'		assertEquals("$attributeName", ${javaType}Factory.createById("$ExampleData"), instance.get${AttributeName}() );
			
			#elseif ( $javaType == "String[]" )
					
				'		assertEquals("$attributeName", "$ExampleData", CollectionsStringUtils.stringArrayToCommaSeparatedString( instance.get${AttributeName}() ));
			
			#elseif ( $javaType == "java.util.HashSet<String>" )
			
				'		assertEquals("$attributeName", "$ExampleData", CollectionsStringUtils.stringHashSetToCommaSeparatedString( instance.get${AttributeName}() ));
						
			#elseif ( $javaType == "java.util.List<String>" )
			
				'		assertEquals("$attributeName", "$ExampleData", CollectionsStringUtils.stringListToCommaSeparatedString( instance.get${AttributeName}() ));
						
			#elseif ( $javaType == "java.util.List<Long>" )
			
				'		assertEquals("$attributeName", "$ExampleData", CollectionsStringUtils.listOfLongsToCommaSeparatedString( instance.get${AttributeName}() ));
						
			#elseif ( $TemplateJavaUtility.isJavaMetaTypeCollection($javaType) )
			
				#set( $ElementType = $TemplateJavaUtility.getCollectionElementType($javaType) )
    			#set( $elementType = $TemplateStringUtility.firstToLowerCase($ElementType) ) 
			
				'		assertEquals("$attributeName", ${elementType}List, instance.get${AttributeName}());
						
			#elseif ( $javaType == "String" )
			
				'		assertEquals("$attributeName", "$ExampleData", instance.get${AttributeName}());

			#elseif ( $javaType == "org.joda.time.DateTime" || $javaType == "DateTime" )
			
				'		assertEquals("$attributeName", dateTimeFormatter.parseDateTime( "${ExampleData}" ), instance.get${AttributeName}());
				
			#else
			
				'		assertEquals("$attributeName", "$ExampleData", "" + instance.get${AttributeName}());
		
			#end			

			
			
		#end
'	}
