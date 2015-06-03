#if ($javaType == "String")
'		if (this.${attributeName} != null) clone.$attributeName = new String(${attributeName});

#else
'		if (this.${attributeName} != null) clone.$attributeName = ($javaType)this.${attributeName}.clone();  // probably, here is need of manual adaptation

#end
