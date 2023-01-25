package seng300.software.selfcheckout.observers;

import java.math.BigDecimal;
import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;

import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;

public class CardObserver implements CardReaderObserver {
	SelfCheckoutStationLogic station;

	public CardObserver(SelfCheckoutStationLogic s) {
		this.station = s;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
	}

	@Override
	public void cardInserted(CardReader reader) {

	}

	@Override
	public void cardRemoved(CardReader reader) {

	}

	@Override
	public void cardTapped(CardReader reader) {

	}

	@Override
	public void cardSwiped(CardReader reader) {

	}

	@Override
	public void cardDataRead(CardReader reader, CardData data) {
		if (data.getType().equals("Debit") || data.getType().equals("Credit")) {
			station.setSumPaid(station.getFinalPrice());
			station.printReceipt();
			station.setBillTotal(BigDecimal.ZERO);
		}
	}
}
