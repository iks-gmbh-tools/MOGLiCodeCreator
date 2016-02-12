######################################################
###############   Global Variables     ###############
######################################################

#set ($loopCounterBase = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15])

## Note: Velocity VTL only comes with loops forloop that require an existing element container
##       This is the element container used to construct other bigger element containers than the loopCounterBase
#set ($loopCounter = [])
#set ($maxElementsAllowed = 0)
#foreach ($loopCount1 in $loopCounterBase)
	#foreach ($loopCount2 in $loopCounterBase)
		#set ($maxElementsAllowed = $maxElementsAllowed + 1)
		#set ($forgetMe = $loopCounter.add($maxElementsAllowed) )
	#end
#end

#set( $integer = 0 )

#set( $dynamicAreaWidth = ${model.getMetaInfoValueFor("dynamicAreaWidth")} )
#if ( $model.isValueAvailable($dynamicAreaWidth))
	#set( $dynamicAreaWidth = $integer.parseInt( $dynamicAreaWidth ) )
#else
	#set( $dynamicAreaWidth = -1 )
#end

#set( $dynamicAreaHeight = ${model.getMetaInfoValueFor("dynamicAreaHeight")} )
#if ( $model.isValueAvailable($dynamicAreaHeight))
	#set( $dynamicAreaHeight = $integer.parseInt( $dynamicAreaHeight ) )
#else
	#set( $dynamicAreaHeight = -1 )
#end


#set( $scriptType = ${model.getMetaInfoValueFor("scriptType")} )
#set( $batchScript = "batch" )

#set( $modelSecondsToSleepWithEachFrame = ${model.getMetaInfoValueFor("secondsToSleepWithEachFrame")} )
#if ( $model.isValueAvailable($modelSecondsToSleepWithEachFrame) )
	#if( $modelSecondsToSleepWithEachFrame.startsWith("0") && $scriptType.equals($batchScript) )
		#set( $modelSecondsToSleepWithEachFrame = "1" )
	#end
#else
		#set( $modelSecondsToSleepWithEachFrame = "1" )
#end

## loop for dynamicAreaWidth iteration
#set ($dynamicAreaWidthCounter = [])
#foreach ($loopCount in $loopCounter)
	#if( $loopCount <= $dynamicAreaWidth) 
	  #set ($forgetMe = $dynamicAreaWidthCounter.add($loopCount) )
	#end
#end

## loop for dynamicAreaHeight iteration
#set ($dynamicAreaHeightCounter = [])
#foreach ($loopCount in $loopCounter)
	#if( $loopCount <= $dynamicAreaHeight) 
	  #set ($forgetMe = $dynamicAreaHeightCounter.add($loopCount) )
	#end
#end

#set( $headerLines = ${model.getAllMetaInfoValuesFor("staticHeaderLine")} )
#set( $footerLines = ${model.getAllMetaInfoValuesFor("staticFooterLine")} )

#set( $modelForegroundTransparancy = ${model.getMetaInfoValueFor("foregroundTransparancy")}  )
#if ( ! $model.isValueAvailable($modelForegroundTransparancy)) 
	#set( $modelForegroundTransparancy = "off"  )
#end