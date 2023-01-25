package seng300.software.selfcheckout.observers;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;

public class BaggingAreaObserver implements ElectronicScaleObserver {

	SelfCheckoutStationLogic station;
	ElectronicScale scale;
	Boolean weightDiscrepency;

	public BaggingAreaObserver(SelfCheckoutStationLogic s) {
		this.station = s;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		scale.enable();
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		scale.disable();
	}

	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		station.weightChanged(scale, weightInGrams);
	}

	@Override
	public void overload(ElectronicScale scale) {
		try {
			if (scale.getCurrentWeight() > scale.getWeightLimit()) {
				weightDiscrepency = true;
			}
		} catch (OverloadException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void outOfOverload(ElectronicScale scale) {

	}

}
