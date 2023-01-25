package seng300.testing;

import org.junit.Test;

import seng300.software.selfcheckout.customer.membership.EnterMembership;

public class EnterMembershipTest {
	
	@Test
	public void testEnterMembership() {
		String membershipNumber = "1234";
		EnterMembership enter = new EnterMembership(membershipNumber);
	}

}
