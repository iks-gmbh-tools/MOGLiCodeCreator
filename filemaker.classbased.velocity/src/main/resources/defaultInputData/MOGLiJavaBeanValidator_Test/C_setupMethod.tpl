
'	@Before
'	public void setup() {
'		final ${classDescriptor.simpleName}Builder builder = new ${classDescriptor.simpleName}Builder();
'		testData = builder

		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
			#set( $exampleData = $attributeDescriptor.getMetaInfoValueFor("ExampleData") )
		    #set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
	
			#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")

			#if ( $isJavaTypeDomainObject.equals( "true" ) )
	
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
				
			#end
			
		#end
		
				'		          .build();
'	}
