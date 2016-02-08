/**
 * @file   Chatserve.java
 * @author Daniel Bonnin 
 * Class: 	CS372
 * Assn:	Project 1
 * Desc:	Chatserve is the server for an internet chat application.
 * 
 * 			Chatserve waits for a connection from chatclient. Upon 
 * 			connection, chatclient sends the first text message. Chatserve
 * 			and chatclient then alternate sending text messages. 
 * 
 * 			The connection is ended when either Chatserve or chatclient
 * 			message the text "\quit". At that point, Chatserve is available
 * 			to receive a new client connection. 
 * 
 * 			Chatserve exits gracefully upon SIGINT or SIGKILL. 
 */

import java.net.*;
import java.io.*;

public class Chatserve {
	public static String handle = ""; //The Server's screen name
	public static String clientHandle = ""; //The client's screen name
	
	public static boolean processInput(Socket clientSocket, BufferedReader in) {
		/**
		 * Process tcp data from client
		 * @param clientSocket The socket descriptor for the current client
		 * @param in The byte stream to read from
		 * @return false on error or quit, true otherwise
		 */
	    String inputLine = "";
    	try {  //Read from the socket. 
    		inputLine = in.readLine();
    	}
    	catch(IOException ie) {
    		System.out.println("Conversation with client is disconnected");
    		return false;  //Return to main to end the program.            		
    	}
    	if (inputLine == null || inputLine.length() < 15) {
    		System.out.println(clientHandle + " has disconnected");
    		return false;
    	}
		clientHandle = inputLine.substring(0, 10).replace("_", "");
		String msg = inputLine.substring(10, inputLine.indexOf("<END>"));
		System.out.println(clientHandle + ">" + msg);
		System.out.print(handle.replace("_", "") + "> ");
		return true;
	}
	
	public static boolean processOutput(String handle, BufferedReader consoleInput, PrintWriter out) {
		/**
		 * Process and send command line input from Server user.
		 * @param handle The server's screen name
		 * @param consoleInput The terminal input byte stream
		 * @param out The byte stream to write to
		 * @return false on error or quit, true otherwise. 
		 */
		String input = "";
		try { //Read from terminal
			input = consoleInput.readLine();
		} catch (Exception e){
			System.err.println("Invalid input");
			return false;  //Return to main to end program.
		}
		
		if (input.contains("\\quit")) {  //server user typed '\quit'
			System.out.println("You disconnected from " + clientHandle);
			return false;  //Return to main to end program.
		}
		else {  //Prepend username, append end signal to user input, and send. 
			out.println(handle + input + "<END>");
			return true;
		}
	}
	
	public static void closeClient(Socket clientSocket) {
		/**
		 * Gracefully close client socket
		 * @param clientSocket The current open tcp connection
		 * @return exit gracefully on error.
		 */
		try {  //close tcp connection to client.
			clientSocket.close();
		}
		catch (Exception e) {
			System.out.println("There was an error disconnecting from " + clientHandle);
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		/**
		 * Handle cli, Establish server socket, wait for connections, manage connections
		 * @param args User-inputted port number
		 */
		
		//Usage statement in case of incorrect args input.
		String Usage = "Usage: $java Chatserve <Port Number>\n";
		int portno = 0; //port number (input by user)
		
		if (args.length > 1) {  //Too many arguments
			System.err.println("Only 1 argument permitted\n" + Usage);
			return;  //Close program.
		}
		else if(args.length == 0) {  //Missing required port argument.
			System.err.println("Port argument required\n" + Usage);
			return;  //Close program.
		}	
		/* CLI parsing help obtained from 
		 * docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html*/
		else {
			try {   //Verify user inputted port number.
				portno = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("Port argument must be an integer.\n" + Usage);
				return;  //Close program.
			}
		}
		
		//Valid port number entered. Get user to input username.
		
		System.out.print("Welcome to chatserver on port " + 
				Integer.toString(portno) +
				"\nPlease enter a handle: ");
		
		/* Help with terminal input athspk's answer on the following page:
		 * http://stackoverflow.com/questions/4644415/java-how-to-get-input-from-system-console
		 */
		
		//User input stream.
		BufferedReader consoleInput =  new BufferedReader(new InputStreamReader(System.in));
		try {  //Read user input from terminal.
			handle = consoleInput.readLine();
			if (handle.length() > 10) {  //Handle is too long: truncate.
				handle = handle.substring(0, 10);
				System.out.println("Handle truncated to: " + handle);
			} else {  //Pad inputted handle with underscores (removed on print)
				int spaces = 10 - handle.length();
				for (int i = 0; i < spaces; i++) {
					handle += '_';
				}
				
				//Print accepted username
				System.out.println("Handle: " + handle.replace("_", ""));
			} 
		} catch(Exception e) {  //Invalid handle.
			System.err.println("\nInvalid handle input. ");
			return;  //close program
		}
		
		/**
		 * The main loop of Chatserve. 
		 * Each iteration is a different client connection.
		 */
		while (true) {
			
		System.out.println("Waiting for a connection...");
		
		/* Code help obtained from docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html */
		try (  //try-with-resources: set up socket, wait for client, set up streams.
			    ServerSocket serverSocket = new ServerSocket(portno);
			    Socket clientSocket = serverSocket.accept();
			    PrintWriter out =
			        new PrintWriter(clientSocket.getOutputStream(), true);
			    BufferedReader in = new BufferedReader(
			        new InputStreamReader(clientSocket.getInputStream()));
			) {
			System.out.println("A client has connected. Awaiting initial message. . .");
			out.println(handle + "***You are now connected***<END>");

			/**
			 * The connection loop. 
			 * Each iteration is 1 message exchange
			 */
            while (true) {
            	//Print message from client.
            	if (!processInput(clientSocket, in)) {
            		closeClient(clientSocket);
            		break;
            	}
            	
            	//Send message from server user.
            	if (!processOutput(handle, consoleInput, out)) {
            		closeClient(clientSocket);
            		break;
            	}
            }

		} catch (Exception ie) {
			return;  //close program on error.
		}  //Client loop
		}  //Main loop
	}  //void main()
}  //class Chatserve
