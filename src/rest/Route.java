package rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be used on a method that can be accessed by the REST API
 * Defines the path and the method
 * 
 * @author Johanna Kirsch
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Route {
    /**
     * The path starts with a / character
     * 
     * @return path
     */
    public String path();

    /**
     * The method is the HTTP Request type
     * Supported values are GET, POST, PATCH, DELETE
     * 
     * @return method
     */
    public String method();

}
