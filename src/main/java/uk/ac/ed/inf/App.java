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
        HashMap<LngLatPair, Direction> moveDirection = new HashMap<>();
        LngLat pos1 = new LngLat(50, 50);
        LngLat pos2 = new LngLat(50, 100);
        LngLatPair pair = new LngLatPair(pos1, pos2);

        moveDirection.put(pair, Direction.SOUTH);

        LngLat newpos1 = new LngLat(50, 50);
        LngLat newpos2 = new LngLat(50, 100);
        LngLatPair newPair = new LngLatPair(newpos1, newpos2);

        //System.out.println(moveDirection.get(newPair).getAngle());
        LngLat source = new LngLat(-3.187024, 55.944494);
        LngLatHandler lngLatHandler = new LngLatHandler();
        LngLat destination = lngLatHandler.nextPosition(source, 157.5);
        System.out.println(destination.lng() + " " + destination.lat());

    }
}
