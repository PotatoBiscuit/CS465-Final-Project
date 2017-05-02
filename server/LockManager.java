package server;

import java.util.*;

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
}
