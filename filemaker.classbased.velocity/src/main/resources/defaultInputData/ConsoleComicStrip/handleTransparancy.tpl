
#######################################################
# Full transparancy
#######################################################

#if ( $foregroundTransparancy.equalsIgnoreCase("FULL") )

	#foreach ($loopCount in $loopCounter)
	
		#if( $loopCount <= $lineToDraw.length()) 
		
			#set( $index = $loopCount - 1 )
			#if ( $lineToDraw.charAt($index) == ' ')
				
				#set( $charFromBackground = $backgroundLineToDraw.charAt($index) )
				#set( $lineToDraw = $lineToDraw.substring(0, $index) + $charFromBackground + $lineToDraw.substring($loopCount) )
				
			#end
		
		#end
	#end

#end


#######################################################
# Margin transparancy
#######################################################


#if ( $foregroundTransparancy.equalsIgnoreCase("atMarginOnly") )

	#set( $position1 = $dynamicLeftSpaceForCurrentLine.length() )
	#set( $position2 = $position1 + $partOfImageLineToDisplay.length() )

	#set( $leftMarginReached = false )
	#if( $position1 == 0) 
		#set( $leftMarginReached = true )
	#end
	
	#foreach ($loopCount in $loopCounter)
	
		#if( $loopCount > $position1 && $loopCount < $position2) 
		
			#set( $index = $loopCount - 1 )
			#if ( $lineToDraw.charAt($index) == ' ' && ! $leftMarginReached)
				
				#set( $charFromBackground = $backgroundLineToDraw.charAt($index) )
				#set( $lineToDraw = $lineToDraw.substring(0, $index) + $charFromBackground + $lineToDraw.substring($loopCount) )
				
			#else
			
				#set( $leftMarginReached = true )
				
			#end
		
		#end
	#end
	
	#set( $rightMarginReached = false )
	#if( $dynamicRightSpaceForCurrentLine.length() == 0) 
		#set( $rightMarginReached = true )
	#end
	#set( $index = $lineToDraw.length() )

	#foreach ($loopCount in $loopCounter)
	
		#set( $index = $index - 1 )
		#if( $index > $position1 && $index < $position2) 
		
			#if ( $lineToDraw.charAt($index) == ' ' && ! $rightMarginReached)
				
				#set( $indexPlusOne = $index + 1 )
				#set( $charFromBackground = $backgroundLineToDraw.charAt($index) )
				#set( $lineToDraw = $lineToDraw.substring(0, $index) + $charFromBackground + $lineToDraw.substring($indexPlusOne) )
				
			#else
			
				#set( $rightMarginReached = true )
				
			#end
		
		#end
	#end
	
#end
