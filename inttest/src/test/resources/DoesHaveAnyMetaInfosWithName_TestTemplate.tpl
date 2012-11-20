#if($model.doesHaveAnyMetaInfosWithName("foo"))
	model '${model.name}' has MetaInfo 'foo'
#else
	No MetaInfo 'foo' in model '${model.name}'
#end

#if($model.doesHaveAnyMetaInfosWithName("ModelMetaInfo"))
	model '${model.name}' has MetaInfo 'ModelMetaInfo' 
#else
	No MetaInfo 'ModelMetaInfo' in model '${model.name}'
#end


#foreach($classDescriptor in $model.classDescriptorList)

	#set( $MetaInfoName = "ClassMetaInfo" ) 

	#if($classDescriptor.doesHaveAnyMetaInfosWithName($MetaInfoName))
		class '${classDescriptor.simpleName}' has MetaInfo '${MetaInfoName}'
	#else
		No MetaInfo '${MetaInfoName}' in class '${classDescriptor.simpleName}'
	#end
	
	#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
		#if($attributeDescriptor.doesHaveAnyMetaInfosWithName("AttributeMetaInfo"))
			attribute '${attributeDescriptor.name}' has MetaInfo 'AttributeMetaInfo'
		#else
			No MetaInfo 'AttributeMetaInfo' in attribute '${attributeDescriptor.name}'
		#end
	#end

#end