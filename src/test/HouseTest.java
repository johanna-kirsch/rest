package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import entity.House;
import entity.Person;
import entity.JSONCodec.JSONCodecException;

class HouseTest {
    private static String FILEPATH = "data/house.json";

    @Test
    void testToJsonHappyPath() {
	House h = new House("1, Rue Principale, 57070 Metz");

	Person p1 = new Person(1, "Jon Doe", 38);
	Person p2 = new Person(2, "Alice", 20);

	h.addInhabitant(p1);
	h.addInhabitant(p2);

	try {
	    String jsonString = h.toJSON();

	    assertTrue(jsonString.equals("{\"address\":\"1, Rue Principale, 57070 Metz\",\"inhabitants\":[{\"inhabitantId\":\"1\",\"fullName\":\"Jon Doe\",\"age\":\"38\"},{\"inhabitantId\":\"2\",\"fullName\":\"Alice\",\"age\":\"20\"}]}"));
	} catch (JSONCodecException e) {
	    fail(e.getMessage());
	}
    }

    @Test
    void testToJsonDefaultConstructor() {
	House h = new House();
	assertThrows(JSONCodecException.class, ()->{ h.toJSON(); });
    }
    
    @Test
    void testAddDuplicatePerson() {
	House h = new House();
	Person p1 = new Person(1, "Jon Doe", 38);
	Person p2 = new Person(1, "Alice Doe", 38);
	
	h.addInhabitant(p1);
	
	assertThrows(IllegalArgumentException.class, () -> {h.addInhabitant(p2);});
	
	assertEquals(h.getInhabitants().size(), 1);
	assertTrue(p1.equals(h.getInhabitants().get(0)));
    }

    @Test
    void testFromJsonHappyPath() {
	String json = "{\"address\":\"1, Rue Principale, 57070 Metz\",\"inhabitants\":[{\"inhabitantId\":\"1\",\"fullName\":\"Jon Doe\",\"age\":\"38\"},{\"inhabitantId\":\"2\",\"fullName\":\"Alice\",\"age\":\"20\"}]}";

	House h = new House();
	try {
	    h.fromJSON(json);
	} catch (JSONCodecException e) {
	    fail(e.getMessage());
	}

	assertEquals("1, Rue Principale, 57070 Metz", h.getAddress());

	assertEquals(2, h.getInhabitants().size());

	assertEquals("Jon Doe", h.getInhabitants().get(0).getFullName());
	assertEquals(38, h.getInhabitants().get(0).getAge());
	assertEquals("Alice", h.getInhabitants().get(1).getFullName());
	assertEquals(20, h.getInhabitants().get(1).getAge());
    }


    @Test
    void testToAndFromJson() {
	House h = new House("1, Rue Principale, 57070 Metz");

	Person p1 = new Person(1, "Jon Doe", 38);
	Person p2 = new Person(2, "Alice", 20);

	h.addInhabitant(p1);
	h.addInhabitant(p2);

	House h2 = new House();
	try {
	    h2.fromJSON(h.toJSON());
	} catch (JSONCodecException e) {
	    fail(e.getMessage());
	}
	assertTrue(h.equals(h2));
    }
    
    @Test
    void testToFile() {
	House h = new House("1, Rue Principale, 57070 Metz");

	Person p1 = new Person(1, "Jon Doe", 38);
	Person p2 = new Person(2, "Alice", 20);

	h.addInhabitant(p1);
	h.addInhabitant(p2);
	
	try {
	    h.toFile(FILEPATH);
	    File f = new File(FILEPATH);
	    
	    assertTrue(f.exists());
	    assertTrue(f.canRead());
	    
	    String content = "";
	    Scanner reader = new Scanner(f);
	    while(reader.hasNextLine()) {
		content += reader.nextLine();
	    }
	    reader.close();
	    
	    assertTrue(content.equals(h.toJSON()));
	    

	} catch (JSONCodecException | IOException e) {
	    fail(e.getMessage());
	} 
    }
    
    @Test
    void testToFileOverrideExistingFile() {
	House h = new House("10, Boulevard de Gaulle, Paris");

	Person p1 = new Person(1, "Jon Doe", 38);
	Person p2 = new Person(2, "Alice", 20);

	h.addInhabitant(p1);
	h.addInhabitant(p2);
	try {
	    h.toFile(FILEPATH);
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
	    
	    assertTrue(content.equals(h.toJSON()));
	    

	} catch (JSONCodecException | IOException e) {
	    fail(e.getMessage());
	}
    }

}
