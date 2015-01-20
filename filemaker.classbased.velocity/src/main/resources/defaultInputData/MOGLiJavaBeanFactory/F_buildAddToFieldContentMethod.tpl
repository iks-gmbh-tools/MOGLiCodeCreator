'	/**
'	 * Appends the content of the named field by the textToAdd value if the content is not null. 
'    * This value must represent a number for numerical fields.
'	 */
'	public static void addToFieldContent(final ${classDescriptor.simpleName} instance, final String fieldname, final String textToAdd)
'	{

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

		#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) ) 
	    #set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
	    
	    #if ($javaType == "String")
	    
			'		if ( "$AttributeName".equals(fieldname) )
			'		{
			'			final String value = instance.get${AttributeName}();
			'
			'			if (value != null)
			'			{
			'				instance.set${AttributeName}(value + textToAdd);
			'			}
			'
			'			return;
			'		}
			'
	    
	    #elseif ($javaType == "Integer")
	    
			'		if ( "$AttributeName".equals(fieldname) )
			'		{
			'			if (instance.get${AttributeName}() != null)
			'			{
			'				final String value = "" + instance.get${AttributeName}() + textToAdd;
			'				instance.set${AttributeName}( new Integer( value ) );
			'			}		
			'
			'			return;
			'		}
			'
			
	    
	    #elseif ($javaType == "int")
	    
			'		if ( "$AttributeName".equals(fieldname) )
			'		{
			'			final String value = "" + instance.get${AttributeName}() + textToAdd;
			'			instance.set${AttributeName}( new Integer( value ).intValue() );
			'			return;
			'		}
			'
			
	    #elseif ($javaType == "Long")
	    
			'		if ( "$AttributeName".equals(fieldname) )
			'		{
			'			if (instance.get${AttributeName}() != null)
			'			{
			'				final String value = "" + instance.get${AttributeName}() + textToAdd;
			'				instance.set${AttributeName}( new Long( value ) );
			'			}		
			'
			'			return;
			'		}
			'	    				    

	    #elseif ($javaType == "long")
	    
			'		if ( "$AttributeName".equals(fieldname) )
			'		{
			'			final String value = "" + instance.get${AttributeName}() + textToAdd;
			'			instance.set${AttributeName}( new Long( value ).longValue() );
			'			return;
			'		}
			'	    				    
			
	    #elseif ($javaType == "Byte")
	    
			'		if ( "$AttributeName".equals(fieldname) )
			'		{
			'			if (instance.get${AttributeName}() != null)
			'			{
			'				final String value = "" + instance.get${AttributeName}() + textToAdd;
			'				instance.set${AttributeName}( new Byte( value ) );
			'			}		
			'
			'			return;
			'		}
			'	    				    

	    #elseif ($javaType == "byte")
	    
			'		if ( "$AttributeName".equals(fieldname) )
			'		{
			'			final String value = "" + instance.get${AttributeName}() + textToAdd;
			'			instance.set${AttributeName}( new Byte( value ).byteValue() );
			'			return;
			'		}
			'	    				    

	    #elseif ($javaType == "BigDecimal" || $javaType == "java.math.BigDecimal")
	    
			'		if ( "$AttributeName".equals(fieldname) )
			'		{
			'			if (instance.get${AttributeName}() != null)
			'			{
			'				final String value = textToAdd + instance.get${AttributeName}().toPlainString() + textToAdd;
			'				instance.set${AttributeName}( new BigDecimal( value ) );
			'			}		
			'
			'			return;
			'		}
			'	    				    
		#end	    
#end

'		System.err.println("Field <" + fieldname + "> is either unkown to the class '${classDescriptor.simpleName}' or appending its field content is not supported.");
'	}
'