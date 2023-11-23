package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

import java.util.Objects;

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


    @Override
    public boolean equals(Object obj) {
        //Check same instance
        if(this == obj) {
            return true;
        }

        //Check if the passed object is null or a different type
        if(obj == null || getClass() != obj.getClass()) {
            return false;
        }

        //Now check equality based on both lnglat positions being equal
        LngLatPair otherLngLat = (LngLatPair) obj;
        return sourceLngLat.lng() == otherLngLat.sourceLngLat.lng() &&
                sourceLngLat.lat() == otherLngLat.sourceLngLat.lat() &&
                destinationLngLat.lng() == otherLngLat.destinationLngLat.lng() &&
                destinationLngLat.lat() == otherLngLat.destinationLngLat.lat();
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceLngLat, destinationLngLat);
    }


}
