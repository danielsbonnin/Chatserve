/**
 * File: 	Chatserve.java
 * Author: 	Daniel Bonnin
 * Class: 	CS372
 * Assn:	Project 1
 * Desc:	Chatserver is the server for an internet chat application.
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
package project1;
import java.net.*;
import java.io.*;

public class Chatserve {
	
	/**
	 * Info and code samples about shutdown hooks Source: 
	 * http://hellotojavaworld.blogspot.com/2010/11/runtimeaddshutdownhook.html
	 * 
	 */
	public static void attachHook(){
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {	
				System.err.println("bye");
				System.exit(1);
			}
		});
	}
 
	public static String Usage = "Usage: $java Chatserve <Port Number>\n";
	public static int portno;
	
	public static boolean processInput(Socket clientSocket, BufferedReader in) {
	    String inputLine = "";
    	try {
    		inputLine = in.readLine();
    	}
    	catch(IOException ie) {
    		System.out.println("Conversation with client is disconnected");
    		return false;            		
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
		String input = "";
		try {
			input = consoleInput.readLine();
		} catch (Exception e){
			System.err.println("Invalid input");
			return false;
		}
		if (input.contains("\\quit")) {
			System.out.println("You disconnected from " + clientHandle);
			return false;
		}
		else {
			out.println(handle + input + "<END>");
			return true;
		}
	}
	public static void closeClient(PrintWriter out, BufferedReader in, Socket clientSocket, ServerSocket serverSocket) {
		try {
			out.close();
			in.close();
			clientSocket.shutdownInput();
			clientSocket.shutdownOutput();
			clientSocket.close();
			serverSocket.close();
		}
		catch (Exception e) {
			System.out.println("There was an error disconnecting from " + clientHandle);
			System.exit(1);
		}
	}
	private static String handle;
	private static String clientHandle = "";
	
	public static void main(String[] args) {
		if (args.length > 1) {
			System.err.println("Only 1 argument permitted\n" + Usage);
			System.exit(1);
		}
		
		else if(args.length == 0) {
			System.err.println("Port argument required\n" + Usage);
			System.exit(1);
		}	
		
		/* CLI parsing help obtained from 
		 * docs.oracle.com/javase/tutorial/essential/environment/cmdLineArgs.html*/
		else {
			try {
				portno = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("Port argument must be an integer.\n" + Usage);
				System.exit(1);
			}
		}
		System.out.print("Welcome to chatserver on port " + 
				Integer.toString(portno) +
				"\nPlease enter a handle: ");
		/* Help with terminal input athspk's answer on the following page:
		 * http://stackoverflow.com/questions/4644415/java-how-to-get-input-from-system-console
		 */
		
		BufferedReader consoleInput =  new BufferedReader(new InputStreamReader(System.in));
		try {
			handle = consoleInput.readLine();
			if (handle.length() > 10) {
				handle = handle.substring(0, 10);
				System.out.println("Handle truncated to: " + handle);

			} else {
				int spaces = 10 - handle.length();
				for (int i = 0; i < spaces; i++) {
					handle += '_';
				}
				System.out.println("Handle: " + handle.replace("_", ""));
			} 
		
		} catch(Exception e) {
			System.err.println("\nInvalid handle input. ");
			System.exit(1);
		}
		
		
		while (true) {
		System.out.println("Waiting for a connection...");
		/* Code help obtained from docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html */
		try (
			    ServerSocket serverSocket = new ServerSocket(portno);
			    Socket clientSocket = serverSocket.accept();
			    PrintWriter out =
			        new PrintWriter(clientSocket.getOutputStream(), true);
			    BufferedReader in = new BufferedReader(
			        new InputStreamReader(clientSocket.getInputStream()));
			) {
			System.out.println("A client has connected. Awaiting initial message. . .");
			out.println(handle + "***You are now connected***<END>");

			attachHook(serverSocket, clientSocket, in, out);


            while (true) {
            	if (!processInput(clientSocket, in)) {
            		closeClient(out, in, clientSocket, serverSocket);
            		break;
            	}
            		
            	if (!processOutput(handle, consoleInput, out)) {
            		closeClient(out, in, clientSocket, serverSocket);
            		break;
            	}
            	
            }

		} catch (Exception ie) {
//			System.err.print(ie.toString());
			System.exit(1);
			
		}
		}
	}
}
