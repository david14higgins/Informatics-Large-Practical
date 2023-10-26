package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

import static uk.ac.ed.inf.ilp.constant.SystemConstants.DRONE_MOVE_DISTANCE;

public class RoutePlanner {

    /*Use A* Search to plan a route from source to destination that
        - Does not enter no fly zones
        - Does not leave central area once it has entered
     */

    /* Efficiency improvements
     - Use priority queue for open list
     - Use hash table for closed list
     */
    public ArrayList<RouteNode> planRoute(LngLat source, LngLat destination, NamedRegion[] noFlyZones, NamedRegion centralArea) {
        LngLatHandler lngLatHandler = new LngLatHandler();

        //Create source and destination nodes
        RouteNode startNode = new RouteNode(null, source);
        RouteNode destinationNode = new RouteNode(null, destination);

        //Initialize open and closed list
        PriorityQueue<RouteNode> openList = new PriorityQueue<>();
        Set<RouteNode> closedList = new HashSet<>();

        //Add the start node
        openList.add(startNode);

        //Iterate until we reach the destination
        while(!openList.isEmpty()) {

            //Get the current node - this is the node with the lowest f value
            RouteNode currentNode = openList.poll();

            //Remove current node from open list and add to closed list
            openList.remove(currentNode);
            closedList.add(currentNode);

            // Check to see if destination node has been found
            if(lngLatHandler.isCloseTo(currentNode.getPosition(), destinationNode.getPosition())) {
                ArrayList<RouteNode> path = new ArrayList<>();
                RouteNode current = currentNode;
                while(current != null) {
                    path.add(current);
                    current = current.getParent();
                }
                Collections.reverse(path);
                return path;
            }

            //Generate children nodes
            //THIS IS WHERE WE WILL NEED TO CHECK NODES ARE NOT IN NO-FLY-ZONES OR ARE REENTERING CENTRAL AREA
            ArrayList<RouteNode> children = new ArrayList<>();
            for (Direction direction : Direction.values()) {
                LngLat newPosition = lngLatHandler.nextPosition(currentNode.getPosition(), direction.getAngle());
                RouteNode newNode = new RouteNode(currentNode, newPosition);
                children.add(newNode);
            }

            //Iterate through all children that are not in the closed list
            for (RouteNode child : children) {
                double tentativeG = currentNode.getG() + DRONE_MOVE_DISTANCE;

                //Check child is not in the closed list
                if (closedList.contains(child) && tentativeG >= child.getG()) {
                    continue;
                }

                if (!openList.contains(child) || tentativeG < child.getG()) {
                    child.setG(tentativeG);
                    child.setH(lngLatHandler.distanceTo(child.getPosition(), destinationNode.getPosition()));
                    child.setF(child.getG() + child.getH());

                    if (!openList.contains(child)) {
                        openList.add(child);
                    }
                }
            }
        }
        return null;
    }

}
