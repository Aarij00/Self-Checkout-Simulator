package seng300.testing;

import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.lsmr.selfcheckout.Item;

import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.Product;
import seng300.software.attendant.AttendantLogic;
import seng300.software.selfcheckout.product.ProductDatabase;
import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;

public class AttendantLogicTest {
    ProductDatabase pd;
    ReceiptPrinter rp;
    AttendantLogic al;
    Coin dime;
    SelfCheckoutStation SCS;
    SelfCheckoutStationLogic SCSLogic;
    @Before
    public void settingUp() {
    	Currency cad = Currency.getInstance("CAD");
    	int[] bankNotes = {100, 50, 20, 10, 5};
		BigDecimal[] bankCoins = { new BigDecimal(2.00), new BigDecimal(1.00), new BigDecimal(0.25), new BigDecimal(0.10), new BigDecimal(0.05)};
		this.SCS = new SelfCheckoutStation(cad, bankNotes, bankCoins, 100, 1);
		this.pd = new ProductDatabase(1,0);
		this.rp = new ReceiptPrinter();
		this.SCSLogic = new SelfCheckoutStationLogic(SCS, pd);
        this.al = new AttendantLogic(SCS, pd, rp);

    }

    
    
    @Test
    public void testAttendantProductLookup() {
    	BigDecimal price = new BigDecimal(3.99);
    	Barcode barcode = new Barcode(new Numeral[] { Numeral.one });
    	BarcodedProduct barcodedProduct = new BarcodedProduct(barcode, "Pringles", price, 500);
        al.attendantProductLookup(barcodedProduct);
        ArrayList<Product> productList = pd.getProducts();
    	assertEquals(barcodedProduct, productList.get(1));
    }
    
    
    @Test
    public void testEmptyCoinStorage() throws SimulationException, OverloadException {
    	Currency cad = Currency.getInstance("CAD");
    	Coin loonie = new Coin(cad, new BigDecimal(1.00));
    	Coin toonie = new Coin(cad, new BigDecimal(2.00));
    	this.SCS.coinStorage.load(loonie, toonie);
    	List<Coin> coinList = al.emptyCoinStorage();
    	assertEquals(loonie, coinList.get(0));
    	assertEquals(toonie, coinList.get(1));
    	
    
    }
    
   
    @Test
    public void testBanknoteCoinStorage() throws SimulationException, OverloadException {
    	Currency cad = Currency.getInstance("CAD");
    	Banknote oneHundredBill = new Banknote(cad, 100);
    	Banknote fiftyBill = new Banknote(cad, 50);
    	this.SCS.banknoteStorage.load(oneHundredBill, fiftyBill);
    	List<Banknote> banknoteList = al.emptyBanknoteStorage();
    	assertEquals(oneHundredBill, banknoteList.get(0));
    	assertEquals(fiftyBill, banknoteList.get(1));
    	
    
    }
    
    @Test
    public void testRefillCoinDispenser () throws SimulationException, OverloadException {
    	Currency cad = Currency.getInstance("CAD");
    	BigDecimal denom = new BigDecimal(2.00);
    	Coin toonie = new Coin (cad, new BigDecimal(2.00));
    	al.refillCoinDispenser(denom, toonie);
    }
    
    @Test
    public void testRefillBanknoteDispenser () throws SimulationException, OverloadException {
    	Currency cad = Currency.getInstance("CAD");
    	int denom = 20;
    	Banknote twentyBill = new Banknote(cad, 20);
    	al.refillBanknoteDispenser(denom, twentyBill);
    }
  
    @Test
    public void testAttendantAddsInk() throws OverloadException {
        int amountAdded = 1;
        al.attendantAddsInk(amountAdded);
    }

    @Test
    public void testAttendantAddsPaper() throws OverloadException {
        int paperAdded = 1;
        al.attendantAddsPaper(paperAdded);
    }
    
    
    @Test
    public void testAttendantBlocksStation () {
        al.consoleLogin();
        this.SCSLogic.initializeObservers();
        al.addStations(this.SCS);
        al.attendantBlocksStation(this.SCS);
    }

    @Test
    public void testAttendantUnblocksStation () {
        al.consoleLogin();
        al.addStations(this.SCS);
        al.attendantBlocksStation(this.SCS);
        al.attendantUnblocksStation(this.SCS);

    }
    
    
   	@Test
    public void testApprovingWeightDiscrenpancy () {
        al.attendantApprovesWeightDiscrepency();
    }
	
   	@Test
   	public void testAttendantRemovesProductPurchase () {
   		Barcode barcode = new Barcode(new Numeral[] { Numeral.one });
   		BarcodedItem barcodeItem = new BarcodedItem(barcode, 1.0);
   		al.attendantRemovesProductPurchase(barcodeItem);
   	}
    @Test
    public void testConsoleLogin() {
        assertTrue(al.consoleLogin() == true);
    }
    
    @Test
    public void testLoginwhileLoggedIn() {
    	al.consoleLogin();
    	assertTrue(al.consoleLogin() == false);
    }

    @Test
    public void testConsoleLogout() {
        al.consoleLogin();
        assertTrue(al.consoleLogout() == true);
    }

    @Test
    public void testLogoutwhileLoggedOut () {
    	assertTrue(al.consoleLogout() == false);
    }
    @Test
    public void testStartUpStation() {
    	al.consoleLogin();
        assertTrue(al.startUpStation(this.SCS) == true);
    }
    @Test
    public void testStartUpwhileLoggedOut () {
    	assertTrue(al.startUpStation(this.SCS) == false);
    }

    @Test
    public void testShutDownStation() {
    	al.consoleLogin();
        al.startUpStation(this.SCS);
        assertTrue(al.shutDownStation(this.SCS) == true);
    }
    
    @Test
    public void testShutDownwhileLoggedOut () {
    	assertTrue(al.shutDownStation(this.SCS) == false);
    }
    @Test
    public void testGetSupervisedStations() {
    	ArrayList<SelfCheckoutStation> superStation = al.getSupervisedStations();
    }
}
