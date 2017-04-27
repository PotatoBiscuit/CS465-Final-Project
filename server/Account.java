package server;

public class Account{
	private int balance;
	
	public Account(int newBalance){
		balance = newBalance;
	}
	
	public int readBalance(){
		return balance;
	}
	
	public int writeBalance(int newBalance){
		balance = newBalance;
	}
}