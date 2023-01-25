package seng300.software.selfcheckout.observers;

import java.math.BigDecimal;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinValidator;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CoinValidatorObserver;

import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;

public class CoinObserver implements CoinValidatorObserver {
	SelfCheckoutStationLogic station;

	public CoinObserver(SelfCheckoutStationLogic s) {
		this.station = s;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void validCoinDetected(CoinValidator validator, BigDecimal value) {
		// get the new total to update
		BigDecimal totalUpdate = station.getSumPaid().add(value);
		station.setSumPaid(totalUpdate);

	}

	@Override
	public void invalidCoinDetected(CoinValidator validator) {
	}

}
