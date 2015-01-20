'	/**
'	 * Calls createInstanceWithAllFieldsAtMinLength and cutFieldContent to fields of supported JavaType
'	 * (these are; byte, Byte, int, Integer, double, Double, String, java.math.BigDecimal),
'	 * if MinLength-metainfo is defined for the corresponding attribute in the data model.
'	 * @return instance that causes validation exceptions for the corresponding fields.
'	 */
'	public static ${classDescriptor.simpleName} createInstanceWithAllSupportedFieldsNotReachingMinLength()
'	{
'		final ${classDescriptor.simpleName} toReturn = createInstanceWithAllFieldsAtMinLength();
'

#set( $counter = 0 )

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

    #set( $MinLength = $attributeDescriptor.getMetaInfoValueFor("MinLength") )
    
    #if ( $classDescriptor.isValueNotAvailable($MinLength) )
    
    	# do nothing
    
    #else
    
		#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) ) 
	    
		'		cutFieldContent(toReturn, "$AttributeName", 1);
		
		#set( $counter = $counter + 1 )
		
	#end
		
#end

#if ( $counter == 0 )

'		// the meta model contains for class '${classDescriptor.simpleName}' no attribute with metainfo 'MinLength' 
	
#end

'		
'		return toReturn;
'	}
