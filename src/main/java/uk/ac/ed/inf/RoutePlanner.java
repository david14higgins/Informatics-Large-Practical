package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import java.util.ArrayList;

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
        ArrayList<RouteNode> openList = new ArrayList<>();
        ArrayList<RouteNode> closedList = new ArrayList<>();

        //Add the start node
        openList.add(startNode);

        //Iterate until we reach the destination
        while(!openList.isEmpty()) {

            //Get the current node - this is the node with the lowest f value
            RouteNode currentNode = openList.get(0);
            for (int i = 1; i < openList.size(); i++) {
                RouteNode nextNode = openList.get(i);
                if (nextNode.getF() < currentNode.getF()) {
                    currentNode = nextNode;
                }
            }

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

                //Check child is not in the closed list
                boolean childInClosedList = false;
                for (RouteNode closedChild : closedList) {
                    if (child.equals(closedChild)) {
                        childInClosedList = true;
                        System.out.println("triggered");

                    }
                }
                if(childInClosedList) continue;

                //Calculate the child's f, g and h values
                child.setG(currentNode.getG() + DRONE_MOVE_DISTANCE);
                //Could improve this by not using square root
                child.setH(lngLatHandler.distanceTo(child.getPosition(), destinationNode.getPosition()));
                child.setF(child.getG() + child.getH());

                //Before adding child to open list, check to see if there is already a better route from this node in the open list
                boolean betterOpenNode = false;
                for (RouteNode openNode : openList) {
                    if(child.equals(openNode) && child.getG() > openNode.getG()) {
                        betterOpenNode = true;
                    }
                }
                if (betterOpenNode) continue;

                //Otherwise, we can add this child to the open list
                openList.add(child);
            }
            //System.out.println(currentNode.getH());
        }
        return null;
    }

}
