/*
 * ClientBase.java
 *
 * Created on August 4, 2005, 10:50 PM
 */

package NetworkIO;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;


/**
 * ClientBase maintains a connection with a server of some kind.  It will not allow
 * for any connections to be made to it, nor is it able to connect to more than one
 * server at a time.
 * @author Michael Kent
 */
public class ClientBase extends Thread {
	
	protected NetworkLogger logger = new NetworkLogger("ClientBase");
	protected Socket connection;
	protected HashSet networkListeners = new HashSet();
	protected HashSet connectionListeners = new HashSet();
	protected ObjectInputStream input;
	protected ObjectOutputStream output;
	protected boolean connected;
	//boolean sendingInput;
	
	/**
	 * Creates a new instance of ClientBase using a String host and int port
	 * to create a socket connection.
	 * @param host The host name of a computer running a valid server.
	 * @param port The port of the computer to connect to.
	 * @throws Exception Any exception that occurs while creating a socket and registering a connection.
	 */
	public ClientBase(String host, int port) throws Exception {
		connection = connect(host,port);
		getStreams();
		setPriority(Thread.MIN_PRIORITY);
		start();
	}
	
	/**
	 * Creates a new instance of ClientBase using a pre-defined Socket
	 * to create a connection.
	 * @param socket The connected socket to another computer.
	 * @throws Exception Any exception occuring while registering a connection.
	 */
	public ClientBase(Socket socket) throws Exception {
		connection = socket;
		getStreams();
		setPriority(Thread.MIN_PRIORITY);
		start();
	}
	
	/**
	 * Creates and returns a socket created from the recieved host and port.
	 * @return Returns the created Socket.
	 * @param host The host name of the computer running a valid server.
	 * @param port The port of the computer to connect to.
	 * @throws Exception Any exception that occured while trying to establish a connection to the serving
	 * computer.
	 */
	protected Socket connect(String host, int port) throws Exception {
		return new Socket(InetAddress.getByName(host),port);
	}
	
	/**
	 * Registers the input and output streams to establish a communication connection.
	 * @throws Exception Any exception occured while registering input and output streams.
	 */
	protected void getStreams() throws Exception {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		connected = true;
	}
	
	/**
	 * Starts the client's process of recieving Messages and telling NetworkListeners
	 * to process them.
	 */
	public void run() {
		try {
			while(connected) {
				Object o = input.readObject();
				if(o instanceof String)
					logger.log("\tString is: "+(String)o);
				//Message m = (Message)input.readObject();
				Message m = (Message)o;
				Object[] lis = networkListeners.toArray();
				for(int i=0;i<lis.length;i++) {
					((NetworkListener)lis[i]).processInput(m,connection);
				}
			}
		} catch(EOFException e) {
			logger.log("There was an End Of File Exception.");
		} catch(Exception e) {
			logger.log("There was an error while reading an object - "+e.getMessage());
			//e.printStackTrace();
		} finally {
			connected = false;
			closeConnection();
		}
	}
	
	private void closeConnection() {
		try {
			for(Iterator itr = connectionListeners.iterator();itr.hasNext();)
				((ConnectionListener)itr.next()).lostConnection(connection);
			logger.log("\tposted all the lost connections.");
			output.close();
			input.close();
			connection.close();
		} catch(Exception e) {
			logger.log("There was an Exception while closing - "+e.getMessage());
		}
	}
	
	/**
	 * Sends the Message to the connected server.
	 * @throws Exception Any exception that occurs while trying to send the message.
	 * @param message The Message to send.
	 */
	public synchronized void send(Message message) throws Exception {
		output.writeObject(message);
	}
	
	/**
	 * Returns the current Socket connection.
	 * @return The current Socket connection.
	 */
	public Socket getSocket() {
		return connection;
	}
	
	/**
	 * Disconnects from the current connected server.
	 */
	public void disconnect() {
		connected = false;
	}
	
	/**
	 * Adds a NetworkListener to ClientBase so that any incomming connection will be
	 * sent to it.
	 * @param listener The NetworkListener to add.
	 */
	public void addNetworkListener(final NetworkListener listener) {
		if(connected)
			networkListeners.add(listener);
	}
	
	/**
	 *Adds a ConnectionListener to Client base in case of a lost connection.
	 *@param listener The ConnectionListener to add.
	 */
	public void addConnectionListener(ConnectionListener listener) {
		if(connected)
			connectionListeners.add(listener);
		else
			listener.lostConnection(connection);
	}
}
