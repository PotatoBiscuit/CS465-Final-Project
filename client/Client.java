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

public class Client extends Thread implements MessageTypes{
	String serverIP = null;
	int serverPort = 0;
	int transID = 0;
	
	public Client(String serverPropertiesFile){	//Gather server info connection data
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
		Integer drawAccount;
		Integer depositAccount;
		try { 
           		ClientAux clientInterface = new ClientAux(serverIP, serverPort);
			clientInterface.createTransaction();
			
			Random generator = new Random();	//Randomly generate an account to withdraw from
			drawAccount = new Integer(Math.abs(generator.nextInt() % 10));

			int withdrawal = Math.abs(generator.nextInt() % 25);
			int amount = clientInterface.Withdraw(withdrawal, drawAccount);

			while (true){
				depositAccount = new Integer(Math.abs(generator.nextInt() % 10));
				if (depositAccount.intValue() != drawAccount.intValue())
					break;		
			}
			clientInterface.Deposit(amount, depositAccount);

			clientInterface.closeTransaction();
			
		}catch (Exception ex) {
            		System.err.println("[PlusOneClient.run] Error occurred");
            		ex.printStackTrace();
        	}
	}
	
	public static void main(String args[]) throws InterruptedException{
		for(int i = 0; i < 10; i++){	//Create client threads that assault server with requests and messages
			if(args.length == 1){
				(new Client(args[0])).start();
			}else{
				System.err.println("No server.properties files given");
			}
			//TimeUnit.SECONDS.sleep(2);
		}
		TimeUnit.SECONDS.sleep(2);
		try{
			Client displayClient = new Client(args[0]);
			ClientAux displayClientAux = new ClientAux(displayClient.serverIP, displayClient.serverPort);
			displayClientAux.createTransaction();
			displayClientAux.display();
			displayClientAux.closeTransaction();
		}catch(IOException e){
			System.out.println("Error: " + e);
		}catch(ClassNotFoundException e){
			System.out.println("Error: " + e);
		}
	}
}
