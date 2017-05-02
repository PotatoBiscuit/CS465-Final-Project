package server;
import java.util.Hashtable;

public class LockManager{
	private Hashtable allLocks;
	
	public synchronized void setLock(int accountNum, int transID, int lockType){
		Lock foundLock;
		if((foundLock = allLocks.get(accountNum)) == null){
			foundLock = new Lock();
			allLocks.put(accountNum, foundLock);
		}
		foundLock.acquire(transID, lockType);
	}
	
	public synchronized void unLock(int transID){
		Enumeration e = allLocks.elements();
		while(e.hasMoreElements()){
			Lock tempLock = (Lock) e.nextElement();
			if(tempLock.holds(transID)) tempLock.release(transID);
		}
	}
	//Finish Lock class, and holds() acquire() and release() methods
}