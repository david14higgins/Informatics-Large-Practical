package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;

import java.util.HashMap;

public class App 
{
    public static void main( String[] args )
    {
        //System.out.println("Small test for the IlpDataObjects");

        //var orderStatus = OrderStatus.DELIVERED;

        //System.out.println("IlpDataObjects.jar was used");

//        RestApiClient client = new RestApiClient();
//        Order[] orders = client.getOrderByDate("2023-10-06");
//        System.out.println(orders.length);
        System.out.println(OrderStatus.DELIVERED.toString());

    }
}
