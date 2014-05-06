import gc.common_resources.CommandType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class MyServer {

	// Server UI object
	private static ServerUI serverui;
	
	//Static variables for connectionType
	private static final int WIFICONNECTION = 1;
	private static final int BTCONNECTION = 2;

	private WifiSendSocketThread wifiSThread = null;
	//private WifiSendSocketThread wifiRThread = null;
	
	// Determines which connection type: wifi, bluetooth, usb
	// Wifi Port Number
	private static final int WifiPORT = 8888;
	private static String clientIPAddress = "";
	
	// Bluetooth variables
	private final UUID BTUUID = new UUID("1101", true);
	private final String BTconnectionString = "btspp://localhost:" + BTUUID +";name=Sample SPP Server";

	private LocalDevice BTlocal = null;
	private StreamConnectionNotifier BTserver = null;
	private StreamConnection BTconn = null;
	
	//private BTSendSocketThread BTSThread = null;
	private BTReceiveSocketThread BTRThread = null;
	private WifiReceiveSocketThread WifiRThread = null;
	KeyTouch keyTouch = null;
	
	public static void main(String[] args) throws UnknownHostException {
		serverui = new ServerUI();
		serverui.createServerUI();
		MyServer myServer= new MyServer();
		myServer.startConnection(0);
	}
	
	/**
	 * checks for connection type (Wifi or Bluetooth) and start a connection
	 * @param connectionType
	 * @throws UnknownHostException
	 */
	public void startConnection(int connectionType) throws UnknownHostException {
		// TODO Auto-generated method stub
		while(true) {
			//get connectiontype from serverui here
			
			connectionType= serverui.getUserChoice();
			// Print this variable to ensure the value is set properly
			System.out.println(connectionType);
			switch(connectionType) {
				case WIFICONNECTION:
					// Wifi Connection
					
					//Start the listening thread here
					WifiRThread = new WifiReceiveSocketThread(WifiPORT);
					WifiRThread.start();
					
					keyTouch = new KeyTouch();
					while (connectionType == WIFICONNECTION) {
						if(WifiRThread.newClientConnected()) {
							clientIPAddress = new String(WifiRThread.getClientIP());
						}
						
						if(WifiRThread.receivedWifiCommand()) {

							CommandType commandFromClient = CommandType.DEFAULT; 
							
							commandFromClient = WifiRThread.getWifiCommand();
							
							switch(commandFromClient) {
							case INIT:
								//First message from the client
							break;
							case CONFIG: 
								//The client is requesting for config info
								//Send packet to client to notify of keymapping
								sendKeymapListToClient();
								break;
							case SCREENSHOT:
								break;
							default:
								keyTouch.identifyKey(commandFromClient, new ArrayList<String>(serverui.getKeyMappings()), serverui.getMouseRatio());
				
							break;				
							}
							WifiRThread.currCommandProcessed();
						} else {
							if(serverui.isKeyMapUpdated()){
								sendKeymapListToClient();
								serverui.setKeyMapNotUpdated();
							}
						}
					}
				//End Wifi connection
			break;
			case BTCONNECTION:
					//Bluetooth Connection
					try {
						System.out.println("Setting device to be discoverable...");
			           
						BTlocal = LocalDevice.getLocalDevice();
						
						if(BTlocal.getDiscoverable() != DiscoveryAgent.GIAC)
							BTlocal.setDiscoverable(DiscoveryAgent.GIAC);
			            
			            System.out.println("Start advertising service...");

			            
		            }catch (Exception  e) {
						 System.out.println("Exception Occured: " + e.toString());
					 }

					keyTouch = new KeyTouch();
					try {

			            BTserver = (StreamConnectionNotifier)Connector.open(BTconnectionString);
			            
			            System.out.println("Waiting for incoming connection...");
			            
			            BTconn = BTserver.acceptAndOpen();
			            			            
			            System.out.println("Client Connected...");
				            
			            //BTdin = new DataInputStream(BTconn.openInputStream());
			            ObjectOutputStream BTOOStream = new ObjectOutputStream(BTconn.openOutputStream());
			            BTOOStream.flush();
			            ObjectInputStream BTOIStream = new ObjectInputStream(BTconn.openInputStream());
			            	
			            System.out.println("Input stream is set up.");
						serverui.updateStatus(true);						

						//Start Listening thread for packets from client
						BTRThread = new BTReceiveSocketThread(BTOIStream);
						BTRThread.start();				
						
						//Test take a screenshot
						//takeScreenshot();
						
						CommandType commandFromClient = CommandType.DEFAULT; 

						while(connectionType == BTCONNECTION && BTRThread.isAlive()) {
							
							//If receive packets from client, process it
							if(BTRThread.receivedBTCommand()) {
								synchronized(this) {
									commandFromClient = BTRThread.getBTCommand();
									BTRThread.currCommandProcessed();
									switch(commandFromClient) {
										case CONFIG:
											//The client is requesting for config info
											//Send packet to client to notify of keymapping
											BTOOStream.writeObject(CommandType.CONFIG);
											synchronized(this){
												BTOOStream.writeObject(serverui.getKeyMappings());
											}
											BTOOStream.flush();
											break;
										/*case SCREENSHOT:
											byte[] buffer;
											FileInputStream fis = new FileInputStream("SmartController\\screenshots\\screenshot.png");
											synchronized(this){
												buffer = new byte[fis.available()];
												fis.read(buffer);
											}
											if(buffer.length > 0) {
												BTOOStream.writeObject(CommandType.SCREENSHOT);
												BTOOStream.writeObject(buffer);
												BTOOStream.flush();
											}
											fis.close();
											break;*/
										default:
											keyTouch.identifyKey(commandFromClient, serverui.getKeyMappings(), serverui.getMouseRatio());
										break;
									}
								}
							}
						}
						BTOIStream.close();
					 }catch (Exception  e) {
						 System.out.println("Exception Occured: " + e.toString());
					 }
					try{
						BTserver.close();
					}catch (Exception  e) {
						 System.out.println("Exception Occured: " + e.toString());
					 }
					
					break;
				case 0:	//connectionType not selected yet
				default:
					break;
			}			
		}
	}
	
	public synchronized void sendKeymapListToClient(){
		
		CommandType commandToSend = CommandType.CONFIG;

		commandToSend.setArrayList(new ArrayList<String>(serverui.getKeyMappings()));

		WifiSendSocketThread WifiSThread = new WifiSendSocketThread(commandToSend, clientIPAddress, WifiPORT);
		WifiSThread.start();
	}
	
	public void sendToDeviceWifi(CommandType currCommand) {
		// Initiate a new thread
		wifiSThread = new WifiSendSocketThread(currCommand, clientIPAddress, WifiPORT);
		wifiSThread.start();
	}
		
	public void takeScreenshot() {
		keyTouch = new KeyTouch();
		
		keyTouch.takeScreenshot();
	}
	
	public class BTReceiveSocketThread extends Thread {

		private CommandType commandReceived = CommandType.DEFAULT;
		
		private ObjectInputStream objInputStream;
		
		private boolean bRun = true;
		private boolean bReceivedMessage = false;

		public BTReceiveSocketThread(ObjectInputStream btObjInputStream) {
			objInputStream = btObjInputStream;
		}

		/**
		 * listen for commands from server to client over socket connection
		 */
		public void run() {
			
			try {
				while(bRun) {					
					System.out.println("Waiting for data from stream...");

					try {
						commandReceived = (CommandType) objInputStream
								.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						serverui.updateStatus(false);
						e.printStackTrace();
					}
					switch(commandReceived) {
						case CONFIG:
						default:
							
							synchronized(this) {
								float X = objInputStream.readFloat();
								float Y = objInputStream.readFloat();
				
								commandReceived.setX(X);
								commandReceived.setY(Y);						
		
								System.out.println("Message Received: " + commandReceived
										+ "  X: " + commandReceived.getX() + "  Y: "
										+ commandReceived.getY());
								
								bReceivedMessage = true;
							}
							break;
					}
											
				}
				// Close inputstream
				objInputStream.close();

			} catch (Exception e) {
				System.out.println("MyServer: Error at BT listening thread");
			}
		}	
		
		public CommandType getBTCommand(){
			return commandReceived;
		}
		
		public boolean receivedBTCommand(){
			return bReceivedMessage;
		}
		
		public void currCommandProcessed() {
			bReceivedMessage = false;
		}
		
		public void stopThread(){
			bRun = false;
		}
	}
	
	
	public class WifiSendSocketThread extends Thread {

		private int WifiPORT = 8888;	
		private CommandType commandToSend;
		private Socket socket = null;
		private ObjectOutputStream objOutputStream;
		private String ipAddress;

		public WifiSendSocketThread(CommandType currCommand, String ipString, int numPort) {
			commandToSend = currCommand;
			ipAddress = new String(ipString);
			WifiPORT = numPort;

			System.out.println("MyServer Wi-Fi Send Socket commandToSend: "
					+ commandToSend + " " + commandToSend.getX() + " "
					+ commandToSend.getY());
		}
		/**
		 * wrap and send commands from client to server over socket connection
		 */
		public void run() {
			try {
				socket = new Socket(ipAddress, WifiPORT);
				objOutputStream = new ObjectOutputStream(
						socket.getOutputStream());

				try {
					System.out.println("MyServer Wi-Fi Send Socket Sending: " + commandToSend + " "
							+ commandToSend.getX() + " " + commandToSend.getY());

					// Send the enum(commandToSend) and the fields(X and Y)
					// separately
					// as the serializing and deserializing of enum through
					// ObjectOutputStream
					// and ObjectInputStream will not save the fields in the
					// enum
					objOutputStream.writeObject(commandToSend);
					
					switch(commandToSend) {
					case CONFIG:
						objOutputStream.writeObject(new ArrayList <String>(commandToSend.getArrayList()));
					break;
					default:
						objOutputStream.writeFloat(commandToSend.getX());
						objOutputStream.writeFloat(commandToSend.getY());
					break;
				}
				} catch (Exception e) {
					System.out.println("MyServer Wi-Fi Send Socket error: "+ e.toString());
				}
				// Close outputstream and socket
				objOutputStream.close();
				socket.close();

			} catch (Exception e) {
				System.out.println("MyServer Wi-Fi Send Socket error: "+ e.toString());
			}
		}
	}

	public class WifiReceiveSocketThread extends Thread {

		private int WifiPORT = 8888;		
		private CommandType commandReceived = CommandType.DEFAULT;
		private ServerSocket serverSocket = null;
		private Socket socket = null;
		private ObjectInputStream objInputStream;
		private String ipAddress;
		
		private boolean bRun = true, bNewClientIP = false, bReceivedMessage = false;

		public WifiReceiveSocketThread(int numPort) {
			ipAddress = "";
			WifiPORT = numPort;
		}

		/**
		 * listen for commands from server to client over socket connection
		 */
		public void run() {
			try {
				serverSocket = new ServerSocket(WifiPORT+1);
				System.out.println("Listening :" + (WifiPORT+1));
			} catch (IOException e) {
	     				// TODO Auto-generated catch bl ock
			  	e.printStackTrace();
			}
			
			try {
				while(bRun) {
					socket = serverSocket.accept();
					objInputStream = new ObjectInputStream(
							socket.getInputStream());

					if(!ipAddress.equals(socket.getInetAddress().toString().substring(1))) {
						bNewClientIP = true;
						ipAddress = new String(socket.getInetAddress().toString().substring(1));						
					}
						
					try {	
						commandReceived = (CommandType) objInputStream.readObject();	
						
						System.out.println("MyServer Wi-Fi Socket Received Command: "+ commandReceived.toString());
						
						switch(commandReceived) {
						case CONFIG:
						default:
							
							synchronized(this) {
								float X = objInputStream.readFloat();
								float Y = objInputStream.readFloat();
				
								commandReceived.setX(X);
								commandReceived.setY(Y);						
		
								System.out.println("Message Received: " + commandReceived
										+ "  X: " + commandReceived.getX() + "  Y: "
										+ commandReceived.getY());
								
								bReceivedMessage = true;
							}
							break;
						}
						
					} catch (Exception e) {
						System.out.println("MyServer Wi-Fi Listening Socket error: "+ e.toString());
					}
				}
				// Close inputstream and socket
				objInputStream.close();
				socket.close();

			} catch (Exception e) {
				System.out.println("MyServer Wi-Fi Listening Socket error: "+ e.toString());
			}
		}
		
		public void stopThread(){
			bRun = false;
		}
		
		public CommandType getWifiCommand(){
			return commandReceived;
		}
		
		public boolean receivedWifiCommand(){
			return bReceivedMessage;
		}
		
		public boolean newClientConnected(){
			return bNewClientIP;
		}
		
		public void currCommandProcessed() {
			bReceivedMessage = false;
		}
		
		public void currClientIPObtained() {
			bNewClientIP = false;
		}
		
		public String getClientIP(){
			return ipAddress;
		}
		
	}
}
