
'	@Before
'	public void setup() {
'		final ${classDescriptor.simpleName}Builder builder = new ${classDescriptor.simpleName}Builder();
'		testData = builder

		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
<<<<<<< HEAD
			#set( $exampleData = $attributeDescriptor.getMetaInfoValueFor("ExampleData") )
=======
			#set( $ExampleData = $attributeDescriptor.getMetaInfoValueFor("ExampleData") )
>>>>>>> 656c84c58ad794ed34c58c30ecc9bf656c921412
		    #set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
	
			#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")

			#if ( $isJavaTypeDomainObject.equals( "true" ) )
	
<<<<<<< HEAD
				'		          .with${AttributeName}( ${javaType}Factory.getById( "$exampleData" ) )
	
			#elseif ( $javaType == "String" )
	
				'		          .with$AttributeName("$exampleData")
	
			#elseif ( $javaType == "String[]" )
					
				'		          .with$AttributeName( CollectionsStringUtils.commaSeparatedStringToStringArray( "$exampleData" ) )
			
			#elseif ( $javaType == "java.util.HashSet<String>" )
			
				'		          .with$AttributeName( CollectionsStringUtils.commaSeparatedStringToHashSet( "$exampleData" ) )
						
			#elseif ( $javaType == "java.util.List<String>" )
			
				'		          .with$AttributeName( CollectionsStringUtils.commaSeparatedStringToStringList( "$exampleData" ) )
				
			#elseif ( $javaType == "java.util.List<Long>" )
			
				'		          .with$AttributeName( CollectionsStringUtils.commaSeparatedStringToLongList( "$exampleData" ) )
				
			#elseif ( $javaType == "byte" )
			
				'		          .with$AttributeName((byte) $exampleData)
			
			#elseif ( $javaType == "Byte" )
			
				'		          .with$AttributeName( new Byte( "$exampleData") )
			
			#elseif ( $javaType == "char" )
	
				'		          .with$AttributeName('$exampleData')
	
			#elseif ( $javaType == "Character" )
			
				'		          .with$AttributeName( new Character('$exampleData'))
			
			#elseif ( $javaType == "float" )
			
				'		          .with$AttributeName(${exampleData}F)

			#elseif ( $javaType == "Float" )
			
				'		          .with$AttributeName(new Float(${exampleData}F))
				
			#elseif ( $javaType == "Long" )
			
				'		          .with$AttributeName( new Long(${exampleData}) )
				
			#elseif ( $javaType == "java.math.BigDecimal" )
			
				'		          .with$AttributeName( new BigDecimal("" + ${exampleData}) )

			#else
	
				'		          .with$AttributeName($exampleData)
=======
				'		          .with${AttributeName}( ${javaType}Factory.getById( "${ExampleData}" ) )
	
			#elseif ( $javaType == "String" )
	
				'		          .with$AttributeName("${ExampleData}")
	
			#elseif ( $javaType == "String[]" )
					
				'		          .with$AttributeName( CollectionsStringUtils.commaSeparatedStringToStringArray( "${ExampleData}" ) )
			
			#elseif ( $javaType == "java.util.HashSet<String>" )
			
				'		          .with$AttributeName( CollectionsStringUtils.commaSeparatedStringToHashSet( "${ExampleData}" ) )
						
			#elseif ( $javaType == "java.util.List<String>" )
			
				'		          .with$AttributeName( CollectionsStringUtils.commaSeparatedStringToStringList( "${ExampleData}" ) )
				
			#elseif ( $javaType == "java.util.List<Long>" )
			
				'		          .with$AttributeName( CollectionsStringUtils.commaSeparatedStringToLongList( "${ExampleData}" ) )
				
			#elseif ( $javaType == "byte" )
			
				'		          .with$AttributeName((byte) ${ExampleData})
			
			#elseif ( $javaType == "Byte" )
			
				'		          .with$AttributeName( new Byte( "${ExampleData}") )
			
			#elseif ( $javaType == "char" )
	
				'		          .with$AttributeName('${ExampleData}')
	
			#elseif ( $javaType == "Character" )
			
				'		          .with$AttributeName( new Character('${ExampleData}'))
			
			#elseif ( $javaType == "float" )
			
				'		          .with$AttributeName(${ExampleData}F)

			#elseif ( $javaType == "Float" )
			
				'		          .with$AttributeName(new Float(${ExampleData}F))
				
			#elseif ( $javaType == "Long" )
			
				'		          .with$AttributeName( new Long(${ExampleData}) )
				
			#elseif ( $javaType == "java.math.BigDecimal" || $javaType == "BigDecimal" )
			
				'		          .with$AttributeName( new BigDecimal("" + ${ExampleData}) )

			#elseif ( $javaType == "org.joda.time.DateTime" || $javaType == "DateTime" )
			
				'		          .with$AttributeName( dateTimeFormatter.parseDateTime( "${ExampleData}" ) )
				
			#else
	
				'		          .with$AttributeName(${ExampleData})
>>>>>>> 656c84c58ad794ed34c58c30ecc9bf656c921412
				
			#end
			
		#end
		
				'		          .build();
'	}
