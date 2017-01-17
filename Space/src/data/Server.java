package data;

import java.util.ArrayList;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import data.Item;
import data.ItemType;
import data.Character;
import data.Collidable;
import helpers.Artist;
import helpers.Clock;

public class Server extends Thread{
	
	public static final int PORT = 34956;
	private ArrayList<ServerThread> threads;
	private ServerSocket serverSocket;
	
	public final int maxAsteroids;
	public int numAsteroids; 
	private ArrayList<Item> items;
	private ArrayList<Character> characters;
	
	//constructor for a new Server
	public Server() {
		threads = new ArrayList<ServerThread>();
		items = new ArrayList<Item>();
		characters = new ArrayList<Character>();
		maxAsteroids = 10;
		numAsteroids = 0;
	}
	
	//runnable method that creates ServerThread objects, storing them in threads
	public void run() {
		while (true) {
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
	}
	
	//broadcasts a message from the Server to all Clients.
	public void broadcast(String data) {
		//System.out.println("Broadcasting: " + data);
		handleInput(data, null);
	}
	
	//Handles input send from a particular client. It sends it to all other clients.
	public void handleInput(String data, ServerThread client) {
		for (ServerThread i : threads) {
			if (i != client) {
				sendData(data, i);
			}
		}
	}
	
	//simulates a client on the server, handling data that passes through the server
	public synchronized void  handleData(String data) {
		//System.out.println(data);
		String[] datapoints = data.split(" ");
		switch (datapoints[0]) {
		case "ship":
			Character dataChar = new Character(data, null, null);
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
					characters.remove(toBeRemoved);
				}
				characters.add(dataChar);
			}
		break;
		case "item":
			Item dataItem =  new Item(data, null);
			//System.out.println("ITEM: makes it to the for loop");
			Item toBeRemoved = null;
			synchronized (items) {
				for (Item i : items) {
					if (i.getID() == dataItem.getID()) {
						//System.out.println("matches IDs");
						toBeRemoved = i;
					}
				}
				if (toBeRemoved != null) {
					items.remove(toBeRemoved);
				}
				items.add(dataItem);
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
						characters.remove(toRemove);
					}
				}
				break;
			case "item":
				synchronized(items) {
					Item itemToBeRemoved = null;
					for (Item i : items) {
						if (i.getID() == Integer.parseInt(datapoints[2])) {
							itemToBeRemoved = i;
							break;
						}
					}
					if (itemToBeRemoved != null) {
						System.out.println("successfully removed");
						items.remove(itemToBeRemoved);
					}
				}
				break;
			}
		break;
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
	
	//generates asteroids so that maxAsteroids asteroids are in they ArrayList items at all times.
	private void tryGenerateAsteroids() {
		//asteroid generation
		while (numAsteroids < maxAsteroids) {
			//public Item(int x, int y, double direction, int lifespan, ItemType type, int ownerID) {
			int x = (int)(Math.random()*Artist.WIDTH);
			int y = (int)(Math.random()*Artist.HEIGHT);
			double direction = (Math.random()*360);
			int lifespan = 100;
			int sign = 1;
			if (Math.random() < .5) {
				sign = -1;
			}
			double rotVel = 3*(.5 + Math.random()) * sign;
			ItemType type = ItemType.Asteroid;
			int ownerID = 0;
			//server items have client == null
			Item asteroid = new Item(x, y, direction, lifespan, type, ownerID, rotVel, null);
			items.add(asteroid);
			//increment numAsteroids with every additional asteroid placed into items
			numAsteroids++;
		}
	}
	
	private void updateServerItems() {
		ArrayList<Item> toBeRemoved = new ArrayList<Item>();
		synchronized(items) {
			for (Item i : items) {
				if (i.getOwnerID() == 0) {
					if (i.update()) {
						broadcast(i.toString());
					}
					else {
						//System.out.println("removing");
						toBeRemoved.add(i);
					}
				}
			}
			for (Item i : toBeRemoved) {
				sendRemovalMessage(i);
				items.remove(i);
				numAsteroids--;
			}
			for (Item i : items) {
				System.out.println(i.name + " " + i.getID());
			}
		}
	}
	
	private void sendRemovalMessage(Collidable i) {
		//System.out.println("removal message");
		String message = "remove ";
		if (i instanceof Item) {
			message += "item " + ((Item) i).getID();
		}
		else if (i instanceof Character) {
			message += "character " + ((Character) i).getID();
		}
		broadcast(message);
	}
	
	private void collisionMessage(Collidable i, Collidable j) {
		String message = "collision  " + i.toString() + "  " + j.toString();
		//System.out.println(message);
		broadcast(message);
	}
	
	//checks collisions with all server objects
	private void checkCollisions() {
		synchronized(items) {
			synchronized (characters) {
				for (Item item : items) {
					for (Character j : characters) {
						if (item.collision(j) && item.getOwnerID() != j.getID()) {
							collisionMessage(item, j);
							if (item.getOwnerID() == 0) {
								item.handleCollision(j);
							}
						}
					}
					for (Item j : items) {
						if (item.collision(j) && item.getID() != j.getID()) {
							collisionMessage(item, j);
							if (item.getOwnerID() == 0) {
								item.handleCollision(j);
							}
							if (j.getOwnerID() == 0) {
								j.handleCollision(item);
							}
						}
					}
				}
				for (Character i : characters) {
					for (Character j : characters) {
						if (i.collision(j) && i.getID() != j.getID()) {
							collisionMessage(i, j);
						}
					}
					for (Item j : items) {
						if (i.collision(j) && i.getID() != j.getOwnerID()) {
							collisionMessage(i, j);
							if (j.getOwnerID() == 0) {
								j.handleCollision(i);
							}
						}
					}
				}
			}
		}
	}
	
	//simulates a client in order to view the server's current state
	private void drawAll() {
		System.out.println("drawing?");
		//Draws all of the client's Items
		synchronized(items) {
			for (Item i : items) {
				i.Draw();
			}
		}
		//Draws all of the client's characters
		synchronized(characters) {
			for (Character c : characters) {
				c.Draw();
			}
		}
	}
	
	//run this to run the Server
	public static void main (String args[]) {
		Server server = new Server();
		Clock.update();
		try {
			server.serverSocket = new ServerSocket(PORT);
			server.start();
			Artist.BeginSession();
			while (true) {
				Clock.update();
				server.tryGenerateAsteroids();
				//server.tryGeneratePowerups(); //(TODO: maybe do this?)
				server.updateServerItems();
				server.checkCollisions();
				Thread.sleep(100);
				
				//server.drawAll();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}