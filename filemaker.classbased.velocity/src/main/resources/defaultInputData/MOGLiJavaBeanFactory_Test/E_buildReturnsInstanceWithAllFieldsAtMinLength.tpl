'	@Test
'	public void returnsInstanceWithAllFieldsAtMinLength() {
'		final ${classDescriptor.simpleName} instance = ${classDescriptor.simpleName}Factory.createInstanceWithAllFieldsAtMinLength();
'		assertNotNull("Not null expected for ", instance);

		#set( $useJavaBeanRegistry = $model.getMetaInfoValueFor("useJavaBeanRegistry") )

		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) ) 
		    #set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
		    
			#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
			
			#if ( $isJavaTypeDomainObject.equals( "true" ) )
		     

				#if ( $useJavaBeanRegistry == "true")
				
					'		assertEquals("$AttributeName of type String", ${javaType}Factory.createByIndex(1), instance.get${AttributeName}() );
				
				#else
				
					'		assertEquals("$AttributeName of type String", ${javaType}Factory.createInstanceWithAllFieldsAtMinLength(), instance.get${AttributeName}() );
		    		
				#end
		     
		    #elseif ($javaType == "String")
		    
				'		assertEquals("$AttributeName of type String", MOGLiFactoryUtils.createStringValue("i", ${classDescriptor.simpleName}Factory.getMinLength("$AttributeName")), instance.get${AttributeName}() );
		    
		    #elseif ($javaType == "int")

				'		assertEquals("$AttributeName of type String", MOGLiFactoryUtils.createIntValue(${classDescriptor.simpleName}Factory.getMinLength("$AttributeName")), instance.get${AttributeName}() );

		    #elseif ($javaType == "Integer")

				'		assertEquals("$AttributeName of type String", MOGLiFactoryUtils.createIntValue(${classDescriptor.simpleName}Factory.getMinLength("$AttributeName")), instance.get${AttributeName}().intValue() );

		    #elseif ($javaType == "long")

				'		assertEquals("$AttributeName of type String", MOGLiFactoryUtils.createLongValue(${classDescriptor.simpleName}Factory.getMinLength("$AttributeName")), instance.get${AttributeName}() );

		    #elseif ($javaType == "Long")

				'		assertEquals("$AttributeName of type String", MOGLiFactoryUtils.createLongValue(${classDescriptor.simpleName}Factory.getMinLength("$AttributeName")), instance.get${AttributeName}().longValue() );

		    #elseif ($javaType == "byte")

				'		assertEquals("$AttributeName of type String", MOGLiFactoryUtils.createByteValue(${classDescriptor.simpleName}Factory.getMinLength("$AttributeName")), instance.get${AttributeName}() );
				
		    #elseif ($javaType == "Byte")

				'		assertEquals("$AttributeName of type String", MOGLiFactoryUtils.createByteValue(${classDescriptor.simpleName}Factory.getMinLength("$AttributeName")), instance.get${AttributeName}().byteValue() );
				
		    #elseif ($javaType == "BigDecimal")
		    
				'		assertEquals("$AttributeName of type String", new BigDecimal( MOGLiFactoryUtils.createStringValue("i", ${classDescriptor.simpleName}Factory.getMinLength("$AttributeName"))).toPlainText(), instance.get${AttributeName}().toPlainText() );
		    
		    #else
		    
				# do nothing

		    #end
		#end

'	}
