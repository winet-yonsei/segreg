package com.segreg;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.stat.regression.RegressionResults;
import java.util.Deque;

public class SegmentedRegression {
    private double [] xData;
    private double [] yData;
    private SimpleRegression regression;
    private Deque<int[]> segmentStack;

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
        xData = newXData;
        yData = newYData;
    }

    public void setData(double[] xData, double[] yData) {
        this.xData = xData;
        this.yData = yData;
    }   


    public RegressionResults[] performRegression(int maxSegments) {
        regression.clear();
        segmentStack.clear();

        int[] range = new int[]{0, xData.length};

        segmentStack.add(range);
        while (!segmentStack.isEmpty() ){
            int[] currentRange = segmentStack.pop();
            // Logic to find the best split point and add new segments to the stack
            // This is a placeholder for the actual segmentation logic
            if 
        }
        for (int i = 0; i < xData.length; i++) {
            regression.addData(xData[i], yData[i]);
        }



        
        return new RegressionResults[] { regression.regress() };
    }

    public boolean CanSplitSegment(int[] segment,  ) {
        // Placeholder logic for determining if a segment can be split
        return (endIndex - startIndex) > 2; // Example condition
    }

    public double[] CalculateSMA(int[] range, int half_len){
        int n_values = range[1] - range[0];
        double[] result = new double[n_values];
        System.arraycopy(yData, range[0], result, 0, n_values);
        if ( half_len<=0 || n_values<3 ) {
        }
        else if( ( 2*half_len + 1 ) > n_values ) {
        }
        else{
            int ix = 0; 
            double sum_y = 0.0;

            sum_y = result[0];
            result[0] = sum_y / 1.0  ;

            //  the front range:
            //  processing accumulates sum_y using gradually increasing length window

            for (int i = range[0]; i < half_len; i++) {
                sum_y = sum_y + yData[range[0] + i][ 2*ix - 1 ] + data_copy [ 2*ix ] ;
            }
        }
        return result;
    }


}


