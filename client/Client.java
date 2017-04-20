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

public class Client extends Thread implements MessageTypes{
	String serverIP = null;
	int serverPort = 0;
	
	public Client(String serverPropertiesFile){
		try{
			// read server port from server properties file
			BufferedReader serverFile = new BufferedReader(new FileReader(serverPropertiesFile));
			serverIP = serverFile.readLine().split(":")[1].trim();
			serverPort = Integer.parseInt(serverFile.readLine().split(":")[1].trim());
			
		}catch(IOException e){
			System.err.println("Error: " + e);
		}
	}
	
	public void run(){
		try { 
            // connect to application server
            Socket server = new Socket(serverIP, serverPort);
			ObjectOutputStream writeToNet = new ObjectOutputStream(server.getOutputStream());
			ObjectInputStream readFromNet = new ObjectInputStream(server.getInputStream());
			
			Random generator = new Random();
			Integer number = new Integer(Math.abs(generator.nextInt() % 10));
            
            // create job and job request message
            Job job = new Job("", number);
            Message message = new Message(READ_REQUEST, job);
            
            // sending job out to the application server in a message
            writeToNet.writeObject(message);
			
			server = new Socket(serverIP, serverPort);
			writeToNet = new ObjectOutputStream(server.getOutputStream());
			readFromNet = new ObjectInputStream(server.getInputStream());
            
            // job request message
            message = new Message(WRITE_REQUEST, job);
            
            // sending job out to the application server in a message
            writeToNet.writeObject(message);
			
		}catch (Exception ex) {
            System.err.println("[PlusOneClient.run] Error occurred");
            ex.printStackTrace();
        }
	}
	
	public static void main(String args[]){
		for(int i = 0; i < 20; i++){
			if(args.length == 1){
				(new Client(args[0])).start();
			}else{
				System.err.println("No server.properties files given");
			}
		}
	}
}