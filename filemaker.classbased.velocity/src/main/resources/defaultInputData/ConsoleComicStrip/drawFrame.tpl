#parse("drawStaticHeader.tpl")

#parse("readLinesOfCurrentImage.tpl")

#foreach ($dynamicAreaHeightCount in $dynamicAreaHeightCounter)

	#set ($lineIndex = $dynamicAreaHeightCount - 1 )
	#set ($backgroundLineToDraw = $backgroundLines.get($lineIndex) )
	#set( $numberOfTheImageLineToDraw = $dynamicAreaHeightCount - $currentImagePositionY + 1 )
	#set( $posOfLastCharInImage = $currentImagePositionX + $imageWidth - 1 )
	#set( $isImageToDraw = $numberOfImages > 0 && 
	                       $numberOfTheImageLineToDraw > 0 && $numberOfTheImageLineToDraw <= $imageHeight && 
	                       $currentImagePositionX <= $dynamicAreaWidth && $posOfLastCharInImage > 0)
	
	#if ( $isImageToDraw )
	
		# at least one char or one line of the image has to be displayed 
		#parse("drawImageLineIntoFrame.tpl")
	
	#else 
	
		# image is outside the dynamic area, either right, left, above or below 

		#if ($backgroundLineToDraw.trim().equals(""))
		
			#set ($frameLine = "" )
			
		#else	

			#set ($frameLine = $backgroundLineToDraw )
			
		#end
				
		#parse("echoFrameLine.tpl")		
			
	#end

	
#end

#adjust image position
#set( $currentImagePositionX = $currentImagePositionX + $motionSpeedX )
#set( $currentImagePositionY = $currentImagePositionY + $motionSpeedY )


#parse("drawStaticFooter.tpl")

