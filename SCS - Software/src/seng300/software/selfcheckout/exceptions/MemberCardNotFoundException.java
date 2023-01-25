package seng300.software.selfcheckout.exceptions;

@SuppressWarnings("serial")
public class MemberCardNotFoundException extends RuntimeException {
	private String nested;

	/**
	 * Constructor used to nest other exceptions.
	 * 
	 * @param nested An underlying exception that is to be wrapped.
	 */
	public MemberCardNotFoundException(Exception nested) {
		this.nested = nested.toString();
	}

	/**
	 * Basic constructor.
	 * 
	 * @param message An explanatory message of the problem.
	 */
	public MemberCardNotFoundException(String message) {
		nested = message;
	}

}