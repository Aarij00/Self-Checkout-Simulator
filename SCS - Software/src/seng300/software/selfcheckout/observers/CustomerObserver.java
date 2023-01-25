package seng300.software.selfcheckout.observers;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;

import seng300.software.selfcheckout.customer.Customer;

public interface CustomerObserver extends AbstractDeviceObserver {
	void barcodeScanned(Customer customer, Item item);
}
