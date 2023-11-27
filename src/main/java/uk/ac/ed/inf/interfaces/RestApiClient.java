package uk.ac.ed.inf.interfaces;

import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

public interface RestApiClient {

    /**
     * @return Array of Restaurant objects from the rest API
     */
    Restaurant[] getRestaurants();

    /**
     * @return Array of NamedRegion objects representing the no-fly-zones from the rest API
     */
    NamedRegion[] getNoFlyZones();

    /**
     * @return NamedRegion object representing the central area from the rest API
     */
    NamedRegion getCentralArea();

    /**
     * @return An array of Order objects for the date passed to the client from the rest API
     */
    Order[] getOrderByDate();
}
