package rest;

import java.util.ArrayList;

import entity.JSONCodec;
import entity.JSONCodec.JSONCodecException;

/**
 * Response in JSON format
 * 
 * @author Johanna Kirsch
 *
 */
public class JsonResponse implements Response {
    private String json;
    private int status;

    /**
     * Calls toJSON method on object of type JSONCodec.
     * Uses status HTTP_OK
     * 
     * @param object type JSONCodec, not null
     * @throws JSONCodecException if the object cannot be serialized
     */
    public JsonResponse(JSONCodec object) throws JSONCodecException {
	if(null == object) {
	    throw new IllegalArgumentException("JSONCodec object cannot be null!");
	}
	this.json = object.toJSON();
	this.status = HTTP_OK;
    }

    /**
     * Calls toJSON method on object of type JSONCodec.
     * Uses given HTTP status
     * 
     * @param object type JSONCodec, not null
     * @param status HTTP status between 100 and 599
     * @throws JSONCodecException if the object cannot be serialized
     */
    public JsonResponse(JSONCodec object, int status) throws JSONCodecException {
	if(status < 100 || status >= 600) {
	    throw new IllegalArgumentException(status + " is not a valid http status code");
	}
	if(null == object) {
	    throw new IllegalArgumentException("JSONCodec object cannot be null!");
	}
	this.json = object.toJSON();
	this.status = status;
    }

    /**
     * Generates JSON string of the list of JSON objects.
     * Uses status HTTP_OK
     * 
     * @param objects Array List of classes implementing JSONCodec
     * @throws JSONCodecException if the objects cannot be serialized
     */
    public JsonResponse(ArrayList<? extends JSONCodec> objects) throws JSONCodecException {
	ArrayList<String> jsonObjects = new ArrayList<String>();

	for(JSONCodec o : objects) {
	    jsonObjects.add(o.toJSON());
	}

	this.json = "[" + String.join(",", jsonObjects) + "]";
	this.status = HTTP_OK;
    }

    /**
     * Response used when no method is found that corresponds to the request.
     * Status code HTTP_NOT_FOUND
     * 
     * @param exception NotFoundException, not null
     */
    public JsonResponse(NotFoundException exception) {
	if(null == exception) {
	    throw new IllegalArgumentException("Exception cannot be null");
	}
	String message;
	if(null == exception.getMessage() || exception.getMessage().equals("")) {
	    message = "Method not found";
	}else {
	    message = exception.getMessage();
	}

	this.json = "{\"exception\":\"" + message + "\"}";
	this.status = HTTP_NOT_FOUND;
    }
    
    /**
     * Response used when the provided parameters are invalid
     * Status code HTTP_BAD_REQUEST
     * 
     * @param exception IllegalArgumentException
     */
    public JsonResponse(IllegalArgumentException exception) {
	String message;
	if(null == exception || null == exception.getMessage() || exception.getMessage().equals("")) {
	    message = "Arguments are invalid";
	}else {
	    message = exception.getMessage();
	}

	this.json = "{\"exception\":\"" + message + "\"}";
	this.status = HTTP_BAD_REQUEST;
    }
    

    /**
     * Response used when an exception of any type occurs. 
     * Status code HTTP_INTERNAL_SERVER_ERROR
     * 
     * @param throwable any exception
     */
    public JsonResponse(Throwable throwable) {
	if(null == throwable) {
	    throw new IllegalArgumentException("Exception cannot be null");
	}

	this.json = "{\"exception\":\"" + throwable.getCause() + "\",\"message\":\"" + throwable.getMessage() + "\"}";
	this.status = HTTP_INTERNAL_SERVER_ERROR;
    }

    @Override
    public String responseString() {
	return json;
    }

    @Override
    public int getStatus() {
	return status;
    }
}
