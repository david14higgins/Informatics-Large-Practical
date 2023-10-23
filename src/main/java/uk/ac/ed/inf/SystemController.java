package uk.ac.ed.inf;

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


    }
}
