package rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpExchange;

/**
 * Wrapper class for a HttpExchange object.
 * If the request contains a body, it must have the Content-Type 'application-json'
 * 
 * @author Johanna Kirsch
 *
 */
public class Request {

    private String method;
    private String path;
    private String query;
    private String body;

    private HashMap<String, String> queryParams;

    /**
     * Extracts the method, the path, the body and the query string as well as the parameters passed in the query
     * @param exchange exchange object to be wrapped
     * @throws IOException
     */
    public Request(HttpExchange exchange) throws IOException {
	method = exchange.getRequestMethod();
	path = exchange.getRequestURI().getPath();
	body = new String(exchange.getRequestBody().readAllBytes());
	query = exchange.getRequestURI().getQuery();

	// extract names and values of the parameters passed via the query string
	queryParams = new HashMap<String, String>();
	if(null != query && !query.equals("")) {
	    Pattern pattern = Pattern.compile("([^=&]+)=([^&]+)");
	    Matcher matcher = pattern.matcher(query);
	    while(matcher.find()) {
		queryParams.put(matcher.group(1), matcher.group(2));
	    }
	}
    }

    /**
     * Getter for HTTP method
     * @return HTTP method
     */
    public String getMethod() {
	return method;
    } 
    /**
     * Getter for path
     * @return the path
     */
    public String getPath() {
	return path;
    }
    /**
     * Getter for query
     * @return query
     */
    public String getQuery() {
	return query;
    }
    /**
     * Getter for body
     * @return body
     */
    public String getBody() {
	return body;
    }
    /**
     * Getter for parameters extracted from the query string
     * @return HashMap where the key is a parameter name and the value is the parameter value
     */
    public HashMap<String, String> getQueryParams(){
	return queryParams;
    }
}
