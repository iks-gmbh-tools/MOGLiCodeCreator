#set( $prefix = $model.getMetaInfoValueFor("nameSpacePrefix") )

@targetFileName ShoppingCart.xml # Name of output file with extension but without path 
@targetDir <applicationRootDir>
@NameOfValidModel XMLBuilder
@insertAbove <$prefix:boughItemList>

#set( $classDescriptor = $model.getClassDescriptor("Customer") )
#set( $element = $classDescriptor.name )
#set( $metaInfoList = $classDescriptor.getMetaInfoList() )

#parse("metaInfoListToXmlAttributes.tpl")

<$prefix:$element $xmlAttributes>

#set( $attributeDescriptorList = $classDescriptor.getAttributeDescriptorList() )

#foreach ($attributeDescriptor in $attributeDescriptorList)

	#set( $element = $attributeDescriptor.name )
	#set( $metaInfoList = $attributeDescriptor.getMetaInfoList() )
	
	#parse("metaInfoListToXmlAttributes.tpl")
	
	'	<$prefix:$attributeDescriptor.name $xmlAttributes>
	
#end

</$prefix:$element>
	
