package com.fangxu.allangleexpandablebutton;

/**
 * Created by dear33 on 2016/9/9.
 */
public class ButtonData implements Cloneable{
    private static final int DEFAULT_BACKGROUND_COLOR_ID = android.R.color.white;

    private boolean isMainButton = false;
    private boolean iconButton;

    private String text;
    private int iconResId;
    private float iconPaddingDp;
    private int backgroundColorId = DEFAULT_BACKGROUND_COLOR_ID;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ButtonData buttonData = (ButtonData)super.clone();
        buttonData.setIsIconButton(this.iconButton);
        buttonData.setBackgroundColorId(this.backgroundColorId);
        buttonData.setIsMainButton(this.isMainButton);
        buttonData.setIconResId(this.iconResId);
        buttonData.setIconPaddingDp(this.iconPaddingDp);
        buttonData.setText(this.text);
        return buttonData;
    }

    public void configTextButton(String text) {
        this.iconButton = false;
        this.text = text;
    }

    public void configIconButton(int iconResId, float iconPaddingDp) {
        this.iconButton = true;
        this.iconResId = iconResId;
        this.iconPaddingDp = iconPaddingDp;
    }

    public ButtonData(boolean iconButton) {
        this.iconButton = iconButton;
    }

    public void setIsMainButton(boolean isMainButton) {
        this.isMainButton = isMainButton;
    }

    public boolean isMainButton() {
        return isMainButton;
    }

    public void setIsIconButton(boolean isIconButton) {
        iconButton = isIconButton;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public boolean isIconButton() {
        return iconButton;
    }

    public float getIconPaddingDp() {
        return iconPaddingDp;
    }

    public void setIconPaddingDp(float padding) {
        this.iconPaddingDp = padding;
    }

    public int getBackgroundColorId() {
        return backgroundColorId;
    }

    public void setBackgroundColorId(int backgroundColorId) {
        this.backgroundColorId = backgroundColorId;
    }
}
