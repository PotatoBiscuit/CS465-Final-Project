package server;
import comm.MessageTypes;
import java.util.ArrayList;

public class DataManager{	//Skeleton DataManager class
	private ArrayList<Account> accountList;
	private LockManager lockManager;
	
	public DataManager(){
		accountList = new ArrayList<Account>();
		for(int i = 0; i < 10; i++){
			accountList.add(new Account(10));
		}
		lockManager = new LockManager();
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
	public void display(){
		int i = 0;
		System.out.println("Accounts have the following balances:");
		for(Account account : accountList){
			System.out.println(i + ": has $" + account.readBalance());
			i++;
		}
	}
}