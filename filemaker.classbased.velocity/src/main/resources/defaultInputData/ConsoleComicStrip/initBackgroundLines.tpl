
#set ($backgroundLines = [])

#foreach ($count in $dynamicAreaHeightCounter)

	#set ($metainfoName = "backgroundFillingPatternLine" + $count )
	
	#set( $fillingPatternModel = ${model.getMetaInfoValueFor( $metainfoName )} )
	#if ( ! $model.isValueAvailable($fillingPatternModel)) 
		#set( $fillingPatternModel = " " )
	#end

	#set( $fillingPattern = ${classDescriptor.getMetaInfoValueFor( $metainfoName )} )
	#if ( ! $classDescriptor.isValueAvailable($fillingPattern) || $fillingPattern.length() == 0) 
	
		#set( $fillingPattern = $fillingPatternModel )
	
	#end

	#set ($currentBackgroundLine = "")
	
	#foreach ($count in $dynamicAreaWidthCounter)
		
		#if( $currentBackgroundLine.length() < $dynamicAreaWidth)
			#set ($currentBackgroundLine = $currentBackgroundLine + $fillingPattern)
		#end
	
	#end

	#set ($currentBackgroundLine = $currentBackgroundLine.substring(0, $dynamicAreaWidth))
	
	#set ($forgetMe = $backgroundLines.add($currentBackgroundLine) )

#end