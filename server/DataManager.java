package server;
import comm.MessageTypes;
import java.util.ArrayList;
import static comm.MessageTypes.READ_LOCK;
import static comm.MessageTypes.WRITE_LOCK;

public class DataManager{
	private ArrayList<Account> accountList;	//An array of all accounts
	private LockManager lockManager;		//Holds lock manager
	
	public DataManager(LockManager lockManager){	//Create new array of accounts, store lock manager
		accountList = new ArrayList<Account>();
		for(int i = 0; i < 10; i++){
			accountList.add(new Account(10));
		}
		this.lockManager = lockManager;
	}
		
	public int readAccount(int transID, int accountNum){	//Obtain READ_LOCK, read from account
		lockManager.setLock(accountNum, transID, READ_LOCK);
		return accountList.get(accountNum).readBalance();
	}
	public int writeAccount(int transID, int accountNum, int balance){	//Obtain WRITE_LOCK, write new balance to account
		lockManager.setLock(accountNum, transID, WRITE_LOCK);
		Account accountToWrite = accountList.get(accountNum);
		accountToWrite.writeBalance(balance);
		return accountToWrite.readBalance();
	}
	public void display(){	//Display all account information
		int i = 0;
		System.out.println("Accounts have the following balances:");
		for(Account account : accountList){
			System.out.println(i + ": has $" + account.readBalance());
			i++;
		}
		lockManager.deadlockDisplay();
	}
}