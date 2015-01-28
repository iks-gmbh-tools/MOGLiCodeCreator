package <domainPathToReplace>.validator.helper;

public class FieldValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FieldValidationException(final String errorMessage) {
		super(errorMessage);
	}

}
