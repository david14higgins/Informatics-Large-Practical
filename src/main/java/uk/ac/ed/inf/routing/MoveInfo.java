package uk.ac.ed.inf.routing;

import uk.ac.ed.inf.constant.Direction;
import uk.ac.ed.inf.routing.LngLatPair;

public class MoveInfo {

    private LngLatPair sourceToDestinationPair;
    private Direction direction;

    /**
     * A helpful object for rebuilding the route path. Stores a pair of adjacent LngLat positions in the route, along
     * with the direction between them
     * @param lngLatPair A source and destination LngLat pair in the route
     * @param direction The direction between the pair
     */
    public MoveInfo(LngLatPair lngLatPair, Direction direction) {
        this.sourceToDestinationPair = lngLatPair;
        this.direction = direction;
    }

    /**
     * @return The LngLat pair of the move
     */
    public LngLatPair getSourceToDestinationPair() {
        return sourceToDestinationPair;
    }

    /**
     * @return The direction between the source and destination
     */
    public Direction getDirection() {
        return direction;
    }
}
