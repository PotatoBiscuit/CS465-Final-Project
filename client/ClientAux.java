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
	
	public ClientAux(String serverPropertiesFile){	//Gather server info connection data
		try{
			// read server port from server properties file
			BufferedReader serverFile = new BufferedReader(new FileReader(serverPropertiesFile));
			serverIP = serverFile.readLine().split(":")[1].trim();
			serverPort = Integer.parseInt(serverFile.readLine().split(":")[1].trim());
			
		}catch(IOException e){
			System.err.println("Error: " + e);
		}
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

	public int Withdrawal() throws IOException{
		//ACTUALLY REMOVING MONEY TO BE IMPLEMENTED

		//SEND READ REQUEST
		Random generator = new Random();
		Integer number = new Integer(Math.abs(generator.nextInt() % 10));
				
		// create job and job request message
		Message message = new Message(READ_REQUEST, new Job(Integer.toString(transID), number));
				
		// sending job out to the application server in a message
		writeToNet.writeObject(message);
		//--------------------------------------------
				
		//SEND WRITE REQUEST
		server = new Socket(serverIP, serverPort);
		writeToNet = new ObjectOutputStream(server.getOutputStream());
		readFromNet = new ObjectInputStream(server.getInputStream());
				
		// job request message
		message = new Message(WRITE_REQUEST, new Job(Integer.toString(transID), number));
				
		// sending job out to the application server in a message
		writeToNet.writeObject(message);

		return number;
	}

	public Deposit(int cash) throws IOException{
		Random generator = new Random();
		Integer number = new Integer(Math.abs(generator.nextInt() % 10));

		//SEND READ REQUEST
		Random generator = new Random();
		Integer number = new Integer(Math.abs(generator.nextInt() % 10));
				
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
