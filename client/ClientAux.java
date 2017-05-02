package client;

import comm.Message;
import comm.MessageTypes;
import job.Job;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.Socket;
import java.util.Random;
import java.lang.Math;
import java.util.concurrent.TimeUnit;

public class ClientAux extends Thread implements MessageTypes{
	String serverIP = null;
	int serverPort = 0;
	int transID = 0;
	Socket server;
	ObjectOutputStream writeToNet;
	ObjectInputStream readFromNet;
	
	public ClientAux(String IP, int port){	//Gather server info connection data
		serverIP = IP;
		serverPort = port;
	}

	public void createTransaction() throws IOException, ClassNotFoundException{	//Creates a transaction
		server = new Socket(serverIP, serverPort);	//Setup the server connection and object streams
		writeToNet = new ObjectOutputStream(server.getOutputStream());
		readFromNet = new ObjectInputStream(server.getInputStream());
			
		Message message = new Message(CREATE_TRANS, new Job("", null));	//Make a message to create the transaction
			
		writeToNet.writeObject(message);	//Send the create message out to the server
		Integer transaction = (Integer) readFromNet.readObject();	//Read in the transaction ID replied
		transID = transaction.intValue();							//Store it in global transID
		System.out.println("I received a Transaction ID of: " + transID);
	}

	public void display() throws IOException{	//Displays all accounts on server
		Message message = new Message(DISPLAY, null);	//Create display message
		writeToNet.writeObject(message);				//Send message
		System.out.println("Display request sent to server.");
	}

	public void closeTransaction() throws IOException{
		Message message = new Message(CLOSE_TRANS, new Job(Integer.toString(transID), null));	//Make a message to close the transaction
		writeToNet.writeObject(message);	//Send the closing message to the server
		System.out.println("Closed transaction: " + transID);
	}

	public int Withdraw(int amount, Integer account) throws IOException, ClassNotFoundException{	//Withdraw from specified account
		int balance = 0;
				
		//Create job and read request message, specifying account number
		Message message = new Message(READ_REQUEST, new Job(Integer.toString(transID), account));
				
		writeToNet.writeObject(message);	//Sending message out to the application server
		balance = ((Integer) readFromNet.readObject()).intValue();	//receive message back from server and store balance
		//--------------------------------------------
		System.out.println("Balance in account " + account.intValue() + " before withdraw: " + balance + " (" + transID + ")");
		balance = balance - amount;	//Perform withdraw
				
		//Create write request message, specifying account number and new balance
		message = new Message(WRITE_REQUEST, new Job(Integer.toString(transID), account, new Integer(balance)));
				
		writeToNet.writeObject(message);	//Sending message out to the application server
		balance = ((Integer) readFromNet.readObject()).intValue();	//receive message back from server and store balance
		System.out.println("Balance in account " + account.intValue() + " after withdraw: " + balance + " (" + transID + ")");
		return amount;	//Return current balance
	}

	public void Deposit(int amount, Integer account) throws IOException, ClassNotFoundException{	//Deposit to specified account
		int balance = 0;
				
		//Create job and read request message, specifying account number
		Message message = new Message(READ_REQUEST, new Job(Integer.toString(transID), account));
				
		writeToNet.writeObject(message);	//Sending message out to the application server
		balance = ((Integer) readFromNet.readObject()).intValue();	//receive message back from server and store balance
		//--------------------------------------------
		System.out.println("Balance in account " + account.intValue() + " before deposit: " + balance + " (" + transID + ")");
		balance = balance + amount;		//Perform deposit
				
		//Create write request message, specifying account number and new balance
		message = new Message(WRITE_REQUEST, new Job(Integer.toString(transID), account, new Integer(balance)));
				
		writeToNet.writeObject(message);	//Sending message out to the application server
		balance = ((Integer) readFromNet.readObject()).intValue();	//receive message back from server and store balance
		System.out.println("Balance in account " + account.intValue() + " after deposit: " + balance + " (" + transID + ")");
	}
}
