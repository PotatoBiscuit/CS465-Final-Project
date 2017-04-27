package server;
import comm.MessageTypes;
import java.util.ArrayList;

public class DataManager{	//Skeleton DataManager class
	private ArrayList<Account> accountList;
	private LockManager lockManager;
	
	public DataManager(LockManager newLockManager){
		for(int i = 0; i < 10; i++){
			accountList.add(new Account(10));
		}
		lockManager = newLockManager;
	}
		
	public int readAccount(int transID, int accountNum){
		//lockManager.lock(transID, accountNum, READ_LOCK);
		return accountList.get(accountNum).readBalance();
	}
	public int writeAccount(int transID, int accountNum, int balance){
		//lockManager.lock(transID, accountNum, WRITE_LOCK);
		Account accountToWrite = accountList.get(accountNum);
		accountToWrite.writeBalance(balance);
		return accountToWrite.readBalance();
	}
}