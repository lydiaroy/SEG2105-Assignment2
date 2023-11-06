package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;
import edu.seg2105.client.common.ChatIF;
import ocsf.client.AbstractClient;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables ***************************************
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF serverUI; 
  AbstractClient client;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	String message = (String) msg;
    System.out.println("Message received: " + msg + " from " + client.getInfo("loginIDKey"));
    
    if(message.startsWith("#login")) {
    	// we know that it specifies the login ID
		String[] partsOfMessage = message.split(" ");
		// login id will be partsOfMessage[0]
		if(client.getInfo("loginIDKey") == null ) { // if the client has not logged in, create their loginID
			client.setInfo("loginIDKey", partsOfMessage[1]);
			this.serverUI.display(partsOfMessage[1] + " has logged in.");
		} else if(!client.isAlive()) { // if this client is not active, but we want it to be active again set the login ID
			client.setInfo("loginIDKey", partsOfMessage[1]);
			this.serverUI.display(partsOfMessage[1] + " has logged in.");
		}else { // they're already logged in, terminate?
			try {
				client.sendToClient("Cannot login once already logged in. Terminating connection.");
				client.close();
			} catch (IOException e) {
				System.out.println("Error closing connection.");
			}
		}
		
	}else {
		 sendToAllClients("From "+ client.getInfo("loginIDKey")+ " > " + msg);
	}
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  
  //Class methods ***************************************************
  
  /**
   * Implementing the hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * 
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println("A client has connected.");
  }

  /**
   * Implementing the hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  // tell the server UI that a client disconnected, and who
	  System.out.println(client.getInfo("loginIDKey") + " has disconnected.");
  }
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
	// tell the server UI that a client disconnected, and who
	  System.out.println(client.getInfo("loginIDKey") + " has disconnected.");
  }
  
  public void handleMessageFromServerUI(String message)
  {
	  try
	    {
		  // check what the message starts with
	      if(message.startsWith("#")) {
	    	 handleCommand(message);
	      }
	      else {
	    	  // if it's not a command and the client exists, send the message to be processed
	    	  if(this.client != null) {
	    		  client.sendToServer(message);
	    	  }
	    	  // also display in server
	    	  serverUI.display(message);
	      }
	    }
	    catch(IOException e)
	    {
	      serverUI.display
	        ("Could not send message.");
	    }
    
  }
  
  private void handleCommand(String message) {
	  try {
		  // split the message to process to account for other parameters
		  String[] partsOfMessage = message.split(" ");
		  
		  // the first part should be the command we need to check for
		  String firstPartOfMessage = partsOfMessage[0];
		  
		  // check this first part against possible commands
		  switch (firstPartOfMessage) {
		  	case "#quit": {
		  		// close the server all together
		  		System.out.println("Terminating server...");
		  		System.exit(0);
		  		break;
		  	}
		  	case "#stop": {
		  		// if its not listening, nothing to stop
		  		if(!isListening()) {
		  			System.out.println("Cannot stop when the server is already stopped");
		  			break;
		  		}
		  		// otherwise we can just stop listening, allowing current clients to remain connected
		  		stopListening();
		  		break;
		  	}
		  	case "#close": {
		  		// need to disconnect all clients
		  		// close the server and stop listening but DON'T terminate!
		  		close();
		  		break;
		  	}
		  	case "#setport": {
		  		// has other parameters
		  		setPort(Integer.parseInt(partsOfMessage[1]));
		  		System.out.println("The port has been set to " + getPort());
		  		break;
		  	}
		  	case "#start": {
		  		if(!isListening()) {
		  			listen();
		  		} else { // if already listening, we cant start listening again
		  			System.out.println("Cannot start listening for clients when the server is already listening");
		  		}
		  		break;
		  	}
		  	case "#getport": {
		  		System.out.println("The port number is " + getPort());
		  		break;
		  	}
		  	default: {
		  		// unknown command sent, try again
		  		System.out.println("Could not recognize command. Please try again.");
		  		break;
		  	}
		}
	  } catch(IOException e) {
		  System.out.println("Could not send message to server. Terminating client.");
	  } catch(IndexOutOfBoundsException i) {
		  System.out.println("Please provide required parameters");
	  }
  }
  
}
//End of EchoServer class
