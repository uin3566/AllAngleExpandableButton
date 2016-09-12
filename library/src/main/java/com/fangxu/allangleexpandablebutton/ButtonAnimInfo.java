package com.fangxu.allangleexpandablebutton;

import android.content.Context;
import android.graphics.RectF;

/**
 * Created by dear33 on 2016/9/9.
 */
public class ButtonAnimInfo {
    private float scale;
    private float alpha;
    private float rotate;
    private RectF rectF;

    public void set(ButtonData buttonData, int buttonElevation) {
        int sizePx = buttonData.getButtonSizePx();
        rectF = new RectF(buttonElevation, buttonElevation, sizePx + buttonElevation, sizePx + buttonElevation);
        scale = 1;
        alpha = 1;
        rotate = 0;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getRotate() {
        return rotate;
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
    }

    public RectF getRectF() {
        return rectF;
    }

    public void setRectF(RectF rectF) {
        this.rectF = rectF;
    }
}
