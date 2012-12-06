@TargetFileName ${classDescriptor.simpleName}Validator.java # Name of output file with extension but without path 
@TargetDir C:/Temp/MogliCodeCreator/_Demo/src/main/java/<package>
@CreateNew true # creates target dir if not existing and overwrites target file if existing

package ${classDescriptor.package};
'
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
'
import com.iksgmbh.moglicc.demo.validator.FieldValidationException;
import com.iksgmbh.moglicc.demo.validator.FieldValidator;
import com.iksgmbh.moglicc.demo.validator.JavaBeanValidator;
import com.iksgmbh.moglicc.demo.validator.MandatoryFieldValidator;
import com.iksgmbh.moglicc.demo.validator.MaxLengthValidator;
import com.iksgmbh.moglicc.demo.validator.MinLengthValidator;
'
#parse("commonSubtemplates/B_ImportStatements.tpl")
'
public class ${classDescriptor.simpleName}Validator extends JavaBeanValidator {
'	
'	private static ${classDescriptor.simpleName}Validator instance;
'
'	public static void doYourJob(final ${classDescriptor.simpleName} data) {
'		if (instance == null) {
'			instance = new ${classDescriptor.simpleName}Validator(); 
'		} else {
'			instance.validationErrors.clear();
'		}
'		instance.validate(data);
'	}
'	
	#parse("C_Constructor.tpl")

'

	#parse("D_validateMethod.tpl")

'
}