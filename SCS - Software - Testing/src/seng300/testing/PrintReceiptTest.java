package seng300.testing;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import org.junit.*;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import seng300.software.selfcheckout.product.ProductDatabase;
import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;

public class PrintReceiptTest {
	private ProductDatabase db;
	private SelfCheckoutStationLogic logic;
	private int receiptLength;

	@Before
	public void setUp() {
		// init self checkout station to install logic on
		Currency cad = Currency.getInstance("CAD");
		int[] notes = { 100, 50, 20, 10, 5 };
		BigDecimal[] coins = {
				new BigDecimal(2.00), // Toonie
				new BigDecimal(1.00), // Loonie
				new BigDecimal(0.25), // Quarter
				new BigDecimal(0.10), // Dime
				new BigDecimal(0.05) // Nickel
		};
		SelfCheckoutStation scs = new SelfCheckoutStation(cad, notes, coins, 1000, 1);
		try {
			scs.printer.addPaper(ReceiptPrinter.MAXIMUM_PAPER);
		} catch (OverloadException e) {
			e.printStackTrace();
		}
		try {
			scs.printer.addInk(ReceiptPrinter.MAXIMUM_INK);
		} catch (OverloadException e) {
			e.printStackTrace();
		}
		// init product database for testing
		this.db = new ProductDatabase();
		Barcode b1 = new Barcode(new Numeral[] { Numeral.one });
		Barcode b2 = new Barcode(new Numeral[] { Numeral.two });
		Barcode b3 = new Barcode(new Numeral[] { Numeral.three });
		String d1 = "product 1";
		String d2 = "product 2 description that is really long and needs multiple lines to print";
		String d3 = "product 3";
		this.receiptLength = d1.length() + d2.length() + d3.length() + "Total Price\t$".length();
		BarcodedProduct p1 = new BarcodedProduct(b1, d1, new BigDecimal("3.99"), receiptLength);
		BarcodedProduct p2 = new BarcodedProduct(b2, d2, new BigDecimal("4.99"), receiptLength);
		BarcodedProduct p3 = new BarcodedProduct(b3, d3, new BigDecimal("2.99"), receiptLength);
		this.db.addProduct(p1);
		this.db.addProduct(p2);
		this.db.addProduct(p3);
		// init self checkout logic and scan items in cart
		this.logic = new SelfCheckoutStationLogic(scs, db);

	}

	@Test
	public void testPrintReceipt() {
		try {
			this.logic.printReceipt();
			String receipt = this.logic.getSelfCheckoutStation().printer.removeReceipt();
			assertTrue(receipt.length() > 0);
		} catch (Exception e) {
			fail();
		}
	}
}
