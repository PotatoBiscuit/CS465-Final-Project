package server;

import comm.Message;
import static comm.MessageTypes.EMPTY_LOCK;
import static comm.MessageTypes.READ_LOCK;
import static comm.MessageTypes.WRITE_LOCK;
import java.util.ArrayList;

public class Lock {
	private int AccountNum;
	private ArrayList holders;
	private int lockType;

	public Lock (int num) {
		AccountNum = num;
		holders = new ArrayList();
		lockType = EMPTY_LOCK;
	}

	public synchronized void acquire(int transID, int aLockType){
		while (isConflict(transID, aLockType)){
			try{
				wait();	
			} catch (InterruptedException e){
				System.out.println(e);			
			}
		}
		
		if (holders.isEmpty()){
			holders.add(transID);
			lockType = aLockType;					
		} else if (holders.isEmpty() != true) {
			if (holders.indexOf(transID) == -1)
				holders.add(transID);
		} else if (holders.indexOf(transID) != -1 && lockType == READ_LOCK && aLockType == WRITE_LOCK) {
			lockType = aLockType;			
		}
		
		if(aLockType == READ_LOCK)
			System.out.println("Read lock on account: " + AccountNum + " for transaction: " + transID);
		else
			System.out.println("Write lock on account: " + AccountNum + " for transaction: " + transID);
	}

	public synchronized void release(int transID) {
		holders.remove(Integer.valueOf(transID));
		if (holders.isEmpty()){
			lockType = EMPTY_LOCK;
		}
		notifyAll();
	}

	public boolean holds(int transID) {
		if (holders.indexOf(transID) != -1) {
			return true;
		}
		return false;
	}

	public synchronized boolean isConflict(int transID, int aLockType){
		if (lockType == WRITE_LOCK && (aLockType == WRITE_LOCK || aLockType == READ_LOCK) && holders.indexOf(transID) == -1) {
			if(aLockType == WRITE_LOCK)
				System.out.println("\nCONFLICT DETECTED! Trans: " + transID + " couldn't place a WRITE_LOCK on account: " + AccountNum + "\n");
			else
				System.out.println("\nCONFLICT DETECTED! Trans: " + transID + " couldn't place a READ_LOCK on account: " + AccountNum + "\n");
			return true;
		} else if (lockType == READ_LOCK && aLockType == WRITE_LOCK && holders.indexOf(transID) != -1 && holders.size() == 1) {
			return false;		
		} else if (lockType == READ_LOCK && aLockType == WRITE_LOCK) {
			System.out.println("\nCONFLICT DETECTED! Trans: " + transID + " couldn't place a WRITE_LOCK on account: " + AccountNum + "\n");
			return true;		
		}
		return false;
	}
}
