package uk.ac.ed.inf.routing;

import uk.ac.ed.inf.constant.Direction;
import uk.ac.ed.inf.routing.LngLatPair;

public class MoveInfo {

    private LngLatPair sourceToDestinationPair;
    private Direction direction;

    public MoveInfo(LngLatPair lngLatPair, Direction direction) {
        this.sourceToDestinationPair = lngLatPair;
        this.direction = direction;
    }

    public LngLatPair getSourceToDestinationPair() {
        return sourceToDestinationPair;
    }

    public Direction getDirection() {
        return direction;
    }
}
