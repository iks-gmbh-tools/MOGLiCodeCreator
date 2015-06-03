#set( $classNameList = $TemplateJavaUtility.searchForImportClasses($classDescriptor) )
#set( $enumList = $classDescriptor.getAllMetaInfoValuesFor("Enum") )


#foreach($className in $classNameList)

	#if ( $TemplateStringUtility.contains($enumList, $className) )
	
		import ${classDescriptor.fullyQualifiedName}.$className;
	
	#else
	
		import $className;
		
	#end

#end

#set( $useJavaBeanRegistry = $model.getMetaInfoValueFor("useJavaBeanRegistry") )

#if ( $useJavaBeanRegistry == "true" )

	#set( $namespace = $model.getMetaInfoValueFor("namespace") )

	import ${namespace}.utils.MOGLiCCJavaBean;

#end
