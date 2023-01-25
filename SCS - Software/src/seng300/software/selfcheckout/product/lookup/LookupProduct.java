package seng300.software.selfcheckout.product.lookup;

import java.util.ArrayList;

import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

import seng300.software.selfcheckout.product.ProductDatabase;

public class LookupProduct {

	// method for finding product with full description name: simulates use case lookup product
	public static PLUCodedProduct findPLUProductByDescriptionName(ProductDatabase db, String description_name) {
		ArrayList<PLUCodedProduct> pluProds = getFilteredPLUProducts(db);
		for (int i = 0; i < pluProds.size(); i++) {
			if (pluProds.get(i).getDescription().equals(description_name)) {
				return pluProds.get(i);
			}

		}
		return null;
	}

	// method to find entered product name stuff in partial way: simulates use case lookup product
	public static ArrayList<PLUCodedProduct> tryFindPLUProductByDescriptionName(ProductDatabase db, String partial_description_name) {
		ArrayList<PLUCodedProduct> pluProds = getFilteredPLUProducts(db);
		ArrayList<PLUCodedProduct> temp = new ArrayList<PLUCodedProduct>();
		for (int i = 0; i < pluProds.size(); i++) {
			if (pluProds.get(i).getDescription().startsWith(partial_description_name)) {
				temp.add(pluProds.get(i));
			}

		}
		return temp;
	}

	// method for finding visual catalogue: simulates use case lookup product
	// it should be not case sensitive
	public static ArrayList<PLUCodedProduct> getVisualCatalogue(ProductDatabase db, Character let_description_name) {
		let_description_name = Character.toLowerCase(let_description_name); // makes it case insensitive
		return tryFindPLUProductByDescriptionName(db, let_description_name.toString());
	}

	// method to find entered product with partially entered plu code: simulates use case ENTERS PLU CODE
	public static ArrayList<PLUCodedProduct> tryFindPLUProductByPLUcode(ProductDatabase db, String partial_code) {
		ArrayList<PLUCodedProduct> pluProds = getFilteredPLUProducts(db);
		ArrayList<PLUCodedProduct> temp = new ArrayList<PLUCodedProduct>();
		for (int i = 0; i < pluProds.size(); i++) {
			if (pluProds.get(i).getPLUCode().toString().startsWith(partial_code)) {
				temp.add(pluProds.get(i));
			}

		}
		return temp;
	}

	/*
	 * ***************************************************************************************************************************
	 */
	public static ArrayList<PLUCodedProduct> filterPLUProducts(ArrayList<Product> products) {
		ArrayList<PLUCodedProduct> temp = new ArrayList<PLUCodedProduct>();
		for (int i = 0; i < products.size(); i++) {
			try {
				PLUCodedProduct maybePLUProd = (PLUCodedProduct) products.get(i);
				temp.add(maybePLUProd);
			} catch (Exception e) {
			}
		}
		return temp;
	}

	public static ArrayList<PLUCodedProduct> getFilteredPLUProducts(ProductDatabase db) {
		return filterPLUProducts(db.getProducts());
	}
}
