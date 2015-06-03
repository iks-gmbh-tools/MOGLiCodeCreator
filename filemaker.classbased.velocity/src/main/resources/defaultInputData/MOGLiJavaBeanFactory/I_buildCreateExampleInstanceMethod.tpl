'	public static ${classDescriptor.simpleName} createInstanceWithExampleData() {
'		${classDescriptor.simpleName}Builder builder = new ${classDescriptor.simpleName}Builder();
'
		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
			#set( $ExampleData = $attributeDescriptor.getMetaInfoValueFor("ExampleData") )
			#set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
			#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
		 	
			#if ( $isJavaTypeDomainObject.equals( "true" ) )
			
				'		builder = builder.with${AttributeName}( ${javaType}Factory.createById("$ExampleData") );
								
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

			#elseif ( $javaType == "java.math.BigDecimal" || $javaType == "BigDecimal" )
			
				'		builder = builder.with${AttributeName}( new BigDecimal("" + ${ExampleData}) );
				
			#elseif ( $javaType == "org.joda.time.DateTime" || $javaType == "DateTime" )
			
				'		builder = builder.with${AttributeName}( dateTimeFormatter.parseDateTime( "${ExampleData}" ) );
				
			#elseif ( $javaType == "String[]" )
					
				'		final String[] strArr${AttributeName} = CollectionsStringUtils.commaSeparatedStringToStringArray( "$ExampleData" );
				'		builder = builder.with${AttributeName}( strArr${AttributeName} );
			
			#elseif ( $javaType == "java.util.HashSet<String>" )
			
				'		final HashSet<String> strHashSet${AttributeName} = CollectionsStringUtils.commaSeparatedStringToHashSet( "$ExampleData" );
				'		builder = builder.with${AttributeName}( strHashSet${AttributeName} );
				
			#elseif ( $TemplateJavaUtility.isJavaMetaTypeGeneric($javaType) )
			
		    	#set( $CollectionType = $TemplateJavaUtility.getCollectionMetaType($javaType) ) 
			 	
				#if ($CollectionType == "java.util.List")

		    		#set( $ElementType = $TemplateJavaUtility.getCollectionElementType($javaType) )
					
					#if ($ElementType == "Long")
					
						'		final List<Long> list${AttributeName} = CollectionsStringUtils.commaSeparatedStringToLongList( "$ExampleData" );
						'		builder = builder.with${AttributeName}( list${AttributeName} );
		    		
		    		#elseif	($ElementType == "String")
		    		
						'		final List<String> strList${AttributeName} = CollectionsStringUtils.commaSeparatedStringToStringList( "$ExampleData" );
						'		builder = builder.with${AttributeName}( strList${AttributeName} );
		    		
		    		#else
		    		
		    			#set( $elementType = $TemplateStringUtility.firstToLowerCase($ElementType) ) 
		    		
						'		final List<String> strList${AttributeName} = CollectionsStringUtils.commaSeparatedStringToStringList( "$ExampleData" );
						'		final java.util.List<${ElementType}> ${elementType}List = new ArrayList<${ElementType}>();
						'		for (final String element : strList${AttributeName}) {
						'			${ElementType} instanceById = ${ElementType}Factory.createById(element);
						'			if ( instanceById != null)
						'				${elementType}List.add(${ElementType}Factory.createById(element));
						'		}						
						'		builder = builder.with$AttributeName( ${elementType}List );
						
						 
		    		#end
				
				#else
				
						'		// Unkown CollectionType: $collectionType 
					
				#end 
				
			#else
			
				'		builder = builder.with${AttributeName}($ExampleData);
		
			#end			
			
		#end
'
'		return builder.build();
'	}
