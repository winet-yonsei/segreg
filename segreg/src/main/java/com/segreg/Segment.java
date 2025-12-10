package com.segreg;

import java.util.Vector;
import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class Segment {
    private double startX;
    private double endX;
    private int start_idx;
    private int end_idx;

    private Vector<Datapoint> SegmentLocalMaxima; // 해당 세그먼트에 속하는 국소 최대값들 x , y:|이동평균-원본|  쌍으로 저장
    private int minseglen = 2; // 세그먼트의 최소 길이

    private double curErr = 0.0;
    public boolean canSplit = true;  // 세그먼트를 나눌 수 있는지 여부

    private boolean err_chk = false;

    public RegressionResults regResults;  // 이전의 회귀 결과 저장
    public SimpleRegression regression;  // 세그먼트 내의 회귀값 저장

    public Segment(double startX, double endX, int start_idx, int end_idx) {
        this.startX = startX;
        this.endX = endX;

        this.start_idx = start_idx;
        this.end_idx = end_idx;

        this.SegmentLocalMaxima = new Vector<Datapoint>();
        this.canSplit = true;
        this.err_chk = false;
    }

    public double getCurErr() {
        return curErr;
    }

    public void setCurErr(double Err) {
        this.curErr = Err;
        this.err_chk = true;
    }

    public boolean isErr_chk() {
        return err_chk;
    }

    public void SetRegression(SimpleRegression regression, RegressionResults regResults) {
        this.regResults = regResults;
        this.regression = regression;
    }

    public void SetRange(double startX, double endX, int start_idx, int end_idx) {
        this.startX = startX;
        this.endX = endX;
        this.start_idx = start_idx;
        this.end_idx = end_idx;
    }

    public double getStartX() {
        return startX;
    }

    public double getEndX() {
        return endX;
    }

    public Vector<Datapoint> getSegmentLocalMaxima() {
        return SegmentLocalMaxima;
    }

    public void setSegmentLocalMaxima(Vector<Datapoint> segmentLocalMaxima) {
        this.SegmentLocalMaxima = segmentLocalMaxima;
    }

    public void addSegmentLocalMaxima(Datapoint localMax) {
        this.SegmentLocalMaxima.add(localMax);
    }

    public void clearSegmentLocalMaxima() {
        this.SegmentLocalMaxima.clear();
    }

    public boolean canSplit() {
        if (canSplit == false) {
            return false;
        }
        int segmentLength = end_idx - start_idx;
        if (SegmentLocalMaxima.size() == 0) {
            canSplit = false;
            return false;
        }

        if (segmentLength < 2 * minseglen) {
            canSplit = false;
            return false;
        }

        return true;
    }

    public boolean SplitSegmentbyLocalMaxima(Segment[] outSegments) {
        if (canSplit == false) {
            return false;
        }

        int max_idx = -1;
        int segmentLength = end_idx - start_idx;
        if (SegmentLocalMaxima.size() == 0) {
            canSplit = false;
            return false;
        }        

        else if (end_idx - start_idx < 2 * minseglen) {
            canSplit = false;
            return false;
        }

        else {
            double max_value = -1.;
            for (int i = 0; i < SegmentLocalMaxima.size(); i++) {
                // 수정 필요
                int dp_idx = SegmentLocalMaxima.get(i).idx;
                if (dp_idx - start_idx> minseglen && end_idx - dp_idx > minseglen) {
                    Datapoint localMax = SegmentLocalMaxima.get(i);
                    if (localMax.yData > max_value) {
                        max_value = localMax.yData;
                        max_idx = i;
                    }
                }
            }
        }

        if (max_idx == -1) {
            canSplit = false;
            return false;
        }
        else {
            // 
            Datapoint localmaxdatapoint = SegmentLocalMaxima.get(max_idx);

            Segment left_seg = new Segment(this.startX, localmaxdatapoint.xData, this.start_idx, localmaxdatapoint.idx);
            Segment right_seg = new Segment(localmaxdatapoint.xData, this.endX, localmaxdatapoint.idx, this.end_idx);

            for (int i=0; i<max_idx; i++) {
                left_seg.addSegmentLocalMaxima(SegmentLocalMaxima.get(i));
            }

            for (int i=max_idx+1; i<SegmentLocalMaxima.size(); i++) {
                right_seg.addSegmentLocalMaxima(SegmentLocalMaxima.get(i));
            }
            outSegments[0] = left_seg;
            outSegments[1] = right_seg;

            return true;
        }
    }

    public int getStart_idx() {
        return start_idx;
    }

    public int getEnd_idx() {
        return end_idx;
    }
}
