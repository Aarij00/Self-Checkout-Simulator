package seng300.testing;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import org.junit.*;
import org.lsmr.selfcheckout.Barcode;

import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.Product;

import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;
import seng300.software.selfcheckout.product.ProductDatabase;

public class UpdateBillAddTest {
	private ProductDatabase db;
	private SelfCheckoutStationLogic logic;

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
		SelfCheckoutStation scs = new SelfCheckoutStation(cad, notes, coins, 1000, 1);
		this.db = new ProductDatabase(7, 0);
		this.logic = new SelfCheckoutStationLogic(scs, db);
	}

	@Test
	public void testUpdateBill_BarcodedItem() {
		BarcodedProduct p = (BarcodedProduct) this.db.getProducts().get(0);
		BarcodedItem item = new BarcodedItem(p.getBarcode(), 1);
		BigDecimal total = p.getPrice().setScale(2, RoundingMode.HALF_EVEN);
		while (!this.logic.notifiedItemScanned)
			this.logic.getSelfCheckoutStation().mainScanner.scan(item);
		assertTrue(this.logic.getFinalPrice().equals(total));
	}

	@Test
	public void testUpdateBill_BarcodedItems() {
		BigDecimal total = new BigDecimal("0.00");
		for (Product p : this.db.getProducts()) {
			BarcodedItem item = new BarcodedItem(((BarcodedProduct) p).getBarcode(), 1);
			while (!this.logic.notifiedItemScanned)
				this.logic.getSelfCheckoutStation().mainScanner.scan(item);
			this.logic.notifiedItemScanned = false;
			total = total.add(p.getPrice());
		}
		assertTrue(this.logic.getFinalPrice().equals(total.setScale(2, RoundingMode.HALF_EVEN)));
	}

	@Test
	public void testUpdateBill_BarcodedItemWithoutProduct() {
		Numeral[] code = { Numeral.one, Numeral.two, Numeral.three, Numeral.four };
		BarcodedItem item = new BarcodedItem(new Barcode(code), 1);
		try {
			while (!this.logic.notifiedItemScanned)
				this.logic.getSelfCheckoutStation().mainScanner.scan(item);
			fail();
		} catch (org.lsmr.selfcheckout.SimulationException e) {
			assertTrue(this.logic.getFinalPrice().equals(new BigDecimal("0.00")));
		}
	}
}
