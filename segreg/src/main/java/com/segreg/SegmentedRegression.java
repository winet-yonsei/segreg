package com.segreg;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.stat.regression.RegressionResults;
import java.util.Deque;
import java.util.Vector;

public class SegmentedRegression {
    private double [] xData;
    private double [] yData;
    private SimpleRegression regression;
    private Deque<int[]> segmentStack;
    private int SMAWindowLen = 10; // 이동 평균 윈도우의 길이

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

        double[] smadata = CalculateSMA(SMAWindowLen);

        double[] deviations = new double[yData.length];

        for (int i = 0; i < yData.length; i++) {
            deviations[i] = Math.abs(yData[i] - smadata[i]);
        }

        //  find positions of local maxima in the vector of deviations 
        Vector<Integer> vec_max_indices = new Vector<Integer>(); 
        FindLocalMaxima (deviations, vec_max_indices) ; 

        while (!segmentStack.isEmpty() ){
            int[] currentRange = segmentStack.pop();
            // Logic to find the best split point and add new segments to the stack
            // This is a placeholder for the actual segmentation logic

            // TODO: Implement segmentation based on local maxima and maxSegments
        }
        for (int i = 0; i < xData.length; i++) {
            regression.addData(xData[i], yData[i]);
        }        
        return new RegressionResults[] { regression.regress() };
    }

    public void FindLocalMaxima(double[] data, Vector<Integer> vec_max_indices) {
        int n = data.length;
        
        if (n < 3) {
            return;
        }
        
        for (int i = 1; i < n - 1; i++) {
            if (data[i] > data[i - 1] && data[i] > data[i + 1]) {
                vec_max_indices.add(i);
            }
        }
    }

    public boolean CanSplitSegment(int[] segment,  ) {
        // Placeholder logic for determining if a segment can be split
        return (endIndex - startIndex) > 2; // Example condition
        // TODO: Implement actual logic based on regression error or other criteria
    }

    public double[] CalculateSMA(int half_len){
        int n_values = yData.length;
        double[] result = new double[n_values];
        System.arraycopy(yData, 0, result, 0, n_values);
        if ( half_len<=0 || n_values<3 ) {
        }

        else if( ( 2*half_len + 1 ) > n_values ) {
        }

        else{
            int ix = 0; 
            double sum_y = 0.0;

            sum_y = result[0];
            for (int i = 1; i <= half_len; i++) {
                sum_y = sum_y + yData[i];
            }
            result[0] = sum_y / (half_len + 1);

            //  the front range:
            //  processing accumulates sum_y using gradually increasing length window

            for (int i = 1; i <= half_len; i++) {
                sum_y = sum_y + yData[i];
                result[i] = sum_y / (half_len + 1 + i);
            }

            for (int i = half_len + 1; i < n_values - half_len; i++) {
                sum_y = sum_y + yData[i] - yData[i - half_len - 1];
                result[i] = sum_y / (2 * half_len + 1);
            }
            //  the back range:
            //  processing accumulates sum_y using gradually decreasing length window

            for (int i = n_values - half_len; i < n_values; i++) {
                sum_y = sum_y - yData[i - half_len - 1];
                result[i] = sum_y / (half_len + n_values - i);
            }
        }
        return result;
    }    
}


