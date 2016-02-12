
#if( $numberOfImages > 0)

	#if( $rotatingImageNumber < $imageRotationCounter.size() )

		#set( $rotatingImageNumber = $rotatingImageNumber + 1 )

	#else

		#set( $rotatingImageNumber = 1 )

	#end

	#set( $rotatingImageIndex = $rotatingImageNumber - 1 )
	#set ($imageAttributeName = "image" + $imageRotationCounter.get($rotatingImageIndex))
	#set ($attributeDescriptor = $classDescriptor.getAttributeDescriptor($imageAttributeName))
	#set( $metainfos = $attributeDescriptor.getMetaInfosWithNameStartingWith("line") )
	#set ($imageLines = [])

	#foreach ($metainfo in $metainfos)

		  #set ($forgetMe = $imageLines.add( $metainfo.getValue() ) )	
		  
	#end


	#set( $spaceholder = $attributeDescriptor.getMetaInfoValueFor("spaceholder") )
	#if ( $attributeDescriptor.isValueAvailable($spaceholder)) 
		#set( $spaceholder = "" + $spaceholder.charAt(0) )
	#else
		#set( $spaceholder = "" )
	#end

#end