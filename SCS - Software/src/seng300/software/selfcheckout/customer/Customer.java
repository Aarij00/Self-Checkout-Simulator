package seng300.software.selfcheckout.customer;

import java.util.ArrayList;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import seng300.software.selfcheckout.observers.CustomerObserver;

public class Customer extends AbstractDevice<CustomerObserver> {

	ArrayList<Item> obItems = new ArrayList<Item>();

	public Customer() {

	}

	public void scanAnItem(Item anotherItem) {
		notifyBarcodeScanned(anotherItem);
	}

	public void notifyBarcodeScanned(Item anotherItem) {
		obItems.add(anotherItem);
	}

	public ArrayList<Item> getItemsScanned() {
		return obItems;
	}
}
