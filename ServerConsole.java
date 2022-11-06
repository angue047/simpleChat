
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import common.*;
import ocsf.server.ConnectionToClient;

/**
 * This class constructs the UI for a chat server.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 * @version September 2020
 */
public class ServerConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF serverUI;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the server that created this ConsoleChat.
   */
  EchoServer server;//ChatClient server;
  
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  /**
   * The instance of the server that created this ConsoleChat.
   */
  String serverid = "SERVER MESSAGE> ";
  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the serverConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ServerConsole(int port) throws IOException 
  {
    server = new EchoServer(port);
    server.listen();
    //server.run();
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the server's message handler.
   */
  public void accept() 
  {
    try
    {
      String message;

      while (true) 
      {
        message = fromConsole.nextLine() ;
        this.handleMessageFromServerUI(message);        
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
    System.out.println(serverid + message);
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromServerUI(String message)
  {
    try
    {
    	if(message.startsWith("#")) {
    		handleCommand(message);
    	}
    	else 
    	{
    		this.display(message);
            message = serverid + message;
            server.sendToAllClients(message);
    	}	
    }
    catch(IOException e)
    {
      this.display
        ("Could not send message to server.  Terminating client.");
      System.exit(0);
    }
  }
  
  /**
   * This method handles all data starting with a "#" coming from the server UI
   * 
   * @param command the message from the server UI starting with a "#"
   */
  private void handleCommand (String command) throws IOException 
  { 
	  command = command.toLowerCase();
	  if(command.equals("#start")) 
	  {
		  if(server.isListening()) 
		  	{
			  System.out.println("Server was already started");
			}
			else 
			{
				EchoServer oldServer = server ;
				server = new EchoServer(server.getPort());
				server.listen();
				oldServer.close();
			}  
	  }
	  else if(command.equals("#stop")) 
	  {
		  if(server.isListening()) 
		  	{	
			    server.stopListening();
			}
			else 
			{
				System.out.println("Server was already stopped");
			}  
	  }	
	  else if(command.equals("#close")) 
	  {
	        server.close();
	        for(Thread c : server.getClientConnections()) 
		  	{
		  		ConnectionToClient o = (ConnectionToClient) c;
		  		o.close();
		  	}
	        System.out.println("All conections were severed. Server was closed");
	  }
	  else if(command.equals("#quit")) 
	  {  
		  System.out.println("Server has shut down");  
		  this.quit();
	  }
	  else if(command.startsWith("#setport")) 
	  {
		  if(server.isListening()) 
		  	{
				this.display("This command cannot be executed if the server is listening for connections.");
			}
			else 
			{
				int port = Integer.parseInt(command.substring(9));
				server.setPort(port);
				System.out.println("New port set to: " + server.getPort() );
				System.out.println("Please start server to connect using new port");
			}
	  }
	  else if(command.equals("#getport")) 
	  {
		  System.out.println("The current port is: " + server.getPort());
	  }   
	  else 
	  {
		  this.display("The command entered does not exist.");
	  }
  }
	  
  /**
   * This method terminates the server.
   */
  public void quit()
  {
    System.exit(0);
  }
	  


  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the server UI.
   *
   * @param args[0] The host to connect to.
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException 
  {
    String host = "";
    int port = 0;
    ServerConsole chat;

    try
    {
      host = args[0];
      port = Integer.parseInt(args[1]);
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      host = "localhost";
      port = DEFAULT_PORT;
    }
    
	try 
	{	
		chat = new ServerConsole(port);
		chat.accept();  //Wait for console data
	} 
	catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    
  }
}
//End of ConsoleChat class
