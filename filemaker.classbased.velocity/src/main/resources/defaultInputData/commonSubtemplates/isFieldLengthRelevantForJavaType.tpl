#if ($javaType == "String")

	#set( $isFieldLengthRelevantForJavaType = "true" )

#elseif ($javaType == "long")

	#set( $isFieldLengthRelevantForJavaType = "true" )

#elseif ($javaType == "int")

	#set( $isFieldLengthRelevantForJavaType = "true" )

#elseif ($javaType == "byte")

	#set( $isFieldLengthRelevantForJavaType = "true" )
	
#elseif ($javaType == "Long")

	#set( $isFieldLengthRelevantForJavaType = "true" )

#elseif ($javaType == "Integer")

	#set( $isFieldLengthRelevantForJavaType = "true" )

#elseif ($javaType == "Byte")

	#set( $isFieldLengthRelevantForJavaType = "true" )

#else

	#set( $isFieldLengthRelevantForJavaType = "false" )

#end
