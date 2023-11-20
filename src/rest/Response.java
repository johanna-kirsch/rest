package rest;

/**
 * A response is composed of a response string and a HTTP status code.
 * The implementing classes define the format of the response string such as JSON, XML or a custom format
 * 
 * @author Johanna Kirsch
 *
 */
public interface Response {

    /**
     * Exception to be thrown when no method can be found that corresponds to the request parameters
     *
     */
    public class NotFoundException extends Exception{
	/**
	 * Default constructor
	 */
	public NotFoundException() {}
	
	/**
	 * Constructor with exception message
	 * @param message exception message
	 */
	public NotFoundException(String message) {
	    super(message);
	}
    }
    /**
     * Everything went well
     */
    public int HTTP_OK = 200;
    
    /**
     * The request is invalid
     */
    public int HTTP_BAD_REQUEST = 400;
    
    /**
     * The resource cannot be found
     */
    public int HTTP_NOT_FOUND = 404;
    
    /**
     * An error occurred
     */
    public int HTTP_INTERNAL_SERVER_ERROR = 500;

    /**
     * The response String
     * @return response string
     */
    public String responseString();

    /**
     * HTTP status code
     * @return status code
     */
    public int getStatus();
}
