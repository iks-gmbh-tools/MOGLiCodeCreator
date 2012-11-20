#set( $superClass = $classDescriptor.getMetaInfoValueFor("extends") )
#set( $classNameList = $TemplateJavaUtility.searchForImportClasses($classDescriptor) )

#foreach($className in $classNameList)

	import $className;

#end