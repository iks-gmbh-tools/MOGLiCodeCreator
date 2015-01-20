'	/**
'	 * Calls createInstanceWithAllFieldsAtMaxLength and addToFieldContent to fields of supported JavaType
'	 * (these are: byte, Byte, int, Integer, double, Double, String, java.math.BigDecimal,
'    * if MaxLength-metainfo is defined for the corresponding attribute in the data model.
'	 * @return instance that causes validation exceptions for the supported fields.
'	 */
'	public static ${classDescriptor.simpleName} createInstanceWithAllSupportedFieldsExceedingMaxLength()
'	{
'		final ${classDescriptor.simpleName} toReturn = createInstanceWithAllFieldsAtMaxLength();
'

#set( $counter = 0 )

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

    #set( $MaxLength = $attributeDescriptor.getMetaInfoValueFor("MaxLength") )
	    
    #if ( $classDescriptor.isValueNotAvailable($MaxLength) )
    
    	# do nothing
    
    #else
    
		#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
		 
		'		addToFieldContent(toReturn, "$AttributeName", "9");
	
		#set( $counter = $counter + 1 )
			
	#end

#end

#if ( $counter == 0 )

'		// the meta model contains for class '${classDescriptor.simpleName}' no attribute with metainfo 'MaxLength' 
	
#end

'		
'		return toReturn;
'	}
