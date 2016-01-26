/**
 * Author: Daniel Bonnin
 * Class: CS372
 * 
 */
package project1;
import java.net.*;
import java.io.*;

public class Chatserve {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello World");
		/* Code help obtained from docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html */
		try ( 
			    ServerSocket serverSocket = new ServerSocket(4444);
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
