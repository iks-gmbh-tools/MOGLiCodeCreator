		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

		    #set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
		    
			#if ( $TemplateJavaUtility.isJavaMetaTypeGeneric($javaType) )
			
		    	#set( $CollectionType = $TemplateJavaUtility.getCollectionMetaType($javaType) ) 
			 	
				#if ($CollectionType == "java.util.List")

		    		#set( $ElementType = $TemplateJavaUtility.getCollectionElementType($javaType) )
					#set( $isElementTypeDomainObject = "false" )

					#foreach($classDescriptor in $model.classDescriptorList)
					
						#if ( $classDescriptor.getFullyQualifiedName().equals( $ElementType ) )
						
							#set( $isElementTypeDomainObject = "true" )
							
						#end
						
						#if ( $classDescriptor.getSimpleName().equals( $ElementType ) )
						
							#set( $isElementTypeDomainObject = "true" )
							
						#end
						
					#end
					
					#if ( $isElementTypeDomainObject.equals( "true" ) )
		    		
		    			#set( $elementType = $TemplateStringUtility.firstToLowerCase($ElementType) ) 
		    			
					
					    #if ( $useExampleData == "true")
						
							#set( $testInstancesToUse = $attributeDescriptor.getMetaInfoValueFor("ExampleData") )
						
						#elseif ( $useFirstFromDataPool == "true")
						
								#set( $testDataMetaInfoName =  $classDescriptor.simpleName + "1" )
						  		#set( $testInstancesToUse = $attributeDescriptor.getMetaInfoValueFor( $testDataMetaInfoName ) )
			
						#elseif ( $useLastFromDataPool == "true")
						
							#set( $metaInfoList = $attributeDescriptor.getMetaInfosWithNameStartingWith( $classDescriptor.simpleName ) )
					  		
					  		#if ($metaInfoList.size() == 0)
					  		
					  			'		// no data defined for $attributeDescriptor.name
						  		#set( $testInstancesToUse =  $classDescriptor.simpleName )
					  		
					  		#else
					  		
								#set( $testDataMetaInfoName =  $classDescriptor.simpleName + $metaInfoList.size() )
						  		#set( $testInstancesToUse = $attributeDescriptor.getMetaInfoValueFor( $testDataMetaInfoName ) )
					  		
					  		#end
							
						#else
						
							specify either $useExampleData (as true) or $useFirstFromDataPool as true  
						
						#end
		    			
		    		
						'		final List<String> strList${ElementType} = CollectionsStringUtils.commaSeparatedStringToStringList( "$testInstancesToUse" );
						'		final java.util.List<${ElementType}> ${elementType}List = new java.util.ArrayList<${ElementType}>();
						'		for (final String element : strList${ElementType}) {
						'			${ElementType} instanceById = ${ElementType}Factory.createById(element);
						'			if ( instanceById != null)
						'				${elementType}List.add(${ElementType}Factory.createById(element));
						'		}						
						'
						 
		    		#end
				
				#else
				
						'		// Unkown CollectionType: $collectionType 
					
				#end 
				
			#end
			
		#end
