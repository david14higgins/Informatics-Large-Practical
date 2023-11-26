package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.input.RestApiClient;
import uk.ac.ed.inf.orders.OrderValidator;
import uk.ac.ed.inf.output.DeliveriesJsonWriter;
import uk.ac.ed.inf.output.FlightpathJsonWriter;
import uk.ac.ed.inf.output.DroneGeoJsonWriter;
import uk.ac.ed.inf.routing.MoveInfo;
import uk.ac.ed.inf.routing.RoutePlanner;

import java.util.ArrayList;
import java.util.HashMap;

//This class will be the centre point of the program
public class SystemController {



    /* ------------------- UP NEXT -------------------------------

    - JavaDoc comments
    - Comment all code
    - RestApiClient interface
    - Graceful error handling
    - Decide on A* or greedy
    - Implement no central area return policy
    - Build uber jar
    - Test on DICE student machine
    - Delete App class
    
     */

    //Entry point to program
    //Command line parameters are the REST API base URL and the date - Needs validating!
    public static void main(String[] args) {

        //Program timer
        long startTime = System.currentTimeMillis();

        //Fetch data from REST API
        RestApiClient client = new RestApiClient(args);
        Restaurant[] restaurants = client.getRestaurants();
        NamedRegion[] noFlyZones = client.getNoFlyZones();
        NamedRegion centralArea = client.getCentralArea();
        Order[] ordersByDate = client.getOrderByDate();

        //Setup OrderValidator
        OrderValidator orderValidator = new OrderValidator();

        RoutePlanner routePlanner = new RoutePlanner();
        LngLat appletonTower = new LngLat(-3.186874, 55.944494);

        //Create data structures which will store information about the routes and orders

        //Hashmap storing the route associated with every restaurant
        HashMap<String, ArrayList<MoveInfo>> routesTable = new HashMap<>();
        //Stores complete route for the given day
        ArrayList<MoveInfo> dailyRoute = new ArrayList<>();
        //Hashmap which maps all orders to their corresponding restaurant
        HashMap<String, String> ordersRestaurant = new HashMap<>();

        //Iterate through all fetched orders for that day
        for (Order order : ordersByDate) {
            //Validate the order
            order = orderValidator.validateOrder(order, restaurants);
            if (order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {
                //Use helper function to find the order's restaurant
                Restaurant orderRestaurant = findOrderRestaurant(order, restaurants);
                assert orderRestaurant != null;
                ordersRestaurant.put(order.getOrderNo(), orderRestaurant.name());

                //If route has not already been found, find route and add to hashtable
                if (!routesTable.containsKey(orderRestaurant.name())) {
                    LngLat destination = orderRestaurant.location();
                    ArrayList<MoveInfo> route = routePlanner.planRoute(appletonTower, destination, noFlyZones, centralArea);
                    ArrayList<MoveInfo> returnRoute = routePlanner.reverseRoute(route);
                    route.addAll(returnRoute);
                    routesTable.put(orderRestaurant.name(), route);
                }

                //Retrieve route information and add to daily route
                ArrayList<MoveInfo> route = routesTable.get(orderRestaurant.name());
                dailyRoute.addAll(route);

                //Update order status
                order.setOrderStatus(OrderStatus.DELIVERED);
            }
        }

        //Can now use information from data structures to write output files

        //Write daily route to GeoJSON file
        DroneGeoJsonWriter DroneGeoJsonWriter = new DroneGeoJsonWriter(dailyRoute);
        DroneGeoJsonWriter.writeToFile("drone-" + args[0]);

        //Write flightpath data to JSON file
        FlightpathJsonWriter flightpathJsonWriter = new FlightpathJsonWriter(ordersByDate, ordersRestaurant, routesTable);
        flightpathJsonWriter.writeToFile("flightpath-" + args[0]);

        //Write deliveries to JSON file
        DeliveriesJsonWriter deliveriesJsonWriter = new DeliveriesJsonWriter(ordersByDate);
        deliveriesJsonWriter.writeToFile("deliveries-" + args[0]);

        //Output program duration
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
    }


    //Returns the Restaurant of an order - order has been validated and should have one corresponding restaurant
    private static Restaurant findOrderRestaurant (Order order, Restaurant[]restaurants){
        Pizza firstPizzaInOrder = order.getPizzasInOrder()[0];
        //Iterate through possible restaurants
        for (Restaurant restaurant : restaurants) {
            //Search menu for this pizza
            for (Pizza pizzaOnMenu : restaurant.menu()) {
                if (firstPizzaInOrder.name().equals(pizzaOnMenu.name()) &&
                        firstPizzaInOrder.priceInPence() == pizzaOnMenu.priceInPence()) {
                        //Pizza found, return restaurant location
                    return restaurant;

                }
            }
        }
        //Should never return null
        return null;
    }
}
