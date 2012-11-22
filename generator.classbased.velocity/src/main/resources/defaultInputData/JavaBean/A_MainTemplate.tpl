@TargetFileName ${classDescriptor.simpleName}.java # Name of output file with extension but without path 
@TargetDir C:/Temp/MogliCodeCreator/_Demo/src/<package>
@CreateNew true # creates target dir if not existing and overwrites target file if existing

package ${classDescriptor.package};
'
#parse("B_ImportStatements.tpl")
'
#parse("C_ClassDefinitionLine.tpl")
{
	#parse("D_Serializable.tpl")

	#parse("E_Variables.tpl")

	#parse("F_SetterMethods.tpl")
	
	#parse("G_GetterMethods.tpl")

'	// ===============  additional Javabean methods  ===============
'
	#parse("H_toStringMethod.tpl")
'
	#parse("I_equalsMethod.tpl")
'
	#parse("J_hashCodeMethod.tpl")
'

#if ( $classDescriptor.doesHaveMetaInfo( "implements", "java.lang.Cloneable") )

	#parse("K_cloneMethod.tpl")
	
#end	
}