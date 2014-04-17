#set($dotEquals = ".equals")
'		if ($attributeName == null) 
'		{
'			if (other.$attributeName != null)
'				return false;
'		} else 
'		{
'			if (! $attributeName$dotEquals(other.$attributeName))
'				   return false;
'		}