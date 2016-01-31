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

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		System.out.println("chatserver has started.");
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
		
		System.out.println("The port number you entered was " + Integer.toString(portno));
				
		
		
		/* Code help obtained from docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html */
		try (
			    ServerSocket serverSocket = new ServerSocket(portno);
			    Socket clientSocket = serverSocket.accept();
			    PrintWriter out =
			        new PrintWriter(clientSocket.getOutputStream(), true);
			    BufferedReader in = new BufferedReader(
			        new InputStreamReader(clientSocket.getInputStream()));
			) {
		    String inputLine, outputLine;
            
		    // Initiate conversation with client
		    outputLine = "Hello, client";
		    out.println(outputLine);

		    while ((inputLine = in.readLine()) != null) {
		        out.println(outputLine);
		    }
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
}
