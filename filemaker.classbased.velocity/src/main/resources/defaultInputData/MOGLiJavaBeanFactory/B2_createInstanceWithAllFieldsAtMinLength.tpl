'	/**
'	 * @return the instance with all fields with the minimum allowed number of chars
'	 */
'	public static ${classDescriptor.simpleName} createInstanceWithAllFieldsAtMinLength()
'	{

		#set( $useJavaBeanRegistry = $model.getMetaInfoValueFor("useJavaBeanRegistry") )

'		final ${classDescriptor.simpleName} toReturn = new ${classDescriptor.simpleName}();
'
		
		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) ) 
		    #set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )

			#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
			#parse("commonSubtemplates/checkForJavaTypeListOfDomainObjects.tpl")

			#if ( $isJavaTypeListOfDomainObjects == "true" )
				
				'
				'		final List<${ElementType}> list${AttributeName} = new ArrayList<${ElementType}>();
				
				
				#if ( $useJavaBeanRegistry == "true")
				
					'		list${AttributeName}.add( ${ElementType}Factory.createByIndex(1) );   // Using minimum length in referenced domain objects not necessary!
				
				#else
				
					'		list${AttributeName}.add( ${ElementType}Factory.createInstanceWithAllFieldsAtMinLength() );
		    		
				#end
				
				
		    	'		toReturn.set${AttributeName}( list${AttributeName} );
			
			#elseif ( $isJavaTypeDomainObject.equals( "true" ) )


				#if ( $useJavaBeanRegistry == "true")
				
					'		toReturn.set${AttributeName}( ${javaType}Factory.createByIndex(1) );  // Using minimum length in referenced domain objects not necessary! 
				
				#else
				
		    		'		toReturn.set${AttributeName}( ${javaType}Factory.createInstanceWithAllFieldsAtMinLength() );
		    		
				#end
		    
		     
		    #elseif ($javaType == "String")
		    
				'		toReturn.set${AttributeName}( MOGLiFactoryUtils.createStringValue("i", getMinLength("$AttributeName").intValue() ) );

		    #elseif ($javaType == "byte" || $javaType == "Byte")

				'		toReturn.set${AttributeName}( MOGLiFactoryUtils.createByteValue( getMinLength("$AttributeName").intValue() ) );

		    #elseif ($javaType == "int" || $javaType == "Integer")

				'		toReturn.set${AttributeName}( MOGLiFactoryUtils.createIntValue( getMinLength("$AttributeName").intValue() ) );

		    #elseif ($javaType == "long" || $javaType == "Long")

				'		toReturn.set${AttributeName}( MOGLiFactoryUtils.createLongValue( getMinLength("$AttributeName").intValue() ) );

		    #elseif ($javaType == "BigDecimal" || $javaType == "java.math.BigDecimal")
		    
				'		toReturn.set${AttributeName}( new BigDecimal( "" + MOGLiFactoryUtils.createLongValue(getMinLength("$AttributeName").intValue() ) ) );

		    #elseif ($javaType == "Boolean")
		    
				'		toReturn.set${AttributeName}( Boolean.FALSE );
				
		    #elseif ($javaType == "float" || $javaType == "Float")

				'		toReturn.set${AttributeName}( Float.valueOf("0") );

		    #elseif ($javaType == "double" || $javaType == "Double")

				'		toReturn.set${AttributeName}( Double.valueOf("0") );

		    #elseif ($javaType == "Character")
		    
				'		toReturn.set${AttributeName}( Character.MIN_VALUE );
				
			#elseif ($javaType == "String[]")
			
				'		String[] strArr = new String[0];
				'		toReturn.set${AttributeName}( strArr );
			
			#elseif ( $TemplateJavaUtility.isJavaMetaTypeGeneric($javaType) )
			
		    	#set( $CollectionType = $TemplateJavaUtility.getCollectionMetaType($javaType) ) 
			 	
				#if ($CollectionType == "java.util.List")

		    		#set( $ElementType = $TemplateJavaUtility.getCollectionElementType($javaType) )
		    		
		    		'		List<$ElementType> list${ElementType} = new ArrayList<$ElementType>();
					'		toReturn.set${AttributeName}( list${ElementType} );
				
				#else
				
					'		// Unkown CollectionType: $collectionType 
					
				#end			    #else
		    
				# do nothing for other types, e.g. collection types or boolean

		    #end
		#end
'			
'		return toReturn;
'	}
