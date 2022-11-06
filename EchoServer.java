// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  /**
   * The default host .
   */
  final public static String DEFAULT_HOST = "127.0.0.1";
  
  /**
   * A string used to save login IDs
   */
  final private String key = "login ID";
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
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
	String message = msg.toString();
	if(message.startsWith("#login")) 
	{
		String loginid = message.substring(7);
		if(client.getInfo(key) == null) 
		{
			client.setInfo(key, loginid);
			System.out.println(loginid + " has logged on");
			this.sendToAllClients(loginid + " has logged on");
			System.out.println("Message received: " + msg + " from null");
		}
		else 
		{
			try 
			{
				System.out.println("ERROR - Second login. Connection terminated");
				client.close();
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	else 
	{
		String loginid = client.getInfo(key).toString();
		System.out.println("Message received: " + msg + " from " + loginid );
	    this.sendToAllClients(loginid + "> " + message );
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
  
  //Exercice 1-C: Methods derived from AbstractServer 
  //              to acknowledge connections and disconnections
  
  /**
   * Implementation of the hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  System.out.println
	  	("A new client is connected to the server.");
  }

  /**
   * Implementation of the hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  String loginid = client.getInfo(key).toString();
	  System.out.println(loginid + " has disconnected.");
  }

  /**
   * Implementation of the hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   * The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  @Override
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
	  String loginid = client.getInfo(key).toString();
	  System.out.println(loginid + " has created an exception and was disconnected.");
  }
	    
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
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
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
      //ServerConsole console = new ServerConsole(DEFAULT_HOST,port);
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
