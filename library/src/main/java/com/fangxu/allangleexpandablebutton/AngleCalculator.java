package com.fangxu.allangleexpandablebutton;

/**
 * Created by dear33 on 2016/9/10.
 */
public class AngleCalculator {

    private double startAngleRadians;
    private double averageAngleRadians;

    public AngleCalculator(float startAngleDegree, float endAngleDegree, int expandButtonCount) {
        this.startAngleRadians = Math.toRadians(startAngleDegree);
        double endAngleRadians = Math.toRadians(endAngleDegree);
        if (expandButtonCount > 1) {
            this.averageAngleRadians = (endAngleRadians - this.startAngleRadians) / (expandButtonCount - 1);
        }
    }

    public int getDesX(int radius, int index) {
        double angle =  getCurrentAngle(index);
        int desX;
        if (averageAngleRadians == 0) {
            desX = (int)(Math.cos(angle) * radius) * index;
        } else {
            desX = (int)(Math.cos(angle) * radius);
        }
        return desX;
    }

    public int getDesY(int radius, int index) {
        double angle = getCurrentAngle(index);
        int desY;
        if (averageAngleRadians == 0) {
            desY = (int)(Math.sin(angle) * radius) * index;
        } else {
            desY = (int)(Math.sin(angle) * radius);
        }
        return desY;
    }

    private double getCurrentAngle(int index) {
        return startAngleRadians + averageAngleRadians * (index - 1);
    }
}
