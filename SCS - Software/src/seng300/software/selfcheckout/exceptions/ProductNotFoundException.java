package seng300.software.selfcheckout.exceptions;

/**
 * Used when product not found in product database.
 *
 */
public class ProductNotFoundException extends Exception {
	private static final long serialVersionUID = 6068525038601569705L;
	
	public ProductNotFoundException() {
	}
	
	public ProductNotFoundException(String s) {
		super(s);
	}

}
