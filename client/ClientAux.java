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

	public void openTransaction() throws IOException, ClassNotFoundException{
		server = new Socket(serverIP, serverPort);
		writeToNet = new ObjectOutputStream(server.getOutputStream());
		readFromNet = new ObjectInputStream(server.getInputStream());
			
		Message message = new Message(CREATE_TRANS, new Job("", null));
			
		writeToNet.writeObject(message);
		Integer transaction = (Integer) readFromNet.readObject();
		transID = transaction.intValue();
		System.out.println("I received a Transaction ID of: " + transID);
	}

	public void closeTransaction() throws IOException{
		Message message = new Message(CLOSE_TRANS, new Job(Integer.toString(transID), null));
		writeToNet.writeObject(message);
	}

	public int withdraw(int amount) throws IOException, ClassNotFoundException{
		//ACTUALLY REMOVING MONEY TO BE IMPLEMENTED
		int balance = 0;
		//SEND READ REQUEST
		Random generator = new Random();
		Integer number = new Integer(Math.abs(generator.nextInt() % 10));
				
		// create job and job request message
		Message message = new Message(READ_REQUEST, new Job(Integer.toString(transID), number));
				
		// sending job out to the application server in a message
		writeToNet.writeObject(message);
		balance = ((Integer) readFromNet.readObject()).intValue();
		//--------------------------------------------
		System.out.println("Balance before withdraw: " + balance);
		balance = balance - amount;
				
		// job request message
		message = new Message(WRITE_REQUEST, new Job(Integer.toString(transID), number, new Integer(balance)));
				
		// sending job out to the application server in a message
		writeToNet.writeObject(message);
		balance = ((Integer) readFromNet.readObject()).intValue();
		System.out.println("Balance after withdraw: " + balance);
		return balance;
	}

	public void Deposit(int amount) throws IOException{
		Random generator = new Random();
		Integer number = new Integer(Math.abs(generator.nextInt() % 10));

		//SEND READ REQUEST
		generator = new Random();
		number = new Integer(Math.abs(generator.nextInt() % 10));
				
		// create job and job request message
		Message message = new Message(READ_REQUEST, new Job(Integer.toString(transID), number));
				
		// sending job out to the application server in a message
		writeToNet.writeObject(message);

		// ADD CASH TO AMOUNT READ THEN WRITE IT IN
		//--------------------------------------------
				
		//SEND WRITE REQUEST
		server = new Socket(serverIP, serverPort);
		writeToNet = new ObjectOutputStream(server.getOutputStream());
		readFromNet = new ObjectInputStream(server.getInputStream());
				
		// job request message
		message = new Message(WRITE_REQUEST, new Job(Integer.toString(transID), number));
				
		// sending job out to the application server in a message
		writeToNet.writeObject(message);
	}
}
