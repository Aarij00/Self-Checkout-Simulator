package seng300.software.selfcheckout.customer.membership;

import java.io.IOException;
import java.util.ArrayList;

import org.lsmr.selfcheckout.Card;

import seng300.software.selfcheckout.exceptions.MemberCardAlreadyInsertedException;
import seng300.software.selfcheckout.exceptions.MemberCardNotFoundException;
import seng300.software.selfcheckout.exceptions.MemberCardNotYetInsertedException;
import seng300.software.selfcheckout.observers.MachineCardReaderObserver;

/**
 * Simulates a card reader machine
 * There are three options to scan a membership card: swipe tap insert
 * 
 * @author harsh
 *
 */

public class MachineCardReader extends ScanMemberLogic {
	// this class simulates the card reader being a machine
	protected boolean card_is_inserted;
	protected ArrayList<MachineCardReaderObserver> observers = new ArrayList<MachineCardReaderObserver>();

	public void MachineCardReader() {
		card_is_inserted = false;
		// end config phase
		endConfigurationPhase();
	}

	public void swipeMemberCard(Card memberCard) throws IOException {
		if (isThereMember(memberCard)) {
			swipe(memberCard);
			notifySuccessfulMemberCardScan();
		} else {
			// TODO: throw exception?? runtime card not found
			notifyFailMemberCardScan();
			throw new MemberCardNotFoundException("BHANGU MISSION WRITE");
		}
	}

	public void tapMemberCard(Card memberCard) throws IOException {
		if (isThereMember(memberCard)) {
			tap(memberCard);
			notifySuccessfulMemberCardScan();
		} else {
			// TODO: throw exception?? runtime card not found
			notifyFailMemberCardScan();
			throw new MemberCardNotFoundException("BHANGU MISSION WRITE");
		}
	}

	public void insertMemberCard(Card memberCard, String pin) throws IOException {
		if (card_is_inserted) {
			throw new MemberCardAlreadyInsertedException("BHANGU MISSION WRITE");
		}
		if (isThereMember(memberCard)) {
			insert(memberCard, pin);
			card_is_inserted = true;
			notifySuccessfulMemberCardScan();
		} else {
			notifyFailMemberCardScan();
			throw new MemberCardNotFoundException("BHANGU MISSION WRITE");
		}
	}

	public void removeMemberCard() {
		if (card_is_inserted == false) {
			throw new MemberCardNotYetInsertedException("BHANGU MISSION WRITE");
		}
		remove();
		card_is_inserted = false;
		notifySuccessRemoveCardInserted();

	}

	private void notifySuccessfulMemberCardScan() {
		for (MachineCardReaderObserver o : observers) {
			o.scanSuccess();
		}
	}

	private void notifyFailMemberCardScan() {
		for (MachineCardReaderObserver o : observers) {
			o.scanFail();
		}
	}

	private void notifySuccessRemoveCardInserted() {
		for (MachineCardReaderObserver o : observers) {
			o.removeSuccess();
		}
	}

	public void attachMachineCardReaderObserver(MachineCardReaderObserver o) {
		observers.add(o);
	}

	public void detachMachineCardReaderObserver(MachineCardReaderObserver o) {
		observers.remove(o);
	}

	public void detachAllMachineCardReaderObserver() {
		observers.clear();
	}

}
