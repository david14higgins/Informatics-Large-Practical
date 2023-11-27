package uk.ac.ed.inf.output;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.ac.ed.inf.constant.Direction;
import uk.ac.ed.inf.constant.OutputPath;
import uk.ac.ed.inf.interfaces.OutputWriter;
import uk.ac.ed.inf.routing.LngLatPair;
import uk.ac.ed.inf.routing.MoveInfo;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.File;
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
                    LngLat source = sourceDestinationPair.getSourceLngLat();
                    LngLat destination = sourceDestinationPair.getDestinationLngLat();
                    Direction direction = move.getDirection();

                    //Create move JSON object and add to movesArrayNode
                    ObjectNode moveJsonNode = objectMapper.createObjectNode();
                    moveJsonNode.put("orderNo", order.getOrderNo());
                    moveJsonNode.put("fromLongitude", source.lng());
                    moveJsonNode.put("fromLatitude", source.lat());
                    moveJsonNode.put("angle", direction.getAngle());
                    moveJsonNode.put("toLongitude", destination.lng());
                    moveJsonNode.put("toLatitude", destination.lat());
                    movesArrayNode.add(moveJsonNode);
                }
            }
        }
        // Write JSON array to a file
        String outputDirectory = OutputPath.PATH + "/";
        //Path outputPath = Paths.get(outputDirectory, fileName + ".json");
        try {
            File outputFile = new File(outputDirectory + fileName + ".json");
            objectMapper.writeValue(outputFile, movesArrayNode);
            System.out.println("Flightpath JSON file created successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while creating the flightpath JSON file.");
        }


    }
}
