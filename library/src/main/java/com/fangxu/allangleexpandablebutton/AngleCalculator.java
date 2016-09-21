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

    public int getMoveX(int radius, int buttonIndex) {
        double angle =  getCurrentAngle(buttonIndex);
        int moveX;
        if (averageAngleRadians == 0) {
            moveX = (int)(Math.cos(angle) * radius) * buttonIndex;
        } else {
            moveX = (int)(Math.cos(angle) * radius);
        }
        return moveX;
    }

    public int getMoveY(int radius, int buttonIndex) {
        double angle = getCurrentAngle(buttonIndex);
        int moveY;
        if (averageAngleRadians == 0) {
            moveY = (int)(Math.sin(angle) * radius) * buttonIndex;
        } else {
            moveY = (int)(Math.sin(angle) * radius);
        }
        return moveY;
    }

    private double getCurrentAngle(int buttonIndex) {
        return startAngleRadians + averageAngleRadians * (buttonIndex - 1);
    }
}
