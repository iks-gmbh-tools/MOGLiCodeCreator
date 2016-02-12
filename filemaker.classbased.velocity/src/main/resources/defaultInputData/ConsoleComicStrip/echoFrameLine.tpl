
#if ($frameLine.equals(""))

	#if ( $scriptType.equals( "batch" ) ) 
	
		echo.
		
	#else
	
		echo ""
		
	#end
	
#else

	#if ( $scriptType.equals( "batch" ) ) 
	
		echo $frameLine
		
	#else
	
		# workaround that \ is interpreted as escaping symbol
		echo "$frameLine "
		
	#end

#end
