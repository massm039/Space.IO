package data;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {
	
	private ArrayList<Item> items;
	private ArrayList<Character> characters;
	private Player player;
	
	public static final int PORT = 34956;
	
	private Socket s;
	private OutputStreamWriter os;
	private PrintWriter out; //output channel to server. :: out.println();
	private BufferedReader br; //input channel form server. :: br.readLine();
	
	//Constructor
	public Client(String ip){
		items = new ArrayList<Item>();
		characters = new ArrayList<Character>();
		try{
			s = new Socket(ip, PORT);
			os = new OutputStreamWriter(s.getOutputStream());
			out = new PrintWriter(os);
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		}
		catch (Exception e) {
			//System.out.println("Port is in use, try another port.");
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		while (true) {
			String data;
			try {
				//read from server
				data = br.readLine();
				handleData(data);
				System.out.println(data);
				//send to server
				synchronized (this) {
					for (Character i : characters) {
						if (i != null)
							sendData(i);
					}
					for (Item i : items) {
						if (i != null)
							sendData(i);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//handles new data coming in from the server, run this in another thread? TODO
	private void  handleData(String data) {
		System.out.println(data);
		String[] datapoints = data.split(" ");
		if (datapoints[0].equals("ship")) {
			Character dataChar = new Character(data, player, this);
			for (Character i : characters) {
				if (i.getID() == dataChar.getID()) {
					i = dataChar;
				}
				else {
					characters.add(dataChar);
				}
			}
		}
		else if (datapoints[0].equals("item")) {
			Item dataItem =  new Item(data);
			for (Item i : items) {
				if (i.getName().equals(dataItem.getName())) {
					i = dataItem;
				}
				else {
					items.add(dataItem);
				}
			}
		}
	}
	
	//Sends data on an object to the server process
	private void sendData(Object obj) {
		try {
			out.println(obj.toString());
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Draw Functions
	public void drawHealthBars(){
		for (Character i : characters) {
			if (i.isVisible()) {
				i.drawHealthBar();
			}
		}
	}
	
	//Adds a player's ship to the list of ships in the game
	public void addPlayer(Character ship) {
		characters.add(ship);
	}
	
	//Adds an item to the list of items in the game
	public void addItem(Item item) {
		items.add(item);
	}
	
	//Updates the given ship's location and statistics
	public void updateShip(Character ship) {
		for (Character i : characters) {
			if (i.getID() == ship.getID()) {
				characters.remove(i);
				characters.add(ship);
				break;
			}
		}
	}
	
	//Getters and Setters
	//-------------------
	
	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public ArrayList<Character> getCharacters() {
		return characters;
	}

	public void setCharacters(ArrayList<Character> characters) {
		this.characters = characters;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	//--------------------

}