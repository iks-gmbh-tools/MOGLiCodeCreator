@TargetFileName BeanFactory.java # Name of output file with extension but without path 
@TargetDir <applicationRootDir>/example
@ReplaceStart startReplace
@replaceEnd endReplace

#foreach($classDescriptor in $model.classDescriptorList)

	#set( $className = $TemplateStringUtility.firstToUpperCase($classDescriptor.simpleName) )
	
'	public static $className get$className() {;
'		return new $className();
'	}
'

#end
	