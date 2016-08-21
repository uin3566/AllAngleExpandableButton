package fangxu.com.library;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dear33 on 2016/8/20.
 */
public class AllAngleExpandableButton extends FrameLayout {
    private static final int DEFAULT_START_ANGLE = 90;
    private static final int DEFAULT_END_ANGLE = 180;

    private boolean mCollapsed;
    private Context mContext;

    private Map<ButtonBean, View> mViewMap;
    private List<ButtonBean> mDataList;

    private ButtonClickListener mListener;
    private int mStartAngle = DEFAULT_START_ANGLE;
    private int mEndAngle = DEFAULT_END_ANGLE;

    private int mLayoutX;
    private int mLayoutY;
    private ButtonBean mMainBean;

    private AngleCalculator mAngleCalculator;

    public AllAngleExpandableButton(Context context) {
        this(context, null);
    }

    public AllAngleExpandableButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllAngleExpandableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public AllAngleExpandableButton startAngle(int startAngle) {
        mStartAngle = startAngle;
        return this;
    }

    public AllAngleExpandableButton endAngle(int endAngle) {
        mEndAngle = endAngle;
        return this;
    }

    public AllAngleExpandableButton setClickListener(ButtonClickListener clickListener) {
        mListener = clickListener;
        return this;
    }

    public void setItems(List<ButtonBean> datas) {
        reset();
        initializeButtonList(datas);
        constructButtons();
        mAngleCalculator = new AngleCalculator(mStartAngle, mEndAngle, mDataList.size() - 1);
    }

    private void reset() {
        removeAllViews();
        mCollapsed = true;
        if (mViewMap != null) {
            mViewMap.clear();
            mViewMap = null;
        }
        if (mDataList != null) {
            mDataList.clear();
        }
    }

    private void initializeButtonList(List<ButtonBean> datas) {
        if (datas == null) {
            throw new NullPointerException("Button data list is null");
        }
        mDataList = new ArrayList<>(datas);
        mViewMap = new HashMap<>(datas.size());
    }

    private void constructButtons() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (ButtonBean bean : mDataList) {
            View view;
            if (bean.hasIcon()) {
                view = inflater.inflate(R.layout.image_button_expand, this, false);
                view.setBackgroundResource(bean.getIconRes());
            } else {
                view = inflater.inflate(R.layout.button_expand, this, false);
                ((Button) view).setText(bean.getText());
            }
            view.setVisibility(INVISIBLE);
            if (bean.isMainButton()) {
                mMainBean = bean;
                view.setVisibility(VISIBLE);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onViewClicked(0);
                    }
                });
            }
            mViewMap.put(bean, view);
            addView(view);
        }
    }

    private int getRadius() {
        int plusTime = 0;
        int radius = 0;
        for (ButtonBean bean : mDataList) {
            View view = mViewMap.get(bean);
            if (bean.isMainButton()) {
                radius += view.getWidth() / 2 + ((LayoutParams) view.getLayoutParams()).rightMargin;
                plusTime++;
                if (plusTime == 2) {
                    break;
                }
            } else {
                radius += view.getWidth() / 2 + ((LayoutParams) view.getLayoutParams()).rightMargin;
                plusTime++;
                if (plusTime == 2) {
                    break;
                }
            }
        }
        return radius;
    }

    private void onViewClicked(int position) {
        if (!canClick()) {
            return;
        }

        if (position == 0) {
            if (mCollapsed) {
                expand();
            } else {
                collapse();
            }
        } else {
            mListener.onSubButtonClick(position);
        }
    }

    private void expand() {
        mCollapsed = false;
        expandButtons();
        expandLayout();
    }

    private void expandButtons() {
        List<Animator> animators = new ArrayList<>(mDataList.size() * 2);
        for (int i = 0, len = mDataList.size(); i < len; i++) {
            ButtonBean bean = mDataList.get(i);
            if (bean.isMainButton()) {
                continue;
            }
            mViewMap.get(bean).setVisibility(VISIBLE);
            int index = bean.getIndex();
            int radius = getRadius();
            int toX = mAngleCalculator.getX(index, radius);
            int toY = mAngleCalculator.getY(index, radius);
            updateLayoutXY(toX, toY);
            View view = mViewMap.get(bean);
            int curX = (int)view.getTranslationX();
            int curY = (int)view.getTranslationY();
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "translationX", curX, toX);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "translationY", curY, toY);
            animators.add(animatorX);
            animators.add(animatorY);
        }
        playAnimators(animators);
    }

    private void expandLayout() {
        ViewGroup.LayoutParams params = getLayoutParams();
        View view = mViewMap.get(mMainBean);
        FrameLayout.LayoutParams viewParams = (FrameLayout.LayoutParams)view.getLayoutParams();
        int width = view.getWidth() + viewParams.rightMargin + viewParams.leftMargin + Math.abs(mLayoutX);
        int height = view.getHeight() + viewParams.topMargin + viewParams.bottomMargin + Math.abs(mLayoutY);
        params.width = width;
        params.height = height;
        requestLayout();
    }

    private void collapse() {
        mCollapsed = true;
        collapseButtons();
        collapseLayout();
    }

    private void collapseButtons() {
        List<Animator> animators = new ArrayList<>(mDataList.size() * 2);
        for (int i = 0, len = mDataList.size(); i < len; i++) {
            ButtonBean bean = mDataList.get(i);
            if (bean.isMainButton()) {
                continue;
            }
            mViewMap.get(bean).setVisibility(INVISIBLE);
            int toX = 0;
            int toY = 0;
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(mViewMap.get(bean), "translationX", toX);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(mViewMap.get(bean), "translationY", toY);
            animators.add(animatorX);
            animators.add(animatorY);
        }
        playAnimators(animators);
    }

    private void playAnimators(List<Animator> animators) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        set.start();
    }

    private void collapseLayout() {
        ViewGroup.LayoutParams params = getLayoutParams();
        View view = mViewMap.get(mMainBean);
        FrameLayout.LayoutParams viewParams = (FrameLayout.LayoutParams)view.getLayoutParams();
        params.width = view.getWidth() + viewParams.rightMargin + viewParams.leftMargin;
        params.height = view.getHeight() + viewParams.topMargin + viewParams.bottomMargin;
        mLayoutX = 0;
        mLayoutY = 0;
        requestLayout();
    }

    private void updateLayoutXY(int x, int y) {
        if (Math.abs(mLayoutX) < Math.abs(x)) {
            mLayoutX = x;
        }
        if (Math.abs(mLayoutY) < Math.abs(y)) {
            mLayoutY = y;
        }
    }

    private boolean canClick() {
        return mListener != null;
    }
}
