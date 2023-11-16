package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.Pizza;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

//This class will be the centre point of the program
public class SystemController {

    //Entry point to program
    //Command line parameters are the REST API base URL and the date - Needs validating!
    public static void main(String[] args) {

        //Fetch data from REST API
        RestApiClient client = new RestApiClient(args);
        Restaurant[] restaurants = client.getRestaurants();
        NamedRegion[] noFlyZones = client.getNoFlyZones();
        NamedRegion centralArea = client.getCentralArea();
        Order[] ordersByDate = client.getOrderByDate();

        //Setup OrderValidator and LngLatHandler
        OrderValidator orderValidator = new OrderValidator();
        RoutePlanner routePlanner = new RoutePlanner();
        LngLat appletonTower = new LngLat(-3.186874, 55.944494);
        LngLat testSource = new LngLat(-3.1855, 55.9436);

        //Routes Hashtable key = restaurant name, value = lnglat route
        HashMap<String, ArrayList<LngLat>> routesTable = new HashMap<>();
        ArrayList<LngLat> dailyRoute = new ArrayList<>();

        //Iterate through all fetched orders for that day
        for (Order order : ordersByDate) {
            order = orderValidator.validateOrder(order, restaurants);
            if (order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {
                //Get the order's restaurant
                Restaurant orderRestaurant = findOrderRestaurant(order, restaurants);
                //System.out.println(orderRestaurant.name());
                //If route has not already been found, find route and add to hashtable

                assert orderRestaurant != null;
                if(!routesTable.containsKey(orderRestaurant.name())) {
                    LngLat destination = orderRestaurant.location();
                    ArrayList<LngLat> route = routePlanner.planRoute(appletonTower, destination, noFlyZones, centralArea);
                    routesTable.put(orderRestaurant.name(), route);
                }
                ArrayList<LngLat> sourceToDestinationRoute = routesTable.get(orderRestaurant.name());
                //Create a copy and reverse it
                ArrayList<LngLat> destinationToSourceRoute = new ArrayList<>(sourceToDestinationRoute);
                Collections.reverse(destinationToSourceRoute);

                //Add trip to daily route
                dailyRoute.addAll(sourceToDestinationRoute);
                dailyRoute.addAll(destinationToSourceRoute);
            }
        }
        GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
        geoJsonWriter.writeToGeoJson(dailyRoute, "drone-" + args[0]);
    }

    //Returns the LngLat location of the restaurant in a valid order
    private static Restaurant findOrderRestaurant(Order order, Restaurant[] restaurants) {
        Pizza firstPizzaInOrder = order.getPizzasInOrder()[0];
        //Iterate through possible restaurants
        for (Restaurant restaurant : restaurants) {
            //Search menu for this pizza
            for(Pizza pizzaOnMenu : restaurant.menu()) {
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
