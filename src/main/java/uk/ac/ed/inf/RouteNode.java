package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

public class RouteNode {

    private RouteNode parent;
    private LngLat position;

    private double f = 0;
    private double g = 0;
    private double h = 0;

    public RouteNode(RouteNode parent, LngLat position) {
        this.parent = parent;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RouteNode) {
            return this.position.lng() == ((RouteNode) o).position.lng() &&
                    this.position.lat() == ((RouteNode) o).position.lat();
        }
        return false;
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

    public LngLat getPosition() {
        return position;
    }

    public RouteNode getParent() {
        return parent;
    }
}
