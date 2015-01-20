#if ($javaType == "String")

	'		testData.set${AttributeName}( MOGLiFactoryUtils.createStringValue("i", $MaxLength + 1) );

#elseif ($javaType == "BigDecimal")

	'		testData.set${AttributeName}( new BigDecimal( MOGLiFactoryUtils.createStringValue("1", $MaxLength + 1) ) );

#elseif ($javaType == "long")

	'		testData.set${AttributeName}( MOGLiFactoryUtils.createLongValue($MaxLength + 1) );

#elseif ($javaType == "int")

	'		testData.set${AttributeName}( MOGLiFactoryUtils.createIntValue($MaxLength + 1) );

#elseif ($javaType == "byte")

	'		testData.set${AttributeName}( MOGLiFactoryUtils.createByteValue($MaxLength + 1) );

	
#elseif ( $TemplateJavaUtility.isPrimitiveTypeWrapper($javaType) )

	'		testData.set${AttributeName}( new $javaType("" + MOGLiFactoryUtils.createLongValue($MaxLength + 1) ) );

#end
