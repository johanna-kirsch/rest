package rest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Wrapper class for HttpServer
 * 
 * @author Johanna Kirsch
 *
 */
public class Server {
    /**
     * Port to use by default
     */
    public static final int DEFAULT_PORT = 4701;

    private HttpServer httpServer;

    /**
     * Creates a new HttpServer using the given port
     * 
     * @param port Port to use
     * @throws IOException if httpServer cannot be created
     */
    public Server(int port) throws IOException {
	httpServer = HttpServer.create(new InetSocketAddress(port), 0);
	httpServer.setExecutor(null);
    }

    /**
     * start server
     */
    public void start() {
	httpServer.start();
    }

    /**
     * Read stream System.in and stop the httpServer on any entry
     */
    public void stopOnUserInput() {
	System.out.println("Stop the server by hitting any key.");
	Scanner sc = new Scanner(System.in);
	sc.next();
	sc.close();
	httpServer.stop(0);
	System.out.println("Stopped server");

    }

    /**
     * Create context for the given path
     * 
     * @param path relative path
     * @param handler a HttpHandler
     */
    public void addHttpHandler(String path, HttpHandler handler) {
	if(null == path || path.equals("")) {
	    this.addHttpHandler(handler);
	}else {
	    httpServer.createContext(path, handler);
	}
    }

    /**
     * Create context for default path "/"
     * 
     * @param handler a HttpHandler
     */
    public void addHttpHandler(HttpHandler handler) {
	httpServer.createContext("/", handler);
    }
}
