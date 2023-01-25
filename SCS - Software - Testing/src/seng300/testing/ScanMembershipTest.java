package seng300.testing;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.lsmr.selfcheckout.Card;

import seng300.software.selfcheckout.customer.membership.MachineCardReader;
import seng300.software.selfcheckout.customer.membership.ScanMemberLogic;
import seng300.software.selfcheckout.exceptions.MemberCardAlreadyInsertedException;
import seng300.software.selfcheckout.exceptions.MemberCardNotFoundException;
import seng300.software.selfcheckout.exceptions.MemberCardNotYetInsertedException;

@RunWith(JUnit4.class)
public class ScanMembershipTest {
	String def_pin = "1234";
	public Card sampleCard1;
	public Card sampleCard2;
	public Card sampleCard3;
	public Card sampleCard4;
	public Card sampleCard5;
	public Card sampleCard6_NEVER_ADDED;

	ArrayList<Card> sampleCards;

	ScanMemberShipObserverCustomStub observer;
	MachineCardReader machine;

	@Before
	public void setup() {
		String type = "membership";
		String number = "1";
		String cardholder = "Harsreet Singh";
		String cvv = null;
		String pin = def_pin;
		boolean isTapEnabled = true;
		boolean hasChip = true;

		sampleCard1 = new Card(type, number, cardholder, cvv, pin, isTapEnabled, hasChip);

		number = "2";
		cardholder = "Harsweet Singh";
		sampleCard2 = new Card(type, number, cardholder, cvv, pin, isTapEnabled, hasChip);

		number = "3";
		cardholder = "Safe Jatt";
		sampleCard3 = new Card(type, number, cardholder, cvv, pin, isTapEnabled, hasChip);

		number = "4";
		cardholder = "Jatt Airways";
		sampleCard4 = new Card(type, number, cardholder, cvv, pin, isTapEnabled, hasChip);

		number = "5";
		cardholder = "Jatt Airline";
		sampleCard5 = new Card(type, number, cardholder, cvv, pin, isTapEnabled, hasChip);

		number = "6";
		cardholder = "NEVER USED";
		sampleCard6_NEVER_ADDED = new Card(type, number, cardholder, cvv, pin, isTapEnabled, hasChip);

		sampleCards = new ArrayList<Card>();
		sampleCards.add(sampleCard1);
		sampleCards.add(sampleCard2);
		sampleCards.add(sampleCard3);
		sampleCards.add(sampleCard4);
		sampleCards.add(sampleCard5);

		observer = new ScanMemberShipObserverCustomStub();
		machine = new MachineCardReader();
		machine.attachMachineCardReaderObserver(observer);
	}

	@Test
	public void assist_setup1() {
		for (Card c : sampleCards) {
			ScanMemberLogic.addNewMember(c);
		}
	}

	/* CHECKS for ScanMemberLogic */
	// CHECK1: add a new member: change in arrays
	@Test
	public void addMemberTest1() {
		Assert.assertTrue(ScanMemberLogic.membersDatabase.size() == 0);
		ScanMemberLogic.addNewMember(sampleCard1);
		Assert.assertTrue(ScanMemberLogic.membersDatabase.size() == 1);
		Assert.assertTrue(ScanMemberLogic.membersDatabase.get(0).equals(sampleCard1));
	}

	// CHECK2: add a pre-existing member: no change in array
	@Test
	public void addMemberTest2() {
		Assert.assertTrue(ScanMemberLogic.membersDatabase.size() == 0);
		assist_setup1();
		Assert.assertTrue(ScanMemberLogic.membersDatabase.size() == sampleCards.size());
		Assert.assertTrue(ScanMemberLogic.membersDatabase.equals(sampleCards));
	}

	// CHECK3: remove an existing member: change in array
	@Test
	public void removeMemberTest1() {
		addMemberTest2();

		// change in array
		ScanMemberLogic.removeMember(sampleCard1);
		Assert.assertTrue(ScanMemberLogic.membersDatabase.size() == sampleCards.size() - 1);

		// no change
		ScanMemberLogic.removeMember(sampleCard1);
		Assert.assertTrue(ScanMemberLogic.membersDatabase.size() == sampleCards.size() - 1);

		// change in array
		ScanMemberLogic.removeMember(sampleCard2);
		Assert.assertTrue(ScanMemberLogic.membersDatabase.size() == sampleCards.size() - 2);
	}

	// CHECK4: remove a non-existing member: no change in array
	@Test
	public void removeMemberTest2() {
		Assert.assertTrue(ScanMemberLogic.membersDatabase.size() == 0);
		ScanMemberLogic.removeMember(sampleCard1);
		Assert.assertTrue(ScanMemberLogic.membersDatabase.size() == 0);
		addMemberTest2();
		Assert.assertTrue(ScanMemberLogic.membersDatabase.size() == sampleCards.size());
		ScanMemberLogic.removeMember(sampleCard6_NEVER_ADDED);
		Assert.assertTrue(ScanMemberLogic.membersDatabase.size() == sampleCards.size());

	}

	int MAX_ATTEMPTS = 5;

	/* CHECKS for MachineCardReader */
	@Test
	public void swipeCardTest1() {
		assist_setup1();
		boolean flag = true;
		int count = 0;
		while (flag && count < MAX_ATTEMPTS) {
			try {
				machine.swipeMemberCard(sampleCard1);
				flag = false;
			} catch (Exception e) {
				e.printStackTrace();
				count++;
				continue;
			}
		}

		Assert.assertTrue(flag == false);
	}

	@Test(expected = MemberCardNotFoundException.class)
	public void swipeCardTest2() throws IOException {
		assist_setup1();
		machine.swipeMemberCard(sampleCard6_NEVER_ADDED);
	}

	@Test
	public void tapCardTest1() {
		assist_setup1();
		boolean flag = true;
		int count = 0;
		while (flag && count < MAX_ATTEMPTS) {
			try {
				machine.tapMemberCard(sampleCard1);
				flag = false;
			} catch (Exception e) {
				e.printStackTrace();
				count++;
				continue;
			}
		}

		Assert.assertTrue(flag == false);
	}

	@Test(expected = MemberCardNotFoundException.class)
	public void tapCardTest2() throws IOException {
		assist_setup1();
		machine.tapMemberCard(sampleCard6_NEVER_ADDED);
	}

	@Test
	public void insertCardSlotTest1() {
		assist_setup1();
		boolean flag = true;
		int count = 0;
		while (flag && count < MAX_ATTEMPTS) {
			try {
				machine.insertMemberCard(sampleCard1, def_pin);
				flag = false;
			} catch (Exception e) {
				e.printStackTrace();
				count++;
				continue;
			}
		}

		Assert.assertTrue(flag == false);
	}

	@Test(expected = MemberCardAlreadyInsertedException.class)
	public void insertCardSlotTest2() throws IOException {
		insertCardSlotTest1();

		machine.insertMemberCard(sampleCard2, def_pin);
	}

	@Test
	public void removeCardSlotTest1() {
		insertCardSlotTest1();
		machine.removeMemberCard();
		Assert.assertTrue(observer.get_status_remove());
	}

	@Test(expected = MemberCardNotYetInsertedException.class)
	public void removeCardSlotTest2() {
		machine.removeMemberCard();
		Assert.assertTrue(observer.get_status_remove());
	}

	@After
	public void hard_reset() {
		ScanMemberLogic.membersDatabase.clear();
		sampleCards.clear();
		machine.detachAllMachineCardReaderObserver();
	}
}
