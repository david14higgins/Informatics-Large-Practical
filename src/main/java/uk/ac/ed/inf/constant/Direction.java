package uk.ac.ed.inf.constant;

/**
 * Enumeration of all 16 compass directions along with their corresponding angle
 */
public enum Direction {
    EAST(0),
    EAST_NORTH_EAST(22.5),
    NORTH_EAST(45.0),
    NORTH_NORTH_EAST(67.5),
    NORTH(90),
    NORTH_NORTH_WEST(112.5),
    NORTH_WEST(135.0),
    WEST_NORTH_WEST(157.5),
    WEST(180),
    WEST_SOUTH_WEST(202.5),
    SOUTH_WEST(225.0),
    SOUTH_SOUTH_WEST(247.5),
    SOUTH(270.0),
    SOUTH_SOUTH_EAST(292.5),
    SOUTH_EAST(315.0),
    EAST_SOUTH_EAST(337.5);

    private final double angle;

    /**
     * Constructor which creates Direction and assigns its angle
     * @param angle double representing the angle of the direction
     */
    Direction(double angle) {
        this.angle = angle;
    }

    /**
     * @return angle of the compass direction
     */
    public double getAngle() {
        return angle;
    }


}
