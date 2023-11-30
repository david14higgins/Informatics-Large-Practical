package uk.ac.ed.inf.routing;

import uk.ac.ed.inf.constant.Direction;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.interfaces.RoutePlanning;
import java.util.*;

import static uk.ac.ed.inf.ilp.constant.SystemConstants.DRONE_MOVE_DISTANCE;


public class RoutePlanner implements RoutePlanning{

    //Maps each compass direction to its opposite direction (used when reversing a route)
    private final HashMap<Double, Double> oppositeAngle = new HashMap<>();

    /**
     * Constructor calls method that populates the oppositeAngle hashmap
     */
    public RoutePlanner() {
        setOppositeAngles();
    }


    /*
    If current position is not in central area, make sure no new nodes can be made that are now in the central area
     */

    /**
     * A greedy pathfinding algorithm based upon A*
     * The algorithm is A* but without the check for improved G positions (since the 16 compass points makes it unlikely
     * exact positions will be revisited)
     * @param source LngLat position the route starts at
     * @param destination LngLat position the route ends at
     * @param noFlyZones NamedRegions which the route planner must avoid
     * @param centralArea NamedRegion which the route cannot leave once entered
     * @return A list of moves representing the route
     */
    @Override
    public ArrayList<MoveInfo> planRoute(LngLat source,
                                         LngLat destination,
                                         NamedRegion[] noFlyZones,
                                         NamedRegion centralArea) {
        LngLatHandler lngLatHandler = new LngLatHandler();

        //G value is the cost to reach this LngLat position from source
        HashMap<LngLat, Double> gValues = new HashMap<>();
        //H value is the heuristic distance from a LngLat position to the destination
        HashMap<LngLat, Double> hValues = new HashMap<>();
        //F value is the sum of the cost and heuristic distance at a LngLat (g + h)
        HashMap<LngLat, Double> fValues = new HashMap<>();
        //Parent LngLat is the position explored before the current child LngLat position
        HashMap<LngLat, LngLat> parentOfChild = new HashMap<>();

        //The following lists are used in the search
        HashSet<LngLat> closedList = new HashSet<>();
        PriorityQueue<LngLat> openList = new PriorityQueue<>(Comparator.comparingDouble(fValues::get));

        //Stores the compass direction between two LngLat positions
        HashMap<LngLatPair, Direction> moveDirection = new HashMap<>();

        //Initialize data structures for search
        openList.add(source);
        gValues.put(source, 0.0);
        hValues.put(source, lngLatHandler.distanceTo(source, destination));
        fValues.put(source, gValues.get(source) + hValues.get(source));
        parentOfChild.put(source, null);

        //Begin search
        while(!openList.isEmpty()) {
            LngLat currentNode = openList.poll();
            closedList.add(currentNode);
            //Check to see if destination node has been found
            if (lngLatHandler.isCloseTo(currentNode, destination)) {
                //If so, rebuild path and return
                ArrayList<MoveInfo> path = new ArrayList<>();
                LngLat moveDestination = currentNode;
                LngLat moveSource = parentOfChild.get(moveDestination);
                while(moveSource != null) {
                    LngLatPair sourceToDestinationPair = new LngLatPair(moveSource, moveDestination);
                    Direction direction = moveDirection.get(sourceToDestinationPair);
                    MoveInfo routeNode = new MoveInfo(sourceToDestinationPair, direction.getAngle());
                    path.add(routeNode);
                    moveDestination = moveSource;
                    moveSource = parentOfChild.get(moveDestination);
                }
                Collections.reverse(path);
                //return path;
                return path;
             }

            //Otherwise, continue search and generate children nodes
            for (Direction direction : Direction.values()) {
                LngLat child = lngLatHandler.nextPosition(currentNode, direction.getAngle());

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
                    LngLatPair sourceDestination = new LngLatPair(currentNode, child);
                    moveDirection.put(sourceDestination, direction);
                    openList.add(child);
                }
            }
        }
        return null;
    }

    /**
     * Reverses a given route (cannot just flip list because need to update directions between LngLat pairs)
     * @param route A list of moves representing the route to be reversed
     * @return A list of moves representing the reversed route
     */
    @Override
    public ArrayList<MoveInfo> reverseRoute(ArrayList<MoveInfo> route) {
        ArrayList<MoveInfo> reversedRoute = new ArrayList<>();
        for(int i = route.size() - 1; i >= 0; i--) {
            MoveInfo moveToReverse = route.get(i);
            LngLatPair pairToReverse = moveToReverse.getSourceToDestinationPair();
            LngLat oldSource = pairToReverse.sourceLngLat();
            LngLat oldDestination = pairToReverse.destinationLngLat();
            LngLatPair reversedPair = new LngLatPair(oldDestination, oldSource);
            Double reversedAngle = oppositeAngle.get(moveToReverse.getAngle());

            //Build new MoveInfo object and add to new reversed route
            MoveInfo reversedMove = new MoveInfo(reversedPair, reversedAngle);
            reversedRoute.add(reversedMove);
        }
        return reversedRoute;
    }

    /**
     * Populates the opposite angle hashmap with key value pairs where the key is an angle and the value
     * associated is the angle of the direction opposite to it. This is helpful in reversing a route
     */
    private void setOppositeAngles() {
        oppositeAngle.put(Direction.EAST.getAngle(), Direction.WEST.getAngle());
        oppositeAngle.put(Direction.EAST_SOUTH_EAST.getAngle(), Direction.WEST_NORTH_WEST.getAngle());
        oppositeAngle.put(Direction.SOUTH_EAST.getAngle(), Direction.NORTH_WEST.getAngle());
        oppositeAngle.put(Direction.SOUTH_SOUTH_EAST.getAngle(), Direction.NORTH_NORTH_WEST.getAngle());
        oppositeAngle.put(Direction.SOUTH.getAngle(), Direction.NORTH.getAngle());
        oppositeAngle.put(Direction.SOUTH_SOUTH_WEST.getAngle(), Direction.NORTH_NORTH_EAST.getAngle());
        oppositeAngle.put(Direction.SOUTH_WEST.getAngle(), Direction.NORTH_EAST.getAngle());
        oppositeAngle.put(Direction.WEST_SOUTH_WEST.getAngle(), Direction.EAST_NORTH_EAST.getAngle());
        oppositeAngle.put(Direction.WEST.getAngle(), Direction.EAST.getAngle());
        oppositeAngle.put(Direction.WEST_NORTH_WEST.getAngle(), Direction.EAST_SOUTH_EAST.getAngle());
        oppositeAngle.put(Direction.NORTH_WEST.getAngle(), Direction.SOUTH_EAST.getAngle());
        oppositeAngle.put(Direction.NORTH_NORTH_WEST.getAngle(), Direction.SOUTH_SOUTH_EAST.getAngle());
        oppositeAngle.put(Direction.NORTH.getAngle(), Direction.SOUTH.getAngle());
        oppositeAngle.put(Direction.NORTH_NORTH_EAST.getAngle(), Direction.SOUTH_SOUTH_WEST.getAngle());
        oppositeAngle.put(Direction.NORTH_EAST.getAngle(), Direction.SOUTH_WEST.getAngle());
        oppositeAngle.put(Direction.EAST_NORTH_EAST.getAngle(), Direction.WEST_SOUTH_WEST.getAngle());
    }
}


