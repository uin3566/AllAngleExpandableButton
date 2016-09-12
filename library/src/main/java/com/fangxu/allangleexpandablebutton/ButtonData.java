package com.fangxu.allangleexpandablebutton;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

/**
 * Created by dear33 on 2016/9/9.
 */
public class ButtonData implements Cloneable{
    private static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private static final int DEFAULT_TEXT_COLOR = Color.BLUE;
    private static final int DEFAULT_TEXT_SIZE_SP = 20;

    private String text;
    private Drawable icon;
    private boolean iconData;
    private float paddingDp;
    private int buttonSizePx;
    private boolean isMainButton = false;
    private int textSizeSp = DEFAULT_TEXT_SIZE_SP;
    private int textColor = DEFAULT_TEXT_COLOR;
    private int backgroundColor = DEFAULT_BACKGROUND_COLOR;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ButtonData buttonData = (ButtonData)super.clone();
        buttonData.setIsIconData(this.iconData).setButtonSizePx(this.buttonSizePx)
                .setBackgroundColor(this.backgroundColor).setIsMainButton(this.isMainButton)
                .setIcon(this.icon).setPaddingDp(this.paddingDp).setText(this.text)
                .setTextColor(this.textColor).setTextSizeSp(this.textSizeSp);
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

    public ButtonData setButtonSizePx(int buttonSizePx) {
        this.buttonSizePx = buttonSizePx;
        return this;
    }

    public int getButtonSizePx() {
        return buttonSizePx;
    }

    public int getTextSizeSp() {
        return textSizeSp;
    }

    public ButtonData setTextSizeSp(int textSizeSp) {
        this.textSizeSp = textSizeSp;
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
