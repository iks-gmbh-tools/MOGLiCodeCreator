'	private static ${classDescriptor.simpleName} buildObject(final int index)
'	{
'		${classDescriptor.simpleName}Builder builder = new ${classDescriptor.simpleName}Builder();
'		String value = null;
		
		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) ) 
		    #set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") ) 
		    
		    #if ($javaType == "String")
		    
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( value );
			
			#elseif ($javaType == "boolean")
		    
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( new Boolean( value ).booleanValue() );

			#elseif ($javaType == "char") 
			
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( value.charAt(0) );

			#elseif ($javaType == "byte") 
			
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( new Byte( value ).byteValue() );
				
			#elseif ($javaType == "long") 
			
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( new Long( value ).longValue() );
				
			#elseif ($javaType == "int") 
			
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( new Integer( value ).intValue() );
				
			#elseif ($javaType == "float") 
			
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( new Float( value ).floatValue() );
				
			#elseif ($javaType == "double") 
			
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( new Double( value ).doubleValue() );
				
			#elseif ($javaType == "Boolean")
		    
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( new Boolean( value ) );

			#elseif ($javaType == "Character") 
			
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( value.charAt(0) );

			#elseif ($javaType == "Byte") 
			
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( new Byte( value ) );
				
			#elseif ($javaType == "Long") 
			
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( new Long( value ) );
				
			#elseif ($javaType == "Integer") 
			
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( new Integer( value ) );
				
			#elseif ($javaType == "Float") 
			
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( new Float( value ) );
				
			#elseif ($javaType == "Double") 
			
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( new Double( value ) );

<<<<<<< HEAD
			#elseif ($javaType == "java.math.BigDecimal") 
=======
			#elseif ($javaType == "java.math.BigDecimal" || $javaType == "BigDecimal") 
>>>>>>> 656c84c58ad794ed34c58c30ecc9bf656c921412
			
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( new BigDecimal( "" + value ) );
<<<<<<< HEAD
=======

			#elseif ($javaType == "org.joda.time.DateTime" || $javaType == "DateTime") 
			
				'		value = getValue("$AttributeName", index);
				'		if ( ! StringUtils.isEmpty( value ) )
				'			builder = builder.with$AttributeName( dateTimeFormatter.parseDateTime( value ) );
>>>>>>> 656c84c58ad794ed34c58c30ecc9bf656c921412
				
			#elseif ($javaType == "String[]") 
			
				'		builder = builder.with$AttributeName( CollectionsStringUtils.commaSeparatedStringToStringArray( getValue("$AttributeName", index) ) );
				
			#elseif ($javaType == "java.util.HashSet<String>") 
			
				'		builder = builder.with$AttributeName( CollectionsStringUtils.commaSeparatedStringToHashSet( getValue("$AttributeName", index) ) );
				
			#elseif ($javaType == "java.util.List<String>")
			 
				'		builder = builder.with$AttributeName( CollectionsStringUtils.commaSeparatedStringToStringList( getValue("$AttributeName", index) ) );
				
			#elseif ($javaType == "java.util.List<Long>")
			 
				'		builder = builder.with$AttributeName( CollectionsStringUtils.commaSeparatedStringToLongList( getValue("$AttributeName", index) ) );
				
			#elseif ( $TemplateJavaUtility.isPrimitiveTypeWrapper($javaType) )
			
				'		value = getValue("$AttributeName", index);
				
				#set( $javaTypeSimple = $TemplateJavaUtility.getSimpleClassName($javaType) )

				'		builder = builder.with${AttributeName}( $javaTypeSimple.valueOf( value ) );
			
			#else

				'		value = getValue("$AttributeName", index);
				
				#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
				 
				'		final $javaType $attributeName = ${javaType}Factory.getById( value );
				'		builder = builder.with${AttributeName}( $attributeName );
			
			#end
			
			'
			
		#end

'		return builder.build();
'	}