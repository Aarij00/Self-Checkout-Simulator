package seng300.software.selfcheckout.station;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BarcodeScanner;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.SimulationException;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BarcodeScannerObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;
import org.lsmr.selfcheckout.devices.observers.ReceiptPrinterObserver;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.PLUCodedProduct;
import org.lsmr.selfcheckout.products.Product;

import seng300.software.selfcheckout.exceptions.ProductNotFoundException;
import seng300.software.selfcheckout.observers.BaggingAreaObserver;
import seng300.software.selfcheckout.observers.BanknoteObserver;
import seng300.software.selfcheckout.observers.CardObserver;
import seng300.software.selfcheckout.observers.CoinObserver;
import seng300.software.selfcheckout.product.ProductDatabase;

import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.IllegalErrorPhaseSimulationException;
import org.lsmr.selfcheckout.Item;
import java.util.Locale;

public class SelfCheckoutStationLogic
		implements BarcodeScannerObserver, ReceiptPrinterObserver, ElectronicScaleObserver {
	public ArrayList<BarcodedProduct> cart = new ArrayList<>();
	public ArrayList<PLUCodedProduct> pluCart = new ArrayList<>();

	private ArrayList<Barcode> scannedBarcodes = new ArrayList<>();
	private ArrayList<Barcode> baggedItems = new ArrayList<>();
	private ArrayList<Barcode> customerBaggedItems = new ArrayList<>();

	private ProductDatabase products;
	private BigDecimal billTotal;
	private BigDecimal sumPaid;
	private int bagsUsed;
	private int[] banknoteDenominations = { 100, 50, 25, 10, 5 };
	private BigDecimal dollar = new BigDecimal(1.00);
	private BigDecimal quarter = new BigDecimal(0.25);
	private BigDecimal dime = new BigDecimal(0.10);
	private BigDecimal nickel = new BigDecimal(0.05);
	private BigDecimal[] coinDenominations = { dollar, quarter, dime, nickel };
	private Currency cad = Currency.getInstance(Locale.CANADA);
	public SelfCheckoutStation selfCheckoutStation = new SelfCheckoutStation(cad, banknoteDenominations,
			coinDenominations, 10000, 5);
	private boolean ownBag;
	private boolean lowInk;
	private boolean lowPaper;
	private ElectronicScale scale = new ElectronicScale(10000, 5);
	private ReceiptPrinter printer = new ReceiptPrinter();
	public Boolean weightDiscrepency;
	private double stationTrackedWeight;
	private boolean isCurrentlyScanning;
	public boolean notifiedItemScanned = false; // to be used for testing only

	/**
	 * Basic constructor
	 * 
	 * @param scs              Self checkout station to install logic on.
	 * @param acceptedProducts A list of products the self checkout station can add
	 *                         to cart.
	 */
	public SelfCheckoutStationLogic(SelfCheckoutStation scs, ProductDatabase pd) throws NullPointerException {
		if (scs == null || pd == null)
			throw new NullPointerException("arguments cannot be null");
		this.billTotal = new BigDecimal("0.00");
		this.selfCheckoutStation = scs;
		this.products = pd;
		this.selfCheckoutStation.mainScanner.attach(this);
		this.sumPaid = new BigDecimal("0.00");
	}

	public BigDecimal getSumPaid() {
		return this.sumPaid;
	}

	public void setSumPaid(BigDecimal newAmount) {
		this.sumPaid = newAmount;
	}

	public void initializeObservers() {
		CoinObserver validatorObs = new CoinObserver(this);
		this.selfCheckoutStation.coinValidator.attach(validatorObs);
		BanknoteObserver validatorObsBanknote = new BanknoteObserver(this);
		this.selfCheckoutStation.banknoteValidator.attach(validatorObsBanknote);

		// initalize scale
		CardObserver cardObs = new CardObserver(this);
		this.selfCheckoutStation.cardReader.attach(cardObs);

		// initalize scale
		BaggingAreaObserver validatorBaggingArea = new BaggingAreaObserver(this);
		scale.attach(validatorBaggingArea);
	}

	public void insertCoin(Coin InsertedCoin) throws DisabledException, OverloadException {
		this.selfCheckoutStation.coinSlot.accept(InsertedCoin);
	}

	public void insertBanknote(Banknote InsertedBanknote) throws DisabledException, OverloadException {
		this.selfCheckoutStation.banknoteInput.accept(InsertedBanknote);
	}

	public void printReceipt() {
		String line;
		for (Product p : cart) {
			line = getReceiptLine(p); // get formatted receipt line
			try {
				printLine(line);
			} catch (EmptyException | OverloadException e) {
				e.printStackTrace();
			}
		}
		line = "Total Price\t$" + billTotal.setScale(2, RoundingMode.HALF_EVEN) + '\n';
		try {
			printLine(line);
		} catch (EmptyException | OverloadException e) {
			e.printStackTrace();
		}
		this.selfCheckoutStation.printer.cutPaper();
	}

	/**
	 * Returns the bill total, rounded to two decimal places.
	 * 
	 * @return
	 */
	public BigDecimal getFinalPrice() {
		return billTotal.setScale(2, RoundingMode.HALF_EVEN);
	}

	/**
	 * Returns instance of self checkout station.
	 * 
	 * @return
	 */
	public SelfCheckoutStation getSelfCheckoutStation() {
		return selfCheckoutStation;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		scale.enable();
		printer.enable();
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		scale.disable();
		printer.disable();
	}

	@Override
	public void barcodeScanned(BarcodeScanner barcodeScanner, Barcode barcode) {
		this.notifiedItemScanned = true;
		try {
			BarcodedProduct p = (BarcodedProduct) this.products.getProductForItem(new BarcodedItem(barcode, 1));
			if (p == null)
				throw new ProductNotFoundException("Product not found");
			this.cart.add(p);
			this.updateBillTotal(p.getPrice());

			// scanners will be re-enabled when scanned item has been bagged
			this.selfCheckoutStation.mainScanner.disable();
			this.selfCheckoutStation.handheldScanner.disable();

		} catch (ProductNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void PLUCodedProductAdded(PLUCodedProduct p) {
		this.notifiedItemScanned = true;
		try {
			if (p == null)
				throw new ProductNotFoundException("Product not found");
			this.pluCart.add(p);
			this.updateBillTotal(p.getPrice());
		} catch (ProductNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void removeBarcodedItem(BarcodedProduct p) {
		cart.remove(p);
		updateBillTotal(p.getPrice().negate());
	}
	
	public void removePLUcodedItem(PLUCodedProduct p) {
		pluCart.remove(p);
		updateBillTotal(p.getPrice().negate());
	}

	public void productRemovedFromPurchase(Barcode barcode) {
		try {
			BarcodedProduct p = (BarcodedProduct) this.products.getProductForItem(new BarcodedItem(barcode, 1));
			if (p == null)
				throw new ProductNotFoundException();
			this.cart.remove(0);
			BigDecimal price = p.getPrice();
			price = price.negate();
			this.updateBillTotal(price);

		} catch (ProductNotFoundException e) {
			e.printStackTrace();
		}
	}

	// scanners have to be re-enabled
	public void addAdditionalItems() {
		this.selfCheckoutStation.mainScanner.enable();
		this.selfCheckoutStation.handheldScanner.enable();
	}

	// scan item using main scanner
	public void scanMain(BarcodedItem barcodeItem) {
		this.selfCheckoutStation.mainScanner.scan(barcodeItem);
		this.scale.add(barcodeItem);
	}

	// scan item using main scanner during partial payment
	public void scanPartialPaymentMain(BarcodedItem barcodeItem) {
		this.addAdditionalItems();
		this.selfCheckoutStation.mainScanner.scan(barcodeItem);
		this.scale.add(barcodeItem);
		this.setBillTotal(this.getBillTotal().subtract(this.getSumPaid()));
	}

	// scan item using handheld scanner
	public void scanHandheld(BarcodedItem barcodeItem) {
		this.selfCheckoutStation.handheldScanner.scan(barcodeItem);
		this.scale.add(barcodeItem);
	}

	// scan item using handheld scanner during partial payment
	public void scanPartialPaymentHandheld(BarcodedItem barcodeItem) {
		this.addAdditionalItems();
		this.selfCheckoutStation.handheldScanner.scan(barcodeItem);
		this.scale.add(barcodeItem);
		this.setBillTotal(this.getBillTotal().subtract(this.getSumPaid()));
	}

	// customer chooses to use own bag
	public void setOwnBag() {
		ownBag = true;
	}

	// customer chooses to use no bag
	public void setNoBag() {
		ownBag = true; // Effectively equivalent since to the system, not using the store's bag is the
						// same as us either using our own, or not using a bag at all
	}

	// item placed
	public void itemPlaced(Item item) {
		Barcode itemCode = getLastItemBarcode();
		double itemWeight = products.getWeightForItem(item);
		if (itemCode != null && ownBag == false) {
			baggedItems.add(itemCode);
			stationTrackedWeight += itemWeight;
		} else if ((itemCode != null) && ownBag == true) {
			customerBaggedItems.add(itemCode);
		}
	}

	// item removed from bagging area
	public void itemRemovedBaggingArea(Item item) {
		Barcode itemCode = getLastItemBarcode();
		double itemWeight = products.getWeightForItem(item);
		if (ownBag == false) {
			baggedItems.remove(itemCode);
			stationTrackedWeight -= itemWeight;
		} else if (ownBag == true) {
			customerBaggedItems.remove(itemCode);
		}
	}

	// customer enters the number of plastic bags used
	public void customerEntersPlasticBagsUsed(int bagsEntered) {
		if (bagsEntered > 0) {
			bagsUsed += bagsEntered;
		}
	}

	// item not placed
	public void itemNotPlaced() throws org.lsmr.selfcheckout.SimulationException {
		Barcode code = getLastItemBarcode();
		if ((code != null) && !(baggedItems.contains(code))) {
			System.err.println("Item not placed in bagging area.");
			CustomerStopsAddingItems();
		}
	}

	// Station detects that the weight in the bagging area does not conform to
	// expectations
	public void baggingAreaWeightInvalid() {
		if (weightDiscrepency == true) {
			System.err.println("Bagging Area Weight Invalid");
			CustomerStopsAddingItems();
		}
	}

	public void scanItem(BarcodedItem barcodeItem) {
		if (!isCurrentlyScanning) {
			System.err.println("We are not allowing scanning at this time");
			return;
		}
		this.selfCheckoutStation.mainScanner.scan(barcodeItem);
		this.scale.add(barcodeItem);
	}

	public Barcode getLastItemBarcode() {
		int size = scannedBarcodes.size();
		if (size > 0)
			return scannedBarcodes.get(size - 1);
		return null;
	}

	/**
	 * Finds price of item and updates bill total. Does not currently support
	 * removing items from bill.
	 * 
	 */
	private void updateBillTotal(BigDecimal itemPrice) {
		billTotal = billTotal.add(itemPrice);
	}

	public void setBillTotal(BigDecimal b) {
		billTotal = b;
	}

	public BigDecimal getBillTotal() {
		return billTotal;
	}

	private String getReceiptLine(Product p) {
		StringBuilder b = new StringBuilder();
		b.append('$').append(p.getPrice().setScale(2, RoundingMode.HALF_EVEN));
		b.append('\t').append(((BarcodedProduct) p).getDescription()).append('\n');
		return b.toString();
	}

	private void printLine(String line) throws EmptyException, OverloadException {
		if (line.length() <= ReceiptPrinter.CHARACTERS_PER_LINE) {
			char[] chars = line.toCharArray();
			for (char c : chars)
				this.selfCheckoutStation.printer.print(c); // print line char by char
			return;
		}
		int numLines = line.length() / ReceiptPrinter.CHARACTERS_PER_LINE;
		int start = 0;
		int end = ReceiptPrinter.CHARACTERS_PER_LINE - 1;
		for (int i = 0; i < numLines; i++) {
			String l = line.substring(start, end) + '\n';
			printLine(l);
			start = end;
			end += ReceiptPrinter.CHARACTERS_PER_LINE;
		}
		if (line.length() % ReceiptPrinter.CHARACTERS_PER_LINE != 0)
			printLine(line.substring(start));
	}

	// Station detects that the paper in a receipt printer is low.
	// idk if this really "detects" that the receipt printer is low?
	public void paperIsLow(ReceiptPrinter printer) {
		lowPaper = true;
	}

	// Station detects that the ink in a receipt printer is low.
	public void inkIsLow(ReceiptPrinter printer) {
		lowInk = true;
	}

	public void CustomerStopsAddingItems() {
		this.isCurrentlyScanning = false;
	}

	public void CustomerStartsAddingItems() {
		this.isCurrentlyScanning = true;
	}

	public boolean getLowpaperStatus() {
		return lowPaper;
	}

	public boolean getLowInkStatus() {
		return lowInk;
	}

	@Override
	public void outOfPaper(ReceiptPrinter printer) {
		try {
			printer.addPaper(ReceiptPrinter.MAXIMUM_PAPER);
		} catch (OverloadException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void outOfInk(ReceiptPrinter printer) {
		try {
			printer.addInk(ReceiptPrinter.MAXIMUM_INK);
		} catch (OverloadException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paperAdded(ReceiptPrinter printer) {
	}

	@Override
	public void inkAdded(ReceiptPrinter printer) {
	}

	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		try {
			if (scale.getCurrentWeight() != stationTrackedWeight + weightInGrams) {
				weightDiscrepency = true;
			}
		} catch (OverloadException e) {
			e.printStackTrace();
		}
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
		weightDiscrepency = false;
	}
	
	public ProductDatabase getProductDatabase() {
		return products;
	}
}
