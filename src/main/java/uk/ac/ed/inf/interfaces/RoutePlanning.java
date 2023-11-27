package uk.ac.ed.inf.interfaces;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.routing.MoveInfo;

import java.util.ArrayList;


public interface RoutePlanning {

    /**
     * Plans a route from source to destination avoiding no-fly-zones and entering central area only once
     * @param source LngLat position the route starts at
     * @param destination LngLat position the route ends at
     * @param noFlyZones NamedRegions which the route planner must avoid
     * @param centralArea NamedRegion which the route cannot leave once entered
     * @return A list of moves representing the route
     */
    ArrayList<MoveInfo> planRoute(LngLat source, LngLat destination, NamedRegion[] noFlyZones, NamedRegion centralArea);

    /**
     * Reverses a given route
     * @param route The route to be reversed
     * @return The reversed route
     */
    ArrayList<MoveInfo> reverseRoute(ArrayList<MoveInfo> route);
}
