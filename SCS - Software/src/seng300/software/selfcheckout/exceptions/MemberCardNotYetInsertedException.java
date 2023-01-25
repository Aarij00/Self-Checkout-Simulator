package seng300.software.selfcheckout.exceptions;

@SuppressWarnings("serial")
public class MemberCardNotYetInsertedException extends RuntimeException {
	private String nested;

	/**
	 * Constructor used to nest other exceptions.
	 * 
	 * @param nested An underlying exception that is to be wrapped.
	 */
	public MemberCardNotYetInsertedException(Exception nested) {
		this.nested = nested.toString();
	}

	/**
	 * Basic constructor.
	 * 
	 * @param message An explanatory message of the problem.
	 */
	public MemberCardNotYetInsertedException(String message) {
		nested = message;
	}

}
