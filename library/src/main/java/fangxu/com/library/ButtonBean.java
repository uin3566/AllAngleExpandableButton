package fangxu.com.library;

/**
 * Created by dear33 on 2016/8/20.
 */
public class ButtonBean {
    private static final int NO_ICON = -1;
    private String mText = null;
    private int mIconRes = NO_ICON;
    private int mIndex;

    public ButtonBean(String mText, int index) {
        this.mText = mText;
        mIndex = index;
    }

    public ButtonBean(int mIconRes, int index) {
        this.mIconRes = mIconRes;
        mIndex = index;
    }

    public boolean isMainButton() {
        return mIndex == 0;
    }

    public int getIndex() {
        return mIndex;
    }

    public boolean hasIcon() {
        return mIconRes != NO_ICON;
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public int getIconRes() {
        return mIconRes;
    }

    public void setIconRes(int mIconRes) {
        this.mIconRes = mIconRes;
    }
}
