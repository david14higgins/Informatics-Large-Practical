package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

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
        LngLatHandler lngLatHandler = new LngLatHandler();

        //Iterate through all fetched orders for that day
        for (Order order : ordersByDate) {
            order = orderValidator.validateOrder(order, restaurants);
            if (order.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {
                //Plan drone route to and from restaurant and write to files



            }

            //Need to write order to deliveries file regardless of whether the order was delivered

        }


    }
}
