#if ($javaType == "float")
' 		result = prime * result + Float.floatToIntBits($attributeName);

#elseif ($javaType == "double")
'		long temp = Double.doubleToLongBits($attributeName);
'		result = prime * result + (int) (temp ^ (temp >>> 32));

#elseif ($javaType == "boolean")
' 		result = prime * result + ($attributeName ? 1231 : 1237);

#elseif ($javaType == "long")
' 		result = prime * result + (int) ($attributeName ^ ($attributeName >>> 32));

#else
'		result = prime * result + $attributeName;

#end
		