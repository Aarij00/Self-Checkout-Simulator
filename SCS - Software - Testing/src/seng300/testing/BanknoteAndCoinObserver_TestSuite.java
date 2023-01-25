package seng300.testing;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.*;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import seng300.software.selfcheckout.product.ProductDatabase;
import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;

public class BanknoteAndCoinObserver_TestSuite {

	ProductDatabase pddatabase;
	SelfCheckoutStation SCS;
	SelfCheckoutStationLogic SCSLogic;
	Currency currCAD = Currency.getInstance("CAD");
	Currency currUSD = Currency.getInstance("USD");

	@Before
	public void testSetup() {
		int[] notedenominations = { 100, 50, 20, 10, 5 };
		BigDecimal denomToonie = new BigDecimal("2.00");
		BigDecimal denomLoonie = new BigDecimal("1.00");
		BigDecimal denomQuarter = new BigDecimal("0.25");
		BigDecimal denomDime = new BigDecimal("0.10");
		BigDecimal denomNickel = new BigDecimal("0.05");
		BigDecimal[] coindenominations = { denomToonie, denomLoonie, denomQuarter, denomDime, denomNickel };
		pddatabase = new ProductDatabase();
		SCS = new SelfCheckoutStation(currCAD, notedenominations, coindenominations, 100, 1);
		SCSLogic = new SelfCheckoutStationLogic(SCS, pddatabase);
		SCSLogic.initializeObservers();
	}

	@After
	public void testTearDown() {
		pddatabase = null;
		SCS = null;
		SCSLogic = null;
	}

	@Test
	public void testSuccessfulInsert5() throws DisabledException, OverloadException {
		BigDecimal testBigDecimal = new BigDecimal("5.00");
		Banknote banknote = new Banknote(currCAD, 5);
		SCSLogic.insertBanknote(banknote);
		assertTrue("Returned value incorrect",
				0 == testBigDecimal.compareTo(SCSLogic.getSumPaid()));
	}

	@Test
	public void testSuccesfulInsertTonnie() throws DisabledException, OverloadException {
		BigDecimal testBigDecimal = new BigDecimal("2.00");
		Coin coin = new Coin(currCAD, testBigDecimal);
		SCSLogic.insertCoin(coin);
		assertTrue("Returned value incorrect",
				0 == testBigDecimal.compareTo(SCSLogic.getSumPaid()));
	}

	@Test
	public void testSuccesfulInsertdenomNickel() throws DisabledException, OverloadException {
		BigDecimal testBigDecimal = new BigDecimal("0.05");
		Coin coin = new Coin(currCAD, testBigDecimal);
		SCSLogic.insertCoin(coin);
		assertTrue("Returned value incorrect",
				0 == testBigDecimal.compareTo(SCSLogic.getSumPaid()));
	}

	@Test
	public void testSuccessfulInsert20() throws DisabledException, OverloadException {
		BigDecimal testBigDecimal = new BigDecimal("20.00");
		Banknote banknote = new Banknote(currCAD, 20);
		SCSLogic.insertBanknote(banknote);
		assertTrue("Returned value incorrect",
				0 == testBigDecimal.compareTo(SCSLogic.getSumPaid()));
	}

	@Test
	public void testSuccessfulInsert5and5() throws DisabledException, OverloadException {
		BigDecimal testBigDecimal = new BigDecimal("10.00");
		Banknote banknote = new Banknote(currCAD, 5);
		SCSLogic.insertBanknote(banknote);
		SCSLogic.insertBanknote(banknote);
		assertTrue("Returned value incorrect",
				0 == testBigDecimal.compareTo(SCSLogic.getSumPaid()));
	}

	@Test
	public void testInvalidNoteDenom() throws DisabledException, OverloadException{
		BigDecimal testBigDecimal = new BigDecimal("0.00");
		Banknote banknote = new Banknote(currCAD, 6);
		SCSLogic.insertBanknote(banknote);
		assertTrue("Returned value incorrect",
				0 == testBigDecimal.compareTo(SCSLogic.getSumPaid()));
	}

	@Test
	public void testInvalidNoteCurr() throws DisabledException, OverloadException{
		BigDecimal testBigDecimal = new BigDecimal("0.00");
		Banknote banknote = new Banknote(currUSD, 5);
		SCSLogic.insertBanknote(banknote);
		assertTrue("Returned value incorrect",
				0 == testBigDecimal.compareTo(SCSLogic.getSumPaid()));
	}

	@Test
	public void testDanglingRemoval() throws DisabledException, OverloadException{
		BigDecimal testBigDecimal = new BigDecimal("5.00");
		Banknote banknote = new Banknote(currCAD, 6);
		SCSLogic.insertBanknote(banknote);
		SCSLogic.getSelfCheckoutStation().banknoteInput.removeDanglingBanknotes();
		banknote = new Banknote(currCAD, 5);
		SCSLogic.insertBanknote(banknote);
		assertTrue("Returned value incorrect",
				0 == testBigDecimal.compareTo(SCSLogic.getSumPaid()));
	}

	@Test
	public void testBlockedInputError() throws DisabledException, OverloadException{
		Banknote banknote = new Banknote(currCAD, 6);
		SCSLogic.insertBanknote(banknote);
		banknote = new Banknote(currCAD, 5);
		try {
			SCSLogic.insertBanknote(banknote);
		} catch (OverloadException e) {
			return;
		}
		fail("OverloadException expected");
	}

	@Test
	public void invalidCoinValue() throws DisabledException, OverloadException{
		BigDecimal testBigDecimal = new BigDecimal("0.02");
		Coin coin = new Coin(currUSD, testBigDecimal);
		SCSLogic.insertCoin(coin);
		assertFalse("Returned value incorrect",
				0 == testBigDecimal.compareTo(SCSLogic.getSumPaid()));
	}

	@Test
	public void invalidCoinCurr() throws DisabledException, OverloadException{
		Currency Euro = Currency.getInstance("EUR");
		BigDecimal testBigDecimal = new BigDecimal("0.02");
		Coin coin = new Coin(Euro, testBigDecimal);
		SCSLogic.insertCoin(coin);
		assertFalse("Returned value incorrect",
				0 == testBigDecimal.compareTo(SCSLogic.getSumPaid()));
	}

	@Test
	public void testDisabledInputError() throws DisabledException, OverloadException{
		SCSLogic.getSelfCheckoutStation().banknoteInput.disable();
		Banknote banknote = new Banknote(currCAD, 5);
		try {
			SCSLogic.insertBanknote(banknote);
		} catch (DisabledException e) {
			return;
		}
		fail("DisabledException expected");
	}

	@Test
	public void testERRORPhaseInputError() {
		SCSLogic.getSelfCheckoutStation().banknoteInput.forceErrorPhase();
		Banknote banknote = new Banknote(currCAD, 5);
		try {
			SCSLogic.insertBanknote(banknote);
		} catch (SimulationException e) {
			return;
		}
		fail("SimulationException expected");
	}

}
