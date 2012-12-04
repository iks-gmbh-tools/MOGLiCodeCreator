#if ($javaType == "float")
'		if (Float.floatToIntBits($attributeName) != Float.floatToIntBits(other.$attributeName))
'			return false;

#elseif ($javaType == "double")
'		if (Double.doubleToLongBits($attributeName) != Double.doubleToLongBits(other.$attributeName))
'			return false;

#else
'		if ($attributeName != other.$attributeName)
'			return false;
#end
