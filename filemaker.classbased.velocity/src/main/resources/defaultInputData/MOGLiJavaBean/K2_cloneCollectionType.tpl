#set( $collectionMetaType = $TemplateJavaUtility.getCollectionMetaType($javaType) )
#set( $collectionElementType = $TemplateJavaUtility.getCollectionElementType($javaType) )
'
'		// probably, here is need of manual adaptation to correct implementation type
'		final  $collectionMetaType<$collectionElementType> collection = new $collectionMetaType<$collectionElementType>();
'		for (final $collectionElementType element : $attributeName) {
'			collection.add(element);
'		}