package com.fangxu.allangleexpandablebutton;

import android.graphics.Color;

/**
 * Created by dear33 on 2016/9/9.
 */
public class ButtonData implements Cloneable{
    private static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;

    private boolean isMainButton = false;
    private boolean iconButton;
    private String text;
    private int iconResId;
    private float iconPaddingDp;
    private int backgroundColor = DEFAULT_BACKGROUND_COLOR;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ButtonData buttonData = (ButtonData)super.clone();
        buttonData.setIsIconButton(this.iconButton).setBackgroundColor(this.backgroundColor).setIsMainButton(this.isMainButton)
                .setIconResId(this.iconResId).setIconPaddingDp(this.iconPaddingDp).setText(this.text);
        return buttonData;
    }

    public ButtonData(boolean iconButton) {
        this.iconButton = iconButton;
    }

    public ButtonData setIsMainButton(boolean isMainButton) {
        this.isMainButton = isMainButton;
        return this;
    }

    public boolean isMainButton() {
        return isMainButton;
    }

    public ButtonData setIsIconButton(boolean isIconButton) {
        iconButton = isIconButton;
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

    public boolean isIconButton() {
        return iconButton;
    }

    public float getIconPaddingDp() {
        return iconPaddingDp;
    }

    public ButtonData setIconPaddingDp(float padding) {
        this.iconPaddingDp = padding;
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
