package seng300.testing.product.lookup;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

import seng300.software.selfcheckout.product.ProductDatabase;
import seng300.software.selfcheckout.product.lookup.LookupProduct;

public class LookupProductTest {
	// private attributes
	private ProductDatabase db;
	public final PriceLookupCode p1 = new PriceLookupCode("10000");
	public final PriceLookupCode p2 = new PriceLookupCode("20000");
	public final PriceLookupCode p3 = new PriceLookupCode("30000");
	public final PriceLookupCode p4 = new PriceLookupCode("40000");
	public final PriceLookupCode p5 = new PriceLookupCode("50000");
	public final PriceLookupCode p6 = new PriceLookupCode("11000");
	public final PriceLookupCode p7 = new PriceLookupCode("22000");
	public final PriceLookupCode p8 = new PriceLookupCode("33000");
	public final PriceLookupCode p9 = new PriceLookupCode("44000");
	public final PriceLookupCode p10 = new PriceLookupCode("55000");

	public final PriceLookupCode p11 = new PriceLookupCode("60000");

	public final PLUCodedProduct prod1 = new PLUCodedProduct(p1, "name1", new BigDecimal(1));
	public final PLUCodedProduct prod2 = new PLUCodedProduct(p2, "name2", new BigDecimal(1));
	public final PLUCodedProduct prod3 = new PLUCodedProduct(p3, "name3", new BigDecimal(1));
	public final PLUCodedProduct prod4 = new PLUCodedProduct(p4, "name4", new BigDecimal(1));
	public final PLUCodedProduct prod5 = new PLUCodedProduct(p5, "name5", new BigDecimal(1));
	public final PLUCodedProduct prod6 = new PLUCodedProduct(p6, "new_name1", new BigDecimal(1));
	public final PLUCodedProduct prod7 = new PLUCodedProduct(p7, "new_name2", new BigDecimal(1));
	public final PLUCodedProduct prod8 = new PLUCodedProduct(p8, "new_name3", new BigDecimal(1));
	public final PLUCodedProduct prod9 = new PLUCodedProduct(p9, "new_name4", new BigDecimal(1));
	public final PLUCodedProduct prod10 = new PLUCodedProduct(p10, "new_name5", new BigDecimal(1));

	@Before
	public void init() {
		db = new ProductDatabase();
		addAllProducts();
	}

	@Test
	public void test1_findPLUProductByDescriptionName() {
		PLUCodedProduct a;
		a = LookupProduct.findPLUProductByDescriptionName(db, "name");
		Assert.assertTrue(a == null);
		a = LookupProduct.findPLUProductByDescriptionName(db, "name1");
		Assert.assertTrue(a != null);
		Assert.assertTrue(a == prod1);
		a = LookupProduct.findPLUProductByDescriptionName(db, "name11");
		Assert.assertTrue(a == null);
		
		removeAllProducts();
		a = LookupProduct.findPLUProductByDescriptionName(db, "name1");
		Assert.assertTrue(a == null);
	}

	@Test
	public void test2_tryFindPLUProductByDescriptionName() {
		ArrayList<PLUCodedProduct> a;
		a = LookupProduct.tryFindPLUProductByDescriptionName(db, "name");
		Assert.assertTrue(a.size() == 5);
		a = LookupProduct.tryFindPLUProductByDescriptionName(db, "new_name");
		Assert.assertTrue(a.size() == 5);
		a = LookupProduct.tryFindPLUProductByDescriptionName(db, "n");
		Assert.assertTrue(a.size() == 10);
		a = LookupProduct.tryFindPLUProductByDescriptionName(db, "ame");
		Assert.assertTrue(a.size() == 0);
		
		removeAllProducts();
		a = LookupProduct.tryFindPLUProductByDescriptionName(db, "n");
		Assert.assertTrue(a.size() == 0);
		
	}

	@Test
	public void test3_getVisualCatalogue() {
		ArrayList<PLUCodedProduct> a;
		a = LookupProduct.getVisualCatalogue(db, 'n');
		Assert.assertTrue(a.size() == 10);
		a = LookupProduct.getVisualCatalogue(db, 'N'); // should be case insensitive
		Assert.assertTrue(a.size() == 10);
		a = LookupProduct.getVisualCatalogue(db, '?');
		Assert.assertTrue(a.size() == 0);
	}

	@Test
	public void test4_tryFindPLUProductByPLUcode() {
		ArrayList<PLUCodedProduct> a;
		a = LookupProduct.tryFindPLUProductByPLUcode(db, "1");
		Assert.assertTrue(a.size() == 2);
		a = LookupProduct.tryFindPLUProductByPLUcode(db, "15");
		Assert.assertTrue(a.size() == 0);
		a = LookupProduct.tryFindPLUProductByPLUcode(db, "1invalid?code");
		Assert.assertTrue(a.size() == 0);
		a = LookupProduct.tryFindPLUProductByPLUcode(db, "10");
		Assert.assertTrue(a.size() == 1);
		a = LookupProduct.tryFindPLUProductByPLUcode(db, "10000");
		Assert.assertTrue(a.size() == 1);
		
		removeAllProducts();
		a = LookupProduct.tryFindPLUProductByPLUcode(db, "10000");
		Assert.assertTrue(a.size() == 0);
		
	}
	

	@After
	public void clear() {
		// NOTHING needed @Before knows how to re-instantiate
	}

	public void addAllProducts() {
		db.addProduct(prod1);
		db.addProduct(prod2);
		db.addProduct(prod3);
		db.addProduct(prod4);
		db.addProduct(prod5);
		db.addProduct(prod6);
		db.addProduct(prod7);
		db.addProduct(prod8);
		db.addProduct(prod9);
		db.addProduct(prod10);
		// DO NOT ADD prod11 so it can help for other checks in test cases
	}

	public void removeAllProducts() {
		db = new ProductDatabase();
	}
}
