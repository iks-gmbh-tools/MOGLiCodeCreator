#if ($javaType == "boolean")
' 		if (${className}1.get${AttributeName}()) {
'			${className}1.set${AttributeName}(false);
'		} else {
'			${className}1.set${AttributeName}(true);
'		}

#elseif ($javaType == "char")
' 		if (${className}1.get${AttributeName}() == 'a') {
'			${className}1.set${AttributeName}('b');
'		} else {
'			${className}1.set${AttributeName}('a');
'		}

#else
'		${className}1.set$AttributeName(($javaType) (${className}1.get$AttributeName() + 1));

#end