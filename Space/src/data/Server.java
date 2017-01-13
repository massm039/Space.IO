package data;

import java.util.ArrayList;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	public static final int PORT = 34956;
	private ArrayList<ServerThread> threads;
	
	public Server() {
		threads = new ArrayList<ServerThread>();
	}
	
	public void run(ServerSocket serverSocket) {
		Socket socket;
		try {
			socket = serverSocket.accept();
			ServerThread thread = new ServerThread(socket, this);
			threads.add(thread);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Handles input send from a particular client. It sends it to all other clients.
	public void handleInput(String data, ServerThread client) {
		for (ServerThread i : threads) {
			if (i != client) {
				sendData(data, i);
			}
		}
	}
	
	//Sends a given string to the given client
	private void sendData(String dataString, ServerThread receiverClient) {		
		try {		
			OutputStreamWriter os = new OutputStreamWriter(receiverClient.socket.getOutputStream());
			PrintWriter out = new PrintWriter(os);
			out.println(dataString);
			os.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//run this to run the Server
	public static void main (String args[]) {
		Server server = new Server();
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			while (true) {
				server.run(serverSocket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}