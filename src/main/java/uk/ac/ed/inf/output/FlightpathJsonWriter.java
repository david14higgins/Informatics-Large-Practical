package uk.ac.ed.inf.output;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.ac.ed.inf.interfaces.OutputWriter;
import uk.ac.ed.inf.routing.LngLatPair;
import uk.ac.ed.inf.routing.MoveInfo;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FlightpathJsonWriter implements OutputWriter {

    private final Order[] orders;
    private final HashMap<String, String> ordersRestaurant;
    private final HashMap<String, ArrayList<MoveInfo>> routesTable;

    public FlightpathJsonWriter(Order[] orders,
                                HashMap<String, String> ordersRestaurant,
                                HashMap<String, ArrayList<MoveInfo>> routesTable) {
        this.orders = orders;
        this.ordersRestaurant = ordersRestaurant;
        this.routesTable = routesTable;
    }

    @Override
    public void writeToFile(String fileName) {
        // Create ObjectMapper instance from Jackson library
        ObjectMapper objectMapper = new ObjectMapper();
        // Enable pretty-printing
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // ArrayNode will store list of moves
        ArrayNode movesArrayNode = objectMapper.createArrayNode();

        //Iterate through the delivered orders
        for (Order order : orders) {
            if(order.getOrderStatus() == OrderStatus.DELIVERED) {
                //Get restaurant name that order was for
                String restaurantName = ordersRestaurant.get(order.getOrderNo());
                //Use the name to get the route information to that restaurant and back
                ArrayList<MoveInfo> route = routesTable.get(restaurantName);

                //Iterate through moves in route and add to movesArrayNode
                for (MoveInfo move : route) {
                    LngLatPair sourceDestinationPair = move.getSourceToDestinationPair();
                    LngLat source = sourceDestinationPair.sourceLngLat();
                    LngLat destination = sourceDestinationPair.destinationLngLat();
                    double angle = move.getAngle();

                    //Create move JSON object and add to movesArrayNode
                    ObjectNode moveJsonNode = objectMapper.createObjectNode();
                    moveJsonNode.put("orderNo", order.getOrderNo());
                    moveJsonNode.put("fromLongitude", source.lng());
                    moveJsonNode.put("fromLatitude", source.lat());
                    moveJsonNode.put("angle", angle);
                    moveJsonNode.put("toLongitude", destination.lng());
                    moveJsonNode.put("toLatitude", destination.lat());
                    movesArrayNode.add(moveJsonNode);
                }
            }
        }

        // Write JSON array to a file
        try {
            File outputFile = new File("resultfiles/" + fileName + ".json");
            outputFile.getParentFile().mkdirs();
            FileWriter outputFileWriter = new FileWriter(outputFile);
            outputFileWriter.write(objectMapper.writeValueAsString(movesArrayNode));
            outputFileWriter.close();
            System.out.println("Flightpath JSON file created successfully.");
        } catch (IOException e) {
            java.lang.System.err.println("An error occurred while creating the flightpath JSON file.");
            java.lang.System.exit(2);
        }


    }
}
