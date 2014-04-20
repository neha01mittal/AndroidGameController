import gc.common_resources.CommandType;

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
						ServerSocket serverSocket = null;
						Socket socket = null;
					try {
						serverSocket = new ServerSocket(WifiPORT);
						System.out.println("Listening :" + WifiPORT);
					} catch (IOException e) {
 		     				// TODO Auto-generated catch block
					  	e.printStackTrace();
					}

					KeyTouch keyTouch = new KeyTouch();
					while (connectionType == WIFICONNECTION) {
						try {
							//Wait for client to connect
							socket = serverSocket.accept();
							ObjectInputStream objInputStream = new ObjectInputStream(
									socket.getInputStream());
							serverui.updateStatus(true);
							
							//Send keymapping info to client here.
							sendToDeviceWifi(CommandType.CONFIG);

							System.out.println("ip: " + socket.getInetAddress());
							clientIPAddress = new String(socket.getInetAddress().toString());
							
							CommandType commandFromClient = CommandType.DEFAULT; 

						try {
							commandFromClient = (CommandType) objInputStream
									.readObject();
							System.out.println("Command "+commandFromClient);
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							serverui.updateStatus(false);
							e.printStackTrace();
						}
						
						processWifiCommand(objInputStream, commandFromClient);
						
						System.out.println("Message Received: " + commandFromClient
								+ "  X: " + commandFromClient.getX() + "  Y: "
								+ commandFromClient.getY());

						objInputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						serverui.updateStatus(false);
						e.printStackTrace();
					} finally {
						if (socket != null) {
							try {
								socket.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								serverui.updateStatus(false);
								e.printStackTrace();
							}
						}
					}
				}//End Wifi connection
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
	
	public void sendToDeviceWifi(CommandType currCommand) {
		// Initiate a new thread
		wifiSThread = new WifiSendSocketThread(currCommand, clientIPAddress, WifiPORT);
		wifiSThread.start();
	}
	
	public void processWifiCommand(ObjectInputStream objInputStream, CommandType currCommand) {
		float X, Y;
		try {
			X = objInputStream.readFloat();
			Y = objInputStream.readFloat();
			
			switch(currCommand) {
				case INIT:
					//First message from the client
				break;
				default:
					currCommand.setX(X);
					currCommand.setY(Y);
					
					keyTouch.identifyKey(currCommand, new ArrayList<String>(serverui.getKeyMappings()), serverui.getMouseRatio());
	
				break;				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
					// Send the enum(commandToSend) and the fields(X and Y)
					// separately
					// as the serializing and deserializing of enum through
					// ObjectOutputStream
					// and ObjectInputStream will not save the fields in the
					// enum
					objOutputStream.writeObject(commandToSend);
					System.out.println("Wi-Fi Send: "+ commandToSend.toString());
					
					//Going to send keymap table to the client here. Figure out a way to 
					// simplify the data structure
					// use enum fields..?
					
				} catch (Exception e) {
					System.out.println("Wi-Fi Send: Error");
				}
				// Close outputstream and socket
				objOutputStream.close();
				socket.close();

			} catch (Exception e) {
				System.out.println("Wi-Fi Send: Error");
			}
		}
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
}
