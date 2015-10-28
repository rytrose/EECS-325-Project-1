import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * EECS 325 Project 1
 * This Class creates a thread that receives requests from the client and sends them to the server
 * @author Ryan Rose
 *
 */
public class ClientToServerThread extends Thread {
	
	private Socket clientSocket = null;
	private Socket serverSocket = null;
	private byte[] clientRequest = new byte[16384];
	private String url = null;
	
	// Client to server thread is allocated, socket is retained
	public ClientToServerThread(Socket s){
		super();
		clientSocket = s;
	}
	
	public void run(){
		//*****RECEIVE REQUEST FROM CLIENT*****
		try{
			// Receive the requests from the client socket
			BufferedInputStream clientRequestReader = new BufferedInputStream(clientSocket.getInputStream());
			
			// Read the request into a byte array
			clientRequestReader.read(clientRequest, 0, 16384);
			
			// Read bytes as a string in order to find URL
			String request = new String(clientRequest, StandardCharsets.UTF_8);
			String[] splitRequest = request.split(" ");
			url = splitRequest[1];
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		//*****SEND REQUEST TO SERVER*****
		try{
			InetAddress destinationAddress = InetAddress.getByName(url);
			serverSocket = new Socket(destinationAddress, 80);
			DataOutputStream serverWriter = new DataOutputStream(serverSocket.getOutputStream());
			serverWriter.write(clientRequest);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

}
