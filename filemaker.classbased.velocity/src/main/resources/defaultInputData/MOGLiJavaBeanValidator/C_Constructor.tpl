
'	public ${classDescriptor.simpleName}Validator() {
'		validationErrors = new ArrayList<String>();
'		validators = new HashMap<String, List<FieldValidator>>();
'		List<FieldValidator> validatorList;

#foreach($attributeDescriptor in $classDescriptor.attributeDescriptorList)
'
		#set( $attributeName = $TemplateStringUtility.firstToLowerCase($attributeDescriptor.name) )
		
'		validatorList = new ArrayList<FieldValidator>();

		#if ( $attributeDescriptor.doesHaveAnyMetaInfosWithName("Mandatory") )
			#set( $value = $attributeDescriptor.getMetaInfoValueFor("Mandatory") )
	'		validatorList.add(new MandatoryFieldValidator("$attributeName", $value));
		#end

		#if ( $attributeDescriptor.doesHaveAnyMetaInfosWithName("ValidChars") )
			#set( $value = $attributeDescriptor.getMetaInfoValueFor("ValidChars") )
	'		validatorList.add(new ValidCharFieldValidator("$attributeName", "$value"));
		#end

		#if ( $attributeDescriptor.doesHaveAnyMetaInfosWithName("InvalidChars") )
			#set( $value = $attributeDescriptor.getMetaInfoValueFor("InvalidChars") )
	'		validatorList.add(new InvalidCharFieldValidator("$attributeName", "$value"));
		#end

		#if ( $attributeDescriptor.doesHaveAnyMetaInfosWithName("MinLength") )
			#set( $value = $attributeDescriptor.getMetaInfoValueFor("MinLength") )
	'		validatorList.add(new MinLengthValidator("$attributeName", $value));
		#end
	
		#if ( $attributeDescriptor.doesHaveAnyMetaInfosWithName("MaxLength") )
			#set( $value = $attributeDescriptor.getMetaInfoValueFor("MaxLength") )
	'		validatorList.add(new MaxLengthValidator("$attributeName", $value));
		#end

'		validators.put("$attributeName", validatorList);

#end

'	}
	