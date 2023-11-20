package rest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import rest.Response.NotFoundException;

/**
 * The RoutingHandler handles http requests.
 * It identifies the concerned method inside the class passed via the constructor and invokes it with the request parameters.
 * 
 * @author Johanna Kirsch
 *
 */
public class RoutingHandler implements HttpHandler {
    private Object controller;

    /**
     * The constructor requires an object of a class that contains methods annotated with {@literal @Route}.
     * The RoutingHandler will invoke the methods on this object.
     * @param controller any type
     */
    public RoutingHandler(Object controller) {
	this.controller = controller;
    }

    /**
     * Takes a request and sends a response
     * @param exchange HttpExchange object, incoming request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
	Request request = new Request(exchange);
	Response response = executeMethod(request);

	exchange.getResponseHeaders().add("Content-Type", "text/html");
	exchange.sendResponseHeaders(response.getStatus(), response.responseString().getBytes().length);
	OutputStream os = exchange.getResponseBody();
	os.write(response.responseString().getBytes());
	os.close();
    }

    /**
     * Finds and executes the method belonging to the request if there is one
     * @param request Request object
     * @return Response object
     */
    private Response executeMethod(Request request) {	
	Class<?> implementedClass = controller.getClass();
	Method[] methods = implementedClass.getDeclaredMethods();

	for(int i = 0; i < methods.length; i++) {
	    Route annotation = methods[i].getAnnotation(Route.class);

	    if(annotation != null && request.getPath().equals(annotation.path()) && request.getMethod().equals(annotation.method()) && methods[i].getReturnType().equals(Response.class)) {
		try {
		    // post methods are only allowed to take one STRING Parameter. It will be passed via the body
		    if(request.getMethod().equals("POST") ) {
			if(null == request.getBody() || request.getBody().equals("")) {
			    throw new IllegalArgumentException("POST requests must contain a body");
			}
			return (Response) methods[i].invoke(controller, request.getBody());
		    }

		    Parameter[] params = methods[i].getParameters();
		    HashMap<String, String> queryParams = request.getQueryParams();
		    if(params.length == queryParams.size()) {
			Object[] methodParams = new Object[params.length];
			for(int j = 0; j< params.length; j++) {
			    if(queryParams.containsKey(params[j].getName())) {
				// parse the query parameters to the types of the method parameters
				String paramValue = queryParams.get(params[j].getName());
				switch(params[j].getType().getTypeName()) {
				case "int":
				    methodParams[j] = Integer.parseInt(paramValue);
				    break;
				case "java.lang.String":
				    methodParams[j] = paramValue;
				    break;
				default:
				    throw new UnsupportedOperationException("Passing method parameters of type " + params[j].getType().getTypeName() + " as query parameters is not supported");
				}

			    }else {
				throw new NotFoundException();
			    }
			}

			return (Response) methods[i].invoke(controller, methodParams);
		    }
		} catch(InvocationTargetException e) {
		    return new JsonResponse(e.getCause());
		} catch(Exception e) {
		    return new JsonResponse(e);
		}
	    }
	}
	return new JsonResponse(new NotFoundException());
    }


}
