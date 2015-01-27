'	@Test
'	public void buildsExample${classDescriptor.simpleName}Instance() {
'		${classDescriptor.simpleName}Builder builder = new ${classDescriptor.simpleName}Builder();
'
		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
			#set( $ExampleData = $attributeDescriptor.getMetaInfoValueFor("ExampleData") )
			#set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
			
			#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
			
			#if ( $isJavaTypeDomainObject.equals( "true" ) )
			
				'		builder = builder.with${AttributeName}( ${javaType}Factory.getById("$ExampleData") );
			
			#elseif ( $javaType == "String[]" )
					
				'		final String[] strArr${AttributeName} = CollectionsStringUtils.commaSeparatedStringToStringArray( "$ExampleData" );
				'		builder = builder.with${AttributeName}( strArr${AttributeName} );
			
			#elseif ( $javaType == "java.util.HashSet<String>" )
			
				'		final HashSet<String> strHashSet${AttributeName} = CollectionsStringUtils.commaSeparatedStringToHashSet( "$ExampleData" );
				'		builder = builder.with${AttributeName}( strHashSet${AttributeName} );
						
			#elseif ( $javaType == "java.util.List<String>" )
			
				'		final List<String> strList${AttributeName} = CollectionsStringUtils.commaSeparatedStringToStringList( "$ExampleData" );
				'		builder = builder.with${AttributeName}( strList${AttributeName} );
					
			#elseif ( $javaType == "java.util.List<Long>" )
			
				'		final List<Long> list${AttributeName} = CollectionsStringUtils.commaSeparatedStringToLongList( "$ExampleData" );
				'		builder = builder.with${AttributeName}( list${AttributeName} );
					
			#elseif ( $javaType == "String" )
			
				'		builder = builder.with${AttributeName}("$ExampleData");

			#elseif ( $javaType == "byte" )
			
				'		builder = builder.with${AttributeName}((byte) $ExampleData);

			#elseif ( $javaType == "Byte" )
			
				'		builder = builder.with${AttributeName}( new Byte( "$ExampleData" ) );

			#elseif ( $javaType == "Character" )
			
				'		builder = builder.with${AttributeName}( new Character('$ExampleData') );
			
			#elseif ( $javaType == "char" )
			
				'		builder = builder.with${AttributeName}('$ExampleData');
			
			#elseif ( $javaType == "float" )
			
				'		builder = builder.with${AttributeName}(${ExampleData}F);
				
			#elseif ( $javaType == "Float" )
			
				'		builder = builder.with${AttributeName}(new Float(${ExampleData}F));

			#elseif ( $javaType == "Long" )
			
				'		builder = builder.with${AttributeName}(new Long(${ExampleData}));

<<<<<<< HEAD
			#elseif ( $javaType == "java.math.BigDecimal" )
			
				'		builder = builder.with${AttributeName}( new BigDecimal("" + ${ExampleData}) );
				
=======
			#elseif ( $javaType == "java.math.BigDecimal" || $javaType == "BigDecimal" )
			
				'		builder = builder.with${AttributeName}( new BigDecimal("" + ${ExampleData}) );
				
			#elseif ( $javaType == "org.joda.time.DateTime" || $javaType == "DateTime" )
			
				'		builder = builder.with${AttributeName}( dateTimeFormatter.parseDateTime( "${ExampleData}" ) );
				
>>>>>>> 656c84c58ad794ed34c58c30ecc9bf656c921412
			#else
			
				'		builder = builder.with${AttributeName}($ExampleData);
		
			#end			
			
		#end
'
'		${classDescriptor.simpleName} instance = builder.build();
'
		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
			#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
			#set( $ExampleData = $attributeDescriptor.getMetaInfoValueFor("ExampleData") )
			#set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
			
			#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
			
			#if ( $isJavaTypeDomainObject.equals( "true" ) )
			
				'		assertEquals("$attributeName", ${javaType}Factory.getById("$ExampleData"), instance.get${AttributeName}() );
			
			#elseif ( $javaType == "String[]" )
					
				'		assertEquals("$attributeName", "$ExampleData", CollectionsStringUtils.stringArrayToCommaSeparatedString( instance.get${AttributeName}() ));
			
			#elseif ( $javaType == "java.util.HashSet<String>" )
			
				'		assertEquals("$attributeName", "$ExampleData", CollectionsStringUtils.stringHashSetToCommaSeparatedString( instance.get${AttributeName}() ));
						
			#elseif ( $javaType == "java.util.List<String>" )
			
				'		assertEquals("$attributeName", "$ExampleData", CollectionsStringUtils.stringListToCommaSeparatedString( instance.get${AttributeName}() ));
						
			#elseif ( $javaType == "java.util.List<Long>" )
			
				'		assertEquals("$attributeName", "$ExampleData", CollectionsStringUtils.listOfLongsToCommaSeparatedString( instance.get${AttributeName}() ));
						
			#elseif ( $TemplateJavaUtility.isJavaMetaTypeCollection($javaType) )
			
				'		assertEquals("$attributeName", "$ExampleData", instance.get${AttributeName}());
						
			#elseif ( $javaType == "String" )
			
				'		assertEquals("$attributeName", "$ExampleData", instance.get${AttributeName}());

<<<<<<< HEAD
=======
			#elseif ( $javaType == "org.joda.time.DateTime" || $javaType == "DateTime" )
			
				'		assertEquals("$attributeName", dateTimeFormatter.parseDateTime( "${ExampleData}" ), instance.get${AttributeName}());
				
>>>>>>> 656c84c58ad794ed34c58c30ecc9bf656c921412
			#else
			
				'		assertEquals("$attributeName", "$ExampleData", "" + instance.get${AttributeName}());
		
			#end			

			
			
		#end
'	}
