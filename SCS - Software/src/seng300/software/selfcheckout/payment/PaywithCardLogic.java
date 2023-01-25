package seng300.software.selfcheckout.payment;

import org.lsmr.selfcheckout.Card;

public class PaywithCardLogic {

	private String type;
	private String number;
	private String cardholder;
	private String cvv;
	private String pin;
	private boolean isTapEnabled;
	private boolean hasChip;
	private Card debitCard;
	private Card creditCard;

	public void initialzeDebit() {
		type = "Debit";
		number = "001";
		cardholder = "John";
		cvv = "000";
		isTapEnabled = true;
		hasChip = true;

		debitCard = new Card(type, number, cardholder, cvv, cardholder, isTapEnabled, hasChip);
	}

	public void initialzeCredit() {
		type = "Credit";
		number = "100";
		cardholder = "Jeff";
		cvv = "123";
		isTapEnabled = true;
		hasChip = true;

		creditCard = new Card(type, number, cardholder, cvv, cardholder, isTapEnabled, hasChip);
	}

	public void intertCard() {

	}

}
