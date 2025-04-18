package uk.ac.ed.inf.output;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.interfaces.OutputWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DeliveriesJsonWriter implements OutputWriter {

    //Stores all the orders to be written to the file
    private final Order[] orders;

    /**
     * Constructor which accepts all information to be written
     * @param orders The orders for the day
     */
    public DeliveriesJsonWriter(Order[] orders) {
        this.orders = orders;
    }

    /**
     * Writes the deliveries output file with all the orders and their status
     * @param fileName The name of the file to be produced/overwritten
     */
    @Override
    public void writeToFile(String fileName) {
        // Create ObjectMapper from Jackson
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
        try {
            File outputFile = new File("resultfiles/" + fileName + ".json");
            outputFile.getParentFile().mkdirs();
            FileWriter outputFileWriter = new FileWriter(outputFile);
            outputFileWriter.write(objectMapper.writeValueAsString(deliveriesArrayNode));
            outputFileWriter.close();
            System.out.println("Deliveries JSON file created successfully.");
        } catch (IOException e) {
            java.lang.System.err.println("An error occurred while creating the deliveries JSON file.");
            java.lang.System.exit(2);
        }


    }
}
