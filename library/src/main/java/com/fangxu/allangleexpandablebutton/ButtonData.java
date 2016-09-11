package com.fangxu.allangleexpandablebutton;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

/**
 * Created by dear33 on 2016/9/9.
 */
public class ButtonData {
    private static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    private static final int DEFAULT_BUTTON_SIZE_DP = 55;
    private static final int DEFAULT_TEXT_SIZE_SP = 20;

    private String text;
    private Drawable icon;
    private boolean iconData;
    private float padding;
    private int textSizeSp = DEFAULT_TEXT_SIZE_SP;
    private int buttonSizeDp = DEFAULT_BUTTON_SIZE_DP;
    private int textColor = DEFAULT_TEXT_COLOR;
    private int backgroundColor = DEFAULT_BACKGROUND_COLOR;

    public ButtonData(boolean iconData) {
        this.iconData = iconData;
    }

    public String getText() {
        return text;
    }

    public ButtonData setText(String text) {
        this.text = text;
        return this;
    }

    public Drawable getIcon() {
        return icon;
    }

    public ButtonData setIcon(Drawable icon) {
        this.icon = icon;
        return this;
    }

    public boolean isIconData() {
        return iconData;
    }

    public ButtonData setButtonSizeDp(int buttonSizeDp) {
        this.buttonSizeDp = buttonSizeDp;
        return this;
    }

    public int getButtonSizeDp() {
        return buttonSizeDp;
    }

    public int getTextSizeSp() {
        return textSizeSp;
    }

    public ButtonData setTextSizeSp(int textSizeSp) {
        this.textSizeSp = textSizeSp;
        return this;
    }

    public float getPadding() {
        return padding;
    }

    public ButtonData setPadding(float padding) {
        this.padding = padding;
        return this;
    }

    public int getTextColor() {
        return textColor;
    }

    public ButtonData setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public ButtonData setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }
}
