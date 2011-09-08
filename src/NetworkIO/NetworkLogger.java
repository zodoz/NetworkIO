/*
 * LogOutput.java
 *
 * Created on August 4, 2005, 11:01 PM
 */

package NetworkIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Simplistic logger that logs output to a file located in program directory.
 * The name of each file is dictated by the name given via instantiation.
 * @author Michael Kent
 */
public class NetworkLogger {
	
	String name;
	PrintWriter out;
	
	/**
	 * Creates a new instance of LogOutput
	 * @param name The name to reference as source of logs. Also, the log file
	 * is name+" log.txt".
	 */
	public NetworkLogger(String name) {
		this.name = name;
	}

	/**
	 * Log a message to console (if available) and log file.
	 * @param msg The message to display.
	 */
	public void log(String msg) {
		String output = name+": "+msg;
		System.out.println(output);
		try {
			out = new PrintWriter(new PrintStream(new FileOutputStream(new File(name+" log.txt"))));
			out.println(new Date().toString()+": "+output);
			out.flush();
			out.close();
		} catch(Exception e) {
			//do nothing
		}
	}
}
