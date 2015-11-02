import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * EECS 325 Project 1
 * This Class creates a thread that receives data from the server and sends it to the client
 * @author Ryan Rose
 *
 */

public class ServerToClientThread extends Thread{

	private Socket clientSocket = null;
	private Socket serverSocket = null;
	private final int BUFFER_SIZE = 32768;
	private byte[] serverReply = new byte[BUFFER_SIZE];
		
	// Server to client thread is allocated, client and server sockets are retained
	public ServerToClientThread(Socket clientS, Socket serverS){
		super();
		clientSocket = clientS;
		serverSocket = serverS;
	}
	
	public void run(){
		try{
			//*****RECEIVE DATA FROM SERVER*****
			BufferedInputStream serverReplyReader = new BufferedInputStream(serverSocket.getInputStream());
			DataOutputStream clientWriter = new DataOutputStream(clientSocket.getOutputStream());
			int index = serverReplyReader.read(serverReply, 0, BUFFER_SIZE);
			
			while(!clientSocket.isClosed() && !serverSocket.isClosed()){
				while(index != -1){
					//*****SEND REPLY TO CLIENT*****
					clientWriter.write(serverReply, 0, BUFFER_SIZE);
					clientWriter.flush();
					System.out.println("Index: " + index);
					index = serverReplyReader.read(serverReply, 0, BUFFER_SIZE);
				}
			}
			clientWriter.flush();
			
		}
		catch(SocketException e){
			System.out.println("CONNECT RESET");
			System.out.println(e.getMessage());
		}
		catch(Exception e){
			System.out.println("FAILURE IN SENDING REPLY TO CLIENT");
			System.out.println(e.getStackTrace());
			System.out.println(e.getMessage());
		}
		/*
		try{
			serverSocket.close();
			System.out.println("SERVER SOCKET CLOSED");
		}
		catch(Exception e){
			System.out.println("SERVER SOCKET CLOSE ERROR");
			System.out.println(e.getMessage());
		}
		try{
			clientSocket.close();
			System.out.println("CLIENT SOCKET CLOSED");
		}
		catch(Exception e){
			System.out.println("CLIENT SOCKET CLOSE ERROR");
			System.out.println(e.getMessage());
		}
		*/
	}
}
