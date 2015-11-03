import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Thread that represents a client to server connection
 * @author Ryan
 *
 */
public class ProxyThread extends Thread {
	
	private final int REQUEST_BUFFER_SIZE = 2048;
	private final int REPLY_BUFFER_SIZE = 32768;
	private byte[] request = new byte[REQUEST_BUFFER_SIZE];
	private byte[] reply = new byte[REPLY_BUFFER_SIZE];
	private Socket clientSocket = null;
	private Socket serverSocket = null;
	
	// Constructor initializes client socket
	public ProxyThread(Socket s){
		clientSocket = s;
	}
	
	public void run(){
		try{
			//*****RECEIVE REQUEST*****
			// Receive the request from the client socket
			BufferedInputStream clientRequestReader = new BufferedInputStream(clientSocket.getInputStream());
			clientRequestReader.read(request, 0, REQUEST_BUFFER_SIZE);
			/*int readByte = clientRequestReader.read(); 
			request[0] = (byte) readByte;
			int i = 1;
			while(readByte != -1){
				System.out.println("Reading request. Byte:" + i);
				readByte = clientRequestReader.read();
				request[i] = (byte) readByte; 
				i++;
			}
			System.out.println("Finished reading request.");
			*/
			
			//******ALTER REQUEST AND CREATE SOCKET ON DESTINATION SERVER*****
			// Replace the Connection: field in order to prevent persistent connections
			request = setConnection(request);
			
			// Get the InetAddress with destination hostname
			InetAddress address = InetAddress.getByName(getHost(request));
			
			// Create Socket on server
			serverSocket = new Socket(address, 80);
			
			//*****WRITE THE REQUEST TO THE SERVER*****
			DataOutputStream serverWriter = new DataOutputStream(serverSocket.getOutputStream());
			serverWriter.write(request);
			serverWriter.flush();
			
			//*****RECEIVE REPLY FROM SERVER AND WRITE TO CLIENT*****
			BufferedInputStream serverReplyReader = new BufferedInputStream(serverSocket.getInputStream());
			DataOutputStream clientWriter = new DataOutputStream(clientSocket.getOutputStream());
			int readByte = serverReplyReader.read();
			while(readByte != -1){
				clientWriter.write(readByte);
				clientWriter.flush();
				readByte = serverReplyReader.read();
			}
			System.out.println("Finished writing reply to client.");
			
			clientSocket.close();
			serverSocket.close();
		}
		catch(Exception e){
			e.getStackTrace();
		}
	}
	
	private String getHost(byte[] request){
		// Parse request to string to find destination address
		String requestString = new String(request, StandardCharsets.UTF_8);
		String[] splitRequest = requestString.split(" ");
		
		// Destination address is after http request type
		String urlString = splitRequest[1];
		URL url = null;
		
		// Use URL API to get hostname
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		System.out.println(url.getHost());
		return url.getHost();
	}
	
	private byte[] setConnection(byte[] request){
		// Parse request to string to find destination address
		String requestString = new String(request, StandardCharsets.UTF_8);
		System.out.println("Initial request: " + requestString);
		
		// Replace keep-alive with close
		String alteredRequest = requestString.replaceAll("Connection: keep-alive" , "Connection: close");
		System.out.println("Altered request: " + alteredRequest);
		
		// Parse back into bytes
		return alteredRequest.getBytes(StandardCharsets.UTF_8);
	}
}
