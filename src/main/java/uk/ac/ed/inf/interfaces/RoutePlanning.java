package uk.ac.ed.inf.interfaces;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.routing.MoveInfo;

import java.util.ArrayList;


public interface RoutePlanning {

    ArrayList<MoveInfo> planRoute(LngLat source, LngLat destination, NamedRegion[] noFlyZones, NamedRegion centralArea);

    ArrayList<MoveInfo> reverseRoute(ArrayList<MoveInfo> route);
}
