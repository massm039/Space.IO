package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread {

	Server master;
	Socket socket;
	
	ServerThread(Socket socket, Server master) {
		this.socket = socket;
		this.master = master;
	}
	
	//runs the server thread, accepting messages form a single client
	public void run() {
		try {
			String message;
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //input reader
			PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream())); //output writer
			
			System.out.println("Connection Established");
			
			out.println("hello client, welcome to the server");
			out.flush();
			
			while ((message = bufferedReader.readLine()) != null) {
				handleData(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//sends the data to the master class to be handled
	private synchronized void handleData(String data) {
		System.out.println("Handling Data");
		String[] datapoints = data.split(" ");
		master.handleInput(data, this);
	}
	
	
}
