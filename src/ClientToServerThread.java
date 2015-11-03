import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
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
	private final int BUFFER_SIZE = 32768;
	private byte[] clientRequest = new byte[BUFFER_SIZE];
	private URL url = null;
	private String previousHostname = null;

	
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
			int index = clientRequestReader.read(clientRequest, 0, BUFFER_SIZE);
			
			// Read bytes as a string in order to find URL
			url = getURL(clientRequest);

			// Resolve the IP address
			InetAddress destinationAddress = InetAddress.getByName(url.getHost());
			
			// Create a socket on the destination server
			serverSocket = new Socket(destinationAddress, 80);
			
			// Create a ServerToClientThread for listening to the server
			if(clientSocket != null && serverSocket != null){
				new ServerToClientThread(clientSocket, serverSocket).start();
			}
			
			//*****SEND REQUEST TO SERVER*****
			// Write to the server socket
			DataOutputStream serverWriter = new DataOutputStream(serverSocket.getOutputStream());
			serverWriter.write(clientRequest);
			serverWriter.flush();
			
			// Read again to see if there are more requests
			index = clientRequestReader.read(clientRequest, 0, BUFFER_SIZE);
			
			while(index != -1){
				
				previousHostname = url.getHost();
				
				// Read bytes as a string in order to find URL
				url = getURL(clientRequest);
				
				if(!previousHostname.equals(url.getHost())){
					// Resolve the IP address
					destinationAddress = InetAddress.getByName(url.getHost());
					
					// Create a socket on the destination server
					serverSocket = new Socket(destinationAddress, 80);
					
					// Create a ServerToClientThread for listening to the server
					new ServerToClientThread(clientSocket, serverSocket).start();
					
					//*****SEND REQUEST TO SERVER*****
					// Write to the server socket
					serverWriter = new DataOutputStream(serverSocket.getOutputStream());
					serverWriter.write(clientRequest);
					serverWriter.flush();
				}
				else{
					//*****SEND REQUEST TO SERVER*****
					// Write to the server socket
					serverWriter = new DataOutputStream(serverSocket.getOutputStream());
					serverWriter.write(clientRequest);
					serverWriter.flush();
				}
				// Read another request
				index = clientRequestReader.read(clientRequest, 0, BUFFER_SIZE);
			}
		}
		catch(Exception e){
			System.out.println("FAILURE IN SENDING REQUEST");
			e.printStackTrace();
			System.out.println(e.getMessage());
						
		}
	}
	
	private URL getURL(byte[] clientRequest){
		// Read bytes as a string in order to find URL
		String request = new String(clientRequest, StandardCharsets.UTF_8);
		String[] splitRequest = request.split(" ");
		String urlString = splitRequest[1];
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("URL String: " + urlString);
			
		}
		return url;
	}
}
