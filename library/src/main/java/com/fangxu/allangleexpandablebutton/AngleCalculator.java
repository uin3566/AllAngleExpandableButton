package com.fangxu.allangleexpandablebutton;

/**
 * Created by dear33 on 2016/9/10.
 */
public class AngleCalculator {

    private double startAngleRadians;
    private double averageAngleRadians;
    private boolean angleStartEqualsEnd;

    /**
     * @param startAngleDegree the value of the attribute aebStartAngleDegree
     * @param endAngleDegree the value of the attribute aebEndAngleDegree
     * @param expandButtonCount the count of buttons that will expand
     */
    public AngleCalculator(float startAngleDegree, float endAngleDegree, int expandButtonCount) {
        angleStartEqualsEnd = (endAngleDegree - startAngleDegree) == 0;
        startAngleDegree = startAngleDegree % 360;
        endAngleDegree = endAngleDegree % 360;
        this.startAngleRadians = Math.toRadians(startAngleDegree);
        double endAngleRadians = Math.toRadians(endAngleDegree);
        if (expandButtonCount > 1) {
            this.averageAngleRadians = (endAngleRadians - this.startAngleRadians) / (expandButtonCount - 1);
            regulateAverageAngle(endAngleRadians, expandButtonCount);
        }
    }

    /**
     * @param radius the sum of main button radius and sub button radius and the px value of attribute aebButtonGapDp
     * @param buttonIndex button index, count from startAngle to endAngle, value is 1 to expandButtonCount
     * @return the px distance in x direction that the button should move when expand
     */
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

    /**
     * regulate averageAngleRadians if endAngleDegree - startAngleDegree = 360 to avoid the first button covers the last button
     * @param endAngleRadians end angle in radians unit
     * @param expandButtonCount the count of buttons that will expand
     */
    private void regulateAverageAngle(double endAngleRadians, int expandButtonCount) {
        if (!angleStartEqualsEnd && startAngleRadians == endAngleRadians) {
            double tmp = 2 * Math.PI / expandButtonCount;
            if (averageAngleRadians < 0) {
                averageAngleRadians = -tmp;
            } else {
                averageAngleRadians = tmp;
            }
        }
    }

    /**
     * @param buttonIndex button index, count from startAngle to endAngle, value is 1 to expandButtonCount
     * @return the angle from first button to the buttonIndex button
     */
    private double getCurrentAngle(int buttonIndex) {
        return startAngleRadians + averageAngleRadians * (buttonIndex - 1);
    }
}
