import java.net.ServerSocket;
import java.net.Socket;

/**
 * EECS 325 Project 1
 * This Class is the top-level class for a Web proxy
 * @author Ryan Rose
 *
 */
public class Proxyd {
	
	private static int portNumber = -1;
	private static ServerSocket serverSocket = null;
	
	public static void main(String[] args) {
		
		// Establish the server port number given in the command line arguments
		try{
			portNumber = Integer.parseInt(args[1]);
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
		
		// Create a server socket on the specified port
		// Exit the program if unable to do so
		try{
			serverSocket = new ServerSocket(portNumber);
		}
		catch(Exception e){
			System.out.println("Unable to create server socket on port " + portNumber + ".");
			System.exit(-1);
		}
		
		while(true){
			Socket clientSocket = null;
			try{
				clientSocket = serverSocket.accept();
			}
			catch(Exception e){
				System.out.println(e.getMessage());
			}
			
			new ClientToServerThread(clientSocket).start();
			new ServerToClientThread(clientSocket).start();
		}
	}

}
