package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

public class MoveRecord {

    private final String orderNo;
    private final LngLat fromPosition;
    private final double angle;
    private final LngLat toPosition;

    public MoveRecord(String orderNo, LngLat fromPosition, double angle, LngLat toPosition) {
        this.orderNo = orderNo;
        this.fromPosition = fromPosition;
        this.angle = angle;
        this.toPosition = toPosition;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public LngLat getFromPosition() {
        return fromPosition;
    }

    public double getAngle() {
        return angle;
    }

    public LngLat getToPosition() {
        return toPosition;
    }
}
