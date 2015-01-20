'	@Test
'	public void returnsInstanceWithAllFieldsAtMaxLength() {
'		final ${classDescriptor.simpleName} instance = ${classDescriptor.simpleName}Factory.createInstanceWithAllFieldsAtMaxLength();
'		assertNotNull("Not null expected for ", instance);

		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) ) 
		    #set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
		    
			#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
			
			#if ( $isJavaTypeDomainObject.equals( "true" ) )
		     
				'		assertEquals("$AttributeName of type String", ${javaType}Factory.createInstanceWithAllFieldsAtMaxLength(), instance.get${AttributeName}() );
		     
		    #elseif ($javaType == "String")
		    
				'		assertEquals("$AttributeName of type String", MOGLiFactoryUtils.createStringValue("i", ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName")), instance.get${AttributeName}() );
		    
		    #elseif ($javaType == "int")

				'		assertEquals("$AttributeName of type String", MOGLiFactoryUtils.createIntValue(${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName")), instance.get${AttributeName}() );

		    #elseif ($javaType == "Integer")

				'		assertEquals("$AttributeName of type String", MOGLiFactoryUtils.createIntValue(${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName")), instance.get${AttributeName}().intValue() );

		    #elseif ($javaType == "long")

				'		assertEquals("$AttributeName of type String", MOGLiFactoryUtils.createLongValue(${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName")), instance.get${AttributeName}() );

		    #elseif ($javaType == "Long")

				'		assertEquals("$AttributeName of type String", MOGLiFactoryUtils.createLongValue(${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName")), instance.get${AttributeName}().longValue() );

		    #elseif ($javaType == "byte")

				'		assertEquals("$AttributeName of type String", MOGLiFactoryUtils.createByteValue(${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName")), instance.get${AttributeName}() );
				
		    #elseif ($javaType == "Byte")

				'		assertEquals("$AttributeName of type String", MOGLiFactoryUtils.createByteValue(${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName")), instance.get${AttributeName}().byteValue() );
				
		    #elseif ($javaType == "BigDecimal")
		    
				'		assertEquals("$AttributeName of type String", new BigDecimal( MOGLiFactoryUtils.createStringValue("i", ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName"))).toPlainText(), instance.get${AttributeName}().toPlainText() );
		    
		    #else
		    
				# do nothing

		    #end
		#end

'	}
