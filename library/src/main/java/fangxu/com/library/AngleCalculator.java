package fangxu.com.library;

/**
 * Created by dear33 on 2016/8/20.
 */
public class AngleCalculator {
    private double mStartPiAngle;
    private double mAveragePiAngle;

    public AngleCalculator(int startAngle, int endAngle, int itemCount) {
        mStartPiAngle = getPiAngle(startAngle);
        double endPiAngle = getPiAngle(endAngle);
        mAveragePiAngle = (endPiAngle - mStartPiAngle) / (itemCount - 1);
    }

    public int getX(int index, double radius) {
        double angle = mStartPiAngle + mAveragePiAngle * (index - 1);
        return (int)(radius * Math.cos(angle));
    }

    public int getY(int index, double radius) {
        double angle = mStartPiAngle + mAveragePiAngle * (index - 1);
        int y = (int)(radius * Math.sin(angle));
        return toAndroidCoordinateY(y);
    }

    private int toAndroidCoordinateY(int y) {
        return -y;
    }


    private double getPiAngle(int angle) {
        angle = angle % 360;
        return ((double)angle / 180) * Math.PI;
    }
}
