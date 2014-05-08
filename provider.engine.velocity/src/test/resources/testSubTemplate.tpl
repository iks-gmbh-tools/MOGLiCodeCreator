'	// comment1
#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
'	private $attributeDescriptor.getMetaInfoValueFor( "JavaType" ) $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name);
#end	
