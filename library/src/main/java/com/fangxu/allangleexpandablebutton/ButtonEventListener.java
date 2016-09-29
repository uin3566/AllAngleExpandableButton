package com.fangxu.allangleexpandablebutton;

/**
 * Created by dear33 on 2016/9/11.
 */
public interface ButtonEventListener {
    /**
     * @param index button index, count from startAngle to endAngle, value is 1 to expandButtonCount
     */
    void onButtonClicked(int index);

    void onExpand();
    void onCollapse();
}
