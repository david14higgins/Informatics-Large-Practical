package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.sql.Driver;
import java.util.*;

import static uk.ac.ed.inf.ilp.constant.SystemConstants.DRONE_MOVE_DISTANCE;


public class RoutePlanner {

    private HashMap<Direction, Direction> oppositeDirection = new HashMap<>();

    public RoutePlanner() {
        setOppositeDirections();
    }

    //Returns a list of LngLat positions in reverse order from destination to source
    public ArrayList<MoveInfo> planRoute(LngLat source, LngLat destination, NamedRegion[] noFlyZones, NamedRegion centralArea) {
        LngLatHandler lngLatHandler = new LngLatHandler();

        HashMap<LngLat, Double> fValues = new HashMap<>();
        HashMap<LngLat, Double> gValues = new HashMap<>();
        HashMap<LngLat, Double> hValues = new HashMap<>();
        HashMap<LngLat, LngLat> parentOfChild = new HashMap<>();

        HashSet<LngLat> closedList = new HashSet<>();
        PriorityQueue<LngLat> openList = new PriorityQueue<>(Comparator.comparingDouble(fValues::get));

        HashMap<LngLatPair, Direction> moveDirection = new HashMap<>();

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

            //Generate children nodes

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


