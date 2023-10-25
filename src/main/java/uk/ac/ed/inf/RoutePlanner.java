package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.ArrayList;

public class RoutePlanner {

    /*Use A* Search to plan a route from source to destination that
        - Does not enter no fly zones
        - Does not leave central area once it has entered
     */
    public ArrayList<MoveRecord> planRoute(LngLat source, LngLat destination, NamedRegion[] noFlyZones, NamedRegion centralArea) {
        return null;
    }

}
