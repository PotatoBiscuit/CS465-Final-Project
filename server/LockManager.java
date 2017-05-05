package server;

import java.util.*;
import static comm.MessageTypes.EMPTY_LOCK;
import static comm.MessageTypes.READ_LOCK;
import static comm.MessageTypes.WRITE_LOCK;

public class LockManager{
	private Hashtable allLocks;	//A hashtable of all existing locks

	public LockManager() {
		allLocks = new Hashtable();	//Create hashtable for locks
	}	
		
	public void setLock(int accountNum, int transID, int lockType){	//Set lock of type lockType for accountNum, using transID
		Lock foundLock;
		synchronized(allLocks){	//Eliminate race condition in lock hashtable
			if((foundLock = (Lock) allLocks.get(accountNum)) == null){	//If lock does not exist, create new one
				foundLock = new Lock(accountNum);
				allLocks.put(accountNum, foundLock);
			}
		}
		foundLock.acquire(transID, lockType);	//Acquire lock
	}
	
	public synchronized void unLock(int transID){	//Unlock all locks for transID
		System.out.println("Unlocking all locks for " + transID);
		Enumeration e = allLocks.elements();	//Get all elements in lock hashtable
		while(e.hasMoreElements()){				//While there are more elements...
			Lock tempLock = (Lock) e.nextElement();
			if(tempLock.holds(transID)) tempLock.release(transID);	//If the lock was set by transID release it
		}
		System.out.println("Unlocking complete for " + transID);
	}
	
	public void deadlockDisplay(){	//Displays current locks held causing of deadlock
		System.out.println("\n------------Deadlock Info-------------");
		Enumeration e = allLocks.elements();
		while(e.hasMoreElements()){
			Lock tempLock = (Lock) e.nextElement();
			if(tempLock.lockType == READ_LOCK){
				System.out.print("READ_LOCK");
			}else if(tempLock.lockType == WRITE_LOCK){
				System.out.print("WRITE_LOCK");
			}else{
				continue;
			}
			System.out.print(" on account " + tempLock.AccountNum + " held by " + Arrays.toString(tempLock.holders.toArray())
			+ " blocking transactions: ");
			System.out.println(Arrays.toString(tempLock.waitList.toArray()));
		}
	}
}
