package seng300.testing;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import seng300.software.selfcheckout.product.ProductDatabase;
import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;

public class PrinterTest {

	ProductDatabase db;
	SelfCheckoutStationLogic logic;
	ReceiptPrinter rp;
	SelfCheckoutStation sc;
	Currency currCAD = Currency.getInstance("CAD");
	
	@Before
	public void setUp() throws OverloadException {
		int[] notedenominations = { 100, 50, 20, 10, 5 };
		BigDecimal denomToonie = new BigDecimal("2.00");
		BigDecimal denomLoonie = new BigDecimal("1.00");
		BigDecimal denomQuarter = new BigDecimal("0.25");
		BigDecimal denomDime = new BigDecimal("0.10");
		BigDecimal denomNickel = new BigDecimal("0.05");
		BigDecimal[] coindenominations = { denomToonie, denomLoonie, denomQuarter, denomDime, denomNickel };
		db = new ProductDatabase();
		sc = new SelfCheckoutStation(currCAD, notedenominations, coindenominations, 100, 1);
		logic = new SelfCheckoutStationLogic(sc, db);
		logic.initializeObservers();
		this.rp = new ReceiptPrinter();
	}
	
	@Test
	public void testLowPaper() {
		this.logic.paperIsLow(rp);
		assertTrue(this.logic.getLowpaperStatus());
	}
	
	@Test
	public void testLowInk() {
		this.logic.inkIsLow(rp);
		assertTrue(this.logic.getLowInkStatus());
	}
}
