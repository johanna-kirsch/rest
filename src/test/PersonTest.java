package test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import entity.Person;
import entity.JSONCodec.JSONCodecException;

class PersonTest {
    
    private static String FILEPATH = "data/person.json";

    @Test
    void testToJsonHappyPath() {
	Person p = new Person(1, "Jon Doe", 38);
	try {
	    String personString = p.toJSON();

	    assertTrue(personString.equals("{\"inhabitantId\":\"1\",\"fullName\":\"Jon Doe\",\"age\":\"38\"}"));

	} catch (JSONCodecException e) {
	    fail(e.getMessage());
	}
    }

    @Test
    void testFromJsonHappyPath() {
	Person p = new Person();
	try {
	    String personString = "{\"inhabitantId\":\"1\",\"fullName\":\"Jon Doe\",\"age\":\"38\"}";
	    p.fromJSON(personString);

	    assertEquals("Jon Doe", p.getFullName());
	    assertEquals(38, p.getAge());

	} catch (JSONCodecException e) {
	    fail(e.getMessage());
	}
    }

    @Test
    void testToAndFromJson() {
	Person p = new Person(1, "Jon Doe", 38);
	try {
	    String personString = p.toJSON();
	    Person p2 = new Person();
	    p2.fromJSON(personString);

	    assertTrue(p.equals(p2));

	} catch (JSONCodecException e) {
	    fail(e.getMessage());
	}
    }

    @Test
    void testToJsonDefaultConstructor() {
	Person p = new Person();
	assertThrows(JSONCodecException.class, ()->{ p.toJSON(); });
    }

    @Test
    void testFromJsonInvalidString() {
	Person p = new Person();
	assertThrows(JSONCodecException.class, ()->{ p.fromJSON("abc:cde");});
    }

    @Test
    void testFromJsonInvalidJson() {
	Person p = new Person();
	assertThrows(JSONCodecException.class, ()->{ p.fromJSON("{fullName:\"Jon Doe\",\"age\":38}");});
    }

    @Test
    void testFromJsonFloatInsteadOfInt() {
	Person p = new Person();
	assertThrows(NumberFormatException.class, ()->{ p.fromJSON("{\"inhabitantId\":\"1\",\"fullName\":\"Jon Doe\",\"age\":\"38.5\"}");});
    }
    
    @Test
    void testToFile() {
	Person p = new Person(1, "Jon Doe", 38);
	try {
	    p.toFile(FILEPATH);
	    File f = new File(FILEPATH);
	    
	    assertTrue(f.exists());
	    assertTrue(f.canRead());
	    
	    String content = "";
	    Scanner reader = new Scanner(f);
	    while(reader.hasNextLine()) {
		content += reader.nextLine();
	    }
	    reader.close();
	    
	    assertTrue(content.equals(p.toJSON()));
	    

	} catch (JSONCodecException | IOException e) {
	    fail(e.getMessage());
	} 
    }
    
    @Test
    void testToFileOverrideExistingFile() {
	Person p = new Person(4, "Alice Doe", 37);
	try {
	    p.toFile(FILEPATH);
	    File f = new File(FILEPATH);
	    if(!f.exists()) {
		    f.createNewFile();
		}
	    
	    
	    String content = "";
	    Scanner reader = new Scanner(f);
	    while(reader.hasNextLine()) {
		content += reader.nextLine();
	    }
	    reader.close();
	    
	    assertTrue(content.equals(p.toJSON()));
	    

	} catch (JSONCodecException | IOException e) {
	    fail(e.getMessage());
	}
    }
}
