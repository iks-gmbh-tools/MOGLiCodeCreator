#set ($areVariablesValid =true)

#if (${classDescriptor.getMetaInfoValueFor("skipSequence")} != "true")

	#if( $numberOfImages > 0) 

		#if( $imageHeight == 0) 
			#set ($areVariablesValid =false)
			#set ($frameLine = "No line definintions found for the first image. Expecting metainfos for the attribute 'image1' like 'line1', 'line2'..." )
			#parse("echoFrameLine.tpl")	
		#end
		
		#if( $imageWidth == 0) 
			#set ($areVariablesValid =false)
			#set ($frameLine = "The first line of the first image defines the image width. It must not be an empty line! Look at sequence ${classDescriptor.simpleName}!" )
			#parse("echoFrameLine.tpl")	
		#end

		#foreach ($imageCount in $loopCounter)

			#if( $imageCount <= $numberOfImages) 
				#set ($imageAttributeName = "image" + $imageCount)
				#set ($attributeDescriptor = $classDescriptor.getAttributeDescriptor($imageAttributeName))
				
				#if( $attributeDescriptor.name != $imageAttributeName) 
					#set ($areVariablesValid =false)
					#set ($frameLine = "Expected image not found: ${imageAttributeName}" )
					#parse("echoFrameLine.tpl")	
				#else
					
					#set( $metainfos = $attributeDescriptor.getMetaInfosWithNameStartingWith("line") )
					
					#if ($metainfos.size() < $imageHeight || $metainfos.size() > $imageHeight)
						#set ($areVariablesValid =false)
						#set ($frameLine = "Unexpected number of image lines for sequence ${classDescriptor.simpleName}. For $attributeDescriptor.name are $metainfos.size() lines defined. Expected $imageHeight lines like that 'line1', 'line2'..." )
						#parse("echoFrameLine.tpl")							
					#else
					
						#set ($lineCount =0)
						#foreach ($metainfo in $metainfos)

							#set ($lineCount = $lineCount + 1)
							
							#if ( $metainfo.getName().endsWith("$lineCount") )
							
								#if( $imageWidth > $metainfo.getValue().length() || $imageWidth < $metainfo.getValue().length() ) 
									#set ($areVariablesValid =false)
									#set ($frameLine = "Unexpected image line length for sequence ${classDescriptor.simpleName}: '$metainfo.value'. Expected: $imageWidth chars! Actual: $metainfo.getValue().length(). See Image$imageCount, Line${lineCount}!" )	
									#parse("echoFrameLine.tpl")							
								#end
								
							#else
							
								#set ($areVariablesValid =false)
								#set ($frameLine = "Unexpected image line for sequence ${classDescriptor.simpleName} and image${imageCount}. Expected 'line${lineCount}', Actual: Actual: '$metainfo.getName()'." )	
								#parse("echoFrameLine.tpl")							
							
							#end
							
						#end
						
					#end
					
				#end
				
			#end
			
		#end

	#end
	
#end