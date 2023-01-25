package seng300.testing;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import seng300.software.selfcheckout.payment.PaywithGiftCard;
import seng300.software.selfcheckout.product.ProductDatabase;
import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;

public class PayWithCardTest {

	ProductDatabase pddatabase;
	SelfCheckoutStation SCS;
	SelfCheckoutStationLogic SCSLogic;
	Currency currCAD = Currency.getInstance("CAD");
	Currency currUSD = Currency.getInstance("USD");

	Card debitCard;
	Card creditCard;
	Card giftCard;
	PaywithGiftCard payGift;

	@Before
	public void testSetup() {
		this.debitCard = new Card("Debit", "11111111111", "John", "111", "1111", true, true);
		this.creditCard = new Card("Credit", "2222222222", "Jeff", "222", "2222", true, true);
		this.giftCard = new Card("Gift", "3333333333", "Joe", "333", "3333", true, true);

		BigDecimal denomToonie = new BigDecimal("2.00");
		BigDecimal denomLoonie = new BigDecimal("1.00");
		BigDecimal denomQuarter = new BigDecimal("0.25");
		BigDecimal denomDime = new BigDecimal("0.10");
		BigDecimal denomNickel = new BigDecimal("0.05");
		int[] notedenominations = { 100, 50, 20, 10, 5 };
		BigDecimal[] coindenominations = { denomToonie, denomLoonie, denomQuarter, denomDime, denomNickel };
		pddatabase = new ProductDatabase();
		SCS = new SelfCheckoutStation(currCAD, notedenominations, coindenominations, 100, 1);
		SCSLogic = new SelfCheckoutStationLogic(SCS, pddatabase);
		this.payGift = new PaywithGiftCard(SCSLogic);
		SCSLogic.initializeObservers();
	}

	@After
	public void testTearDown() {
		pddatabase = null;
		SCS = null;
		SCSLogic = null;
	}

	@Test
	public void testSuccessfulPaymentInsertDebit() throws IOException {
		SCSLogic.initializeObservers();
		SCSLogic.setBillTotal(BigDecimal.TEN);
		SCS.cardReader.insert(debitCard, "1111");
		assertEquals("Returned value incorrect", 0.00, SCSLogic.getBillTotal().doubleValue(), 0.10);
	}

	@Test
	public void testSuccessfulPaymentTapDebit() throws IOException {
		SCSLogic.setBillTotal(new BigDecimal("323.1"));
		SCS.cardReader.tap(debitCard);
		assertEquals("Returned value incorrect", 0.00, SCSLogic.getBillTotal().doubleValue(), 0.10);
	}


	@Test
	public void testSuccessfulPaymentInsertCredit() throws IOException {
		SCSLogic.setBillTotal(BigDecimal.TEN);
		SCS.cardReader.insert(creditCard, "2222");
		assertEquals("Returned value incorrect", 0.00, SCSLogic.getBillTotal().doubleValue(), 0.10);
	}

	@Test
	public void testSuccessfulPaymentSwipeCredit() throws IOException {
		SCSLogic.setBillTotal(new BigDecimal("54.53"));
		SCS.cardReader.swipe(creditCard);
		assertEquals("Returned value incorrect", 0.00, SCSLogic.getBillTotal().doubleValue(), 0.10);
	}

	@Test
	public void testSuccessfulPaymentTapCredit() throws IOException {
		SCSLogic.setBillTotal(new BigDecimal("33.13"));
		SCS.cardReader.tap(creditCard);
		assertEquals("Returned value incorrect", 0.00, SCSLogic.getBillTotal().doubleValue(), 0.10);
	}

	@Test
	public void testFailPayment() throws IOException {
		Card otherCard = new Card("other", "3333333333", "Josh", "333", "3333", true, true);
		SCSLogic.setBillTotal(new BigDecimal("100.00"));
		SCS.cardReader.tap(otherCard);
		assertEquals("Returned value incorrect", 100.00, SCSLogic.getBillTotal().doubleValue(), 0.10);
	}
	
	@Test
	public void testPayWithGiftCard() throws IOException {
		payGift.SetAmountInCard(new BigDecimal(10.00));
		payGift.cardDataRead(SCS.cardReader, SCS.cardReader.swipe(giftCard));
	}

}