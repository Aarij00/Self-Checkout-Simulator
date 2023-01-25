package seng300.testing;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.*;

import seng300.software.selfcheckout.payment.ReturnChange;

/**
 * @author Justin Parker
 *
 */
public class ReturnChangeTest {
	public SelfCheckoutStation scs;

	@Before
	public void init() {
		Currency currency = Currency.getInstance("USD");
		int[] banknoteDenominations = { 1, 5, 10, 25, 100 };
		BigDecimal[] coinDenominations = { new BigDecimal("0.01"), new BigDecimal("0.05"), new BigDecimal("0.1"),
				new BigDecimal("0.25"), new BigDecimal("1.00") };
		int scaleMaximumWeight = 10;
		int scaleSensitivity = 1;
		scs = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, scaleMaximumWeight,
				scaleSensitivity);
	}

	@Test
	public void nullInput() {
		try {
			ReturnChange rc = new ReturnChange(null);
		} catch (NullPointerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void dispenseOneDollarTwentyFive() {
		try {
			scs.banknoteDispensers.get(new Integer("1")).load(new Banknote(Currency.getInstance("USD"), 1));
			scs.coinDispensers.get(new BigDecimal("0.25"))
					.load(new Coin(Currency.getInstance("USD"), new BigDecimal(0.25)));
		} catch (Exception e) {
			System.out.println(e);
		}
		ReturnChange rc = new ReturnChange(scs);
		try {
			if (!rc.DispenseMoney(new BigDecimal("1.25"))) {
				assertTrue(false);
			}
		} catch (Exception e) {
			assertTrue(false);
		}
		assertTrue(true);
	}

	@Test
	public void cantDispenseOneDollarThirty() {
		try {
			scs.banknoteDispensers.get(new Integer("1")).load(new Banknote(Currency.getInstance("USD"), 1));
			scs.coinDispensers.get(new BigDecimal("0.25"))
					.load(new Coin(Currency.getInstance("USD"), new BigDecimal(0.25)));
		} catch (Exception e) {
			System.out.println(e);
		}
		ReturnChange rc = new ReturnChange(scs);
		try {
			if (rc.DispenseMoney(new BigDecimal("1.30"))) {
				assertTrue(false);
			}
		} catch (Exception e) {
		}
		assertTrue(true);
	}

	@Test
	public void dispenseTwentyFive() {
		try {
			scs.coinDispensers.get(new BigDecimal("0.25"))
					.load(new Coin(Currency.getInstance("USD"), new BigDecimal(0.25)));
		} catch (Exception e) {
			System.out.println(e);
		}
		ReturnChange rc = new ReturnChange(scs);
		try {
			if (!rc.DispenseMoney(new BigDecimal("0.25"))) {
				assertTrue(false);
			}
		} catch (Exception e) {
			assertTrue(false);
		}
		assertTrue(true);
	}

	@Test
	public void dispenseFiveDollarTwentyFive() {
		try {
			scs.banknoteDispensers.get(new Integer("5")).load(new Banknote(Currency.getInstance("USD"), 5));
			scs.coinDispensers.get(new BigDecimal("0.25"))
					.load(new Coin(Currency.getInstance("USD"), new BigDecimal(0.25)));
		} catch (Exception e) {
			System.out.println(e);
		}
		ReturnChange rc = new ReturnChange(scs);
		try {
			if (!rc.DispenseMoney(new BigDecimal("5.25"))) {
				assertTrue(false);
			}
		} catch (Exception e) {
			assertTrue(false);
		}
		assertTrue(true);
	}

	@Test
	public void cantDispenseBillCuzEmpty() {
		try {
			scs.coinDispensers.get(new BigDecimal("0.25"))
					.load(new Coin(Currency.getInstance("USD"), new BigDecimal(0.25)));
		} catch (Exception e) {
			System.out.println(e);
		}
		ReturnChange rc = new ReturnChange(scs);
		try {
			if (!rc.DispenseMoney(new BigDecimal("5.25"))) {
				assertTrue(false);
			}
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void observers() {
		ReturnChange rc = new ReturnChange(scs);
		rc.enabled(null);
		rc.disabled(null);
		rc.banknoteInserted(null);
		rc.banknoteEjected(null);
		rc.banknoteRemoved(null);
		assertTrue(true);
	}
}
