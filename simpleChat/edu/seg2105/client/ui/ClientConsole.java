package edu.seg2105.client.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  
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
  public ClientConsole(String loginID, String host, int port) 
  {
    try 
    {
      client= new ChatClient(loginID, host, port, this);
      
      
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        client.handleMessageFromClientUI(message);
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
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
    System.out.println("> " + message);
  } 

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The user's unique login ID.
   * @param args[1] The host to connect to.
   * @param args[2] The port to connect to.
   */
  public static void main(String[] args) 
  {
    String host = null;
    String loginID = null;
    port = -1;


    try
    {
    	// check to see what the client wrote to command line
      loginID = args[0];
      host = args[1];
      port = Integer.parseInt(args[2]);
    }
    // if they didn't write anything / didn't write one of the parameters it will be handled here
    catch(ArrayIndexOutOfBoundsException e)
    {
    	if(loginID == null) { // if the login ID didn't change they didn't send one so we need to close the connection
    		System.out.println("ERROR - No login ID specified. Connection aborted.\nTerminating client...");
    		System.exit(0);
    	}
    	// we can use defaults for everything else, of course
    	if(host == null) {
    		host = "localhost";
    	}
    	if(port == -1) {
    		port = DEFAULT_PORT;
    	}
      
    } catch(NumberFormatException n) {
    	port = DEFAULT_PORT;
    }
    
    ClientConsole chat = new ClientConsole(loginID, host, port);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class
