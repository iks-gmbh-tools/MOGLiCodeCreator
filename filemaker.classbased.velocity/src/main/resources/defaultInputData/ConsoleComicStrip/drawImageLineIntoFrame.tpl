#set( $cutPosition = $currentImagePositionX - 1 )
#set( $dynamicLeftSpaceForCurrentLine = "" )
#if ($cutPosition > 0 && $cutPosition < $dynamicAreaWidth)
	#set( $dynamicLeftSpaceForCurrentLine = $backgroundLineToDraw.substring(0, $cutPosition) )
#end

#set( $cutPosition = $imageWidth + $currentImagePositionX - 1)
#set( $dynamicRightSpaceForCurrentLine = "" )
#if ($cutPosition > 0 && $cutPosition < $dynamicAreaWidth)
	#set( $dynamicRightSpaceForCurrentLine = $backgroundLineToDraw.substring($cutPosition) )
#end

#set( $indexOfTheImageLineToDraw = $numberOfTheImageLineToDraw - 1 )
#set( $lineOfImage = $imageLines.get($indexOfTheImageLineToDraw) )
#set( $numberOfImageCharsBeyondLeftMargin = 1 - $currentImagePositionX )
#set( $numberOfImageCharsBeyondRightMargin = $currentImagePositionX + $imageWidth - $dynamicAreaWidth - 1 )

#parse("buildLineToDraw.tpl")

#if ($lineToDraw.trim().equals(""))

	#set ($frameLine = "" )
	
#else	

	#set ($frameLine = $lineToDraw )
	
#end
		
#parse("echoFrameLine.tpl")	

