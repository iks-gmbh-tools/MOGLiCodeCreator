#set( $quote = '"' )
#set( $xmlAttributes = "" )

#foreach ($metainfo in $metaInfoList)

	#set( $metainfoName = $metainfo.getName() )
	#set( $metainfoValue = $metainfo.getValue() )
	#set( $xmlAttributes = $xmlAttributes + "$metainfoName=" + $quote + "$metainfoValue" + $quote + " " )

#end
