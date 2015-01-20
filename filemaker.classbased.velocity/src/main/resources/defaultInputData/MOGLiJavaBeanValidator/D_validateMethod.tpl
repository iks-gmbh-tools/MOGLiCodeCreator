
'	private void validate(final ${classDescriptor.simpleName} toValidate) {
'
#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)

	#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
	#set( $AttributeName = $TemplateStringUtility.firstToUpperCase($attributeDescriptor.name) )
	#set( $javaType = $TemplateJavaUtility.getSimpleClassName( $attributeDescriptor.getMetaInfoValueFor("JavaType") ) )

	#if ( $TemplateJavaUtility.isJavaMetaTypePrimitive($javaType) )
		'		validateField("$attributeName", "" + toValidate.get$AttributeName());  // convert to String
	#else

		'		if (toValidate.get$AttributeName() == null) {
		'			validateField("$attributeName", null);
		'		} else {
		'			validateField("$attributeName", "" + toValidate.get$AttributeName());  // convert to String
		'		}

	#end
'

#end

'		if (validationErrors.size() > 0) {
'			throw new FieldValidationException(buildErrorMessage( "${classDescriptor.simpleName}" ));
'		}

'	}

