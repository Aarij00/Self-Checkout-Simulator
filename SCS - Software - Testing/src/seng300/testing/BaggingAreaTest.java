package seng300.testing;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Before;
import org.lsmr.selfcheckout.Item;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import org.lsmr.selfcheckout.products.BarcodedProduct;

//import seng300.software.BaggingArea1;
import seng300.software.selfcheckout.product.ProductDatabase;
import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;

public class BaggingAreaTest {

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
	public void itemsPlacedTest() {
		BarcodedItem item1 = new BarcodedItem(codes[0], 1.0);
		SCSLogic.scanItem(item1);
		SCSLogic.itemPlaced(item1);
	}

	@Test
	public void itemNotPlacedTest() {
		BarcodedItem item1 = new BarcodedItem(codes[0], 1.0);
		SCSLogic.scanItem(item1);
		SCSLogic.itemNotPlaced();
	}

	@Test
	public void itemsPlacedOwnBagTest() {
		BarcodedItem item1 = new BarcodedItem(codes[0], 1.0);
		SCSLogic.setOwnBag();
		SCSLogic.scanItem(item1);
		SCSLogic.itemPlaced(item1);
	}
	
	@Test
	public void testWeightDoesNotConform() throws OverloadException {
		BarcodedItem item = new BarcodedItem(codes[0], 1.0);
		SCSLogic.scanItem(item);
		SCSLogic.itemPlaced(item);
		SCSLogic.weightChanged(this.SCS.baggingArea, 1);
		SCSLogic.baggingAreaWeightInvalid();
	}
	
	@Test
	public void testEnterNumberofPlasticBags() {
		SCSLogic.customerEntersPlasticBagsUsed(2);
	}
	
	@Test
	public void testItemRemovedFromBaggingArea() {
		BarcodedItem item = new BarcodedItem(codes[0], 1.0);
		SCSLogic.itemPlaced(item);
		SCSLogic.itemRemovedBaggingArea(item);
	}
	
	@Test
	public void testReturnstoAddingItems () {
		BarcodedItem item = new BarcodedItem(codes[0], 1.0);
		SCSLogic.itemPlaced(item);
		SCSLogic.CustomerStopsAddingItems();
		SCSLogic.CustomerStartsAddingItems();
	}
	
	@Test
	public void testDoesNotWanttoBagScannedItem () {
		BarcodedItem item = new BarcodedItem(codes[0], 1.0);
		SCSLogic.scanItem(item);
		SCSLogic.CustomerStopsAddingItems();
	}
}