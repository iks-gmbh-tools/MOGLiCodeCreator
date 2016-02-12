#######################################################
# here the foreground is embedded into the background
#######################################################

#set( $cutPosition1 = $imageWidth - $numberOfImageCharsBeyondRightMargin )
#set( $cutPosition2 = $numberOfImageCharsBeyondLeftMargin )

#if( $numberOfImageCharsBeyondLeftMargin < 1 )

	#if( $numberOfImageCharsBeyondRightMargin < 1 )
		# draw whole image line
		#set( $partOfImageLineToDisplay = $lineOfImage )
	#else
		# cut chars on the right side of the image line
		#set( $partOfImageLineToDisplay = $lineOfImage.substring(0, $cutPosition1) )
	#end
	
#else

	#if( $numberOfImageCharsBeyondRightMargin < 1 )
		# cut chars on the left side of the image line
		#set( $partOfImageLineToDisplay = $lineOfImage.substring($cutPosition2) )
	#else
		# cut both sides (happens when image is larger than dynamic area)
		#set( $partOfImageLineToDisplay = $lineOfImage.substring($cutPosition2, $cutPosition1) )
	#end	

#end

#set( $lineToDraw = ${dynamicLeftSpaceForCurrentLine} + ${partOfImageLineToDisplay} + ${dynamicRightSpaceForCurrentLine} )

#parse("handleTransparancy.tpl")

#if ( $spaceholder.length() == 1) 
	#set( $lineToDraw = $TemplateStringUtility.replaceAllIn($lineToDraw, $spaceholder, " ") )
#end


