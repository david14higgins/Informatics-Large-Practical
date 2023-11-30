package uk.ac.ed.inf.routing;

public class MoveInfo {

    private final LngLatPair sourceToDestinationPair;
    private final double angle;

    /**
     * A helpful object for rebuilding the route path. Stores a pair of adjacent LngLat positions in the route, along
     * with the direction between them
     * @param lngLatPair A source and destination LngLat pair in the route
     * @param angle The angle between the pair
     */
    public MoveInfo(LngLatPair lngLatPair, double angle) {
        this.sourceToDestinationPair = lngLatPair;
        this.angle = angle;
    }

    /**
     * @return The LngLat pair of the move
     */
    public LngLatPair getSourceToDestinationPair() {
        return sourceToDestinationPair;
    }

    /**
     * @return The angle between the source and destination
     */
    public double getAngle() {
        return angle;
    }
}
