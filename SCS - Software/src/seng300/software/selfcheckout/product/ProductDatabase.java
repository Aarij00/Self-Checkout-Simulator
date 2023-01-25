package seng300.software.selfcheckout.product;

/**
 * Simulate a product database
 * to be used by the self checkout system
 * for testing purposes.
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

import seng300.software.selfcheckout.exceptions.ProductNotFoundException;

public class ProductDatabase {
	private ArrayList<Product> products = new ArrayList<>();

	public ProductDatabase() {
	}

	public ProductDatabase(int numBarcodedProducts, int numPLUCodedProducts) {
		for (int i = 0; i < numBarcodedProducts; i++)
			products.add(new BarcodedProduct(randomBarcode(), "", randomPrice(50.0), randomWeight()));

		for (int i = 0; i < numPLUCodedProducts; i++)
			products.add(new PLUCodedProduct(randomPLUCode(), "", randomPrice(3.00)));
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<Product> getProducts() {
		return products;
	}

	/**
	 * 
	 * @param product
	 */
	public void addProduct(Product product) {
		products.add(product);
	}

	/**
	 * Gets the price of an item.
	 * 
	 * @param item
	 *             Item to find the price for.
	 * 
	 * @return Price of item.
	 * 
	 * @throws NullPointerException
	 *                                  Thrown when item is null.
	 * 
	 * @throws ProductNotFoundException
	 *                                  Thrown when corresponding product could not
	 *                                  be found.
	 */
	public BigDecimal getPriceOfItem(Item item)
			throws NullPointerException, ProductNotFoundException {
		if (item == null)
			throw new NullPointerException("arguments cannot be null");

		Product p = getProductForItem(item);
		if (p == null)
			throw new ProductNotFoundException();

		if (p instanceof BarcodedProduct)
			return p.getPrice();
		// p instanceof PLUCodedProduct
		// Convert item weight from grams to kilos; price given per kilogram
		double weightInKilograms = item.getWeight() / 1000.0;
		return p.getPrice().multiply(new BigDecimal(weightInKilograms));
	}

	/**
	 * Finds and return the product with the same identifier as
	 * the item, if exists.
	 * Currently supports items with a barcode or
	 * price lookup code only.
	 * 
	 * @param item
	 *             Item to find the product for.
	 * 
	 * @return if corresponding product exists, returns that product;
	 *         else, return null
	 */
	public Product getProductForItem(Item item) {
		if (item instanceof BarcodedItem) {
			Barcode b = ((BarcodedItem) item).getBarcode();
			for (Product p : products) {
				if (p instanceof BarcodedProduct &&
						b.equals(((BarcodedProduct) p).getBarcode()))
					return p;
			}
		} else if (item instanceof PLUCodedItem) {
			PriceLookupCode plu = ((PLUCodedItem) item).getPLUCode();
			for (Product p : products) {
				if (p instanceof PLUCodedProduct &&
						plu.equals(((PLUCodedProduct) p).getPLUCode()))
					return p;
			}
		}
		return null;
	}

	public double getWeightForItem(Item item) {
		return item.getWeight();
	}

	private Barcode randomBarcode() {
		Numeral[] code = new Numeral[7];
		Random rand = new Random();
		for (int i = 0; i < 7; i++)
			code[i] = Numeral.valueOf((byte) rand.nextInt(10));
		return new Barcode(code);
	}

	private PriceLookupCode randomPLUCode() {
		Random rand = new Random();
		char[] code = new char[5];
		for (int i = 0; i < 5; i++)
			code[i] = (char) (rand.nextInt(10) + '0');
		return new PriceLookupCode(new String(code));
	}

	private BigDecimal randomPrice(double max) {
		Random rand = new Random();
		return new BigDecimal(((max - 1.99) * rand.nextDouble()) + 1.99);
	}
	
	public double randomWeight() {
		double minWeight = 0.1;
		double maxWeight = 999.9;
		Random rand = new Random();
		return rand.nextDouble() * (maxWeight - minWeight) + minWeight;
	}
}