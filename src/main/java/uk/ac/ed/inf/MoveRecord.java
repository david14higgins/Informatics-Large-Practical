package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

public class MoveRecord {

    private final String orderNo;
    private final LngLat fromPosition;
    private final LngLat toPosition;

    public MoveRecord(String orderNo, LngLat fromPosition, LngLat toPosition) {
        this.orderNo = orderNo;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public LngLat getFromPosition() {
        return fromPosition;
    }

    public LngLat getToPosition() {
        return toPosition;
    }
}
