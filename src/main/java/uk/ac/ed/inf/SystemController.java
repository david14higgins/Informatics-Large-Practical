package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.Pizza;

import java.util.ArrayList;

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
        LngLat appletonTower = new LngLat(-3.186874, 55.944494);

        //Iterate through all fetched orders for that day
        for (Order order : ordersByDate) {
            order = orderValidator.validateOrder(order, restaurants);
            if (order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {
                //Plan drone route to and from restaurant and write to files
                LngLat restaurantLocation = findRestaurantLocation(order, restaurants);
                RoutePlanner routePlanner = new RoutePlanner();
                ArrayList<MoveRecord> route = routePlanner.planRoute(appletonTower, restaurantLocation, noFlyZones, centralArea);
                //These moves will need to be written to the JSON files 
            }

            //Need to write order to deliveries file regardless of whether the order was delivered

        }
    }

    //Returns the LngLat location of the restaurant in a valid order
    private static LngLat findRestaurantLocation(Order order, Restaurant[] restaurants) {
        Pizza firstPizzaInOrder = order.getPizzasInOrder()[0];
        //Iterate through possible restaurants
        for (Restaurant restaurant : restaurants) {
            //Search menu for this pizza
            for(Pizza pizzaOnMenu : restaurant.menu()) {
                if (firstPizzaInOrder.name().equals(pizzaOnMenu.name()) &&
                    firstPizzaInOrder.priceInPence() == pizzaOnMenu.priceInPence()) {
                    //Pizza found, return restaurant location
                    return restaurant.location();
                }
            }
        }
        //This method will only be used on a valid order so the restaurant should be found
        return null;
    }


}
