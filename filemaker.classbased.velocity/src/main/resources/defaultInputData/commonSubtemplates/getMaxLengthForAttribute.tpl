#set( $MaxLength = $attributeDescriptor.getMetaInfoValueFor("MaxLength") )

#if ( $classDescriptor.isValueNotAvailable($MaxLength) )

	#if ($javaType == "String")
	
    	#set( $MaxLength = 1000 )
    	
    #else
    
    	#set( $MaxLength = 10 )
    
    #end

#end