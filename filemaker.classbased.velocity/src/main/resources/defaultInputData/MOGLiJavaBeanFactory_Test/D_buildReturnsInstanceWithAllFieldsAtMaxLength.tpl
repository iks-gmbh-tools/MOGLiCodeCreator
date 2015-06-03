'	@Test
'	public void returnsInstanceWithAllFieldsAtMaxLength() {
'		final ${classDescriptor.simpleName} instance = ${classDescriptor.simpleName}Factory.createInstanceWithAllFieldsAtMaxLength();
'		assertNotNull("Not null expected for ", instance);

		#set( $useJavaBeanRegistry = $model.getMetaInfoValueFor("useJavaBeanRegistry") )

		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) ) 
		    #set( $javaType = $attributeDescriptor.getMetaInfoValueFor("JavaType") )
		    
			#parse("commonSubtemplates/isJavaTypeDomainObject.tpl")
			
			#if ( $isJavaTypeDomainObject.equals( "true" ) )
			
				#if ( $useJavaBeanRegistry == "true")
				
					'		assertEquals("$AttributeName of type ${javaType}", ${javaType}Factory.createInstanceWithExampleData().toString(), instance.get${AttributeName}().toString() );		     
				
				#else
				
					'		assertEquals("$AttributeName of type ${javaType}", ${javaType}Factory.createInstanceWithAllFieldsAtMaxLength().toString(), instance.get${AttributeName}().toString() );
		    		
				#end
		     
		    #elseif ($javaType == "String")
		    
				'		assertEquals("$AttributeName of type ${javaType}", MOGLiFactoryUtils.createStringValue("i", ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName")), instance.get${AttributeName}() );
		    
		    #elseif ($javaType == "int")

				'		assertEquals("$AttributeName of type ${javaType}", MOGLiFactoryUtils.createIntValue(${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName")), instance.get${AttributeName}() );

		    #elseif ($javaType == "Integer")

				'		assertEquals("$AttributeName of type ${javaType}", MOGLiFactoryUtils.createIntValue(${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName")), instance.get${AttributeName}().intValue() );

		    #elseif ($javaType == "long")

				'		assertEquals("$AttributeName of type ${javaType}", MOGLiFactoryUtils.createLongValue(${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName")), instance.get${AttributeName}() );

		    #elseif ($javaType == "Long")

				'		assertEquals("$AttributeName of type ${javaType}", MOGLiFactoryUtils.createLongValue(${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName")), instance.get${AttributeName}().longValue() );

		    #elseif ($javaType == "byte")

				'		assertEquals("$AttributeName of type ${javaType}", MOGLiFactoryUtils.createByteValue(${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName")), instance.get${AttributeName}() );
				
		    #elseif ($javaType == "Byte")

				'		assertEquals("$AttributeName of type ${javaType}", MOGLiFactoryUtils.createByteValue(${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName")), instance.get${AttributeName}().byteValue() );
				
		    #elseif ($javaType == "BigDecimal")
		    
				'		assertEquals("$AttributeName of type ${javaType}", new BigDecimal( MOGLiFactoryUtils.createStringValue("i", ${classDescriptor.simpleName}Factory.getMaxLength("$AttributeName"))).toPlainText(), instance.get${AttributeName}().toPlainText() );
		    
		    #else
		    
				# do nothing

		    #end
		#end

'	}
