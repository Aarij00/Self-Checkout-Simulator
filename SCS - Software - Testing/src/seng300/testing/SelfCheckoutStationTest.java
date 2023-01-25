package seng300.testing;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

import seng300.software.selfcheckout.product.ProductDatabase;
import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;

public class SelfCheckoutStationTest {

	ProductDatabase db;
	SelfCheckoutStation SCS;
	SelfCheckoutStationLogic SCSLogic;

	Barcode[] codes = new Barcode[4];

	@Before
	public void setUp() {
		Currency cad = Currency.getInstance("CAD");
		int[] notes = { 100, 50, 20, 10, 5 };
		BigDecimal[] coins = {
				new BigDecimal(2.00), // Toonie
				new BigDecimal(1.00), // Loonie
				new BigDecimal(0.25), // Quarter
				new BigDecimal(0.10), // Dime
				new BigDecimal(0.05) // Nickel
		};
		this.SCS = new SelfCheckoutStation(cad, notes, coins, 1000, 1);
		this.db = new ProductDatabase();
		codes[0] = new Barcode(new Numeral[] { Numeral.one });
		codes[1] = new Barcode(new Numeral[] { Numeral.two });
		codes[2] = new Barcode(new Numeral[] { Numeral.three });
		codes[3] = new Barcode(new Numeral[] { Numeral.four });
		for (Barcode code : codes)
			this.db.addProduct(new BarcodedProduct(code, "", new BigDecimal("0.99"), db.randomWeight()));
		this.SCSLogic = new SelfCheckoutStationLogic(SCS, db);
	}
	
	@Test
	public void testPLUCodedProductAdded() {
		PriceLookupCode code = new PriceLookupCode("1000");
		PLUCodedProduct pluProduct = new PLUCodedProduct(code, "apple", new BigDecimal(10));
		this.SCSLogic.PLUCodedProductAdded(pluProduct);
	}
	
	@Test
	public void TestremovePLUcodedItem() {
		PriceLookupCode code = new PriceLookupCode("1000");
		PLUCodedProduct pluProduct = new PLUCodedProduct(code, "apple", new BigDecimal(10));
		this.SCSLogic.PLUCodedProductAdded(pluProduct);
		this.SCSLogic.removePLUcodedItem(pluProduct);
	}
	
	@Test
	public void testRemoveBarcodedItem() {
		Barcode barcode = new Barcode(new Numeral[] { Numeral.one });
		BarcodeScanner bs = new BarcodeScanner();
		//this.SCSLogic.barcodeScanned(bs, barcode);
		BarcodedProduct barcodProduct = new BarcodedProduct(barcode, "apple", new BigDecimal(10), 5.0);
		this.SCSLogic.removeBarcodedItem(barcodProduct);
	}
	
	@Test
	public void testSetNoBag() {
		this.SCSLogic.setNoBag();
	}
	
	@Test
	public void testOutOfPaer() {
		ReceiptPrinter rp = new ReceiptPrinter();
		this.SCSLogic.outOfPaper(rp);
	}
	
	@Test
	public void testOutOfInk() {
		ReceiptPrinter rp = new ReceiptPrinter();
		this.SCSLogic.outOfInk(rp);
	}
	
	@Test
	public void testOverload() {
		this.SCSLogic.outOfOverload(this.SCS.baggingArea);
	}
	
	@Test
	public void testGetProductDatabase() {
		this.SCSLogic.getProductDatabase();
	}
}
