'	/**
'	 * @return the instance with all fields with the minimum allowed number of chars
'	 */
'	public static ${classDescriptor.simpleName} createInstanceWithAllFieldsAtMinLength()
'	{
'		final ${classDescriptor.simpleName} toReturn = new ${classDescriptor.simpleName}();
'
		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) ) 
		    #set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )

			#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
			
			#if ( $isJavaTypeDomainObject.equals( "true" ) )
		    
		    	'		toReturn.set${AttributeName}( ${javaType}Factory.createInstanceWithAllFieldsAtMinLength() );
		     
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
				
		    #else
		    
				# do nothing for other types, e.g. collection types or boolean

		    #end
		#end
'			
'		return toReturn;
'	}
