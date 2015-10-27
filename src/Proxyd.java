import java.net.ServerSocket;

/**
 * EECS 325 Project 1
 * 
 * @author Ryan Rose
 *
 */
public class Proxyd {
	
	private static int portNumber = -1;
	
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
			ServerSocket serverSocket = new ServerSocket(portNumber);
		}
		catch(Exception e){
			System.out.println("Unable to create server socket on port " + portNumber + ".");
			System.exit(-1);
		}
		
		while(true){
			
		}
	}

}
