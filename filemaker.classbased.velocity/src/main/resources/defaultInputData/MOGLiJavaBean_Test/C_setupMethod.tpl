'	@Before
'	public void setup() {
'		final ${classDescriptor.simpleName}Builder builder = new ${classDescriptor.simpleName}Builder();
'		${className}1 = builder

		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
			#set( $ExampleData = $attributeDescriptor.getMetaInfoValueFor("ExampleData") )
		    #set( $javaType =  $attributeDescriptor.getMetaInfoValueFor("JavaType") )
	
			#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")

			#if ( $isJavaTypeDomainObject.equals( "true" ) )
	
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
				
			#elseif ( $javaType == "Long" )
			
				'		          .with$AttributeName( new Long(${ExampleData}) )
				
			#elseif ( $javaType == "Byte" )
			
				'		          .with$AttributeName( new Byte( "${ExampleData}") )
			
			#elseif ( $javaType == "Character" )
			
				'		          .with$AttributeName( new Character('${ExampleData}'))
			
			#elseif ( $javaType == "char" )
			
				'		          .with$AttributeName('${ExampleData}')
			
			#elseif ( $javaType == "byte" )
			
				'		          .with$AttributeName((byte) ${ExampleData})
				
			#elseif ( $javaType == "Character" )
			
				'		          .with$AttributeName( new Character('${ExampleData}'))
			
			#elseif ( $javaType == "float" )
			
				'		          .with$AttributeName(${ExampleData}F)
				
			#elseif ( $javaType == "Float" )
			
				'		          .with$AttributeName(new Float(${ExampleData}F))
				
			#elseif ( $javaType == "java.math.BigDecimal" || $javaType == "BigDecimal" )
			
				'		          .with$AttributeName( new BigDecimal("" + ${ExampleData}) )

			#elseif (  $javaType == "org.joda.time.DateTime" || $javaType == "DateTime" )
			
				'		          .with$AttributeName( dateTimeFormatter.parseDateTime( "${ExampleData}" ) )

			#else
	
				'		          .with$AttributeName(${ExampleData})
	
			#end
			
		#end
		
				'		          .build();
		'		${className}2 = builder.build();
'	}