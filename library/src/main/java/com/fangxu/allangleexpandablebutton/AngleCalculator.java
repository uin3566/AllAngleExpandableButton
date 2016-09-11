package com.fangxu.allangleexpandablebutton;

/**
 * Created by dear33 on 2016/9/10.
 */
public class AngleCalculator {

    private double startAngle;
    private double endAngle;
    private int count;
    private double averageAngle;

    public AngleCalculator(float startAngle, float endAngle, int expandButtonCount) {
        this.startAngle = Math.toRadians(startAngle);
        this.endAngle = Math.toRadians(endAngle);
        this.count = expandButtonCount;
        if (this.count <= 1) {
            throw new IllegalArgumentException("expandButtonCount can not smaller than one");
        }
        this.averageAngle = (this.endAngle - this.startAngle) / (count - 1);
    }

    public int getDesX(int radius, int index) {
        double angle =  getCurrentAngle(index);
        return (int)(Math.cos(angle) * radius);
    }

    public int getDesY(int radius, int index) {
        double angle = getCurrentAngle(index);
        return (int)(Math.sin(angle) * radius);
    }

    private double getCurrentAngle(int index) {
        return startAngle + averageAngle * (index - 1);
    }
}
