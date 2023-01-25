package seng300.testing;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.products.*;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;

import seng300.software.selfcheckout.product.ProductDatabase;
import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;

public class ScanItemTest {
    SelfCheckoutStationLogic selfCheckoutLogic;
    SelfCheckoutStation scs;
    ProductDatabase pd;
    BarcodedItem pringles;
    BarcodedItem kinder;
    BarcodedItem ruffles;

    double weight;
    BigDecimal price;
    Barcode barcode;
    BarcodedProduct barcodedProduct;

    @Before
    public void setup() {
        Currency currency = Currency.getInstance("CAD");
        int[] banknoteDenominations = { 5, 10, 20, 50 };
        BigDecimal[] coinDenominations = { BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.25),
                BigDecimal.valueOf(1.0), BigDecimal.valueOf(2.0) };
        int scaleMaximumWeight = 2000;
        int scaleSensitivity = 5;
        pd = new ProductDatabase();
        scs = new SelfCheckoutStation(currency, banknoteDenominations, coinDenominations, scaleMaximumWeight,
                scaleSensitivity);
        selfCheckoutLogic = new SelfCheckoutStationLogic(scs, pd);

        // creating pringles barcoded item
        barcode = new Barcode(new Numeral[] { Numeral.one });
        weight = 500;
        price = new BigDecimal(3.99);
        pringles = new BarcodedItem(barcode, weight);

        // adding pringles to product database
        barcodedProduct = new BarcodedProduct(barcode, "pringles", price, 500);
        pd.addProduct(barcodedProduct);

        // creating kinder barcoded item
        barcode = new Barcode(new Numeral[] { Numeral.two });
        weight = 100;
        price = new BigDecimal(4.59);
        pringles = new BarcodedItem(barcode, weight);

        // adding kiner to product database
        barcodedProduct = new BarcodedProduct(barcode, "kinder", price, 100);
        pd.addProduct(barcodedProduct);
    }

    @Test
    public void testScan() {
        // scans kinder
        boolean inCart = false;
        selfCheckoutLogic.scanMain(kinder);
        for (BarcodedProduct bp : selfCheckoutLogic.cart) {
            if (bp.getBarcode() == kinder.getBarcode()) {
                inCart = true;
            }
        }

        assertTrue("Kinder should be in cart", inCart);

    }

    @Test
    public void testScannersDisabledAfterMainScan() {
        selfCheckoutLogic.scanMain(kinder);
        boolean cond = selfCheckoutLogic.selfCheckoutStation.mainScanner.isDisabled()
                && selfCheckoutLogic.selfCheckoutStation.handheldScanner.isDisabled();
        assertTrue("Scanners should be disabled", cond);
    }

    @Test
    public void testScannersDisabledAfterHandheldScan() {
        selfCheckoutLogic.scanHandheld(kinder);
        boolean cond = selfCheckoutLogic.selfCheckoutStation.mainScanner.isDisabled()
                && selfCheckoutLogic.selfCheckoutStation.handheldScanner.isDisabled();
        assertTrue("Scanners should be disabled", cond);
    }

    @Test
    public void testMultipleScans() {
        boolean pringlesInCart = false;
        boolean kinderInCart = false;

        selfCheckoutLogic.scanMain(kinder);
        selfCheckoutLogic.selfCheckoutStation.mainScanner.enable();
        selfCheckoutLogic.scanMain(pringles);

        for (BarcodedProduct bp : selfCheckoutLogic.cart) {
            if (bp.getBarcode() == kinder.getBarcode()) {
                kinderInCart = true;
            }
            if (bp.getBarcode() == pringles.getBarcode()) {
                pringlesInCart = true;
            }
        }

        assertTrue("Both pringles and kinder should be in cart", pringlesInCart && kinderInCart);

    }

    @Test
    public void testWhenMainScannerDisabled() {
        selfCheckoutLogic.selfCheckoutStation.mainScanner.disable();
        selfCheckoutLogic.scanMain(pringles);
        for (BarcodedProduct bp : selfCheckoutLogic.cart) {
            if (bp.getBarcode() == pringles.getBarcode()) {
                assertFalse("item shouldn't be in cart when scanner is disabled.",
                        bp.getBarcode() == pringles.getBarcode());
            }
        }
    }

    @Test
    public void testWhenHandheldScannerDisabled() {
        selfCheckoutLogic.selfCheckoutStation.handheldScanner.disable();
        selfCheckoutLogic.scanHandheld(pringles);
        for (BarcodedProduct bp : selfCheckoutLogic.cart) {
            if (bp.getBarcode() == pringles.getBarcode()) {
                assertFalse("item shouldn't be in cart when scanner is disabled.",
                        bp.getBarcode() == pringles.getBarcode());
            }
        }
    }

    @Test
    public void testMainScannerAfterPartialPayment() {
        boolean inCart = false;
        selfCheckoutLogic.scanPartialPaymentMain(kinder);
        for (BarcodedProduct bp : selfCheckoutLogic.cart) {
            if (bp.getBarcode() == kinder.getBarcode()) {
                inCart = true;
            }
        }
        assertTrue("kinder should be in cart", inCart);
    }

    public void testHandheldScannerAfterPartialPayment() {
        boolean inCart = false;
        selfCheckoutLogic.scanPartialPaymentHandheld(pringles);
        for (BarcodedProduct bp : selfCheckoutLogic.cart) {
            if (bp.getBarcode() == kinder.getBarcode()) {
                inCart = true;
            }
        }

        assertTrue("kinder should be in cart", inCart);
    }
}
