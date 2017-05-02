package server;

import comm.Message;
import static comm.MessageTypes.READ_REQUEST;
import static comm.MessageTypes.WRITE_REQUEST;
import static comm.MessageTypes.CREATE_TRANS;
import static comm.MessageTypes.CLOSE_TRANS;
import static comm.MessageTypes.DISPLAY;
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
	LockManager lockManager;
	DataManager dataManager;
	
	public TransactionManager(){
		lockManager = new LockManager();
		transactionList = new ArrayList<Transaction>();
		dataManager = new DataManager(lockManager);
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
		lockManager.unLock(transID);
	}
	
	public int read(int transID, int accountNum){
		return dataManager.readAccount(transID, accountNum);
	}
	
	public int write(int transID, int accountNum, int balance){
		return dataManager.writeAccount(transID, accountNum, balance);
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
		public ObjectInputStream readFromNet;
		public ObjectOutputStream writeToNet;
		public Message message;
		
		public Transaction(Socket clientSocket, int transID){
			this.clientSocket = clientSocket;
			this.transID = transID;
			
			try{
				readFromNet = new ObjectInputStream(clientSocket.getInputStream());
				writeToNet = new ObjectOutputStream(clientSocket.getOutputStream());
			}catch(IOException e){
				System.err.println("Error: " + e);
			}
		}
		
		public void run(){
			System.out.println("Trans: " + transID + " created!");
			try{	//Open input and output streams to client
				int balance = 0;
				while(true){
					message = (Message) readFromNet.readObject();	//Read client message
					switch(message.getType()){
						case READ_REQUEST:	//If read request, perform read on the specified account
							System.out.println("Client " + ((Job) message.getContent()).getToolName() +
								" has sent a read request for account: " +
								((Integer) ((Job) message.getContent()).getParameters()).intValue());
							balance = read(transID, ((Integer) ((Job) message.getContent()).getParameters()).intValue());
							writeToNet.writeObject(new Integer(balance));
							break;
						case WRITE_REQUEST:	//If write request, perform read on the specified account
							System.out.println("Client " + ((Job) message.getContent()).getToolName() + 
								" has sent a write request for account: " +
								((Integer) ((Job) message.getContent()).getParameters()).intValue());
							balance = write(transID, ((Integer) ((Job) message.getContent()).getParameters()).intValue(),
												((Integer) ((Job) message.getContent()).getParameters1()).intValue());
							writeToNet.writeObject(new Integer(balance));
							break;
						case CREATE_TRANS:	//If request to open transaction, give client a new Transaction ID
							Integer response = new Integer(transID);
							System.out.println("A client has sent an open transaction request. ID: " + response.intValue());
							writeToNet.writeObject(response);
							break;
						case CLOSE_TRANS:	//If request to close transaction, remove transaction from server
							System.out.println("Client " + ((Job) message.getContent()).getToolName() +
								" has sent a close transaction request");
							closeTransaction(Integer.parseInt(((Job) message.getContent()).getToolName()));
							return;
						case DISPLAY:
							System.out.println("Display request sent");
							dataManager.display();
					}
				}
			}catch(IOException e){
				System.err.println("Error: " + e);
			}catch(ClassNotFoundException e){
				System.err.println("Error: " + e);
			}
		}
	}
}