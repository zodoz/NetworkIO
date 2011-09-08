/*
 * NetworkListener.java
 *
 * Created on August 4, 2005, 10:49 PM
 */

package NetworkIO;

import java.net.Socket;

/**
 * A class NetworkListener class is one that can process any message recieved
 * through ClientBase.
 * @author Michael Kent
 */
public interface NetworkListener {
    /**
     * Process any input recieved from a server.  The Message should always be
     * processed, but not necessarly the Socket connection.
     * @param message The message recieved.
     * @param socket The socket connection from which the message was recieved.
     */
    public void processInput(Message message, Socket socket);
}
