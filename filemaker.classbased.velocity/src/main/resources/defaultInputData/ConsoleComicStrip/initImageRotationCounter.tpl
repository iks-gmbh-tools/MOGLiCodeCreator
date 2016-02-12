## loop for image rotation
#set ($imageRotationCounter = [])
#foreach ($imageCount in $loopCounter)

	#if( $imageCount <= $numberOfImages) 
	
		#set ($imageAttributeName = "image" + $imageCount)
		#set ($attributeDescriptor = $classDescriptor.getAttributeDescriptor($imageAttributeName))
		#set( $repetitionsAsString = $attributeDescriptor.getMetaInfoValueFor("repetitions") )
		
		#if ($attributeDescriptor.isValueAvailable($repetitionsAsString))
			#set( $imageRepetitions = $integer.parseInt( $repetitionsAsString ) )
		#else
			#set( $imageRepetitions = 1 )
		#end
		
		#set ($imageRepetionCounter = [])
		#foreach ($loopCount in $loopCounter)
			#if($loopCount <= $imageRepetitions)
				#set ($forgetMe = $imageRotationCounter.add($imageCount) )
			#end
		#end
		
	#end
	
#end
