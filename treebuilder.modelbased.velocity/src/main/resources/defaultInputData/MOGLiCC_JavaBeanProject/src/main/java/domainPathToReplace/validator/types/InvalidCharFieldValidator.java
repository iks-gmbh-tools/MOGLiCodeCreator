package <domainPathToReplace>.validator.types;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.CharSet;

public class InvalidCharFieldValidator extends ValidCharFieldValidator {

	private CharSet invalidChars;

	public InvalidCharFieldValidator(final String fieldName, final String invalidChars) {
		super(fieldName, null);
		this.invalidChars = CharSet.getInstance(invalidChars);
	}

	@Override
	protected List<Character> getInvalidChars(final String value)
	{
		final char[] charArray = value.toCharArray();
		final List<Character> toReturn = new ArrayList<Character>();
		
		for (char c : charArray) {
			if (invalidChars.contains(c))
			{
				toReturn.add(new Character(c));
			}
		}
		
		return toReturn;
	}
	
}