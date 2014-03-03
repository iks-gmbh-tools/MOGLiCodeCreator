#set( $rootDir = $model.getMetaInfoValueFor("targetDir") )
#set( $classDescriptor = $model.getClassDescriptor("HelloWorldGreeting2") )   # Use second class for this example
#set( $packagePath = $TemplateStringUtility.replaceAllIn(${classDescriptor.package}, ".", "/") )

@TargetFileName HelloWorldGreeting3.java    # Third Example 
@TargetDir $rootDir/hello_word_advanced_example/line_inserter/src/$packagePath
@NameOfValidModel HelloWorldAdvancedExample
@ReplaceStart /** START replace MOGLiCC marker !!! Do not modify this line !!! */
@ReplaceEnd /** END replace MOGLiCC marker !!! Do not modify this line !!! */

#set ( $patternAttributeDescriptor = $classDescriptor.getAttributeDescriptor("greetingPattern") )
#set ( $metaInfoList = $patternAttributeDescriptor.getAllMetaInfos() )
#set ( $greeting = "" )

#foreach( $metaInfo in $metaInfoList )
	
	#set ( $attributeDescriptor = $classDescriptor.getAttributeDescriptor("${metaInfo.value}") )

	#set ( $greetingPart = $attributeDescriptor.getMetaInfoValueFor("print") )
	
	#set ( $greeting = $greeting + $greetingPart + " " )
		
#end

'		System.out.println("${greeting.trim()}");
