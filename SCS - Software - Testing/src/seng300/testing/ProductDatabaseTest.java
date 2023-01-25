package seng300.testing;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.*;

import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

import seng300.software.selfcheckout.product.ProductDatabase;
import seng300.software.selfcheckout.exceptions.ProductNotFoundException;

public class ProductDatabaseTest {
	private ProductDatabase db;

	@Test
	public void testProductDatabase_Empty() {
		this.db = new ProductDatabase();
		assertTrue(this.db.getProducts().size() == 0);
	}

	@Test
	public void testProductDatabase_Negative() {
		this.db = new ProductDatabase(-1, -1);
		assertTrue(this.db.getProducts().size() == 0);
	}

	@Test
	public void testProductDatabase_GetProducts() {
		this.db = new ProductDatabase(0, 0);
		assertTrue(db.getProducts().size() == 0);
	}

	@Test
	public void testProductDatabase_AddProduct() {
		this.db = new ProductDatabase();
		Product p = new PLUCodedProduct(new PriceLookupCode("0000"), "", new BigDecimal(0.99));
		this.db.addProduct(p);
		assertTrue(db.getProducts().size() == 1);
		assertTrue(db.getProducts().get(0) instanceof PLUCodedProduct);
		assertTrue(((PLUCodedProduct) db.getProducts().get(0))
				.getPLUCode().equals(((PLUCodedProduct) p).getPLUCode()));
		assertTrue(((PLUCodedProduct) db.getProducts().get(0))
				.getDescription().equals(((PLUCodedProduct) p).getDescription()));
		assertTrue(((PLUCodedProduct) db.getProducts().get(0))
				.getPrice().equals(((PLUCodedProduct) p).getPrice()));
	}

	@Test
	public void testProductDatabase_BarcodedProducts() {
		this.db = new ProductDatabase(4, 0);
		int numBarcodedProducts = 0;
		int numPLUCodedProducts = 0;
		int numUnknownProducts = 0;
		for (Product p : this.db.getProducts()) {
			if (p instanceof BarcodedProduct)
				numBarcodedProducts++;
			else if (p instanceof PLUCodedProduct)
				numPLUCodedProducts++;
			else
				numUnknownProducts++;
		}
		assertTrue(numUnknownProducts == 0);
		assertTrue(numBarcodedProducts == 4);
		assertTrue(numPLUCodedProducts == 0);
	}

	@Test
	public void testProductDatabase_PLUCodedProducts() {
		this.db = new ProductDatabase(0, 5);
		int numBarcodedProducts = 0;
		int numPLUCodedProducts = 0;
		int numUnknownProducts = 0;
		for (Product p : this.db.getProducts()) {
			if (p instanceof BarcodedProduct)
				numBarcodedProducts++;
			else if (p instanceof PLUCodedProduct)
				numPLUCodedProducts++;
			else
				numUnknownProducts++;
		}
		assertTrue(numUnknownProducts == 0);
		assertTrue(numBarcodedProducts == 0);
		assertTrue(numPLUCodedProducts == 5);
	}

	@Test
	public void testProductDatabase_MixedProducts() {
		this.db = new ProductDatabase(4, 5);
		int numBarcodedProducts = 0;
		int numPLUCodedProducts = 0;
		int numUnknownProducts = 0;
		for (Product p : this.db.getProducts()) {
			if (p instanceof BarcodedProduct)
				numBarcodedProducts++;
			else if (p instanceof PLUCodedProduct)
				numPLUCodedProducts++;
			else
				numUnknownProducts++;
		}
		assertTrue(numUnknownProducts == 0);
		assertTrue(numBarcodedProducts == 4);
		assertTrue(numPLUCodedProducts == 5);
	}

	@Test
	public void testProductDatabase_GetBarcodedItemPrice() {
		this.db = new ProductDatabase(4, 5);
		BarcodedProduct b = (BarcodedProduct) this.db.getProducts().get(3);
		BarcodedItem item = new BarcodedItem(b.getBarcode(), 25);
		try {
			BigDecimal itemPrice = this.db.getPriceOfItem(item);
			assertTrue(itemPrice.equals(b.getPrice()));
		} catch (ProductNotFoundException e) {
			fail();
		}
	}

	@Test
	public void testProductDatabase_GetPLUCodedItemPrice() {
		this.db = new ProductDatabase(4, 5);
		PLUCodedProduct p = (PLUCodedProduct) this.db.getProducts().get(7);
		int weightInGrams = 250;
		double weightInKilograms = weightInGrams / 1000.0;
		PLUCodedItem item = new PLUCodedItem(p.getPLUCode(), weightInGrams);
		try {
			BigDecimal itemPrice = this.db.getPriceOfItem(item);
			assertTrue(itemPrice.equals(p.getPrice().multiply(new BigDecimal(weightInKilograms))));
		} catch (ProductNotFoundException e) {
			fail();
		}
	}

	@Test
	public void testProductDatabase_GetNullItemPrice() {
		this.db = new ProductDatabase(4, 5);
		try {
			BigDecimal itemPrice = this.db.getPriceOfItem(null);
			fail();
		} catch (NullPointerException e) {
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testProductDatabase_GetNonexistentProductPrice() {
		this.db = new ProductDatabase(4, 5);
		PLUCodedItem item = new PLUCodedItem(new PriceLookupCode("0000"), 250);
		try {
			BigDecimal itemPrice = this.db.getPriceOfItem(item);
			fail();
		} catch (ProductNotFoundException e) {
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testProductDatabase_GetProductForBarcodedItem() {
		this.db = new ProductDatabase(4, 5);
		BarcodedProduct p = (BarcodedProduct) this.db.getProducts().get(3);
		BarcodedItem item = new BarcodedItem(p.getBarcode(), 250);
		assertTrue(p.equals(this.db.getProductForItem(item)));
	}

	@Test
	public void testProductDatabase_GetProductForPLUCodedItem() {
		this.db = new ProductDatabase(4, 5);
		PLUCodedProduct p = (PLUCodedProduct) this.db.getProducts().get(7);
		PLUCodedItem item = new PLUCodedItem(p.getPLUCode(), 250);
		assertTrue(p.equals(this.db.getProductForItem(item)));
	}

}
