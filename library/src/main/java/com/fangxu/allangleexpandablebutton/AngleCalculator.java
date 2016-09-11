package com.fangxu.allangleexpandablebutton;

/**
 * Created by dear33 on 2016/9/10.
 */
public class AngleCalculator {

    private double startAngle;
    private double averageAngle;

    public AngleCalculator(float startAngle, float endAngle, int expandButtonCount) {
        this.startAngle = Math.toRadians(startAngle);
        endAngle = (float)Math.toRadians(endAngle);
        if (expandButtonCount > 1) {
            this.averageAngle = (endAngle - this.startAngle) / (expandButtonCount - 1);
        }
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
