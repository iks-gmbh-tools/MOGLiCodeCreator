package <domainPathToReplace>.validator.helper;

public abstract class FieldValidator {
	
	public final static FieldValidationResult STATUS_OK = new FieldValidationResult(FieldValidationResult.Status.OK, null);

	protected String fieldName;
	
	public String getFieldName() {
		return fieldName;
	}

	protected abstract FieldValidationResult validateValue(Object value);
	
}
