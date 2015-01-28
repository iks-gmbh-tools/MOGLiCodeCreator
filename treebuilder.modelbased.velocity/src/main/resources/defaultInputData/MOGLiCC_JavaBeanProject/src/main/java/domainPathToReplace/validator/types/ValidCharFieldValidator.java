package <domainPathToReplace>.validator.types;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharSet;

import <domainPathToReplace>.validator.helper.FieldValidationResult;
import <domainPathToReplace>.validator.helper.FieldValidationResult.Status;
import <domainPathToReplace>.validator.helper.FieldValidator;

public class ValidCharFieldValidator extends FieldValidator {

	private CharSet validChars;

	public ValidCharFieldValidator(final String fieldName, final String validChars) {
		this.fieldName = fieldName;
		this.validChars = CharSet.getInstance(validChars);
	}

	@Override
	public FieldValidationResult validateValue(final Object value) {
		if (value == null) {
			return STATUS_OK; // nothing to validate
		}
		
		if (value instanceof String) {
			final List<Character> invalidChars = getInvalidChars( (String) value);
			if ( invalidChars.isEmpty() )  {
				return STATUS_OK;
			}
			else
			{
				final String invalidCharsAsString = ArrayUtils.toString(invalidChars);
				return new FieldValidationResult(Status.ERROR, this.getClass().getSimpleName() + ": " + 
                        "Field '" + fieldName + "' contains invalid char(s): "  + invalidCharsAsString);
			}
		}
		else
		{
			return new FieldValidationResult(Status.ERROR, this.getClass().getSimpleName() + ": " + 
                    "Field '" + fieldName + "' cannot be applied to fields of type '" + value.getClass().getSimpleName() + "'");
		}
		
			
	}


	protected List<Character> getInvalidChars(final String value)
	{
		final char[] charArray = value.toCharArray();
		final List<Character> toReturn = new ArrayList<Character>();
		
		for (char c : charArray) {
			if (! validChars.contains(c))
			{
				toReturn.add(new Character(c));
			}
		}
		
		return toReturn;
	}
	
}