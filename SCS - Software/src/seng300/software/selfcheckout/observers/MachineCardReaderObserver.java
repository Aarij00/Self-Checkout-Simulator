package seng300.software.selfcheckout.observers;

public interface MachineCardReaderObserver {
	void scanSuccess();

	void scanFail();

	void removeSuccess();
}
