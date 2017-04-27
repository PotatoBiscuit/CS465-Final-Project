package server;

import comm.Message;
import comm.MessageTypes;
import comm.ConnectivityInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.String;
import java.lang.Integer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import job.Job;
import java.util.Random;
import java.lang.Math;

public class TransactionManager{
	ArrayList<Transaction> transactionList;
	
	public TransactionManager(){
		transactionList = new ArrayList<Transaction>();
	}
	
	public synchronized int createTransaction(Socket client){	//Create transaction, add to transaction list
		Random idGen = new Random();
		int transID;
		while(true){	//Assign unique transaction ID
			transID = Math.abs(idGen.nextInt());
			if(findIndexById(transID) == -1) break;
		}
		Transaction newTransaction = new Transaction(client, transID);
		transactionList.add(newTransaction);
		newTransaction.start();
		return transID;	//Return transaction ID to later be returned to the client
	}
	
	public synchronized void closeTransaction(int transID){	//Close transaction, remove from list
		transactionList.remove(findIndexById(transID));
		//TO BE IMPLEMENTED, releasing locks
		//lockManager.release(transID)
	}
	
	public int findIndexById(int transID){
		int index = 0;
		for(Transaction temp : transactionList){
			if(temp.transID == transID){
				return index;
			}
			index++;
		}
		return -1;
	}
	
	private class Transaction extends Thread{
		public Socket clientSocket;
		public int transID;
		
		public Transaction(Socket client, int ID){
			clientSocket = client;
			transID = ID;
		}
		
		public void run(){
			System.out.println("Transaction " + transID + " created");
		}
	}
}