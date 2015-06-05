#set( $isJavaTypeListOfDomainObjects = "false" )
#set( $classDescriptorList = $model.getClassDescriptorList() )

#if ( $TemplateJavaUtility.isJavaMetaTypeGeneric($javaType) )
	
	#set( $CollectionType = $TemplateJavaUtility.getCollectionMetaType($javaType) ) 
		
	#if ($CollectionType == "java.util.List")

		#set( $ElementType = $TemplateJavaUtility.getCollectionElementType($javaType) )
		
		#foreach($classDescriptor in $classDescriptorList)

			#if ( $classDescriptor.getFullyQualifiedName().equals( $ElementType ) )
			
				#set( $isJavaTypeListOfDomainObjects = "true" )
				
			#end
			
			#if ( $classDescriptor.getSimpleName().equals( $ElementType ) )
			
				#set( $isJavaTypeListOfDomainObjects = "true" )
				
			#end
			
		#end
		
		
	#end
			
#end