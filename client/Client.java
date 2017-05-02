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
			// read server port and IP from server properties file
			BufferedReader serverFile = new BufferedReader(new FileReader(serverPropertiesFile));
			serverIP = serverFile.readLine().split(":")[1].trim();
			serverPort = Integer.parseInt(serverFile.readLine().split(":")[1].trim());
			
		}catch(IOException e){
			System.err.println("Error: " + e);
		}
	}
	
	public void run(){
		Integer drawAccount;	//Account numbers to be withdrawn from and deposited to
		Integer depositAccount;
		try { 
			//Create ClientAux object to perform open trans, withdraw, deposit, close trans
           	ClientAux clientInterface = new ClientAux(serverIP, serverPort);
			clientInterface.createTransaction();	//Create transaction
			
			Random generator = new Random();	//Randomly generate an account to withdraw from
			drawAccount = new Integer(Math.abs(generator.nextInt() % 10));	//Number from 0-9

			int withdrawal = Math.abs(generator.nextInt() % 25);			//Number from 0-24
			//Withdraw randomly selected amount from randomly selected account
			int amount = clientInterface.Withdraw(withdrawal, drawAccount);

			while (true){	//Get the account number to deposit to, cannot be the same as account withdrawn from
				depositAccount = new Integer(Math.abs(generator.nextInt() % 10));
				if (depositAccount.intValue() != drawAccount.intValue())
					break;		
			}
			clientInterface.Deposit(amount, depositAccount);	//Deposit amount to selected deposit account

			clientInterface.closeTransaction();					//Close transaction
			
		}catch (Exception ex) {
            		System.err.println("[PlusOneClient.run] Error occurred");
            		ex.printStackTrace();
        	}
	}
	
	public static void main(String args[]) throws InterruptedException{
		if(args.length < 2){
			System.err.println("Incorrect number of arguments");
			return;
		} 
		for(int i = 0; i < Integer.parseInt(args[1]); i++){	//Create client threads that assault server with requests and messages
			(new Client(args[0])).start();					//Based on user input for number of client threads
			//TimeUnit.SECONDS.sleep(2);
		}
		TimeUnit.SECONDS.sleep(5);
		try{	//Create a special client to ask server to display account info
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
