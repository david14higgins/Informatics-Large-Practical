package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.lang.reflect.Array;
import java.util.*;

import static uk.ac.ed.inf.ilp.constant.SystemConstants.DRONE_MOVE_DISTANCE;

public class RoutePlanner {

    //Returns a list of LngLat positions in reverse order from destination to source
    public ArrayList<LngLat> planRoute(LngLat source, LngLat destination, NamedRegion[] noFlyZones, NamedRegion centralArea) {
        LngLatHandler lngLatHandler = new LngLatHandler();

        HashMap<LngLat, Double> fValues = new HashMap<>();
        HashMap<LngLat, Double> gValues = new HashMap<>();
        HashMap<LngLat, Double> hValues = new HashMap<>();
        HashMap<LngLat, LngLat> parentOfChild = new HashMap<>();

        HashSet<LngLat> closedList = new HashSet<>();
        PriorityQueue<LngLat> openList = new PriorityQueue<>(Comparator.comparingDouble(fValues::get));

        openList.add(source);
        gValues.put(source, 0.0);
        hValues.put(source, lngLatHandler.distanceTo(source, destination));
        fValues.put(source, gValues.get(source) + hValues.get(source));
        parentOfChild.put(source, null);

        while(!openList.isEmpty()) {

            LngLat currentNode = openList.poll();
            closedList.add(currentNode);
            //Check to see if destination node has been found
            if (lngLatHandler.isCloseTo(currentNode, destination)) {
                ArrayList<LngLat> path = new ArrayList<>();
                LngLat current = currentNode;
                while(current != null) {
                    path.add(current);
                    current = parentOfChild.get(current);
                }
                Collections.reverse(path);
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

                double tentativeG = gValues.get(currentNode) + DRONE_MOVE_DISTANCE;

                //Check child is not in the closed list
                if (closedList.contains(child)) continue;

                //Check child is not in a no-fly-zone
                boolean inNoFlyZone = false;
                for (NamedRegion namedRegion : noFlyZones) {
                    if (lngLatHandler.isInRegion(child, namedRegion)) {
                        inNoFlyZone = true;
                        break;
                    }
                }
                if(inNoFlyZone) continue;

                gValues.put(child, 0.0);
                if (!openList.contains(child)) {
                    gValues.put(child, tentativeG);
                    hValues.put(child, lngLatHandler.distanceTo(child, destination));
                    fValues.put(child, gValues.get(child) + hValues.get(child));
                    parentOfChild.put(child, currentNode);

                    openList.add(child);
                }
            }

        }
        return null;
    }

}
