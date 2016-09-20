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

    public int getMoveX(int radius, int index) {
        double angle =  getCurrentAngle(index);
        int moveX;
        if (averageAngleRadians == 0) {
            moveX = (int)(Math.cos(angle) * radius) * index;
        } else {
            moveX = (int)(Math.cos(angle) * radius);
        }
        return moveX;
    }

    public int getMoveY(int radius, int index) {
        double angle = getCurrentAngle(index);
        int moveY;
        if (averageAngleRadians == 0) {
            moveY = (int)(Math.sin(angle) * radius) * index;
        } else {
            moveY = (int)(Math.sin(angle) * radius);
        }
        return moveY;
    }

    private double getCurrentAngle(int index) {
        return startAngleRadians + averageAngleRadians * (index - 1);
    }
}
