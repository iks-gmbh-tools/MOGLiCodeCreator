'	@Test
'	public void buildsEmpty${classDescriptor.simpleName}Instance() {
'		${classDescriptor.simpleName}Builder builder = new ${classDescriptor.simpleName}Builder();
'		${classDescriptor.simpleName} instance = builder.build();

		#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		
			#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
			#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
			#set( $javaType = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor.getMetaInfoValueFor("JavaType") ) )
			
			#if ( $TemplateJavaUtility.isJavaMetaTypePrimitive($javaType) )
			
				#if ($javaType == "double")

					'		assertEquals("$attributeName", "0.0", "" + instance.get${AttributeName}());
				#elseif ($javaType == "float")

					'		assertEquals("$attributeName", "0.0", ""  + instance.get${AttributeName}());

				#elseif ($javaType == "char")
				
					'		assertEquals("$attributeName", '\u0000', instance.get${AttributeName}());
				
				#elseif ($javaType == "boolean")
				
					'		assertEquals("$attributeName", false, instance.get${AttributeName}());
					
				#else
				
					'		assertEquals("$attributeName", "0", "" + instance.get${AttributeName}());
				
				#end
			
			#else
			
				#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
				
				'		assertNull("null expected", instance.get${AttributeName}());
				
			#end		
			
		#end
'	}
