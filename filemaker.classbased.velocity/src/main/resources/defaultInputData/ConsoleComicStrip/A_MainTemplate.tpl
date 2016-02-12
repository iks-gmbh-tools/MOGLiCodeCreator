#set( $scriptName = ${classDescriptor.simpleName}  )
#set( $scriptType = ${model.getMetaInfoValueFor("scriptType")} )
#set( $fileExtension = "sh" )
#set( $commentMarker = "rem" )
#if ( $scriptType.equals( "batch" ) ) 
	#set( $fileExtension = "bat" )
	#set( $commentMarker = "noCommentMarkerReplacement" )
#end

@TargetFileName $scriptName.$fileExtension
@NameOfValidModel ConsoleComicStrip
@CreateNew true 

#parse("initVariables.tpl")
#parse("validateVariables.tpl")


#if( ! $areVariablesValid )

	#set ($frameLine = "Please fix the problem(s)." )	
	#parse("echoFrameLine.tpl")
	'
	pause
	
#elseif (${classDescriptor.getMetaInfoValueFor("skipSequence")} == "true")

	#if ( $scriptType.equals( "batch" ) )
		rem This sequence is configured to be skipped.
	#else
		# This sequence is configured to be skipped.
	#end
	
#else

	#foreach ($frameNumber in $frameCounter)

		## Windows systems
		#if ( $scriptType.equals( "batch" ) )
		
			rem ${frameNumber}th frame   
			cls
		   
			#parse("drawFrame.tpl")	
			
			timeout /t $secondsToSleepWithEachFrame /nobreak > NUL 
			'		

		## non-Windows systems
		#else

			rem ${frameNumber}th frame   
			clear
		   
			#parse("drawFrame.tpl")	
			
			sleep $secondsToSleepWithEachFrame
			'		
		
		#end
		
	#end

#end
