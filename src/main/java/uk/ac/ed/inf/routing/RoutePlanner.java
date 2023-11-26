package uk.ac.ed.inf.routing;

import uk.ac.ed.inf.constant.Direction;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.interfaces.RoutePlanning;
import java.util.*;

import static uk.ac.ed.inf.ilp.constant.SystemConstants.DRONE_MOVE_DISTANCE;


public class RoutePlanner implements RoutePlanning{

    //Maps each compass direction to its opposite direction (used when reversing a route)
    private final HashMap<Direction, Direction> oppositeDirection = new HashMap<>();

    //Constructor calls method that populates the opposite direction map
    public RoutePlanner() {
        setOppositeDirections();
    }

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
                    MoveInfo routeNode = new MoveInfo(sourceToDestinationPair, direction);
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

    @Override
    public ArrayList<MoveInfo> reverseRoute(ArrayList<MoveInfo> route) {
        ArrayList<MoveInfo> reversedRoute = new ArrayList<>();
        for(int i = route.size() - 1; i >= 0; i--) {
            MoveInfo moveToReverse = route.get(i);
            LngLatPair pairToReverse = moveToReverse.getSourceToDestinationPair();
            LngLat oldSource = pairToReverse.getSourceLngLat();
            LngLat oldDestination = pairToReverse.getDestinationLngLat();
            LngLatPair reversedPair = new LngLatPair(oldDestination, oldSource);
            Direction reversedDirection = oppositeDirection.get(moveToReverse.getDirection());

            //Build new MoveInfo object and add to new reversed route
            MoveInfo reversedMove = new MoveInfo(reversedPair, reversedDirection);
            reversedRoute.add(reversedMove);
        }
        return reversedRoute;
    }

    private void setOppositeDirections() {
        oppositeDirection.put(Direction.EAST, Direction.WEST);
        oppositeDirection.put(Direction.EAST_SOUTH_EAST, Direction.WEST_NORTH_WEST);
        oppositeDirection.put(Direction.SOUTH_EAST, Direction.NORTH_WEST);
        oppositeDirection.put(Direction.SOUTH_SOUTH_EAST, Direction.NORTH_NORTH_WEST);
        oppositeDirection.put(Direction.SOUTH, Direction.NORTH);
        oppositeDirection.put(Direction.SOUTH_SOUTH_WEST, Direction.NORTH_NORTH_EAST);
        oppositeDirection.put(Direction.SOUTH_WEST, Direction.NORTH_EAST);
        oppositeDirection.put(Direction.WEST_SOUTH_WEST, Direction.EAST_NORTH_EAST);
        oppositeDirection.put(Direction.WEST, Direction.EAST);
        oppositeDirection.put(Direction.WEST_NORTH_WEST, Direction.EAST_SOUTH_EAST);
        oppositeDirection.put(Direction.NORTH_WEST, Direction.SOUTH_EAST);
        oppositeDirection.put(Direction.NORTH_NORTH_WEST, Direction.SOUTH_SOUTH_EAST);
        oppositeDirection.put(Direction.NORTH, Direction.SOUTH);
        oppositeDirection.put(Direction.NORTH_NORTH_EAST, Direction.SOUTH_SOUTH_WEST);
        oppositeDirection.put(Direction.NORTH_EAST, Direction.SOUTH_WEST);
        oppositeDirection.put(Direction.EAST_NORTH_EAST, Direction.WEST_SOUTH_WEST);
    }
}


