package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println("Small test for the IlpDataObjects");

        var orderStatus = OrderStatus.DELIVERED;

        System.out.println("IlpDataObjects.jar was used");

        RestApiClient client = new RestApiClient();
        client.getRestaurants();
    }
}
