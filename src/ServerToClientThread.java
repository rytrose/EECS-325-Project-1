import java.net.Socket;

/**
 * EECS 325 Project 1
 * This Class creates a thread that receives data from the server and sends it to the client
 * @author Ryan Rose
 *
 */

public class ServerToClientThread extends Thread{

	private Socket clientSocket = null;
		
	// Server to client thread is allocated, socket is retained
	public ServerToClientThread(Socket s){
		super();
		clientSocket = s;
	}
	
	public void run(){
		
	}
}
