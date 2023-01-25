package seng300.testing;

import seng300.software.selfcheckout.observers.MachineCardReaderObserver;

public class ScanMemberShipObserverCustomStub implements MachineCardReaderObserver {

	boolean success_has_been_called = false;
	boolean fail_has_been_called = false;
	boolean remove_has_been_called = false;

	@Override
	public void scanSuccess() {
		// TODO Auto-generated method stub
		success_has_been_called = true;
	}

	@Override
	public void scanFail() {
		// TODO Auto-generated method stub
		fail_has_been_called = true;
	}

	@Override
	public void removeSuccess() {
		// TODO Auto-generated method stub
		remove_has_been_called = true;

	}

	public boolean get_status_success() {
		return success_has_been_called;
	}

	public boolean get_status_fail() {
		return fail_has_been_called;
	}

	public boolean get_status_remove() {
		return remove_has_been_called;
	}

}
