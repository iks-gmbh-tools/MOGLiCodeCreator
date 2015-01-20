#set( $classDescriptorList = $model.getClassDescriptorList() )

#set( $isJavaTypeDomainObject = "false" )

#foreach($classDescriptor in $classDescriptorList)

	#if ( $classDescriptor.getFullyQualifiedName().equals( $javaType ) )
	
		#set( $isJavaTypeDomainObject = "true" )
		
	#end
	
	#if ( $classDescriptor.getSimpleName().equals( $javaType ) )
	
		#set( $isJavaTypeDomainObject = "true" )
		
	#end
	


#end