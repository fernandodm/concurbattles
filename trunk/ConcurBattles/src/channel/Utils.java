package channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * This class contains useful functions that simplify the usage of the
 * Java standard library for beginners.
 */
public class Utils {

	
	/**
	 * Parses a <code>String</code> containing the representation of an 
	 * integer number in base 10.
	 *  
	 * @param string - a String containing the int representation to be parsed. 
	 *  
	 * @return the integer value represented by the argument in decimal. 
	 *  
	 * @throws NumberFormatException - if the string does not contain a parsable integer.
	 */
	static int parseInt(String string) {
		return Integer.parseInt(string);
	}
	
	
	/** Generates a pseudorandom number between 0.0 and 1.0. */
	static double random() {
		return Math.random();
	}
	
	
	/** Generates an integer pseudorandom number between <b>low</b> and <b>up</b>. */
	static int random(int low, int up) {
		return (int)(Math.random() * (up - low) + low);
	}
	
	
	/**
	 * Reads a line from an InputStream.
	 * 
	 * @param inputStream an InputStream from where to read the line.
	 * 
	 * @return the String line read from the inputStream.
	 * 
	 * @throws RuntimeException wrapping an IOException.
	 */
	static String readLine(InputStream inputStream) {
		String result = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			result = reader.readLine();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return result;
	}
	
}
