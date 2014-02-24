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

	
	// Determines which connection type: wifi, bluetooth, usb
	// Wifi Port Number
	private static final int WifiPORT = 8888;
	
	// Bluetooth variables
	private final UUID BTUUID = new UUID("1101", true);
	private final String BTconnectionString = "btspp://localhost:" + BTUUID +";name=Sample SPP Server";

	LocalDevice BTlocal = null;
    StreamConnectionNotifier BTserver = null;
    StreamConnection BTconn = null;

	KeyTouch keyTouch = null;
	
	public static void main(String[] args) throws UnknownHostException {
		serverui = new ServerUI();
		serverui.createServerUI();
		MyServer myServer= new MyServer();
		myServer.startConnection(0);
	}
	
	public void startConnection(int connectionType) throws UnknownHostException {
		// TODO Auto-generated method stub
		while(true) {
			//get connectiontype from serverui here
			
			connectionType= serverui.getUserChoice();
			// Print this variable to ensure the value is set properly
			System.out.println(connectionType);
			switch(connectionType) {
				case 1:
						// Wifi Connection
						ServerSocket serverSocket = null;
						Socket socket = null;
					try {
						serverSocket = new ServerSocket(WifiPORT);
						System.out.println("Listening :" + WifiPORT);
					} catch (IOException e) {
 		     				// TODO Auto-generated catch bl ock
					  	e.printStackTrace();
					}

					KeyTouch keyTouch = new KeyTouch();
					while (connectionType == 1) {
						try {
							//Wait for client to connect
							socket = serverSocket.accept();
							ObjectInputStream objInputStream = new ObjectInputStream(
									socket.getInputStream());
							serverui.updateStatus(true);

							System.out.println("ip: " + socket.getInetAddress());
							CommandType commandFromClient = CommandType.DEFAULT; 

						try {
							commandFromClient = (CommandType) objInputStream
									.readObject();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							serverui.updateStatus(false);
							e.printStackTrace();
						}

						float X = objInputStream.readFloat();
						float Y = objInputStream.readFloat();

						commandFromClient.setX(X);
						commandFromClient.setY(Y);

					keyTouch.identifyKey(commandFromClient, new ArrayList<String>(serverui.getKeyMappings()), serverui.getMouseRatio());

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
			case 2:
					//Bluetooth Connection
					try {
						System.out.println("Setting device to be discoverable...");
			           
						BTlocal = LocalDevice.getLocalDevice();
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
						

			            System.out.println("Waiting for data from stream...");

						while(connectionType == 2) {
							// Init CommandType  with  Default type
							CommandType commandFromClient = CommandType.DEFAULT; 
			
							try {
								commandFromClient = (CommandType) BTOIStream
										.readObject();
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								serverui.updateStatus(false);
								e.printStackTrace();
							}
			
							float X = BTOIStream.readFloat();
							float Y = BTOIStream.readFloat();
			
							commandFromClient.setX(X);
							commandFromClient.setY(Y);
			
								keyTouch.identifyKey(commandFromClient, serverui.getKeyMappings(), serverui.getMouseRatio());
			
							System.out.println("Message Received: " + commandFromClient
									+ "  X: " + commandFromClient.getX() + "  Y: "
									+ commandFromClient.getY());
							}
							BTOIStream.close();
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
}
