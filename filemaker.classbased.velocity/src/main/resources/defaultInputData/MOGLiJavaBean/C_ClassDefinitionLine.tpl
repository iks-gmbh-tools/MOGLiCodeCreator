#set( $superClass = $classDescriptor.getMetaInfoValueFor("extends") )
#set( $superClassSimpleName = $TemplateJavaUtility.getSimpleClassName($superClass) )
#set( $interfaceList = $classDescriptor.getAllMetaInfoValuesFor("implements") )
#set( $interfaceSimpleNameList = $TemplateJavaUtility.getSimpleClassName($interfaceList) )
#set( $interfaceSimpleNames = $TemplateStringUtility.toCommaSeparatedString($interfaceSimpleNameList) )

#if ( $classDescriptor.isValueAvailable($superClass) ) 

     #if ( $TemplateStringUtility.isListEmpty($interfaceList) )
     
     	public class ${classDescriptor.simpleName} extends $superClassSimpleName 
     	
     #else
     
     	public class ${classDescriptor.simpleName} extends $superClassSimpleName implements $interfaceSimpleNames
     
     #end
     
#else

     #if ( $TemplateStringUtility.isListEmpty($interfaceList) )
     
     	public class ${classDescriptor.simpleName}
     	
     #else
     
     	public class ${classDescriptor.simpleName} implements $interfaceSimpleNames
     
     #end
          
#end