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
            System.out.println(openList.size());
        }
        return null;
    }

    public ArrayList<LngLat> planRouteNew(LngLat source, LngLat destination) {
        LngLatHandler lngLatHandler = new LngLatHandler();

        ArrayList<LngLat> openList = new ArrayList<>();
        Set<LngLat> closedList = new HashSet<>();

        HashMap<LngLat, Double> fValues = new HashMap<>();
        HashMap<LngLat, Double> gValues = new HashMap<>();
        HashMap<LngLat, Double> hValues = new HashMap<>();
        //Key is child, value is parent
        HashMap<LngLat, LngLat> parentOfChild = new HashMap<>();

        openList.add(source);
        fValues.put(source, 0.0);
        gValues.put(source, 0.0);
        hValues.put(source, 0.0);
        parentOfChild.put(source, null);

        while(!openList.isEmpty()) {

            double minF = Double.MAX_VALUE;
            LngLat currentNode = null;
            for (LngLat position : openList) {
                double positionF = fValues.get(position);
                if (positionF < minF) {
                    minF = positionF;
                    currentNode = position;
                }
            }
            openList.remove(currentNode);
            closedList.add(currentNode);

            //Check to see if destination node has been found
            if (lngLatHandler.isCloseTo(currentNode, destination)) {
                ArrayList<LngLat> path = new ArrayList<>();
                LngLat current = currentNode;
                while(current != null) {
                    path.add(current);
                    current = parentOfChild.get(current);
                }
                return path;
             }

            //Generate children nodes
            ArrayList<LngLat> children = new ArrayList<>();
            for (Direction direction : Direction.values()) {
                LngLat newPosition = lngLatHandler.nextPosition(currentNode, direction.getAngle());
                children.add(newPosition);
            }

            //Iterate through all children that are not in the closed list
            for (LngLat child : children) {

//                if (closedList.contains(child)) {
//                    continue;
//                }
//
//                //Assign f, g and h values of child node
//                double gValue = gValues.get(currentNode) + DRONE_MOVE_DISTANCE;
//                double hValue = lngLatHandler.distanceTo(child, destination);
//                double fValue = gValue + hValue;
//
//                gValues.put(child, gValue);
//                hValues.put(child, hValue);
//                fValues.put(child, fValue);
//                parentOfChild.put(child, currentNode);
//
//                boolean childAlreadyInOpenList = false;
//                for(LngLat openNode : openList) {
//                    if (child.lng() == openNode.lng() && child.lat() == openNode.lat() && gValues.get(child) > gValues.get(openNode)) {
//                        childAlreadyInOpenList = true;
//                    }
//                }
//                if (childAlreadyInOpenList) {
//                    continue;
//                }
//
//                openList.add(child);
//

                gValues.put(child, 0.0);
                hValues.put(child, 0.0);
                fValues.put(child, 0.0);

                double tentativeG = gValues.get(currentNode) + DRONE_MOVE_DISTANCE;

                //Check child is not in the closed list
                if (closedList.contains(child) && tentativeG >= gValues.get(child)) {
                    continue;
                }

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
