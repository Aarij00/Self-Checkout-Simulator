package seng300.software.attendant;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.Keyboard;
import org.lsmr.selfcheckout.devices.SupervisionStation;
import org.lsmr.selfcheckout.devices.TouchScreen;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.products.Product;

import java.util.List;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.InvalidArgumentSimulationException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.SimulationException;

import seng300.software.selfcheckout.observers.BaggingAreaObserver;
import seng300.software.selfcheckout.product.ProductDatabase;
import seng300.software.selfcheckout.station.SelfCheckoutStationLogic;

public class AttendantLogic {

	private SelfCheckoutStation selfCheckoutStation;
	private ProductDatabase products;
	private final ArrayList<SelfCheckoutStation> supervisedStations;

	private enum State {
		LoggedIn,
		LoggedOut
	};

	private State consoleState = State.LoggedOut;

	public SupervisionStation supervisionStation;
	public ElectronicScale scale;
	public final TouchScreen screen;
	public final Keyboard keyboard;
	public ReceiptPrinter rp;
	public SelfCheckoutStationLogic stationLogic;

	/**
	 * Creates a supervisor station.
	 */
	public AttendantLogic(SelfCheckoutStation scs, ProductDatabase pd, ReceiptPrinter rp) throws NullPointerException {
		if (scs == null || pd == null)
			throw new NullPointerException("arguments cannot be null");
		screen = new TouchScreen();
		this.supervisionStation = new SupervisionStation();
		supervisedStations = new ArrayList<SelfCheckoutStation>();
		keyboard = new Keyboard();
		this.stationLogic = new SelfCheckoutStationLogic(scs, pd);
		this.selfCheckoutStation = scs;
		supervisedStations.add(selfCheckoutStation);
		this.products = pd;
		this.rp = rp;
	}

	public void addStations(SelfCheckoutStation station) {
		if (station != null) {
			selfCheckoutStation = station;
			supervisedStations.add(selfCheckoutStation);
		}
	}

	// Attendant empties the coin storage unit. returns a list of all coins
	// previously in the unit which are now unloaded.
	public List<Coin> emptyCoinStorage() {
		return selfCheckoutStation.coinStorage.unload();
	}

	// Attendant empties the banknote storage unit. returns a list of banknotes that
	// were unloaded.
	public List<Banknote> emptyBanknoteStorage() {
		return selfCheckoutStation.banknoteStorage.unload();
	}

	// Attendant refills the coin dispenser
	// denom = denomination of both the dispenser to refill and the coin values
	public void refillCoinDispenser(BigDecimal denom, Coin... coins) throws SimulationException, OverloadException {
		for (Coin coin : coins) {
			if (coin.getValue().compareTo(denom) != 0) {
				throw new InvalidArgumentSimulationException("All coins must be of the correct denomination.");
			}
		}
		selfCheckoutStation.coinDispensers.get(denom).load(coins);
	}

	// Attendant refills the banknote dispenser
	// denom = denomination of both the dispenser to refill and the banknote values
	public void refillBanknoteDispenser(int denom, Banknote... banknotes) throws OverloadException {
		for (Banknote banknote : banknotes) {
			if (banknote.getValue() != denom) {
				throw new InvalidArgumentSimulationException("All banknotes must be of the correct denomination.");
			}
		}
		selfCheckoutStation.banknoteDispensers.get(denom).load(banknotes);
	}

	// attendant looks up a product
	public void attendantProductLookup(Product product) {
		products.addProduct(product);
	}

	// attendant adds ink to receipt printer
	public void attendantAddsInk(int quantity) throws OverloadException {
		rp.addInk(quantity);
	}

	// attendant adds paper to receipt printer
	public void attendantAddsPaper(int units) throws OverloadException {
		rp.addPaper(units);
	}

	// attendant blocks a station
	public void attendantBlocksStation(SelfCheckoutStation stationToBlock) {
		if (this.consoleState == State.LoggedIn &&
				supervisedStations.contains(stationToBlock)) {
			stationToBlock.baggingArea.disable();
			stationToBlock.scanningArea.disable();
			stationToBlock.screen.disable();
			stationToBlock.printer.disable();
			stationToBlock.cardReader.disable();
			stationToBlock.mainScanner.detach(stationLogic);
			stationToBlock.handheldScanner.detach(stationLogic);
			stationToBlock.banknoteInput.disable();
			stationToBlock.banknoteOutput.disable();
			stationToBlock.banknoteValidator.disable();
			stationToBlock.banknoteStorage.disable();
			stationToBlock.coinSlot.disable();
			stationToBlock.coinValidator.disable();
			stationToBlock.coinStorage.disable();
			stationToBlock.coinTray.disable();
		}
	}
	
	public void attendantUnblocksStation(SelfCheckoutStation stationToUnblock) {
		if (this.consoleState == State.LoggedIn &&
				supervisedStations.contains(stationToUnblock)) {
			stationToUnblock.baggingArea.enable();;
			stationToUnblock.scanningArea.enable();
			stationToUnblock.screen.enable();
			stationToUnblock.printer.enable();
			stationToUnblock.cardReader.enable();
			stationToUnblock.mainScanner.attach(stationLogic);
			stationToUnblock.handheldScanner.attach(stationLogic);
			stationToUnblock.banknoteInput.enable();
			stationToUnblock.banknoteOutput.enable();
			stationToUnblock.banknoteValidator.enable();
			stationToUnblock.banknoteStorage.enable();
			stationToUnblock.coinSlot.enable();
			stationToUnblock.coinValidator.enable();
			stationToUnblock.coinStorage.enable();
			stationToUnblock.coinTray.enable();
		}
	}

	// attendant removes a product from purchase
	public void attendantRemovesProductPurchase(BarcodedItem barcodeItem) {
		Barcode barcode = barcodeItem.getBarcode();
		stationLogic.productRemovedFromPurchase(barcode);
	}

	// attendant approves a weight discrepency
	public void attendantApprovesWeightDiscrepency() {
		stationLogic.outOfOverload(scale);
	}

	/**
	 * Attendant logs in to their control console
	 */
	public boolean consoleLogin() {
		if (this.consoleState == State.LoggedOut) {
			//supervisionStation = new SupervisionStation();
			consoleState = State.LoggedIn;
			return true;
		}
		// if already logged in
		return false;

	}

	/**
	 * Attendant logs out to their control console
	 */
	public boolean consoleLogout() {
		if (this.consoleState == State.LoggedIn) {
			//supervisionStation = null;
			consoleState = State.LoggedOut;
			return true;
		}
		// if already logged out
		return false;

	}

	/**
	 * Attendant starts up a station
	 */
	public boolean startUpStation(SelfCheckoutStation stationToStart) {
		if (this.consoleState == State.LoggedIn) {
			try {
				supervisionStation.add(stationToStart);
				return true;
			} catch (IllegalArgumentException | IllegalStateException e) {
				System.err.println(e);
			}
		}
		return false;
	}

	/**
	 * Attendant shuts down a station
	 */
	public boolean shutDownStation(SelfCheckoutStation stationToShut) {
		if (this.consoleState == State.LoggedIn) {
			try {
				supervisionStation.remove(stationToShut);
				return true;
			} catch (IllegalArgumentException | IllegalStateException e) {
				System.err.println(e);
			}
		}
		return false;
	}

	public ArrayList<SelfCheckoutStation> getSupervisedStations() {
		return supervisedStations;
	}
}
