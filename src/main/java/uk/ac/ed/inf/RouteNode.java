package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

public class RouteNode {

    private LngLatPair lngLatPair;
    private Direction direction;

    public RouteNode(LngLatPair lngLatPair, Direction direction) {
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
