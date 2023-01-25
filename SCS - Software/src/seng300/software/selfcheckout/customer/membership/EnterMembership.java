package seng300.software.selfcheckout.customer.membership;

import java.util.ArrayList;
import java.util.List;

import org.lsmr.selfcheckout.Card;

public class EnterMembership {
	
	private String number;
	private String cardholder;
	private Card card;
	private List<Card> memberships = new ArrayList<Card>();
	
	public EnterMembership(String membership) {
		number = membership;
		cardholder = "Member " + membership;
		card = new Card("Membership", number, cardholder, null, null, false, false);
		memberships.add(card);
	}
}
