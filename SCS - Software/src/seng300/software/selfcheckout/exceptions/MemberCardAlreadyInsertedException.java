package seng300.software.selfcheckout.exceptions;

@SuppressWarnings("serial")
public class MemberCardAlreadyInsertedException extends RuntimeException {
	private String nested;

	/**
	 * Constructor used to nest other exceptions.
	 * 
	 * @param nested An underlying exception that is to be wrapped.
	 */
	public MemberCardAlreadyInsertedException(Exception nested) {
		this.nested = nested.toString();
	}

	/**
	 * Basic constructor.
	 * 
	 * @param message An explanatory message of the problem.
	 */
	public MemberCardAlreadyInsertedException(String message) {
		nested = message;
	}
}
