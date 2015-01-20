'	static 
'	{

	'		// fill data pool

	#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

		#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) ) 
		#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) ) 
		#set( $MetaInfoList = $attributeDescriptor.getMetaInfosWithNameStartingWith("${classDescriptor.simpleName}"))  

		'		final List<String> ${attributeName}List = new ArrayList<String>();
		
		#foreach($metainfo in $MetaInfoList)

			'		${attributeName}List.add("$metainfo.value");	
		
		#end

		'		dataPool.put("$AttributeName", ${attributeName}List);
		'
		
	#end
	
	'		final List<String> objectIdList = new ArrayList<String>();

	#foreach($metainfo in $MetaInfoList)

		'		objectIdList.add( "$metainfo.name" );
	
	#end

	'		dataPool.put(OBJECT_ID, objectIdList);



	'
	'		// set maxLength values 

	#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

		#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
	    #set( $MaxLength = $attributeDescriptor.getMetaInfoValueFor("MaxLength") )
		 
		#if ( $classDescriptor.isValueAvailable($MaxLength) )
		
			'		maxLengths.put("$AttributeName", new Integer( $MaxLength ));
		
		#else
		
		    #set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") ) 
			
			#if ($javaType == "String")

				'		maxLengths.put("$AttributeName", new Integer( DEFAULT_MAX_LENGTH_STRING_VALUE ));
			
			#elseif ($javaType == "long" || $javaType == "Long"
			         || $javaType == "int" || $javaType == "Integer"
			         || $javaType == "byte" || $javaType == "Byte")
			         
				'		maxLengths.put("$AttributeName", new Integer( DEFAULT_MAX_LENGTH_NUMBER_VALUE ));
			
			#end
		
		#end
	
	#end




    '
	'		// set minLength values

	#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

		#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
	    #set( $MinLength = $attributeDescriptor.getMetaInfoValueFor("MinLength") ) 
		 
		#if ( $classDescriptor.isValueAvailable($MinLength) )
		
			'		minLengths.put("$AttributeName", new Integer( $MinLength ));
		
		#else
		
		    #set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") ) 
			
			#if ($javaType == "String")

				'		minLengths.put("$AttributeName", new Integer( DEFAULT_MIN_LENGTH_STRING_VALUE ));
			
			#elseif ($javaType == "long" || $javaType == "Long"
			         || $javaType == "int" || $javaType == "Integer"
			         || $javaType == "byte" || $javaType == "Byte")
			         
				'		minLengths.put("$AttributeName", new Integer( DEFAULT_MIN_LENGTH_NUMBER_VALUE ));
			
			#end
		
		#end
	
	#end
	
'	}
'