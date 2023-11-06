package edu.seg2105.client.ui;

import java.util.Scanner;

import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.EchoServer;

public class ServerConsole implements ChatIF {
	//Class variables *************************************************
	  
	  /**
	   * The default port to connect on.
	   */
	  final public static int DEFAULT_PORT = 5555;
	  
	  //Instance variables **********************************************
	  
	  /**
	   * The instance of the server that created this ServerChat.
	   */
	  EchoServer echoServer;
	  
	  
	  /**
	   * Scanner to read from the console
	   */
	  Scanner fromConsole; 
	  
	  /**
	   * Integer to hold the port read from console
	   */
	  	
	  public static int port;
	  
	//Constructors ****************************************************

	  /**
	   * Constructs an instance of the ClientConsole UI.
	   *
	   * @param host The host to connect to.
	   * @param port The port to connect on.
	   */
	  public ServerConsole(int port) 
	  {
		  // create a server
	    echoServer = new EchoServer(port, this);
	    // Create scanner object to read from console
	    fromConsole = new Scanner(System.in); 
	  }
	  
	//Instance methods ************************************************
	  
	  /**
	   * This method waits for input from the console.  Once it is 
	   * received, it sends it to the server's message handler.
	   */
	  public void lookForInput() 
	  {
		  // same as from ClientConsole
	    try
	    {
	      String message;

	      while (true) 
	      {
	        message = fromConsole.nextLine();
	        echoServer.handleMessageFromServerUI(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	      ex.printStackTrace();
	    }
	  }

	  /**
	   * This method overrides the method in the ChatIF interface.  It
	   * displays a message onto the screen.
	   *
	   * @param message The string to be displayed.
	   */
	  public void display(String message) 
	  {
		  // display to server AND to all clients, added the > for consistency
	    System.out.println("> SERVER MSG > " + message);
	    echoServer.sendToAllClients("SERVER MSG > " + message);
	  } 
	  
	  public static void main(String[] args) 
	  {
	    int port = 0; //Port to listen on

	    try
	    {
	      port = Integer.parseInt(args[0]); //Get port from command line
	    }
	    catch(Throwable t)
	    {
	      port = DEFAULT_PORT; //Set port to 5555
	    }
		
	    ServerConsole sc = new ServerConsole(port);
	    
	    try 
	    {
	      sc.echoServer.listen(); //Start listening for connections
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println("ERROR - Could not listen for clients!");
	    }
	    
	    sc.lookForInput();

	  }
}
