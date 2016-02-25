#set( $scriptName = ${model.getMetaInfoValueFor("scriptName")} )
#set( $scriptType = ${model.getMetaInfoValueFor("scriptType")} )
#set( $fileExtension = "sh" )
#set( $commentMarker = "rem" )
#if ( $scriptType.equals( "batch" ) )
	#set( $fileExtension = "bat" )
	#set( $commentMarker = "noCommentMarkerReplacement" )
#end

@TargetFileName $scriptName.$fileExtension
@TargetDir <applicationRootDir>
@NameOfValidModel ConsoleComicStrip
@CreateNew true 
@ReplaceToNumberSign $commentMarker

#if ( ! $scriptType.equals( "batch" ) )
	rem!/bin/sh
#end
'
echo off 
'
#set( $dynamicAreaWidth = ${model.getMetaInfoValueFor("dynamicAreaWidth")} )
#set( $dynamicAreaHeight = ${model.getMetaInfoValueFor("dynamicAreaHeight")} )

## Windows systems
#if ( $scriptType.equals( "batch" ) )

	echo Extend console windows to following minimum size:
	echo $dynamicAreaWidth x $dynamicAreaHeight
	'
	timeout /t 5 /nobreak > NUL 
	cls
	'

## non-Windows systems
#else

	rem If this shell script is generated on a Windows maschine, 
	rem then copy its content and paste it into a file created on the non-windows maschine where you want to run it.
	'
	echo "Extend console windows to following minimum size:"
	echo "$dynamicAreaWidth x $dynamicAreaHeight"
	sleep 5
	clear
	'
	
#end

rem This is the insert marker of the VelocityModelBasedLineInserter - do not delete or modify this line!

#set( $holdConsoleOpenAtEnd = ${model.getMetaInfoValueFor("holdConsoleOpenAtEnd")} )
#if( $holdConsoleOpenAtEnd == "true" ) 

	'
	#if ( $scriptType.equals( "batch" ) )
		pause
	#else
		$SHELL
	#end
	
#end
