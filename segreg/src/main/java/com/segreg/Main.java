package com.segreg;

import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.Deque;

public class Main {
    public static void main(String[] args) {
        System.out.println("input max segments (-1: no limit):");
        int maxSegments = Integer.parseInt(System.console().readLine());

        System.out.println("input max error (<0.0: no error check):");
        double maxError = Double.parseDouble(System.console().readLine());

        double[] x_data = new double[] {1., 2, 3, 5, 6, 4, 7, 8, 9, 10, 11, 12, 13, 14, 21, 22, 23, 24, 25, 26, 15, 16, 17, 18, 19, 20, 27, 28, 29, 30};
        double[] y_data = new double[x_data.length];

        for (int i = 0; i < x_data.length; i++) {
            if (x_data[i] < 10){
                y_data[i] = 2 * x_data[i] + Math.random() * 5;
            }
            else if (x_data[i] < 20){
                y_data[i] = -1 * x_data[i] + 30 + Math.random() * 5;
            }
            else{
                y_data[i] = 0.5 * x_data[i] + Math.random() * 5;
            }
        }

        SegmentedRegression segReg = new SegmentedRegression(x_data, y_data);
        segReg.sortData(); // 데이터 정렬
        Deque<Segment> segments = segReg.performRegression(maxSegments, maxError); // 세그먼트 생성 및 회귀 수행

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        double[] sorted_x = segReg.getXData();
        double[] sorted_y = segReg.getYData();

        int seg_id = -1;
        for (Segment seg : segments) {
            seg_id += 1;
            double startX = seg.getStartX();
            double endX = seg.getEndX();
            int start_idx = seg.getStart_idx();
            int end_idx = seg.getEnd_idx();
            SimpleRegression regression = seg.regression;
            
            
            for (int i=start_idx; i<end_idx; i++){
                dataset.addValue(sorted_y[i], "Data", Double.toString(sorted_x[i]));
                dataset.addValue(regression.predict(sorted_x[i]), "Regression "+seg_id, Double.toString(sorted_x[i]));
            }

            // 회귀 결과는 regression.predict(sorted_x[i]) 으로 얻을 수 있습니다.
        }

        // 예시: Line Chart 생성 (범주형)
        JFreeChart chart = ChartFactory.createLineChart(
            "My Line Chart",          // 차트 제목
            "X Axis",                 // X축 라벨
            "Y Axis",                 // Y축 라벨
            dataset,                  // 데이터셋
            PlotOrientation.VERTICAL, // 차트 방향 (VERTICAL or HORIZONTAL)
            true,                     // 범례 표시 여부 (true/false)
            true,                     // 툴팁 표시 여부 (true/false)
            false                     // URL 링크 표시 여부 (true/false)
        );

        // ChartFrame으로 표시
        ChartFrame frame = new ChartFrame("Line Chart Example", chart);
        frame.setSize(800, 600);
        frame.setVisible(true);

        // ChartPanel을 JFrame에 추가하는 방법도 있습니다.
    }
}