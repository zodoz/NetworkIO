/*
 * ConnectionListener.java
 *
 * Created on August 4, 2005, 10:49 PM
 */

package NetworkIO;

import java.net.Socket;

/**
 * ConnectionListener is a way to tell when a new connection has been made or an
 * old one lost.  To use a ConnectionListener, simply call:
 * ServerBase.addConnectionListener(ConnectionListener);
 * @author Michael Kent
 */
public interface ConnectionListener {
    /**
     * Recieved a connection.
     * @param socket The socket of the new connection.
     */
    public void gotConnection(Socket socket);
    /**
     * Lost an old connection.
     * @param socket The previous socket connection.
     */
    public void lostConnection(Socket socket);
}
