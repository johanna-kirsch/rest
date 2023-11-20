package entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;

import entity.JSONCodec.JSONCodecException;

/**
 * 
 * A Person is an inhabitant of a house. They are identified by their inhabitantId
 * 
 * @author Johanna Kirsch
 *
 */
public class Person implements JSONCodec {

    private String jsonFormat = "'{'\"inhabitantId\":\"{0}\",\"fullName\":\"{1}\",\"age\":\"{2}\"'}'";


    private final int minAge = 0;
    private final int maxAge = 120;

    private int inhabitantId;
    private String fullName;

    private int age;

    /**
     * Constructor to use when a person is created without using the method fromJSON
     * 
     * @param inhabitantId int unique ID between all inhabitants of a house
     * @param name Name of person, not empty
     * @param age The current age of the person. Must be between 0 and 120
     */
    public Person(int inhabitantId, String name, int age) {
	if(null == name || "" == name) {
	    throw new IllegalArgumentException("Name cannot be null");
	}
	if(age > maxAge || age < minAge) {
	    throw new IllegalArgumentException("Age must be between " + minAge + " and " + maxAge);
	}
	if(inhabitantId < 1) {
	    throw new IllegalArgumentException("ID cannot be smaller than 1");
	}
	this.inhabitantId = inhabitantId;
	this.fullName = name;
	this.age = age;
    }

    /**
     * Constructor without any parameters needed to initialize a person object before calling fromJSON on it
     */
    public Person() {}

    /**
     * Getter InhabitantID
     * @return inhabitantID
     */
    public int getInhabitantId() {
	return inhabitantId;
    }

    /**
     * Getter fullName
     * @return fullName
     */
    public String getFullName() {
	return fullName;
    }

    /**
     * Setter fullName
     * @param fullName not null or empty
     */
    public void setFullName(String fullName) {
	if(null == fullName || "" == fullName) {
	    throw new IllegalArgumentException("Name cannot be null");
	}
	this.fullName = fullName;
    }

    /**
     * Getter current age
     * @return age
     */
    public int getAge() {
	return age;
    }

    /**
     * Setter current age
     * @param age integer between 0 and 120
     */
    public void setAge(int age) {
	if(age > maxAge || age < minAge) {
	    throw new IllegalArgumentException("Age must be between " + minAge + " and " + maxAge);
	}
	this.age = age;
    }

    @Override
    public String toJSON() throws JSONCodecException {
	if(null == fullName || "" == fullName) {
	    throw new JSONCodecException("Cannot serialize Person where fullName is null");
	}
	return MessageFormat.format(jsonFormat, this.inhabitantId, this.fullName, this.age);
    }

    @Override
    public void fromJSON(String json) throws JSONCodecException {
	if(null == json || "" == json) {
	    throw new JSONCodecException("Json string cannot be null or empty");
	}

	MessageFormat mf = new MessageFormat(jsonFormat);
	int inhabitantId;
	String name;
	int age;
	try {
	    Object[] parsed = mf.parse(json);

	    inhabitantId = Integer.parseInt((String)parsed[0]);
	    name = (String) parsed[1];
	    age = Integer.parseInt((String)parsed[2]);

	} catch (ParseException e) {
	    throw new JSONCodecException(e.getMessage());
	}
	this.inhabitantId = inhabitantId;
	this.fullName = name;
	this.age = age;

    }

    /**
     * Compares two Person objects based on their inhabitantIds
     * 
     * @param object The object to compare to, of type Person
     * @return true if inhabitantIds are equal
     */
    @Override
    public boolean equals(Object object) {
	if(!(object instanceof Person)) {
	    return false;
	}
	Person p = (Person)object;

	return p.inhabitantId == this.inhabitantId;
    }

    @Override
    public void toFile(String filepath) throws JSONCodecException, IOException {	
	File file = new File(filepath);
	if(!file.exists()) {
	    file.createNewFile();
	}
	FileWriter writer = new FileWriter(file, false);
	writer.write(this.toJSON());
	writer.close();
    }

}
