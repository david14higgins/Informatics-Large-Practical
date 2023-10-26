package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

public class RouteNode {

    private RouteNode parent;
    private LngLat position;

    private double f;
    private double g;
    private double h;

    public RouteNode(RouteNode parent, LngLat position) {
        this.parent = parent;
        this.position = position;
    }

    public double getF() {
        return f;
    }

    public double getG() {
        return g;
    }

    public double getH() {
        return h;
    }

    public void setF(double f) {
        this.f = f;
    }

    public void setG(double g) {
        this.g = g;
    }

    public void setH(double h) {
        this.h = h;
    }
}
