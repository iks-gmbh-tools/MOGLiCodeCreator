
'	public ${classDescriptor.simpleName} clone${classDescriptor.simpleName}(final ${classDescriptor.simpleName} object) {
'		// Alternatively use clone method generated for ${classDescriptor}
'		final ${classDescriptor.simpleName} toReturn = new ${classDescriptor.simpleName}();

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )

	'		toReturn.set$AttributeName(object.get$AttributeName());

#end

'		return toReturn;
'	}
