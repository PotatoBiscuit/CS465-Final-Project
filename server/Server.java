package server;

import comm.Message;
import static comm.MessageTypes.READ_REQUEST;
import static comm.MessageTypes.WRITE_REQUEST;
import static comm.MessageTypes.CREATE_TRANS;
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
	TransactionManager transManager;		//Transaction Manager
	
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
				transManager.createTransaction(clientSocket);		//Start transaction to deal with client
			} catch(IOException e) {
				System.out.println("Failed to establish connection: " + e);
			}
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
