#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
	#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
	#set( $javaType = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor.getMetaInfoValueFor("JavaType") ) )

'	@Test
'	public void doesHashcodeMethodReturnsDifferentHashcodesForTwo${classDescriptor.simpleName}InstancesThatDifferInField${AttributeName}() {

	#if ( $TemplateJavaUtility.isJavaMetaTypePrimitive($javaType) )

		#parse("modifyValueOfPrimitiveType.tpl")

	#else
'		${className}1.set${AttributeName}(null);

	#end

'		System.out.print("Is " + ${className}1.get${AttributeName}() + " != " + ${className}2.get${AttributeName}() + " ?");
'		assertFalse("unexpectedly equal hash codes", ${className}1.hashCode() == ${className}2.hashCode());
'		System.out.println(" - YES!");
'	}
'


#end