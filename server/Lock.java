package server;

import comm.Message;
import static comm.MessageTypes.EMPTY_LOCK;
import static comm.MessageTypes.READ_LOCK;
import static comm.MessageTypes.WRITE_LOCK;
import java.util.ArrayList;

public class Lock {
	private int AccountNum;		//Account this lock locks
	private ArrayList holders;	//List of all transactions holding this lock
	private int lockType;	//Hold current type of lock

	public Lock (int num) {	//Create lock, and specify Account Number it locks
		AccountNum = num;
		holders = new ArrayList();	//Create array of transactions holding lock
		lockType = EMPTY_LOCK;		//Set lockType to EMPTY_LOCK
	}

	public synchronized void acquire(int transID, int aLockType){	//Set lock of type aLockType for transID
		while (isConflict(transID, aLockType)){	//If there is a conflict, wait until a lock is released
			try{
				wait();
			} catch (InterruptedException e){
				System.out.println(e);			
			}
		}
		
		//If no conflict...
		if (holders.isEmpty()){	//If no transactions currently hold this lock, add new transaction to array
			holders.add(transID);
			lockType = aLockType;	//And set lock type to the one specified in aLockType
		} else if (!holders.isEmpty() && holders.indexOf(transID) == -1) {	//If there are other transactions holding lock
			//And new transaction is not among them
			holders.add(transID);			//Add transaction to holders array, but don't change lock type
		} else if (onlyHolder(transID) && lockType == READ_LOCK && aLockType == WRITE_LOCK) {
			lockType = aLockType;			//If transaction wants to override its READ_LOCK with a WRITE_LOCK
		}
		
		if(aLockType == READ_LOCK)
			System.out.println("READ_LOCK on account: " + AccountNum + " for transaction: " + transID);
		else
			System.out.println("WRITE_LOCK on account: " + AccountNum + " for transaction: " + transID);
	}

	public synchronized void release(int transID) {	//Removes transaction from holders array
		holders.remove(Integer.valueOf(transID));
		if (holders.isEmpty()){	//If holders is empty, set lock type to EMPTY_LOCK
			lockType = EMPTY_LOCK;
		}
		notifyAll();			//Notify all wait() functions called
	}

	public boolean holds(int transID) {	//See if lock is held by given transaction
		if (holders.indexOf(transID) != -1) {	//If yes, return true
			return true;
		}
		return false;
	}
	
	public boolean onlyHolder(int transID){	//See if transaction is the only holder of a lock
		if(holders.indexOf(transID) != 1 && holders.size() == 1){
			return true;
		}
		return false;
	}

	public synchronized boolean isConflict(int transID, int aLockType){	//Check to see if there is a lock conflict
		//If transaction not in lock, and the lock type is currently a WRITE_LOCK, there is a conflict
		if (lockType == WRITE_LOCK && (aLockType == WRITE_LOCK || aLockType == READ_LOCK) && holders.indexOf(transID) == -1) {
			if(aLockType == WRITE_LOCK)
				System.out.println("\nCONFLICT DETECTED! Trans: " + transID + " couldn't place a WRITE_LOCK on account: " + AccountNum + "\n");
			else
				System.out.println("\nCONFLICT DETECTED! Trans: " + transID + " couldn't place a READ_LOCK on account: " + AccountNum + "\n");
			
			return true;
		} else if (lockType == READ_LOCK && aLockType == WRITE_LOCK && !onlyHolder(transID)) {
			System.out.println("\nCONFLICT DETECTED! Trans: " + transID + " couldn't place a WRITE_LOCK on account: " + AccountNum + "\n");
			return true;		//If lock type is READ_LOCK, and transaction wants WRITE_LOCK, but it is not the only transaction, is conflict
		}
		return false;			//If all tests pass, there is no conflict
	}
}
