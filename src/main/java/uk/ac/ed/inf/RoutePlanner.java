package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.lang.reflect.Array;
import java.util.*;

import static uk.ac.ed.inf.ilp.constant.SystemConstants.DRONE_MOVE_DISTANCE;

public class RoutePlanner {

    /*Use A* Search to plan a route from source to destination that
        - Does not enter no fly zones
        - Does not leave central area once it has entered
     */

    //Returns a list of LngLat positions in reverse order from destination to source
    public ArrayList<LngLat> planRoute(LngLat source, LngLat destination, NamedRegion[] noFlyZones, NamedRegion centralArea) {
        LngLatHandler lngLatHandler = new LngLatHandler();

        //Rather than using a node object, we opt for a series of hash tables to save memory
        //We can index the desired value using the position as a key
        HashMap<LngLat, Double> fValues = new HashMap<>();
        HashMap<LngLat, Double> gValues = new HashMap<>();
        HashMap<LngLat, Double> hValues = new HashMap<>();

        //Key is child, value is parent
        HashMap<LngLat, LngLat> parentOfChild = new HashMap<>();

        //Consider switching to a hashset
        //ArrayList<LngLat> openList = new ArrayList<>();
        PriorityQueue<LngLat> openList = new PriorityQueue<>((a, b) -> Double.compare(fValues.get(a), fValues.get(b)));
        Set<LngLat> closedList = new HashSet<>();

        gValues.put(source, 0.0);
        hValues.put(source, lngLatHandler.distanceTo(source, destination));
        fValues.put(source, gValues.get(source) + hValues.get(source));
        parentOfChild.put(source, null);

        openList.add(source);

        while(!openList.isEmpty()) {

            LngLat currentNode = openList.poll();
            closedList.add(currentNode);

            //Check to see if destination node has been found
            if (lngLatHandler.isCloseTo(currentNode, destination)) {
                ArrayList<LngLat> path = new ArrayList<>();
                while(currentNode != null) {
                    path.add(currentNode);
                    currentNode = parentOfChild.get(currentNode);
                }
                return path;
             }

            //Generate children nodes
            ArrayList<LngLat> children = new ArrayList<>();
            for (Direction direction : Direction.values()) {
                LngLat newPosition = lngLatHandler.nextPosition(currentNode, direction.getAngle());
                //Check child is not in a no-fly zone
                children.add(newPosition);
            }

            //Iterate through all children that are not in the closed list
            for (LngLat child : children) {

                //Check child is not in the closed list
                if (closedList.contains(child)) {
                    continue;
                }

                double tentativeG = gValues.get(currentNode) + DRONE_MOVE_DISTANCE;


                if (!openList.contains(child) || tentativeG < gValues.get(child)) {
                    gValues.put(child, tentativeG);
                    hValues.put(child, lngLatHandler.distanceTo(child, destination));
                    fValues.put(child, gValues.get(child) + hValues.get(child));
                    parentOfChild.put(child, currentNode);

                    if (!openList.contains(child)) {
                        openList.add(child);
                    }
                }
            }
        }
        return null;
    }

}
