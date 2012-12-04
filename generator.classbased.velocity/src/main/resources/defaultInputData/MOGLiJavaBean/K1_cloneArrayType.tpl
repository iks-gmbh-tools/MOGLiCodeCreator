#set( $arrayElementType = $TemplateJavaUtility.getArrayElementType($javaType) )
'
'		final ${arrayElementType}[] arr = new ${arrayElementType}[${attributeName}.length];
'		for (int i = 0; i < ${attributeName}.length; i++) {
'			if (${attributeName}[i] != null) {
'                  // probably, here is need of manual adaptation
'				arr[i] = ($arrayElementType)${attributeName}[i].clone();
'			}
'		}
'		clone.${attributeName} = arr;