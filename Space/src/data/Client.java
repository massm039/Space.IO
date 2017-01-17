package data;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread{
	
	//private ArrayList<Integer> removedIDs;
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
		//removedIDs = new ArrayList<Integer>();
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
		ClientReceiver clientReceiver = new ClientReceiver(br, this);
		clientReceiver.start();
	}
	
	//Handles data transactions with the server. Both sending and receiving
	public void run() {
		while (true) {
			try {
				synchronized (characters) {
					sendData(player.getCharacter());
				}
				synchronized (items) {
					for (Item i : items) {
						if (i.getOwnerID() == player.getCharacter().getID()) {
							sendData(i);
						}
					}
				}
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//handles new data coming in from the server
	//Thread Safe
	public synchronized void  handleData(String data) {
		//System.out.println(data);
		String[] datapoints = data.split(" ");
		switch (datapoints[0]) {
		case "ship":	
			Character dataChar = new Character(data, player, this);
			/*
			if (removedIDs.contains(dataChar.getID())) {
				break;
			}
			*/
			synchronized (characters) {
				Character toBeRemoved = null;
				for (Character i : characters) {
					//System.out.println("CHARACTER: makes it to the for loop");
					if (i.getID() == dataChar.getID()) {
						//System.out.println("matches IDs");
						toBeRemoved = i;
					}
				}
				if (toBeRemoved != null) {
					update(toBeRemoved, dataChar);
				}
				else {
					characters.add(dataChar);
				}
			}
			break;
		case "item":
			//System.out.println("matched with item");
			Item dataItem =  new Item(data, this);
			/*
			if (removedIDs.contains(dataItem.getID())) {
				break;
			}
			*/
			synchronized (items) {
				//System.out.println("ITEM: makes it to the for loop");
				Item toBeRemoved = null;
				for (Item i : items) {
					if (i.getID() == dataItem.getID()) {
						//System.out.println("matches IDs");
						toBeRemoved = i;
					}
				}
				if (toBeRemoved != null) {
					update(toBeRemoved, dataItem);
				}
				else {
					items.add(dataItem);
				}
			}
			break;
		case "collision":
			//System.out.println(data);
			datapoints = data.split("  ");
			String[] colliderOneData = datapoints[1].split(" ");
			String[] colliderTwoData = datapoints[2].split(" ");
			switch (colliderOneData[0]) {
			case "ship":
				Character dataCharacterOne = new Character(datapoints[1], player, this);
				switch (colliderTwoData[0]) {
				case "ship":
					Character dataCharacterTwo = new Character(datapoints[2], player, this);
					dataCharacterOne.handleCollision(dataCharacterTwo);
					update(dataCharacterOne);
					break;
				case "item":
					Item dataItemTwo = new Item(datapoints[2], this);
					dataCharacterOne.handleCollision(dataItemTwo);
					update(dataCharacterOne);
					break;
				}
				break;
			case "item":
				Item dataItemOne = new Item(datapoints[1], this);
				switch (colliderTwoData[0]) {
				case "ship":
					Character dataCharacterTwo = new Character(datapoints[2], player, this);
					dataItemOne.handleCollision(dataCharacterTwo);
					update(dataItemOne);
					break;
				case "item":
					Item dataItemTwo = new Item(datapoints[2], this);
					dataItemOne.handleCollision(dataItemTwo);
					update(dataItemOne);
					break;
				}
				break;
			}
			break;
		case "remove":
			System.out.println(data);
			switch (datapoints[1]) {
			case "character":
				Character toRemove = null;
				synchronized(characters) {
					for (Character i : characters) {
						if (i.getID() == Integer.parseInt(datapoints[2])) {
							toRemove = i;
							break;
						}
					}
					if (toRemove != null) {
						remove(toRemove);
					}
				}
				break;
			case "item":
				synchronized(items) {
					Item toBeRemoved = null;
					for (Item i : items) {
						if (i.getID() == Integer.parseInt(datapoints[2])) {
							toBeRemoved = i;
							break;
						}
					}
					if (toBeRemoved != null) {
						System.out.println("successfully removed");
						remove(toBeRemoved);
					}
				}
				break;
			}
			break;
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
		synchronized (characters) {
			for (Character i : characters) {
				if (i.isVisible()) {
					i.drawHealthBar();
				}
			}
		}
	}
	
	//Adds a player's ship to the list of ships in the game
	public void addCharacter(Character ship) {
		synchronized (characters) {
			characters.add(ship);
		}
	}
	
	//Adds an item to the list of items in the game
	public void addItem(Item item) {
		synchronized (items) {
			items.add(item);
		}
	}
	
	//Updates the given ship's location and statistics
	public void updateShip(Character ship) {
		synchronized(characters) {
			for (Character i : characters) {
				if (i.getID() == ship.getID()) {
					update(i, ship);
					break;
				}
			}
		}
	}
	
	private void update(Collidable i) {
		if (i instanceof Character) {
			Character toBeRemoved = null;
			synchronized(characters) {
				for (Character check : characters) {
					if (((Character)i).getID() == check.getID()) {
						toBeRemoved = check;
					}
				}
				if (toBeRemoved != null) {
					characters.add((Character) i);
					characters.remove(toBeRemoved);
				}
			}
		}
		if (i instanceof Item) {
			Item toBeRemoved = null;
			synchronized(items) {
				for (Item check : items) {
					if (((Item)i).getID() == check.getID()) {
						toBeRemoved = check;
					}
				}
				if (toBeRemoved != null) {
					items.add((Item) i);
					items.remove(toBeRemoved);
				}
			}
		}
	}
	
	//updates a Collidable, replacing it with the second argument
	private void update(Collidable i, Collidable j) {
		if (i instanceof Character && j instanceof Character) {
			characters.remove((Character)i);
			characters.add((Character)j);
		}
		if (i instanceof Item && j instanceof Item) {
			items.remove((Item)i);
			items.add((Item)j);
		}
	}
	
	//removes a Collidable from the client ONLY CALL WHEN THIS IS PERMENANT
	private void remove(Collidable i) {
		if (i instanceof Character) {
			synchronized(characters) {
				characters.remove((Character)i);
				//removedIDs.add(((Character)i).getID());
			}
		}
		if (i instanceof Item) {
			synchronized(items) {
				items.remove((Item)i);
				//removedIDs.add(((Item)i).getID());
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