package seng300.software.selfcheckout.customer.membership;

import java.util.ArrayList;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.Card;

public class ScanMemberLogic extends CardReader {
	// database of members
	public static ArrayList<Card> membersDatabase = new ArrayList<Card>();

	public static void addNewMember(Card newMember) {
		// duplicates NOT ALLOWED
		if (isThereMember(newMember)) {
			return;
		}
		membersDatabase.add(newMember);
	}

	public static boolean isThereMember(Card newMember) {
		for (Card member : membersDatabase) {
			if (member.equals(newMember))
				return true;
		}
		return false;
	}

	public static void removeMember(Card member) {
		// if there is no member, arraylist is unchanged
		membersDatabase.remove(member);
	}
}
