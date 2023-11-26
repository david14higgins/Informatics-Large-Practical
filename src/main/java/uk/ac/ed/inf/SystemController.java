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
import uk.ac.ed.inf.output.GeoJsonWriter;
import uk.ac.ed.inf.routing.MoveInfo;
import uk.ac.ed.inf.routing.RoutePlanner;

import java.util.ArrayList;
import java.util.HashMap;

//This class will be the centre point of the program
public class SystemController {



    /* ------------------- UP NEXT -------------------------------

    RoutePlanner should return a series of RouteStep objects which contain the lnglat positions of source and destination
    and the angle between them.

    The GeoJsonWriter then needs to be adjusted to build the route using this new data structure

     */

    //Entry point to program
    //Command line parameters are the REST API base URL and the date - Needs validating!
    public static void main(String[] args) {

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
            order = orderValidator.validateOrder(order, restaurants);
            if (order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {
                //Get the order's restaurant
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
                ArrayList<MoveInfo> route = routesTable.get(orderRestaurant.name());
                //Add trip to daily route
                dailyRoute.addAll(route);

                //Update order status
                order.setOrderStatus(OrderStatus.DELIVERED);
            }

        }

        //Write daily route to GeoJSON file
        GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
        geoJsonWriter.writeToGeoJson(dailyRoute, "drone-" + args[0]);

        //Write flightpath data to JSON file
        FlightpathJsonWriter flightpathJsonWriter = new FlightpathJsonWriter();
        flightpathJsonWriter.writeToJson(ordersByDate, ordersRestaurant, routesTable, "flightpath-" + args[0]);

        //Write deliveries to JSON file
        DeliveriesJsonWriter deliveriesJsonWriter = new DeliveriesJsonWriter();
        deliveriesJsonWriter.writeToJson(ordersByDate, "deliveries-" + args[0]);


        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
    }


    //Returns the LngLat location of the restaurant in a valid order
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
        //This method will only be used on a valid order so the restaurant should be found
        return null;
    }
}
