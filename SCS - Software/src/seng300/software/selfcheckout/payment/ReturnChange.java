package seng300.software.selfcheckout.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.lsmr.selfcheckout.devices.*;
import org.lsmr.selfcheckout.devices.observers.*;

/**
 * @author Justin Parker
 */
public class ReturnChange implements BanknoteSlotObserver {
	public SelfCheckoutStation scs;
	public boolean danglingBanknote;

	public ReturnChange(SelfCheckoutStation scs) throws NullPointerException {
		if (scs == null)
			throw new NullPointerException("argument cannot be null");
		this.scs = scs;
		danglingBanknote = false;
	}

	/**
	 * Dispenses an amount of money from the self checkout station through the
	 * dispensers.
	 * 
	 * @param bd
	 * @return boolean; whether enough change was able to be returned to the
	 *         customer
	 * @throws Exception
	 */
	public boolean DispenseMoney(BigDecimal bd) throws Exception {
		int[] bnOrder = scs.banknoteDenominations.clone(); // clone the banknote denominations
		for (int i = 0; i < bnOrder.length; i++) { // sort the banknote denominations in descending order
			for (int j = 0; j < bnOrder.length - 1; j++) {
				if (bnOrder[j] < bnOrder[j + 1]) {
					int temp = bnOrder[j];
					bnOrder[j] = bnOrder[j + 1];
					bnOrder[j + 1] = temp;
				}
			}
		}

		for (int i = 0; i < bnOrder.length; i++) { // Go backwards through the keys of banknotes (biggest to smallest)
			if (bd.subtract(new BigDecimal(bnOrder[i])).compareTo(BigDecimal.ZERO) != -1) { // If you're able to
																							// dispense this banknote
																							// without going over, do it
				try {
					i--;
					if (!danglingBanknote) {
						scs.banknoteDispensers.get(bnOrder[i + 1]).emit();
						bd = bd.subtract(new BigDecimal(bnOrder[i + 1]));
						break;
					}
				} catch (EmptyException e) {
					throw new Exception(e);
				}
			}
		}

		List<BigDecimal> cOrder = new ArrayList<BigDecimal>(scs.coinDenominations); // clone the coin denominations
		for (int i = 0; i < cOrder.size(); i++) {
			for (int j = 0; j < cOrder.size() - 1; j++) {
				if (cOrder.get(j).compareTo(cOrder.get(j + 1)) < 0) {
					BigDecimal temp = cOrder.get(j);
					cOrder.set(j, cOrder.get(j + 1));
					cOrder.set(j + 1, temp);
				}
			}
		}

		for (int i = 0; i < cOrder.size(); i++) { // Go backwards through the keys of coins (biggest to smallest)
			if (bd.subtract(cOrder.get(i)).compareTo(BigDecimal.ZERO) != -1) { // If you're able to dispense this coin
																				// without going over, do it
				try {
					scs.coinDispensers.get(cOrder.get(i)).emit();
					bd = bd.subtract(cOrder.get(i));
					i--;
				} catch (EmptyException ee) {
					continue;
				}
			}
		}
		return bd.compareTo(BigDecimal.ZERO) == 0;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void banknoteInserted(BanknoteSlot slot) {
	}

	// Override call removed, caused error
	public void banknoteEjected(BanknoteSlot slot) {
		danglingBanknote = true;
	}

	@Override
	public void banknoteRemoved(BanknoteSlot slot) {
		danglingBanknote = false;
	}

	@Override
	public void banknotesEjected(BanknoteSlot slot) {
		// TODO Auto-generated method stub
		// Auto generated as required to over-ride
		// Function calls from abstract extension

	}
}
