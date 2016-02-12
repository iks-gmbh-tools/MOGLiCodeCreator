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
@InsertBelow This is the insert marker of the VelocityModelBasedLineInserter - do not delete or modify this line!
@ReplaceToNumberSign $commentMarker

#foreach ($classDescriptor in $model.getClassDescriptorList())

	#set( $subscriptToRead = "/output/VelocityClassBasedFileMaker/ConsoleComicStrip/" + ${classDescriptor.simpleName} + "." + $fileExtension )
	#set( $subscriptToReadWithPath = "." + $subscriptToRead )
	#set( $lineListOfFileContent = $TemplateStringUtility.getTextFileContent($subscriptToReadWithPath) )
	 
	#if( $lineListOfFileContent.size() == 1 && $lineListOfFileContent.get(0).startsWith("ERROR") )

		# special solution for integration test of MOGLiCC 	
		#set( $subscriptToReadWithPath = "./target/TestDir" + $subscriptToRead )
		#set( $lineListOfFileContent = $TemplateStringUtility.getTextFileContent($subscriptToReadWithPath) )
		
	#end 
	
	'
	'
	'
	rem XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	rem               Sequence ${classDescriptor.simpleName}
	rem XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	
	'
	
	#foreach ($line in $lineListOfFileContent)
		
		#if ($line.trim().equals("") )
		
			'

		#else
		
			$line
			
		#end
		
	#end

#end
