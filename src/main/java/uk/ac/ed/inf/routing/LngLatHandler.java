package uk.ac.ed.inf.routing;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class LngLatHandler implements LngLatHandling{

    /**
     * Finds the Pythagorean distance between two points
     *
     * @param startPosition A (long, lat) position
     * @param endPosition Another (long, lat) position
     * @return The distance between the two points (in degrees)
     */
    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        double lngDif = startPosition.lng() - endPosition.lng();
        double latDif = startPosition.lat() - endPosition.lat();
        return Math.sqrt(Math.pow(lngDif, 2) + Math.pow(latDif, 2));
    }

    /**
     * Tests if a point is close to another
     *
     * @param startPosition A (long, lat) position
     * @param otherPosition Another (long, lat) position
     * @return if the first position is close to the other position as defined by the "close to" constant
     */
    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        return distanceTo(startPosition, otherPosition) < SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    /**
     * Tests to see if a point lies within a region (including its border)
     *
     * @param position (long,lat) position to test if in the region
     * @param region the region in question
     * @return if the position lies within the region
     */
    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {

        int numVertices = region.vertices().length;

        //Create a double precision path from the (longitude, latitude) points in the region vertices
        Path2D.Double path = new Path2D.Double();
        //Move to the first point (to start path)
        double firstLng = region.vertices()[0].lng();
        double firstLat = region.vertices()[0].lat();
        path.moveTo(firstLng, firstLat);
        //Create lines to all the following points
        for (int i = 1; i < numVertices; i++) {
            double lng = region.vertices()[i].lng();
            double lat = region.vertices()[i].lat();
            path.lineTo(lng, lat);
        }
        //Create a line back to the first point to close the path
        path.lineTo(firstLng, firstLat);
        //Check if the position is contained within the path
        if (path.contains(position.lng(), position.lat())) {
            return true;
        }

        /* ****************************** README ******************************
        Java AWT definition of insideness does not include ALL borders, hence we check borders manually as well
        https://docs.oracle.com/javase%2F7%2Fdocs%2Fapi%2F%2F/java/awt/Shape.html#:~:text=Definition%20of%20insideness%3A%20A%20point,entirely%20inside%20the%20boundary%20or
         */

        //Create borders and add to arraylist
        ArrayList<Line2D.Double> borders = new ArrayList<>();
        for (int j = 0; j < numVertices - 1; j++) {
            Point2D.Double vertex1 = new Point2D.Double(region.vertices()[j].lng(), region.vertices()[j].lat());
            Point2D.Double vertex2 = new Point2D.Double(region.vertices()[j + 1].lng(), region.vertices()[j + 1].lat());
            borders.add(new Line2D.Double(vertex1, vertex2));
        }
        //Add a border between first and last vertex
        Point2D.Double vertex1 = new Point2D.Double(region.vertices()[0].lng(), region.vertices()[0].lat());
        Point2D.Double vertex2 = new Point2D.Double(region.vertices()[numVertices - 1].lng(), region.vertices()[numVertices - 1].lat());
        borders.add(new Line2D.Double(vertex1, vertex2));


        //Check if the point is on the border line segment using collinearity and then a bounding box check
        for (Line2D.Double border : borders) {
            if (isCollinear(position.lng(), position.lat(), border.x1, border.y1, border.x2, border.y2)) {
                if (position.lng() >= Math.min(border.x1, border.x2) && position.lng() <= Math.max(border.x1, border.x2)
                        && position.lat() >= Math.min(border.y1, border.y2) && position.lat() <= Math.max(border.y1, border.y2)) {
                    return true;
                }
            }
        }

        return false;
    }

    //Helper function to check for collinearity between three points (x1, y1), (x2, y2) and (x3, y3)
    private boolean isCollinear(double x1, double y1, double x2, double y2, double x3, double y3) {
        //Checks to see if the gradients of any two pairs are equal (rearranged gradients formula)
        return (y2 - y1) * (x3 - x2) == (y3 - y2) * (x2 - x1);
    }

    /**
     * Calculates the next (long, lat) position given a starting position and an angle
     *
     * @param startPosition the initial position
     * @param angle the angle which the move will take place
     * @return the new position
     */
    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {
        if (angle == 999) {
            return startPosition;
        } else if(angle >= 0 && angle <= 360) {
            double radians = angle * Math.PI / 180;
            double adjacent = SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(radians);
            double opposite = SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(radians);
            return new LngLat(startPosition.lng() + adjacent, startPosition.lat() - opposite);
        } else {
            return null;
        }
    }
}
