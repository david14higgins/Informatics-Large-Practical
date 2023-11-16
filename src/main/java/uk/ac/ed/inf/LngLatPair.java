package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

public class LngLatPair {

    private final LngLat sourceLngLat;
    private final LngLat destinationLngLat;


    public LngLatPair(LngLat sourceLngLat, LngLat destinationLngLat) {
        this.sourceLngLat = sourceLngLat;
        this.destinationLngLat = destinationLngLat;
    }

    public LngLat getSourceLngLat() {
        return sourceLngLat;
    }

    public LngLat getDestinationLngLat() {
        return destinationLngLat;
    }
}
