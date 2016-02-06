/**
 * Author: Daniel Bonnin
 * Class: CS372
 * 
 */
package project1;
import java.net.*;
import java.io.*;

public class Chatserve {
	public static String Usage = "Usage: $java Chatserve <Port Number>\n";
	public static int portno;
	public static String processInput(String data, Socket clientSocket) {
		String clientName = data.substring(0, 10);
		String msg = data.substring(10);
		return clientName.replace("_", "") + ">" + msg;
	}
	
	public static String prepareOutput(String handle, BufferedReader consoleInput) {
		String input = "";
		System.out.println(handle + "> ");
		
		try {
			input = consoleInput.readLine();
		} catch (IOException e){
			System.err.println("Invalid input");
		}
		return handle + input + "<END>";
	}
	
	private static String handle;
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
		System.out.println("Welcome to chatserver on port " + 
				Integer.toString(portno) +
				"Please enter a handle: ");
		/* Help with terminal input athspk's answer on the following page:
		 * http://stackoverflow.com/questions/4644415/java-how-to-get-input-from-system-console
		 */
		BufferedReader consoleInput =  new BufferedReader(new InputStreamReader(System.in));
		try {
			handle = consoleInput.readLine();
		} catch(IOException e) {
			System.err.println("Invalid handle input");
		}
		/* Code help obtained from docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html */
		try (
			    ServerSocket serverSocket = new ServerSocket(portno);
			    Socket clientSocket = serverSocket.accept();
			    PrintWriter out =
			        new PrintWriter(clientSocket.getOutputStream(), true);
			    BufferedReader in = new BufferedReader(
			        new InputStreamReader(clientSocket.getInputStream()));
			) {
			System.out.println("A client has connected");
			out.println("Server____I am the server so watch yourself<END>");
		    String inputLine = "";
            while (true) {
            	if ((inputLine = in.readLine()) != null) { 
	            	System.out.println(processInput(inputLine, clientSocket));
	            	String output = prepareOutput(handle, consoleInput);
	            	if (output.contains("/quit")) {
	            		System.out.println("Conversation with " + handle + " is disconnected");
	            		clientSocket.close();
	            		
	            	} else {
	            		out.println(output);
	            	}
            	}
            }

		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
}
