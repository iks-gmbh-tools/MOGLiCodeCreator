@targetFileName ShoppingCart.xml # Name of output file with extension but without path 
@targetDir <applicationRootDir>
@NameOfValidModel XMLBuilder
@insertBelow boughItemList>

#set( $prefix = $model.getMetaInfoValueFor("nameSpacePrefix") )
#set( $classDescriptorList = $model.getClassDescriptorList() )

#foreach ($classDescriptor in $classDescriptorList)
	
	#set( $bought = $classDescriptor.getMetaInfoValueFor("bought") )

	#if ($bought.equals("true"))

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
	