package controller;

import java.util.ArrayList;

import entity.House;
import entity.JSONCodec;
import entity.Person;
import entity.JSONCodec.JSONCodecException;

import java.lang.reflect.Field;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import rest.JsonResponse;
import rest.Response;
import rest.Route;
import rest.Response.NotFoundException;

/**
 * The HouseController contains all the methods concerning Houses that can be accessed via the REST API
 * 
 * @author Johanna Kirsch
 */
public class HouseController {
    private House house;

    /**
     * Constructor with house initialization
     * 
     * @param house the house object that the operations defined in the methods will be performed upon
     */
    public HouseController(House house) {
	if(null == house) {
	    throw new IllegalArgumentException("Parameter house cannot be null");
	}
	this.house = house;
    }


    /**
     * Get information on the house object: address and a list of inhabitants
     * 
     * @return a JsonResponse with status HTTP_OK
     * @throws JSONCodecException if the object cannot be serialized
     */
    @Route(path="/houseinfo", method="GET")
    public Response getHouseInfo() throws JSONCodecException {
	return new JsonResponse(house, Response.HTTP_OK);
    }

    /**
     * Get information on an inhabitant identified by their ID
     * 
     * @param id inhabitantId of the inhabitant to search for
     * @return JSONResponse with the inhabitant's data or a message if they cannot be found
     * @throws JSONCodecException if the object cannot be serialized
     */
    @Route(path="/inhabitantById", method="GET")
    public Response getInhabitantById(int id) throws JSONCodecException {
	for(Person person : this.house.getInhabitants()){
	    if(person.getInhabitantId() == id) {

		return new JsonResponse(person, Response.HTTP_OK);

	    }
	}

	return new JsonResponse(new NotFoundException("Could not find inhabitant with id " + id));
    }


    /**
     * Get all inhabitants that fulfill a given condition. A condition is composed of three parts: 
     * First, the property to be used for comparison. This can be any property of the Person class.
     * Second, the comparison operator. Supported operators for integer fields are: =, !=, >, >=, {@literal <} and {@literal <}=. Supported operators for String fields are: = and !=.
     * Third, the value that the person's field's value should be compared to.
     * 
     * Example: Get all adult inhabitants
     * property: age, operator: >=, value: 18
     * 
     * 
     * @param property the name of a field of the Person class
     * @param operator a comparison operator such as = and !=
     * @param value a value to compare to. This is a String but it must be possible to cast it to the type of the property
     * @return a JSON representation of a list of persons, empty list if no inhabitant satisfies the condition
     * @throws JSONCodecException if the object cannot be serialized
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     */
    @Route(path="/inhabitantsByCondition", method="GET")
    public Response getInhabitantsByCondition(String property, String operator, String value) throws JSONCodecException, IllegalArgumentException, IllegalAccessException {
	if(property.equals("") || operator.equals("") || value.equals("")) {
	    return new JsonResponse(new IllegalArgumentException("Empty strings are not valid parameters."));
	}

	ArrayList<Person> validPersons = new ArrayList<Person>();
	try {
	    Field field = Person.class.getDeclaredField(property);
	    field.setAccessible(true);
	    for(Person p : house.getInhabitants()) {

		switch(field.getType().getTypeName()) {
		case "int":
		    int queryValue = Integer.parseInt(value);
		    int fieldValue;

		    fieldValue = (int)field.get(p);


		    if(operator.equals("=") && fieldValue == queryValue) {
			validPersons.add(p);

		    }else if(operator.equals("!=") && fieldValue != queryValue) {
			validPersons.add(p);

		    }else if(operator.equals(">") && fieldValue > queryValue) {
			validPersons.add(p);

		    }else if(operator.equals("<") && fieldValue < queryValue) {
			validPersons.add(p);

		    }else if(operator.equals(">=") && fieldValue >= queryValue) {
			validPersons.add(p);

		    }else if(operator.equals("<=") && fieldValue <= queryValue) {
			validPersons.add(p);
		    }

		    break;
		case "java.lang.String":
		    if(operator.equals("=") && field.get(p).equals(value)) {
			validPersons.add(p);

		    }else if(operator.equals("!=") && !field.get(p).equals(value)) {
			validPersons.add(p);
		    }

		    break;
		default:
		    return new JsonResponse(new UnsupportedOperationException("Fields of type " + field.getType().getTypeName() + " are not supported"));
		}

	    }

	} catch (NoSuchFieldException e) {
	    return new JsonResponse(new IllegalArgumentException("Class Person does not have a property called " + property));
	} catch(NumberFormatException e) {
	    return new JsonResponse(new IllegalArgumentException(value + " cannot be converted"));
	} 


	return new JsonResponse(validPersons);

    }

    /**
     * Removes an inhabitant from the house based on the inhabitantId
     * 
     * @param id of the person to remove
     * @return the data of the person that has just been removed
     */
    @Route(path="/delete", method="DELETE")
    public Response deleteInhabitant(int id) {
	for(Person person : this.house.getInhabitants()){
	    if(person.getInhabitantId() == id) {
		this.house.removeInhabitant(person);

		try {
		    return new JsonResponse(person);
		} catch (JSONCodecException e) {
		    return new JsonResponse(e);
		}
	    }
	}

	return new JsonResponse(new NotFoundException("Could not find inhabitant with id " + id));
    }

    /**
     * Adds one inhabitant to the house if the ID is not already contained
     * Returns the state of the entire house
     * 
     * @param json JSON representation of a Person object
     * @return updated JSON representation of the house object
     */
    @Route(path="/add", method="POST")
    public Response addInhabitant(String json) {
	Person person = new Person();
	try {
	    person.fromJSON(json);
	    this.house.addInhabitant(person);

	    return new JsonResponse(house);
	}catch(Exception e) {
	    return new JsonResponse(e);
	}
    }

    /**
     * Modifies one property of a Person object identified by their inhabitantID
     * 
     * @param inhabitantId the unique identifier of the person
     * @param property the name of the property to be modified. Must be a field of the Person class
     * @param newValue the value to assign to the property
     * @return a JSON representation of the modified object
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws JSONCodecException 
     * @throws InvocationTargetException 
     */
    @Route(path="/modify", method="PATCH")
    public Response modifyInhabitant(int inhabitantId, String property, String newValue) throws IllegalArgumentException, IllegalAccessException, JSONCodecException, InvocationTargetException {
	if(null == property || property.equals("") || null == newValue || newValue.equals("")) {
	    throw new IllegalArgumentException("Property and newValue cannot be emtpy or null");
	}
	for(Person p : house.getInhabitants()) {
	    if(inhabitantId == p.getInhabitantId()) {
		try {
		    Field field = Person.class.getDeclaredField(property);
		    field.setAccessible(true);

		    PropertyDescriptor pd;
		    Method setter;

		    switch(field.getType().getTypeName()) {
		    case "int":
			int value = Integer.parseInt(newValue);

			// invoke setter to include the checks performed there
			pd = new PropertyDescriptor(property, Person.class);
			setter = pd.getWriteMethod();
			setter.invoke(p, value);

			break;
		    case "java.lang.String":
			pd = new PropertyDescriptor(property, Person.class);
			setter = pd.getWriteMethod();
			setter.invoke(p, newValue);
			break;
		    default:
			return new JsonResponse(new UnsupportedOperationException("Fields of type " + field.getType().getName() + " are not supported"));
		    }

		}catch(NoSuchFieldException|IntrospectionException e) {
		    return new JsonResponse(new IllegalArgumentException("Class Person does not have a property called " + property));
		}catch(NumberFormatException e) {
		    return new JsonResponse(new IllegalArgumentException(newValue + " cannot be converted"));
		}catch(IllegalArgumentException e) {
		    return new JsonResponse(e);
		} 

		return new JsonResponse(p);
	    }
	}
	return new JsonResponse(new NotFoundException("No person with ID " + inhabitantId + " found"));
    }

}
