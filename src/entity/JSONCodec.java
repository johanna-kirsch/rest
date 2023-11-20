package entity;

import java.io.IOException;

/**
 * 
 * All classes whose objects should be read, created, deleted or updated by the REST API must implement this interface
 *
 * @author Johanna Kirsch
 */
public interface JSONCodec {

    /**
     * Throw this exception when an object cannot be serialized to a JSON string and vice versa
     */
    public class JSONCodecException extends Exception{
	
	/**
	 * Constructor with exception message
	 * @param message message to use in exception
	 */
	public JSONCodecException(String message) {
	    super(message);
	}
    }

    /**
     * Serialize to JSON
     * 
     * @return JSON string
     * @throws JSONCodecException if the object cannot be serialized
     */
    public String toJSON() throws JSONCodecException;
    
    /**
     * Assign properties to values given by the JSON string
     * 
     * @param json JSON string representing the object
     * @throws JSONCodecException if the object cannot be unserialized
     */
    public void fromJSON(String json) throws JSONCodecException;
    
    /**
     * Create or override a .json file with the JSON representation of the object
     * 
     * @param filepath The absolute path to the file. i.e. : /Users/me/Documents/myfile.json
     * @throws JSONCodecException if the object cannot be serialized
     * @throws IOException if the file cannot be created or written
     */
    public void toFile(String filepath) throws JSONCodecException, IOException;
   
}
