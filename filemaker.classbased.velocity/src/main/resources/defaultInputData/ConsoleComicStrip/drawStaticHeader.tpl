
#foreach ($line in $headerLines)

	#if ($line.trim().equals(""))

		#set ($frameLine = "" )
		
	#else	

		#set ($frameLine = $line )
		
	#end
			
	#parse("echoFrameLine.tpl")	


#end
