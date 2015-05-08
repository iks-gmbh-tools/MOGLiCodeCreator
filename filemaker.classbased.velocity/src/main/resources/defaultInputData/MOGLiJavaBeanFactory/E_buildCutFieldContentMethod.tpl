'	/**
'	 * Cuts the content of the named field by the numberCharToCut value if the content is not null. 
'	 */
'	public static void cutFromFieldContent(final ${classDescriptor.simpleName} instance, final String fieldname, final int numberCharToCut)
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
			'				final int length = value.length() - numberCharToCut;
			'				
			'				if (length < 1)
			'				{
			'					instance.set${AttributeName}("");
			'				}
			'				else
			'				{
			'					instance.set${AttributeName}(value.substring(0, length));
			'				}
			'			}
			'
			'			return;
			'		}
			'
	    
	    #elseif ($javaType == "int" || $javaType == "Integer")
	    
			'		if ( "$AttributeName".equals(fieldname) )
			'		{
			'			final String value = "" + instance.get${AttributeName}();
			'			if (value != null)
			'			{
			'				final int length = value.length() - numberCharToCut;
			'				
			'				if (length < 1)
			'				{
			'					instance.set${AttributeName}( 0 );
			'				}
			'				else
			'				{
			'					instance.set${AttributeName}( new Integer( value.substring(0, length) ) );
			'				}
			'			}		
			'
			'			return;
			'		}
			'
			
	    #elseif ($javaType == "long" || $javaType == "Long")
	    
			'		if ( "$AttributeName".equals(fieldname) )
			'		{
			'			final String value = "" + instance.get${AttributeName}();
			'			if (value != null)
			'			{
			'				final int length = value.length() - numberCharToCut;
			'				
			'				if (length < 1)
			'				{
			'					instance.set${AttributeName}( 0L );
			'				}
			'				else
			'				{
			'					instance.set${AttributeName}( new Long( value.substring(0, length) ) );
			'				}
			'			}		
			'
			'			return;
			'		}
			'	    				    
	    #elseif ($javaType == "byte" || $javaType == "Byte")
	    
			'		if ( "$AttributeName".equals(fieldname) )
			'		{
			'			final String value = "" + instance.get${AttributeName}();
			'			if (value != null)
			'			{
			'				final int length = value.length() - numberCharToCut;
			'				
			'				if (length < 1)
			'				{
			'					instance.set${AttributeName}( (byte) 0 );
			'				}
			'				else
			'				{
			'					instance.set${AttributeName}( new Byte( value.substring(0, length) ) );
			'				}
			'			}		
			'
			'			return;
			'		}
			'	    				    

	    #elseif ($javaType == "BigDecimal" || $javaType == "java.math.BigDecimal")
	    
			'		if ( "$AttributeName".equals(fieldname) )
			'		{
			'			final String value = "" + instance.get${AttributeName}().toPlainString();
			'			if (value != null)
			'			{
			'				final int length = value.length() - numberCharToCut;
			'				
			'				if (length < 1)
			'				{
			'					instance.set${AttributeName}( new BigDecimal ( "0" ) );
			'				}
			'				else
			'				{
			'					instance.set${AttributeName}( new BigDecimal( value.substring(numberCharToCut) ) );
			'				}
			'			}		
			'
			'			return;
			'		}
			'	    				    
		#end	    
#end

'		System.err.println("Field <" + fieldname + "> is either unkown to the class '${classDescriptor.simpleName}' or cutting its field content is not supported.");
'	}
'