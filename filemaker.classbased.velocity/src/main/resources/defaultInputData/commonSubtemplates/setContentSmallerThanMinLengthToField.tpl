#if ($javaType == "String")

	'		testData.set${AttributeName}( MOGLiFactoryUtils.createStringValue("i", $MinLength - 1) );

#elseif ($javaType == "BigDecimal")

	'		testData.set${AttributeName}( new BigDecimal( MOGLiFactoryUtils.createStringValue("1", $MinLength - 1) ) );

#elseif ($javaType == "long")

	'		testData.set${AttributeName}( MOGLiFactoryUtils.createLongValue($MinLength - 1) );

#elseif ($javaType == "int")

	'		testData.set${AttributeName}( MOGLiFactoryUtils.createIntValue($MinLength - 1) );

#elseif ($javaType == "byte")

	'		testData.set${AttributeName}( MOGLiFactoryUtils.createByteValue($MinLength - 1) );

#elseif ( $TemplateJavaUtility.isPrimitiveTypeWrapper($javaType) )

	'		testData.set${AttributeName}( new $javaType("" + MOGLiFactoryUtils.createLongValue($MinLength - 1) ) );

#end
