'	/**
'	 * @return the instance with all fields with maximum allowed number of chars
'	 */
'	public static ${classDescriptor.simpleName} createInstanceWithAllFieldsAtMaxLength()
'	{
'		final ${classDescriptor.simpleName} toReturn = new ${classDescriptor.simpleName}();
'
		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) ) 
		    #set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )

			#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
			
			#if ( $isJavaTypeDomainObject.equals( "true" ) )
		    
		    	'		toReturn.set${AttributeName}( ${javaType}Factory.createInstanceWithAllFieldsAtMaxLength() );
		     
		    #elseif ($javaType == "String")
		    
				'		toReturn.set${AttributeName}( MOGLiFactoryUtils.createStringValue("i", getMaxLength("$AttributeName").intValue() ) );

		    #elseif ($javaType == "byte" || $javaType == "Byte")

				'		toReturn.set${AttributeName}( MOGLiFactoryUtils.createByteValue( getMaxLength("$AttributeName").intValue() ) );

		    #elseif ($javaType == "int" || $javaType == "Integer")

				'		toReturn.set${AttributeName}( MOGLiFactoryUtils.createIntValue( getMaxLength("$AttributeName").intValue() ) );

		    #elseif ($javaType == "long" || $javaType == "Long")

				'		toReturn.set${AttributeName}( MOGLiFactoryUtils.createLongValue( getMaxLength("$AttributeName").intValue() ) );

		    #elseif ($javaType == "float" || $javaType == "Float")

				'		toReturn.set${AttributeName}( Float.MAX_VALUE );

		    #elseif ($javaType == "double" || $javaType == "Double")

				'		toReturn.set${AttributeName}( Double.MAX_VALUE );

		    #elseif ($javaType == "Boolean")
		    
				'		toReturn.set${AttributeName}( Boolean.TRUE );
				
		    #elseif ($javaType == "Character")
		    
				'		toReturn.set${AttributeName}( Character.MAX_VALUE );
				
		    #elseif ($javaType == "BigDecimal" || $javaType == "java.math.BigDecimal")
		    
				'		toReturn.set${AttributeName}( new BigDecimal( "" + MOGLiFactoryUtils.createLongValue( getMaxLength("$AttributeName").intValue() ) ) );
		    
		    #else
		    
				# do nothing for other types, e.g. collection types or boolean

		    #end
		#end
'			
'		return toReturn;
'	}
