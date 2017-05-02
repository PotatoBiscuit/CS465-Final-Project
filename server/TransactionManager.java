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
	ArrayList<Transaction> transactionList;	//Holds list of all transactions
	LockManager lockManager;				//Holds lock manager
	DataManager dataManager;				//Holds data manager
	
	public TransactionManager(){
		lockManager = new LockManager();	//Create new lock manager
		transactionList = new ArrayList<Transaction>();	//Create new transaction array
		dataManager = new DataManager(lockManager);		//Create data manager, pass in lock manager
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
		newTransaction.start();	//Run transaction thread
		return transID;	//Return transaction ID to later be returned to the client
	}
	
	public synchronized void closeTransaction(int transID){	//Close transaction, remove from list
		transactionList.remove(findIndexById(transID));
		lockManager.unLock(transID);
	}
	
	public int read(int transID, int accountNum){	//Use data manager to read from specified account
		return dataManager.readAccount(transID, accountNum);
	}
	
	public int write(int transID, int accountNum, int balance){	//Use data manager to write to specified account
		return dataManager.writeAccount(transID, accountNum, balance);
	}
	
	public int findIndexById(int transID){	//Find transaction in transaction array (by ID)
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
		public Socket clientSocket;		//Holds socket to client thread
		public int transID;				//Holds transaction ID
		public ObjectInputStream readFromNet;	//Read stream from client
		public ObjectOutputStream writeToNet;	//Write stream to client
		public Message message;					//Holds current message to send to client
		
		public Transaction(Socket clientSocket, int transID){	//Create transaction, store transaction ID, and client thread socket
			this.clientSocket = clientSocket;
			this.transID = transID;
			
			try{
				readFromNet = new ObjectInputStream(clientSocket.getInputStream());	//Create input/output streams
				writeToNet = new ObjectOutputStream(clientSocket.getOutputStream());
			}catch(IOException e){
				System.err.println("Error: " + e);
			}
		}
		
		public void run(){
			System.out.println("Trans: " + transID + " created!");
			try{
				int balance = 0;
				while(true){
					message = (Message) readFromNet.readObject();	//Read client message
					switch(message.getType()){
						case READ_REQUEST:	//If read request, perform read on the specified account
							System.out.println("Client " + ((Job) message.getContent()).getToolName() +
								" has sent a read request for account: " +
								((Integer) ((Job) message.getContent()).getParameters()).intValue());
							balance = read(transID, ((Integer) ((Job) message.getContent()).getParameters()).intValue());
							writeToNet.writeObject(new Integer(balance));	//Return balance read
							break;
						case WRITE_REQUEST:	//If write request, perform write on the specified account, with specified balance
							System.out.println("Client " + ((Job) message.getContent()).getToolName() + 
								" has sent a write request for account: " +
								((Integer) ((Job) message.getContent()).getParameters()).intValue());
							balance = write(transID, ((Integer) ((Job) message.getContent()).getParameters()).intValue(),
												((Integer) ((Job) message.getContent()).getParameters1()).intValue());
							writeToNet.writeObject(new Integer(balance));	//Return new balance
							break;
						case CREATE_TRANS:	//If request to open transaction, give client a new Transaction ID
							Integer response = new Integer(transID);
							System.out.println("A client has sent an open transaction request. ID: " + response.intValue());
							writeToNet.writeObject(response);
							break;
						case CLOSE_TRANS:	//If request to close transaction, remove transaction from trans manager, quit thread
							System.out.println("Client " + ((Job) message.getContent()).getToolName() +
								" has sent a close transaction request");
							closeTransaction(Integer.parseInt(((Job) message.getContent()).getToolName()));
							return;
						case DISPLAY:		//If request is display, display all account information on server
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