package uk.ac.ed.inf;

public enum Direction {

    EAST(0),
    EAST_SOUTH_EAST(22.5),
    SOUTH_EAST(45.0),
    SOUTH_SOUTH_EAST(67.5),
    SOUTH(90),
    SOUTH_SOUTH_WEST(112.5),
    SOUTH_WEST(135.0),
    WEST_SOUTH_WEST(157.5),
    WEST(180),
    WEST_NORTH_WEST(202.5),
    NORTH_WEST(225.0),
    NORTH_NORTH_WEST(247.5),
    NORTH(270.0),
    NORTH_NORTH_EAST(292.5),
    NORTH_EAST(315.0),
    EAST_NORTH_EAST(337.5);

    private final double angle;

    Direction(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }


}
