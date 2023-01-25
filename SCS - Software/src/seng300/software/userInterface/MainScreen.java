package seng300.software.userInterface;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.PLUCodedProduct;

import seng300.software.attendant.AttendantLogic;
import seng300.software.selfcheckout.product.ProductDatabase;
import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;

public class MainScreen {
	public static void main(String[] args) {
		
		//set up
		Currency currency = Currency.getInstance(Locale.CANADA);
		int[] banknoteDenominations = {5, 10, 20, 50, 100};
		BigDecimal[] coinDenominations = {new BigDecimal(0.01), new BigDecimal(0.05), new BigDecimal(0.1), new BigDecimal(0.25),
				new BigDecimal(0.5), new BigDecimal(1), new BigDecimal(2)};
		int scaleMaximumWeight = 2000;
		int scaleSensitivity = 5;
		
		SelfCheckoutStation s1 = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, scaleMaximumWeight, scaleSensitivity);
		SelfCheckoutStation s2 = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, scaleMaximumWeight, scaleSensitivity);
		SelfCheckoutStation s3 = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, scaleMaximumWeight, scaleSensitivity);
		
    	ProductDatabase pd = new ProductDatabase();
    	PriceLookupCode code1 = new PriceLookupCode("0001");
    	PriceLookupCode code2 = new PriceLookupCode("0002");
    	PLUCodedProduct p1 = new PLUCodedProduct(code1, "apple", new BigDecimal("2.5"));
    	PLUCodedProduct p2 = new PLUCodedProduct(code2, "banana", new BigDecimal("5.0"));
    	pd.addProduct(p1);
    	pd.addProduct(p2);
    	
    	SelfCheckoutStationLogic sl1 = new SelfCheckoutStationLogic(s1, pd);
    	SelfCheckoutStationLogic sl2 = new SelfCheckoutStationLogic(s2, pd);
    	SelfCheckoutStationLogic sl3 = new SelfCheckoutStationLogic(s3, pd);
    	ReceiptPrinter rp = new ReceiptPrinter();
    	AttendantLogic a = new AttendantLogic(s1, pd, rp);
    	
    	
    	//three checkout stations
    	CheckoutScreen checkout1 = new CheckoutScreen(sl1);
    	CheckoutScreen checkout2 = new CheckoutScreen(sl2);
    	CheckoutScreen checkout3 = new CheckoutScreen(sl3);
    	ArrayList<CheckoutScreen> screens = new ArrayList<CheckoutScreen>();
    	screens.add(checkout1);
    	screens.add(checkout2);
    	screens.add(checkout3);
    	a.addStations(s2);
    	a.addStations(s3);
    	
    	//one attendant station
    	AttendantScreen attendant = new AttendantScreen(a, screens);
		attendant.loginScreen();
		for (int i = 0; i < screens.size(); i ++) {
			attendant.getScreens().get(i).initialScreen();
		}
	}
}
