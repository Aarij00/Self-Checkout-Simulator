package seng300.testing;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import seng300.software.selfcheckout.product.ProductItem;
import seng300.software.selfcheckout.customer.Customer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

//We use this way of paramterized testing (whitebox testing) to make sure if different number of iterms are bought by the customer
// The checkout station still records them well

@RunWith(Parameterized.class)
public class CustomerParameterizedTest {

	static ProductItem a = new ProductItem("0034332");
	static ProductItem b = new ProductItem("34432233"); // Can be accessed by other clasess (in this case the
														// Parametrized Class)
	static ProductItem c = new ProductItem("23423443");
	static ProductItem d = new ProductItem("23444322");

	static ProductItem[] oneItem = { b };
	static ProductItem[] threeItems = { a, d, c };
	static ProductItem[] fourItems = { a, c, d, b };

	@Parameterized.Parameters
	public static Collection<Object[]> parameters() {
		return Arrays.asList(
				new Object[][] {

						{ oneItem, threeItems, false },

						{ threeItems, threeItems, true },
						{ fourItems, null, false },
						{ fourItems, fourItems, true },
				});
	}

	@Parameterized.Parameter(0)
	public ProductItem[] itemsBoughtbyCustomer;

	@Parameterized.Parameter(1)
	public ProductItem[] expectedItemsScanned;

	@Parameterized.Parameter(2)
	public boolean expectedResult;

	@Test
	public void testAllScanned() {

		Customer customerNew = new Customer();

		for (int i = 0; i < itemsBoughtbyCustomer.length; i++) {

			ProductItem anotherItem = itemsBoughtbyCustomer[i];
			// customerNew.scanAnItem(anotherItem);

		}

		ArrayList<org.lsmr.selfcheckout.Item> actualItemsScanned = customerNew.getItemsScanned();

		boolean actualResult = Arrays.equals(expectedItemsScanned, actualItemsScanned.toArray());

		Assert.assertTrue(actualResult == expectedResult);
		;

	}

}
