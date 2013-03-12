@TargetFileName BeanFactory.java # Name of output file with extension but without path 
@TargetDir <applicationRootDir>/example
@ReplaceStart /** This is the StartReplace Marker for the MOGLi Code Creator - Do not modify this line! */
@replaceEnd /** This is the EndReplace Marker for the MOGLi Code Creator - Do not modify this line! */

#foreach($classDescriptor in $model.classDescriptorList)

	#set( $className = $TemplateStringUtility.firstToUpperCase($classDescriptor.simpleName) )
	
'	public static $className get$className() {;
'		return new $className();
'	}
'

#end
	