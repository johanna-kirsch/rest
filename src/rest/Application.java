package rest;

import java.io.IOException;
import com.sun.net.httpserver.HttpHandler;

import controller.HouseController;
import entity.House;
import entity.Person;

/**
 * This class creates the house and houseController objects and launches the server
 * Execute this class's main method to run the application
 * 
 * @author Johanna Kirsch
 *
 */
public class Application {

    /**
     * Creates a house object and adds two inhabitants for testing purposes.
     * Launches the server with the RoutingHandler
     * 
     * @param args this method does not take any arguments
     */
    public static void main(String[] args) {
	House h = new House("1, Rue Principale, 57070 Metz");

	Person p1 = new Person(1, "Jon Doe", 38);
	Person p2 = new Person(2, "Alice Example", 20);

	h.addInhabitant(p1);
	h.addInhabitant(p2);


	HouseController controller = new HouseController(h);


	try {
	    Server server = new Server(Server.DEFAULT_PORT);
	    HttpHandler handler = new RoutingHandler(controller);
	    server.addHttpHandler(handler);

	    server.start();
	    server.stopOnUserInput();

	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

}
