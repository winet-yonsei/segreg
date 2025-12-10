package com.segreg;

public class Datapoint {
    public double xData = 0.;
    public double yData = 0.;
    public int idx = 0;
    
    public Datapoint(double x, double y, int index) {
        this.xData = x;
        this.yData = y;
        this.idx = index;
    }
}
