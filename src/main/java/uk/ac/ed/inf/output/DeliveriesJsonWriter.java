package uk.ac.ed.inf.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.interfaces.OutputWriter;

import java.io.File;
import java.io.IOException;

public class DeliveriesJsonWriter implements OutputWriter {

    private final Order[] orders;

    public DeliveriesJsonWriter(Order[] orders) {
        this.orders = orders;
    }

    @Override
    public void writeToFile(String fileName) {
        // Create ObjectMapper instance from Jackson library
        ObjectMapper objectMapper = new ObjectMapper();
        // Enable pretty-printing
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // ArrayNode will store list of deliveries
        ArrayNode deliveriesArrayNode = objectMapper.createArrayNode();

        //Iterate through the delivered orders
        for (Order order : orders) {
            ObjectNode deliveryJsonNode = objectMapper.createObjectNode();
            deliveryJsonNode.put("orderNo", order.getOrderNo());
            deliveryJsonNode.put("orderStatus", order.getOrderStatus().toString());
            deliveryJsonNode.put("orderValidationCode", order.getOrderValidationCode().toString());
            deliveryJsonNode.put("costInPence", order.getPriceTotalInPence());
            deliveriesArrayNode.add(deliveryJsonNode);
        }
        // Write JSON array to a file
        String outputDirectory = "PizzaDronz/resultfiles/";
        //Path outputPath = Paths.get(outputDirectory, fileName + ".json");
        try {
            File outputFile = new File(outputDirectory + fileName + ".json");
            objectMapper.writeValue(outputFile, deliveriesArrayNode);
            System.out.println("Flightpath JSON file created successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while creating the flightpath JSON file.");
        }


    }
}
