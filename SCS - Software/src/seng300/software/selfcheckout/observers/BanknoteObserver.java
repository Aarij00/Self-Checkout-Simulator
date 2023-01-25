package seng300.software.selfcheckout.observers;

import java.math.BigDecimal;
import java.util.Currency;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteValidator;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BanknoteValidatorObserver;

import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;

public class BanknoteObserver implements BanknoteValidatorObserver {
	SelfCheckoutStationLogic station;

	public BanknoteObserver(SelfCheckoutStationLogic s) {
		this.station = s;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void validBanknoteDetected(BanknoteValidator validator, Currency currency, int value) {
		// update total with value of the banknote
		BigDecimal BDValue = new BigDecimal(value);
		BigDecimal totalUpdate = station.getSumPaid().add(BDValue);
		station.setSumPaid(totalUpdate);
	}

	@Override
	public void invalidBanknoteDetected(BanknoteValidator validator) {
	}

}
