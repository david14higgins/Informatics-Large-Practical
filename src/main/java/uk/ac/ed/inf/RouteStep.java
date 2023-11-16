package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

public class RouteStep {
    private final LngLatPair lngLatPair;
    private final Direction direction;

    public RouteStep(LngLatPair lngLatPair, Direction direction) {
        this.lngLatPair = lngLatPair;
        this.direction = direction;
    }

    public LngLatPair getLngLatPair() {
        return lngLatPair;
    }

    public Direction getDirection() {
        return direction;
    }
}
