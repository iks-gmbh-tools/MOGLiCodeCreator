
#set( $enumList = $classDescriptor.getAllMetaInfoValuesFor("Enum") )

#if( $classDescriptor.doesHaveAnyMetaInfosWithName("Enum") )

	'	// Enums

#end

#foreach( $enum in $enumList )

	'	$enum;

#end

#if( $classDescriptor.doesHaveAnyMetaInfosWithName("Enum") )

	'	

#end



'	// instance fields

#set( $useJavaBeanRegistry = $model.getMetaInfoValueFor("useJavaBeanRegistry") )

#if ( $useJavaBeanRegistry == "true" )

'	private String registryId;

#end


#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) ) 
	#set( $javaType = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor.getMetaInfoValueFor("JavaType") ) )
	
'	private $javaType $attributeName;

#end

'
