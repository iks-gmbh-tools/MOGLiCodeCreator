#if ($javaType == "String")
'		clone.$attributeName = new String(${attributeName});

#else
'		clone.$attributeName = ($javaType)this.${attributeName}.clone();  // probably, here is need of manual adaptation

#end
