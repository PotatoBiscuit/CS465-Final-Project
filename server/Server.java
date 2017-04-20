package server;

import comm.Message;
import static comm.MessageTypes.READ_REQUEST;
import static comm.MessageTypes.WRITE_REQUEST;
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
import job.Job;

//Made by Erik Dixon and Michael Ortega
public class Server{
	static ServerSocket serverSocket = null;
	
	public Server(String serverPropertiesFile){
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
				Socket clientSocket = serverSocket.accept();
				(new ServerThread(clientSocket)).start();
			} catch(IOException e) {
				System.out.println("Failed to establish connection: " + e);
			}
		}
	}
	
	private class ServerThread extends Thread{
		Socket client = null;
		ObjectInputStream readFromNet = null;
        ObjectOutputStream writeToNet = null;
		Message message = null;
		
		private ServerThread(Socket client){
			this.client = client;
		}
		
		@Override
		public void run(){
			try{
				readFromNet = new ObjectInputStream(client.getInputStream());
				writeToNet = new ObjectOutputStream(client.getOutputStream());
				message = (Message) readFromNet.readObject();
			}catch(IOException e){
				System.err.println("Error: " + e);
			}catch(ClassNotFoundException e){
				System.err.println("Error: " + e);
			}
			
			switch(message.getType()){
				case READ_REQUEST:
					System.out.println("A client has sent a read request for account: " +
						((Integer) ((Job) message.getContent()).getParameters()).intValue());
					break;
				case WRITE_REQUEST:
					System.out.println("A client has sent a write request for account: " +
						((Integer) ((Job) message.getContent()).getParameters()).intValue());
					break;
			}
		}
	}
	
	public static void main(String args[]){
		if(args.length == 1){
			(new Server(args[0])).run();
		}else{
			System.err.println("No server.properties files given");
		}
	}
}