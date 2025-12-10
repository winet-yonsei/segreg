package com.segreg;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.stat.regression.RegressionResults;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Vector;

public class SegmentedRegression {
    private double [] xData;
    private double [] yData;
    private SimpleRegression regression;
    private Deque<Segment> segmentStack;
    private int SMAWindowLen = 10; // 이동 평균 윈도우의 길이

    public SegmentedRegression(double[] xData, double[] yData) {
        this.xData = xData;
        this.yData = yData;

        this.regression = new SimpleRegression();
        this.segmentStack = new ArrayDeque<Segment>();
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

    public void sortData() {
        int n = xData.length;

        double[][] pairedData = new double[n][2];
        for (int i = 0; i < n; i++) {
            pairedData[i][0] = xData[i];
            pairedData[i][1] = yData[i];
        }

        java.util.Arrays.sort(pairedData, new java.util.Comparator<double[]>() {
            public int compare(double[] a, double[] b) {
                return Double.compare(a[0], b[0]);
            }
        });

        for (int i = 0; i < n; i++) {
            xData[i] = pairedData[i][0];
            yData[i] = pairedData[i][1];
        }
    }


    public Deque<Segment> performRegression(int maxSegments, double maxError) {
        regression.clear();
        segmentStack.clear();

        this.sortData();

        Segment range = new Segment(xData[0], xData[xData.length - 1], 0, xData.length);

        double[] smadata = CalculateSMA(SMAWindowLen);

        double[] deviations = new double[yData.length];

        for (int i = 0; i < yData.length; i++) {
            deviations[i] = Math.abs(yData[i] - smadata[i]);
        }

        //  find positions of local maxima in the vector of deviations 
        Vector<Integer> vec_max_indices = FindLocalMaxima (deviations); 
        Vector<Datapoint> localmax = new Vector<Datapoint>();
        for (Integer idx : vec_max_indices) {
            localmax.add(new Datapoint(xData[idx], deviations[idx], idx));
        }
        range.setSegmentLocalMaxima(localmax);

        segmentStack.push(range);

        boolean checked_all = false; // 

        while (!checked_all){            
            int max_seg_idx = -1;
            double max_seg_err = -1.;
            int cur_idx = -1;

            for (Segment seg : segmentStack) {
                cur_idx += 1;
                if (!seg.isErr_chk()){ // error값을 계산하지 않았음
                    int start_idx = seg.getStart_idx();
                    int end_idx = seg.getEnd_idx();

                    double[] seg_xdata = new double[end_idx - start_idx];
                    double[] seg_ydata = new double[end_idx - start_idx];

                    System.arraycopy(xData, start_idx, seg_xdata, 0, end_idx - start_idx);
                    System.arraycopy(yData, start_idx, seg_ydata, 0, end_idx - start_idx);

                    SimpleRegression tempRegression = new SimpleRegression();
                    for (int i = 0; i < seg_xdata.length; i++) {
                        tempRegression.addData(seg_xdata[i], seg_ydata[i]);
                    }
                    RegressionResults regResults = tempRegression.regress();
                    seg.SetRegression(tempRegression, regResults);

                    // 최대 오차 계산
                    maxError = 0.;

                    for (int i = start_idx; i < end_idx; i++) {
                        double predictedY = tempRegression.predict(xData[i]);
                        double error = Math.abs(yData[i] - predictedY);
                        if (error > maxError) {
                            maxError = error;
                        } 
                    }

                    seg.setCurErr(maxError);
                }

                if (seg.canSplit()){
                    double seg_err = seg.getCurErr();
                    if (maxError >= 0.0){ // 오류 한도가 설정된 경우
                        if (seg_err > max_seg_err) {
                            max_seg_err = seg_err;
                            max_seg_idx = cur_idx;
                        }
                    }
                }
            }

            if (maxSegments > -1 && segmentStack.size() >= maxSegments){
                // 세그먼트 개수 제한 확인하기
                checked_all = true;
                break;
            }

            if (max_seg_idx > -1){
                Deque<Segment> newSegments = new ArrayDeque<Segment>();
                // 좌편 채우기
                for (int i=0; i<max_seg_idx; i++){
                    newSegments.add(segmentStack.removeFirst());
                }
                Segment split_seg = segmentStack.removeFirst();

                // 세그먼트 나누기

                Segment[] outSegments = new Segment[2];
                boolean split_success = split_seg.SplitSegmentbyLocalMaxima(outSegments);
                if (split_success) {
                    newSegments.add(outSegments[0]);
                    newSegments.add(outSegments[1]);
                } else {
                    newSegments.add(split_seg);
                }

                // 우변 채우기


                int stacklen = segmentStack.size();

                for (int i=0; i<stacklen; i++){
                    newSegments.add(segmentStack.removeFirst());
                }

                segmentStack = newSegments;
            }
            else{
                checked_all = true;
            }            
        }
        
        return segmentStack;
    }

    public Vector<Integer> FindLocalMaxima(double[] data) {
        int n = data.length;
        Vector<Integer> vec_max_indices = new Vector<Integer>();
        
        if (n < 3) {
            return vec_max_indices;
        }
        
        for (int i = 1; i < n - 1; i++) {
            if (data[i] > data[i - 1] && data[i] > data[i + 1]) {
                vec_max_indices.add(i);
            }
        }
        return vec_max_indices;
    }

    public double[] CalculateSMA(int half_len){  // simple moving average
        int n_values = yData.length;
        double[] result = new double[n_values];
        System.arraycopy(yData, 0, result, 0, n_values);
        if ( half_len<=0 || n_values<3 ) {
        }

        else if( ( 2*half_len + 1 ) > n_values ) {
        }

        else{
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


