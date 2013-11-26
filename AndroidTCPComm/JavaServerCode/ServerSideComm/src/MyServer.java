<<<<<<< HEAD:AndroidTCPComm/JavaServerCode/ServerSideComm/src/MyServer.java
import gc.common_resources.CommandType;

=======
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
>>>>>>> 92478ffe591936c0b248eb772cacf8106841e73d:JavaServerCode/ServerSideComm/src/MyServer.java
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class MyServer {
	
	public static void main(String[] args){
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		try {
			serverSocket = new ServerSocket(8888);
			System.out.println("Listening :8888");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		KeyTouch keyTouch = new KeyTouch();
		while(true){
 
			try {
				socket = serverSocket.accept();
				ObjectInputStream objInputStream = new ObjectInputStream(socket.getInputStream());
				
				System.out.println("ip: " + socket.getInetAddress());
				CommandType commandFromClient = CommandType.DEFAULT;	//Init CommandType with Default type
				
				try {
		        	commandFromClient = (CommandType) objInputStream.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				float X = objInputStream.readFloat();
				float Y = objInputStream.readFloat();
		        commandFromClient.setX(X);
		        commandFromClient.setY(Y);

		        keyTouch.identifyKey(commandFromClient);
		      
		        System.out.println("Message Received: "+commandFromClient+"  X: "+commandFromClient.getX()+"  Y: "+commandFromClient.getY());
		        
		        objInputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				if( socket!= null){
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
