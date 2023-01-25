package seng300.software.selfcheckout.payment;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.Card.CardData;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;

import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;

public class PaywithGiftCard implements CardReaderObserver{

    public BigDecimal amountInCard;
    public SelfCheckoutStationLogic scsl;

    public PaywithGiftCard(SelfCheckoutStationLogic scsl) {
        this.scsl = scsl;
    }

    public void SetAmountInCard(BigDecimal amountInCard) {
        this.amountInCard = amountInCard;
    }

    @Override
    public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {}

    @Override
    public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {}

    @Override
    public void cardInserted(CardReader reader) {}

    @Override
    public void cardRemoved(CardReader reader) {}

    @Override
    public void cardTapped(CardReader reader) {}

    @Override
    public void cardSwiped(CardReader reader) {    }

    @Override
    public void cardDataRead(CardReader reader, CardData data) {
        scsl.setSumPaid(scsl.getSumPaid().add(amountInCard)); // obviously more than this?
        return;
    }
}
