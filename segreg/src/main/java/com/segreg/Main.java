package com.segreg;

import org.jfree.chart.ChartFactory;

public class Main {
    public static void main(String[] args) {
        System.out.println("input max segments:");
        int maxSegments = Integer.parseInt(System.console().readLine());
        SegmentedRegression sr = new SegmentedRegression(new double[]{1,2,3,4,5}, new double[]{2,3,5,7,11});
    }
}