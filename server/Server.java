package server;

import comm.Message;
import static comm.MessageTypes.READ_REQUEST;
import static comm.MessageTypes.WRITE_REQUEST;
import static comm.MessageTypes.OPEN_TRANS;
import static comm.MessageTypes.CLOSE_TRANS;
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

//Made by Erik Dixon and Michael Ortega
public class Server{
	static ServerSocket serverSocket = null;	//Server socket for conn to client
	TransactionManager transManager;			//Transaction Manager
	LockManager lockManager;					//Lock Manager
	
	public Server(String serverPropertiesFile){
		transManager = new TransactionManager();	//Initialize transaction manager
		try{
			// read server port from server properties file
			int serverPort = 0;
			BufferedReader serverFile = new BufferedReader(new FileReader(serverPropertiesFile));
			serverFile.readLine();
			serverPort = Integer.parseInt(serverFile.readLine().split(":")[1].trim());
			
			// create server socket
			serverSocket = new ServerSocket(serverPort);
		}catch(IOException e){
			System.err.println("Error: " + e);
		}
	}
	
	public void run(){
		while(true) {
			try {
				Socket clientSocket = serverSocket.accept();	//Connect to a client
				(new ServerThread(clientSocket)).start();		//Start thread to deal with client
			} catch(IOException e) {
				System.out.println("Failed to establish connection: " + e);
			}
		}
	}
	
	private class ServerThread extends Thread{
		Socket client = null;	//Socket of client
		ObjectInputStream readFromNet = null;	//Streams for reading and writing with client
        ObjectOutputStream writeToNet = null;
		Message message = null;					//Message container
		
		private ServerThread(Socket client){
			this.client = client;
		}
		
		@Override
		public void run(){
			try{	//Open input and output streams to client
				readFromNet = new ObjectInputStream(client.getInputStream());
				writeToNet = new ObjectOutputStream(client.getOutputStream());
				message = (Message) readFromNet.readObject();	//Read client message
			
				switch(message.getType()){
					case READ_REQUEST:	//If read request, perform read on the specified account
						System.out.println("Client " + ((Job) message.getContent()).getToolName() +
							" has sent a read request for account: " +
							((Integer) ((Job) message.getContent()).getParameters()).intValue());
						break;
					case WRITE_REQUEST:	//If write request, perform read on the specified account
						System.out.println("Client " + ((Job) message.getContent()).getToolName() + 
							" has sent a write request for account: " +
							((Integer) ((Job) message.getContent()).getParameters()).intValue());
						break;
					case OPEN_TRANS:	//If request to open transaction, give client a new Transaction ID
						Integer response = new Integer(transManager.createTransaction());
						System.out.println("A client has sent an open transaction request. ID: " + response.intValue());
						writeToNet.writeObject(response);
						break;
					case CLOSE_TRANS:	//If request to close transaction, remove transaction from server
						System.out.println("Client " + ((Job) message.getContent()).getToolName() +
							" has sent a close transaction request");
						transManager.closeTransaction(Integer.parseInt(((Job) message.getContent()).getToolName()));
						break;
				}
			}catch(IOException e){
				System.err.println("Error: " + e);
			}catch(ClassNotFoundException e){
				System.err.println("Error: " + e);
			}
		}
	}
	
	public class DataManager{	//Skeleton DataManager class
		
		public DataManager(){
			;
		}
	}
	
	public class LockManager{	//Skeleton LockManager class
		
	}
	
	public class TransactionManager{	//Partially filled-out Transaction Manager class
		ArrayList existingTrans;
		
		public TransactionManager(){	//Create list to hold transactions when TransactionManager started
			existingTrans = new ArrayList();
		}
		
		public synchronized int createTransaction(){	//Create transaction, add to transaction list
			Random idGen = new Random();
			int transID;
			while(true){	//Assign unique transaction ID
				transID = Math.abs(idGen.nextInt());
				if(!existingTrans.contains(transID)) break;
			}
			existingTrans.add(transID);
			return transID;	//Return transaction ID to later be returned to the client
		}
		
		public synchronized void closeTransaction(int transID){	//Close transaction, remove from list
			existingTrans.remove(Integer.valueOf(transID));
			//TO BE IMPLEMENTED, releasing locks
			//lockManager.release(transID)
		}
	}
	
	public static void main(String args[]){	//Start server with correct info, if no properties file given, show error
		if(args.length == 1){
			(new Server(args[0])).run();
		}else{
			System.err.println("No server.properties files given");
		}
	}
}