package uk.ac.ed.inf;

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
