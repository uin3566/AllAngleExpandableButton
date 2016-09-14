package com.fangxu.allangleexpandablebutton;

import android.graphics.Color;

/**
 * Created by dear33 on 2016/9/9.
 */
public class ButtonData implements Cloneable{
    private static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;

    private boolean isMainButton = false;
    private boolean iconData;
    private String text;
    private int iconResId;
    private float paddingDp;
    private int buttonSizePx;
    private int textSizePx;
    private int textColor;
    private int backgroundColor = DEFAULT_BACKGROUND_COLOR;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ButtonData buttonData = (ButtonData)super.clone();
        buttonData.setIsIconData(this.iconData).setButtonSizePx(this.buttonSizePx)
                .setBackgroundColor(this.backgroundColor).setIsMainButton(this.isMainButton)
                .setIconResId(this.iconResId).setPaddingDp(this.paddingDp).setText(this.text)
                .setTextColor(this.textColor).setTextSizePx(this.textSizePx);
        return buttonData;
    }

    public ButtonData(boolean iconData) {
        this.iconData = iconData;
    }

    public ButtonData setIsMainButton(boolean isMainButton) {
        this.isMainButton = isMainButton;
        return this;
    }

    public boolean isMainButton() {
        return isMainButton;
    }

    public ButtonData setIsIconData(boolean isIconData) {
        iconData = isIconData;
        return this;
    }

    public String getText() {
        return text;
    }

    public ButtonData setText(String text) {
        this.text = text;
        return this;
    }

    public int getIconResId() {
        return iconResId;
    }

    public ButtonData setIconResId(int iconResId) {
        this.iconResId = iconResId;
        return this;
    }

    public boolean isIconData() {
        return iconData;
    }

    public ButtonData setButtonSizePx(int buttonSizePx) {
        this.buttonSizePx = buttonSizePx;
        return this;
    }

    public int getButtonSizePx() {
        return buttonSizePx;
    }

    public int getTextSizePx() {
        return textSizePx;
    }

    public ButtonData setTextSizePx(int textSizeSp) {
        this.textSizePx = textSizeSp;
        return this;
    }

    public float getPaddingDp() {
        return paddingDp;
    }

    public ButtonData setPaddingDp(float padding) {
        this.paddingDp = padding;
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
