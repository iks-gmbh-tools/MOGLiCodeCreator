'	@Test
'	public void returnsFirst() {
'		final List<${classDescriptor.simpleName}> result = ${classDescriptor.simpleName}Factory.createFirstFromDataPool(1);
'		assertNotNull("Not null expected for ", result);
'		assertEquals("result", 1, result.size());

		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
			#set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
			#set( $testDataMetaInfoName = ${classDescriptor.simpleName} + "1" )
	  		#set( $testDataMetaInfoValue = $attributeDescriptor.getMetaInfoValueFor( $testDataMetaInfoName ) )
			
			#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
			
			#if ( $isJavaTypeDomainObject.equals( "true" ) )
			
				'		assertEquals("$AttributeName of type $javaType", ${javaType}Factory.getById("$testDataMetaInfoValue").toString(), result.get(0).get${AttributeName}().toString());
			
			#elseif ( $javaType == "String[]" )
					
				'		assertEquals("size of $AttributeName", CollectionsStringUtils.commaSeparatedStringToStringArray("$testDataMetaInfoValue").length, result.get(0).get${AttributeName}().length );
				'		assertEquals("$AttributeName of type $javaType", "$testDataMetaInfoValue", CollectionsStringUtils.stringArrayToCommaSeparatedString( result.get(0).get${AttributeName}() ));
			
			#elseif ( $javaType == "java.util.HashSet<String>" )
			
				'		assertEquals("size of $AttributeName", CollectionsStringUtils.commaSeparatedStringToHashSet("$testDataMetaInfoValue").size(), result.get(0).get${AttributeName}().size() );
				'		assertEquals("$AttributeName of type $javaType", "$testDataMetaInfoValue", CollectionsStringUtils.stringHashSetToCommaSeparatedString( result.get(0).get${AttributeName}() ));
						
			#elseif ( $javaType == "java.util.List<String>" )
			
				'		assertEquals("size of $AttributeName", CollectionsStringUtils.commaSeparatedStringToStringList("$testDataMetaInfoValue").size(), result.get(0).get${AttributeName}().size() );
				'		assertEquals("$AttributeName of type $javaType", "$testDataMetaInfoValue", CollectionsStringUtils.stringListToCommaSeparatedString( result.get(0).get${AttributeName}() ));
					
			#elseif ( $javaType == "java.util.List<Long>" )
			
				'		assertEquals("size of $AttributeName", CollectionsStringUtils.commaSeparatedStringToLongList("$testDataMetaInfoValue").size(), result.get(0).get${AttributeName}().size() );
				'		assertEquals("$AttributeName of type $javaType", "$testDataMetaInfoValue", CollectionsStringUtils.listOfLongsToCommaSeparatedString( result.get(0).get${AttributeName}() ));
					
			#elseif ( $javaType == "String" )
			
				'		assertEquals("$AttributeName of type $javaType", "$testDataMetaInfoValue", result.get(0).get${AttributeName}() );
				
			#elseif ( $javaType == "org.joda.time.DateTime" || $javaType == "DateTime" )
			
				'		assertEquals("$AttributeName of type $javaType", dateTimeFormatter.parseDateTime( "$testDataMetaInfoValue" ), result.get(0).get${AttributeName}() );

			#else
			
				'		assertEquals("$AttributeName of type $javaType", "$testDataMetaInfoValue", "" + result.get(0).get${AttributeName}() );
		
			#end			

		#end
'	}
