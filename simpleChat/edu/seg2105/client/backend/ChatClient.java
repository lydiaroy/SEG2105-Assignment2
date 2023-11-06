// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  /**
   * The String containing the user's login ID. Allows the user to 
   * be identified
   */
  String loginID;
  
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
  
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
     
     if(message.startsWith("#")) {
    	 handleCommand(message);
      }
      else {
    	  sendToServer(message);
      }
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  private void handleCommand(String message) {
	  try {
		  // split the message to process to account for other parameters
		  String[] partsOfMessage = message.split(" ");
		  // any issues with out of bounds at any point in this function will be handled by the catch block 
		  // no need to check if they exist / how long this array is
		  
		  // check this first part against possible commands
		  switch (partsOfMessage[0]) {
		  	case "#quit": {
		  		// close the client all together
		  		System.out.println("Terminating chat client...");
		  		quit();
		  		break;
		  	}
		  	case "#logoff": {
		  		//if they're not connected there is nothing to log off from
		  		if(!isConnected()) {
		  			System.out.println("There is no connection to terminate.");
		  		} else {
		  			// otherwise terminate the connection
		  			closeConnection();
		  			while(isConnected()) {
		  				// in case it takes a second to terminated, so we don't do anything while it does
		  				// could login too quickly if this isn't here
		  			}
		  			// it's actually disconnected now, inform the user they are no longer connected
		  			if(!isConnected()) {
		  				System.out.println("Successfully disconnected from the server.");
		  			}
		  		}
		  		break;
		  	}
		  	case "#sethost": {
		  		// has other parameters
		  		if(isConnected()) {
		  			System.out.println("Cannot set host while connected.");
		  		} else {
		  			// change the host the client wants to connect to
		  			setHost(partsOfMessage[1]);
		  		}
		  		break;
		  	}
		  	case "#setport": {
		  		// has other parameters
		  		if(isConnected()) {
		  			System.out.println("Cannot set port while connected.");
		  		} else {
		  			// change the port client wants to connect to
		  			setPort(Integer.parseInt(partsOfMessage[1]));
		  		}
		  		break; 
		  	}
		  	
		  	case "#login": {
		  		if(!isConnected()) {
		  			// we will open the connection, since #login is handled by the server
		  			openConnection();
		  			// if the connection goes through, inform the user
			  		System.out.println("Successfully connected to the server.");	
		  		} else {
		  			// otherwise we are already logged in! can't login again!
		  			System.out.println("Cannot login while connected.");
		  			quit(); // exit since they tried to login again
		  			// this might be supposed to be handled by the server... so might need to adjust
		  		}
		  		break;
		  	}
		  	case "#gethost": {
		  		// print the host name
		  		System.out.println("The host name is " + getHost());
		  		break;
		  	}
		  	case "#getport": {
		  		// print the port number
		  		System.out.println("The port number is " +getPort());
		  		break;
		  	}
		  	default: {
		  		// we do not recognize the command! give them another change, but don't kick them over it
		  		System.out.println("Could not recognize command. Please try again.");
		  		break;
		  	}
		}
	  } catch(IOException e) {
		  System.out.println("Could not send message to server. Terminating client.");
		  quit();
	  } catch(IndexOutOfBoundsException i) {
		  System.out.println("Please provide required parameters");
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  /**
	 * Implements the method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
	protected void connectionClosed() {
		// want a graceful exit, so the program won't just close, it will inform the user
		clientUI.display("The connection has been closed.");
	}
  
  /**
	 * Implements the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  	@Override
	protected void connectionException(Exception exception) {
  		// want to display a useful message to the user, example: "the server went down" or "the connection was closed"
  		// to display to the user we have to use the UI class
  		
  		clientUI.display("The connection has been shut down.");
  		// once the user has been informed, we can quit
  		quit();
	}
  	
  	/**
	 * Implementing the hook method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
	protected void connectionEstablished(){
		// need to send "#login " + loginID to the server
		System.out.println("Connected. Attempting to login..."); // telling the client there is a login attempt
		try {
			sendToServer("#login " + loginID);
		} catch (IOException e) {
			// if for some reason there is an issue, tell the client and terminate
			System.out.println("Error logging in. \nTerminating client...");
			quit();
		}
		// inform the client they have logged in, welcome them
		System.out.println("Login success! Welcome, " + loginID);
	}
}
//End of ChatClient class
