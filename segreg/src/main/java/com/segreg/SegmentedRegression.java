package com.segreg;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.stat.regression.RegressionResults;

public class SegmentedRegression {
    private double [] xData;
    private double [] yData;
    private SimpleRegression regression;

    public SegmentedRegression(double[] xData, double[] yData) {
        this.xData = xData;
        this.yData = yData;

        this.regression = new SimpleRegression();
    }

    public void clearData() {
        this.xData = new double[0];
        this.yData = new double[0];
    }

    public double[] getXData() {
        return xData;
    }
    
    public double[] getYData() {
        return yData;
    }

    public void addData(double x, double y) {
        double[] newXData = new double[this.xData.length + 1];
        double[] newYData = new double[this.yData.length + 1];
        System.arraycopy(xData, 0, newXData, 0, xData.length);
        System.arraycopy(yData, 0, newYData, 0, yData.length);
        newXData[xData.length] = x;
        newYData[yData.length] = y;
        this.xData = newXData;
        this.yData = newYData;
    }

    public void setData(double[] xData, double[] yData) {
        this.xData = xData;
        this.yData = yData;
    }   

    public RegressionResults[] performRegression(int maxSegments) {
        return this.performRegression(maxSegments, 0, xData.length);
    }


    public RegressionResults[] performRegression(int maxSegments, int startIndex, int endIndex) {
        regression.clear();
        for (int i = startIndex; i < endIndex; i++) {
            regression.addData(xData[i], yData[i]);
        }
        return new RegressionResults[] { regression.regress() };
    }


}


