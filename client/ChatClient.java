// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  String username;
  
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
    username = loginID;
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
    	else 
    	{
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
  
  
  /**
   * This method handles all data starting with a "#" coming from the UI
   * 
   * @param command the message from the UI starting with a "#"
   */
  private void handleCommand (String command) throws IOException 
  { 
	  command = command.toLowerCase();
	  if(command.equals("#quit")) {
		this.quit();
		clientUI.display("Client has quit");
	  }
	  else if(command.equals("#login")) {
		  try 
			{
				if(this.isConnected()) 
				{
					clientUI.display("Client is already connected.");
				}
				else 
				{
					this.openConnection();
				}
			}catch (IOException e) 
			{
				clientUI.display(e.getMessage());
			}
			
	  }
	  else if(command.equals("#logoff")) {
		  try 
			{
			  if(this.isConnected()) 
			  	{
					this.closeConnection();
				}
				else 
				{
					clientUI.display("Client is already disconnected.");
				}
			}catch (IOException e) 
			{
				clientUI.display(e.getMessage());
			}
			
	  }
	  else if(command.startsWith("#setport")) {
		  if(this.isConnected()) 
		  	{
				clientUI.display("This command cannot be executed if the client is connected.");
			}
			else 
			{
				int port = Integer.parseInt(command.substring(9));
				this.setPort(port);
				clientUI.display("New port set to: " + port );
				clientUI.display("Please log in to connect using new port");
			}
	  }
	  else if(command.equals("#getport")) {
		  clientUI.display("The current port is: " + this.getPort());	
	  }
	  else if(command.startsWith("#sethost")) {
		  if(this.isConnected()) 
		  	{
			  clientUI.display("This command cannot be executed if the client is connected.");
			}
			else 
			{
				String host = command.substring(9);
				this.setHost(host);
				clientUI.display("New port set to: " + host );
				clientUI.display("Please log in to connect using new host");
			}
	  }
	  else if(command.equals("#gethost")) {
		  clientUI.display("The current host is: " + this.getHost());  
	  }
	  else {
		  clientUI.display("The command entered does not exist.");
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
	 * Implementation of hook method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
	protected void connectionEstablished() {
		try {
			sendToServer("#login " + username);
			clientUI.display("The connection was established");
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
  /**
  * Implementation of the hook method called after the connection has been closed. The default
  * implementation does nothing. The method may be overriden by subclasses to
  * perform special processing such as cleaning up and terminating, or
  * attempting to reconnect.
  */
 protected void connectionClosed() {
	 clientUI.display("The connection has been closed");
 }

  /**
  * Hook method called each time an exception is thrown by the client's
  * thread that is waiting for messages from the server. The method may be
  * overridden by subclasses.
  * 
  * @param exception
  *            the exception raised.
  */
 protected void connectionException(Exception exception) {
	 //inform client that the server has shut down, and closes session
	 clientUI.display("The server has shut down");
	 System.exit(0);
 }

	
}
//End of ChatClient class
