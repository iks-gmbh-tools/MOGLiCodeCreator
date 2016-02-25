#set( $prefix = $model.getMetaInfoValueFor("nameSpacePrefix") )

@targetFileName ShoppingCart.xml # Name of output file with extension but without path 
@targetDir <applicationRootDir>
@NameOfValidModel XMLBuilder
@ReplaceStart </$prefix:boughItemList>
@replaceEnd </$prefix:shoppingCart>

#set( $classDescriptorList = $model.getClassDescriptorList() )

<$prefix:CurrentAdvertising>

#foreach ($classDescriptor in $classDescriptorList)
	
	#set( $type = $classDescriptor.getMetaInfoValueFor("type") )

	#if ( $type.equals("Current Advertising") )

		'	<$prefix:$classDescriptor.name>

		#set( $attributeDescriptorList = $classDescriptor.getAttributeDescriptorList() )

		#foreach ($attributeDescriptor in $attributeDescriptorList)
		
			#set( $metaInfoList = $attributeDescriptor.getMetaInfoList() )
			#parse("metaInfoListToXmlAttributes.tpl")

			'		<$prefix:$attributeDescriptor.name $xmlAttributes>

		#end
		
		'	</$prefix:$classDescriptor.name>
		
	#end

#end

</$prefix:CurrentAdvertising>
	