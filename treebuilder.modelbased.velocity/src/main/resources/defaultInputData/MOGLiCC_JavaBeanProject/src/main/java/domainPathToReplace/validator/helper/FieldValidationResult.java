package <domainPathToReplace>.validator.helper;

public class FieldValidationResult {

	public enum Status {OK, ERROR};
	
	public String errorMessage; 
	public Status status;
	
	public FieldValidationResult(final Status status, final String errorMessage) {
		if (status == null) {
			throw new IllegalArgumentException("status must not be null"); 
		}
		if (status == Status.OK) {
			if (errorMessage != null && errorMessage.length() > 1) {
				throw new IllegalArgumentException("errorMessage must be null or empty if Status=OK"); 
			}
		}
		if (status == Status.ERROR) {
			if (errorMessage == null || errorMessage.length() == 0) {
				throw new IllegalArgumentException("errorMessage must not be null or empty if Status=ERROR"); 
			}
		}
		this.status = status;
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public Status getStatus() {
		return status;
	}
}
