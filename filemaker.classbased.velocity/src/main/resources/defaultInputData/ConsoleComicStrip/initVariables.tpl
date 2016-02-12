#parse("initGlobalVariables.tpl")


#################################################################
###############   class specific variables     ###############
#################################################################

#set( $motionSpeedXAsString = ${classDescriptor.getMetaInfoValueFor("motionSpeedX")}  )
#if( ! $classDescriptor.isValueAvailable($motionSpeedXAsString) )
	#set( $motionSpeedXAsString = "1" )
#end
#set( $motionSpeedX = $integer.parseInt( $motionSpeedXAsString ) )

#set( $motionSpeedYAsString = ${classDescriptor.getMetaInfoValueFor("motionSpeedY")}  )
#if( ! $classDescriptor.isValueAvailable($motionSpeedYAsString) )
	#set( $motionSpeedYAsString = "1" )
#end
#set( $motionSpeedY = $integer.parseInt( $motionSpeedYAsString ) )

#set( $numberOfFramesAsString = ${classDescriptor.getMetaInfoValueFor("numberOfFrames")}  )
#if( ! $classDescriptor.isValueAvailable($numberOfFramesAsString) )
	#set( $numberOfFramesAsString = "1" )
#end
#set( $numberOfFrames = $integer.parseInt( $numberOfFramesAsString ) )

#set( $startImagePositionXAsString = ${classDescriptor.getMetaInfoValueFor("startImagePositionX")}  )
#if( ! $classDescriptor.isValueAvailable($startImagePositionXAsString) )
	#set( $startImagePositionXAsString = "1" )
#end
#set( $currentImagePositionX = $integer.parseInt( $startImagePositionXAsString ) )

#set( $startImagePositionYAsString = ${classDescriptor.getMetaInfoValueFor("startImagePositionY")}  )
#if( ! $classDescriptor.isValueAvailable($startImagePositionYAsString) )
	#set( $startImagePositionYAsString = "1" )
#end
#set( $currentImagePositionY = $integer.parseInt( $startImagePositionYAsString ) )


## loop for frame iteration
#set ($frameCounter = [])
#foreach ($loopCount in $loopCounter)
	#if( $loopCount <= $numberOfFrames) 
	  #set ($forgetMe = $frameCounter.add($loopCount) )
	#end
#end

#set( $trailingSpace = "" )
#set( $rotatingImageNumber = 0 )

## read image infos
#set( $numberOfImages = 0 )
#set( $attributeDescriptorList = ${classDescriptor.getAttributeDescriptorList()} )
#foreach ($attributeDescriptor in $attributeDescriptorList)
	#if( $attributeDescriptor.getName().startsWith("image") ) 
	  #set ($numberOfImages = $numberOfImages + 1 )
	#end
#end
#set( $attributeDescriptor = $classDescriptor.getAttributeDescriptor("image1"))
#set( $metaInfos = ${attributeDescriptor.getMetaInfosWithNameStartingWith("line")} )
#set( $imageHeight = $metaInfos.size() )
#set( $line1 = ${attributeDescriptor.getMetaInfoValueFor("line1")} )
#set( $imageWidth = $line1.length() )

#parse("initImageRotationCounter.tpl")
#parse("initBackgroundLines.tpl")

#set( $foregroundTransparancy = ${classDescriptor.getMetaInfoValueFor("foregroundTransparancy")}  )
#if ( ! $classDescriptor.isValueAvailable($foregroundTransparancy)) 
	#set( $foregroundTransparancy = $modelForegroundTransparancy  )
#end

#set( $secondsToSleepWithEachFrame = ${classDescriptor.getMetaInfoValueFor("secondsToSleepWithEachFrame")} )
#if ( $classDescriptor.isValueAvailable($secondsToSleepWithEachFrame) )
	#if( $secondsToSleepWithEachFrame.startsWith("0") && $scriptType.equals($batchScript) )
		#set( $secondsToSleepWithEachFrame = "1" )
	#end
#else
		#set( $secondsToSleepWithEachFrame = $modelSecondsToSleepWithEachFrame )
#end
