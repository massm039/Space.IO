package data;

import java.io.BufferedReader;

public class ClientReceiver extends Thread{
	
	BufferedReader br;
	Client client;
	
	public ClientReceiver (BufferedReader br, Client client) {
		this.br = br;
		this.client = client;
	}

	
	//reads lines from the server and runs handleData from the client class on the received data
	public void run() {
		//read from server
		try {
			String message;
			while ((message = br.readLine()) != null) {
				client.handleData(message);
				//System.out.println(message);
				//Thread.sleep(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
