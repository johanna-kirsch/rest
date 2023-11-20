package entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * A house has an address and a list of inhabitants of type Person. The address is the house's identifier and it is used when comparing two house objects
 *
 * @author Johanna Kirsch
 */
public class House implements JSONCodec {

    private String jsonFormat = "'{'\"address\":\"{0}\",\"inhabitants\":[{1}]'}'";

    private String address;

    private ArrayList<Person> inhabitants;

    /**
     * Constructor without any parameters needed to initialize a house object before calling fromJSON on it
     */
    public House() {
	inhabitants = new ArrayList<Person>();
    }

    /**
     * Constructor to use when a house is created without using the method fromJSON
     * 
     * @param address a non-empty string
     */
    public House(String address) {
	if(null == address || "" == address) {
	    throw new IllegalArgumentException("Address cannot be null");
	}
	this.address = address;
	inhabitants = new ArrayList<Person>();
    }

    /**
     * Getter for field address
     * 
     * @return address
     */
    public String getAddress() {
	return address;
    }

    /**
     * Getter for field inhabitants
     * 
     * @return inhabitants of type ArrayList containing Persons
     */
    public ArrayList<Person> getInhabitants() {
	return inhabitants;
    }


    /**
     * Adds a person to the inhabitants list if it does not already contain a person with the given ID
     * 
     * @param person Person object, not null
     */
    public void addInhabitant(Person person) {
	if(null == person) {
	    throw new IllegalArgumentException("Person cannot be null");
	}

	for(Person p : inhabitants) {
	    if(p.equals(person)) {
		throw new IllegalArgumentException("Cannot add person with id " + person.getInhabitantId() + " because id already exists in this house.");
	    }
	}
	inhabitants.add(person);
    }

    /**
     * Removes the given Person from the inhabitants list if the list contains it
     * 
     * @param person Person to remove, not null
     */
    public void removeInhabitant(Person person) {
	if(null == person) {
	    throw new IllegalArgumentException("Person cannot be null");
	}

	if(inhabitants.contains(person)) {
	    inhabitants.remove(person);
	}else {
	    throw new IllegalArgumentException("Cannot remove person " + person + ". Entry does not exist");
	}
    }


    @Override
    public String toJSON() throws JSONCodecException {
	if(null == address || "" == address) {
	    throw new JSONCodecException("Address cannot be null");
	}
	if(null == inhabitants || 0 == inhabitants.size()) {
	    return MessageFormat.format(jsonFormat, address, "");
	}

	ArrayList<String> jsonInhabitants = new ArrayList<String>();

	for(Person p : inhabitants) {
	    jsonInhabitants.add(p.toJSON());
	}

	return MessageFormat.format(jsonFormat, address, String.join(",", jsonInhabitants));
    }

    @Override
    public void fromJSON(String json) throws JSONCodecException {
	if(null == json || "" == json) {
	    throw new JSONCodecException("Json string cannot be null or empty");
	}

	MessageFormat mf = new MessageFormat(jsonFormat);
	String address;
	String inhabitantsString;

	try {
	    Object[] parsed = mf.parse(json);

	    address = (String) parsed[0];
	    inhabitantsString = (String)parsed[1];


	    Matcher m = Pattern.compile("(\\{[^}]+\\})").matcher(inhabitantsString);

	    while(m.find()) {
		Person p = new Person();
		p.fromJSON(m.group(1));
		this.addInhabitant(p);
	    }

	} catch (ParseException e) {
	    throw new JSONCodecException(e.getMessage());
	}

	this.address = address;

    }

    /**
     * Compares two House objects based on their addresses
     * 
     * @param object The object to compare to, of type House
     * @return true if addresses are equal
     */
    @Override
    public boolean equals(Object object) {
	if(!(object instanceof House)) {
	    return false;
	}

	House h = (House)object;

	return address.equals(h.getAddress());

    }

    @Override
    public void toFile(String filepath) throws JSONCodecException, IOException {	
	File file = new File(filepath);
	file.createNewFile();
	FileWriter writer = new FileWriter(file, false);
	writer.write(this.toJSON());
	writer.close();
    }
}
