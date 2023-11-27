package uk.ac.ed.inf.routing;

import uk.ac.ed.inf.ilp.data.LngLat;

import java.util.Objects;

public class LngLatPair {

    private final LngLat sourceLngLat;
    private final LngLat destinationLngLat;


    /**
     * A useful object to store two lnglat positions. Enables us to use a hashmap in the routeplanner between a
     * pair of lnglat positions and the direction between them - helpful in path rebuilding
     * @param sourceLngLat First element of a lnglat pair is the source
     * @param destinationLngLat Second element of the pair is the destination
     */
    public LngLatPair(LngLat sourceLngLat, LngLat destinationLngLat) {
        this.sourceLngLat = sourceLngLat;
        this.destinationLngLat = destinationLngLat;
    }

    /**
     * @return first element of LngLat pair - the source
     */
    public LngLat getSourceLngLat() {
        return sourceLngLat;
    }

    /**
     * @return second element of LngLat pair - the destination
     */
    public LngLat getDestinationLngLat() {
        return destinationLngLat;
    }


    /**
     * Override equals operator to check equality based on equal LngLat source and destinations
     * @param obj The other object we are considering equality with
     * @return boolean which is true if LngLatPair is equal or false otherwise
     */
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

    /**
     * Override hashcode as well to depend on source and destination positions
     * @return value representing the hashcode of the pair
     */
    @Override
    public int hashCode() {
        return Objects.hash(sourceLngLat, destinationLngLat);
    }


}
