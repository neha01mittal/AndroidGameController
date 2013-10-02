import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class MyServer {
	
	public static void main(String[] args){
		ServerSocket serverSocket = null;
		Socket socket = null;
		DataInputStream dataInputStream = null;
		DataOutputStream dataOutputStream = null;
		
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
				dataInputStream = new DataInputStream(socket.getInputStream());
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
				System.out.println("ip: " + socket.getInetAddress());
				String userInput = dataInputStream.readUTF();
				keyTouch.identifyKey(userInput);
				System.out.println("message: " + userInput);
				
				dataOutputStream.writeUTF("Hello!");
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
				
				if( dataInputStream!= null){
					try {
						dataInputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if( dataOutputStream!= null){
					try {
						dataOutputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
