
#foreach ($line in $footerLines)

	#if ($line.trim().equals(""))

		#set ($frameLine = "" )
		
	#else	

		#set ($frameLine = $line )
		
	#end
			
	#parse("echoFrameLine.tpl")	

#end
