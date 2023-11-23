package uk.ac.ed.inf;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

public class FlightpathJsonWriter {
    public static void main(String[] args) {
        // Create ObjectMapper instance from Jackson library
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Enable pretty-printing

        // Create an array node to store JSON objects
        ArrayNode arrayNode = objectMapper.createArrayNode();

        // Create JSON objects and add them to the array
        ObjectNode person1 = objectMapper.createObjectNode();
        person1.put("id", 1);
        person1.put("name", "John Doe");
        person1.put("age", 30);
        person1.put("email", "john@example.com");
        arrayNode.add(person1);

        ObjectNode person2 = objectMapper.createObjectNode();
        person2.put("id", 2);
        person2.put("name", "Jane Smith");
        person2.put("age", 25);
        person2.put("email", "jane@example.com");
        arrayNode.add(person2);

        // Write JSON array to a file
        try {
            File outputFile = new File("output.json");
            objectMapper.writeValue(outputFile, arrayNode);
            System.out.println("JSON data has been written to output.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
