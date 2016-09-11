package com.fangxu.allangleexpandablebutton;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fangxu.com.library.R;

/**
 * Created by dear33 on 2016/9/8.
 */
public class AllAngleExpandableButton extends View implements ValueAnimator.AnimatorUpdateListener {
    private List<ButtonData> buttonDatas;
    private Map<ButtonData, ButtonAnimInfo> animInfoMap;

    private OnButtonClickListener buttonClickListener;

    private static final int DEFAULT_EXPAND_ANIMATE_DURATION = 225;
    private static final int DEFAULT_BUTTON_GAP_DP = 50;
    private static final int DEFAULT_BUTTON_MAIN_SIZE_DP = 60;
    private static final int DEFAULT_BUTTON_SUB_SIZE_DP = 60;
    private static final int DEFAULT_START_ANGLE = 90;
    private static final int DEFAULT_END_ANGLE = 180;
    private static final int DEFAULT_MASK_BACKGROUND_COLOR = 0x00000000;

    private boolean expanded = false;

    private float startAngle;
    private float endAngle;
    private int buttonGapPx;
    private int buttonMainSizePx;
    private int buttonSubSizePx;
    private int animDuration;
    private int maskBackgroundColor;
    private ExpandMode expandMode = ExpandMode.MODE_SELECTION;

    private RectF buttonOval;
    private PointF buttonCenter;
    private int buttonRadius;
    private int width;
    private int height;

    private Paint paint;
    private Paint textPaint;

    private boolean animating = false;
    private boolean maskAttached = false;
    private ValueAnimator updateValueAnimator;
    private float animateProgress;
    private MaskView maskView;
    private AngleCalculator angleCalculator;

    public AllAngleExpandableButton(Context context) {
        this(context, null);
    }

    public AllAngleExpandableButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllAngleExpandableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AllAngleExpandableButton);
        startAngle = ta.getInteger(R.styleable.AllAngleExpandableButton_aebStartAngleDegree, DEFAULT_START_ANGLE);
        endAngle = ta.getInteger(R.styleable.AllAngleExpandableButton_aebEndAngleDegree, DEFAULT_END_ANGLE);
        buttonGapPx = ta.getDimensionPixelSize(R.styleable.AllAngleExpandableButton_aebButtonGapDp, DEFAULT_BUTTON_GAP_DP);
        buttonMainSizePx = ta.getDimensionPixelSize(R.styleable.AllAngleExpandableButton_aebMainSizeDp, DEFAULT_BUTTON_MAIN_SIZE_DP);
        buttonSubSizePx = ta.getDimensionPixelSize(R.styleable.AllAngleExpandableButton_aebSubSizeDp, DEFAULT_BUTTON_SUB_SIZE_DP);
        animDuration = ta.getInteger(R.styleable.AllAngleExpandableButton_aebAnimDurationMillis, DEFAULT_EXPAND_ANIMATE_DURATION);
        maskBackgroundColor = ta.getInteger(R.styleable.AllAngleExpandableButton_aebMaskBackgroundColor, DEFAULT_MASK_BACKGROUND_COLOR);
        ta.recycle();
    }

    public void setButtonClickListener(OnButtonClickListener listener) {
        buttonClickListener = listener;
    }

    public AllAngleExpandableButton setButtonDatas(List<ButtonData> buttonDatas) {
        if (buttonDatas == null) {
            return this;
        }
        this.buttonDatas = new ArrayList<>(buttonDatas);
        if (expandMode == ExpandMode.MODE_SELECTION) {
            try {
                this.buttonDatas.add(0, (ButtonData) buttonDatas.get(0).clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        animInfoMap = new HashMap<>(this.buttonDatas.size());
        for (int i = 0, size = this.buttonDatas.size(); i < size; i++) {
            ButtonData buttonData = this.buttonDatas.get(i);
            if (i == 0) {
                buttonData.setButtonSizePx(buttonMainSizePx);
            } else {
                buttonData.setButtonSizePx(buttonSubSizePx);
            }
            ButtonAnimInfo info = new ButtonAnimInfo();
            info.set(buttonData);
            animInfoMap.put(buttonData, info);
        }
        angleCalculator = new AngleCalculator(startAngle, endAngle, this.buttonDatas.size() - 1);
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (buttonOval == null || buttonDatas == null || buttonDatas.isEmpty()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        ButtonData buttonData = buttonDatas.get(0);
        int desiredWidth = buttonData.getButtonSizePx();
        int desiredHeight = buttonData.getButtonSizePx();

        setMeasuredDimension(desiredWidth, desiredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawButton(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        initButtonInfo();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return !animating;
            case MotionEvent.ACTION_UP:
                if (expanded) {
                    collapse();
                } else {
                    expand();
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        if (valueAnimator == updateValueAnimator) {
            animateProgress = (float) valueAnimator.getAnimatedValue();
            Log.i("animateProgress-->", "" + animateProgress);
        }
        invalidate();
        if (maskAttached) {
            maskView.updateButtons();
            maskView.invalidate();
        }
    }

    private void expand() {
        checkUpdateAnimator();
        updateValueAnimator = ValueAnimator.ofFloat(0, 1);
        updateValueAnimator.setDuration(animDuration);
        updateValueAnimator.addUpdateListener(this);
        updateValueAnimator.addListener(new Animator.AnimatorListener() {
            boolean canceled = false;

            @Override
            public void onAnimationStart(Animator animator) {
                animating = true;
                attachMask();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animating = false;
                if (!canceled) {
                    expanded = true;
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                canceled = true;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        updateValueAnimator.start();
    }

    private void checkUpdateAnimator() {
        if (updateValueAnimator != null) {
            updateValueAnimator.cancel();
        }
    }

    private void collapse() {
        checkUpdateAnimator();
        updateValueAnimator = ValueAnimator.ofFloat(1, 0);
        updateValueAnimator.setDuration(animDuration);
        updateValueAnimator.addUpdateListener(this);
        updateValueAnimator.addListener(new Animator.AnimatorListener() {
            boolean canceled = false;

            @Override
            public void onAnimationStart(Animator animator) {
                animating = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animating = false;
                if (!canceled) {
                    expanded = false;
                    detachMask();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                canceled = true;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        updateValueAnimator.start();
    }

    private void attachMask() {
        if (maskView == null) {
            maskView = new MaskView(getContext(), this);
        }

        if (!maskAttached) {
            ViewGroup root = (ViewGroup) getRootView();
            root.addView(maskView);
            maskAttached = true;
            maskView.initButtonRect();
        }
    }

    private void detachMask() {
        if (maskAttached) {
            ViewGroup root = (ViewGroup) getRootView();
            root.removeView(maskView);
            maskAttached = false;
            for (int i = 0; i < buttonDatas.size(); i++) {
                ButtonData buttonData = buttonDatas.get(i);
                RectF rectF = animInfoMap.get(buttonData).getRectF();
                int size = buttonData.getButtonSizePx();
                rectF.set(0, 0, size, size);
            }
        }
        invalidate();
    }

    private void drawButton(Canvas canvas) {
        if (buttonOval == null || buttonDatas == null || buttonDatas.isEmpty()) {
            return;
        }

        ButtonData buttonData = buttonDatas.get(0);
        drawButton(canvas, paint, buttonData);
    }

    private void drawButton(Canvas canvas, Paint paint, ButtonData buttonData) {
        paint.setColor(buttonData.getBackgroundColor());
        RectF rectF = animInfoMap.get(buttonData).getRectF();
        canvas.drawOval(rectF, paint);
        if (buttonData.isIconData()) {
            if (buttonData.getIcon() == null) {
                throw new IllegalArgumentException("iconData is true, icon drawable cannot be null");
            }
            Drawable drawable = buttonData.getIcon();
            int left = (int) rectF.left + DimenUtil.dp2px(getContext(), buttonData.getPaddingDp());
            int right = (int) rectF.right - DimenUtil.dp2px(getContext(), buttonData.getPaddingDp());
            int top = (int) rectF.top + DimenUtil.dp2px(getContext(), buttonData.getPaddingDp());
            int bottom = (int) rectF.bottom - DimenUtil.dp2px(getContext(), buttonData.getPaddingDp());
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
        } else {
            if (buttonData.getText() == null) {
                throw new IllegalArgumentException("iconData is false, text cannot be null");
            }
            String text = buttonData.getText();
            textPaint = getTextPaint(buttonData.getTextSizeSp(), buttonData.getTextColor());
            canvas.drawText(text, rectF.centerX(), rectF.centerY() - (textPaint.ascent() + textPaint.descent()) / 2, textPaint);
        }
    }

    private Paint getTextPaint(int sizeSp, int color) {
        if (textPaint == null) {
            textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setTextAlign(Paint.Align.CENTER);
        }

        textPaint.setTextSize(DimenUtil.sp2px(getContext(), sizeSp));
        textPaint.setColor(color);
        return textPaint;
    }

    private void initButtonInfo() {
        float innerWidth = width - (getPaddingLeft() + getPaddingRight());
        float innerHeight = height - (getPaddingTop() + getPaddingBottom());
        buttonRadius = (int) (Math.min(innerWidth / 2, innerHeight / 2));
        buttonCenter = new PointF(getPaddingLeft() + innerWidth / 2, getPaddingTop() + innerHeight / 2);
        buttonOval = new RectF(buttonCenter.x - buttonRadius, buttonCenter.y - buttonRadius
                , buttonCenter.x + buttonRadius, buttonCenter.y + buttonRadius);
    }

    @SuppressLint("ViewConstructor")
    private static class MaskView extends View {
        private AllAngleExpandableButton allAngleExpandableButton;
        private Rect rawButtonRect = new Rect();
        private RectF initialSubButtonRectF = new RectF();
        private Paint paint;

        public MaskView(Context context, AllAngleExpandableButton button) {
            super(context);
            allAngleExpandableButton = button;

            paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);

            setBackgroundColor(allAngleExpandableButton.maskBackgroundColor);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            View root = getRootView();
            setMeasuredDimension(root.getWidth(), root.getHeight());
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawButtons(canvas, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return allAngleExpandableButton.expanded;
                case MotionEvent.ACTION_UP:
                    int index = getTouchedButtonIndex(event.getX(), event.getY());
                    onButtonSelected(index);
                    break;
            }
            return super.onTouchEvent(event);
        }

        private void onButtonSelected(int index) {
            Log.i("testOnTouch", "index=" + index);
            if (allAngleExpandableButton.buttonClickListener != null) {
                allAngleExpandableButton.buttonClickListener.onButtonClicked(index);
            }
            if (allAngleExpandableButton.expandMode == ExpandMode.MODE_SELECTION) {
                if (index > 0) {
                    ButtonData buttonData = allAngleExpandableButton.buttonDatas.get(index);
                    ButtonData mainButton = allAngleExpandableButton.buttonDatas.get(0);
                    if (buttonData.isIconData()) {
                        mainButton.setIsIconData(true);
                        mainButton.setIcon(buttonData.getIcon());
                    } else {
                        mainButton.setIsIconData(false);
                        mainButton.setText(buttonData.getText());
                    }
                }
            }
            allAngleExpandableButton.collapse();
        }

        private int getTouchedButtonIndex(float x, float y) {
            for (int i = 0; i < allAngleExpandableButton.buttonDatas.size(); i++) {
                ButtonData buttonData = allAngleExpandableButton.buttonDatas.get(i);
                ButtonAnimInfo animInfo = allAngleExpandableButton.animInfoMap.get(buttonData);
                RectF rectF = animInfo.getRectF();
                if (x >= rectF.left && x <= rectF.right && y >= rectF.top && y <= rectF.bottom) {
                    return i;
                }
            }
            return -1;
        }

        private void initButtonRect() {
            allAngleExpandableButton.getGlobalVisibleRect(rawButtonRect);
            for (int i = 0; i < allAngleExpandableButton.buttonDatas.size(); i++) {
                ButtonData buttonData = allAngleExpandableButton.buttonDatas.get(i);
                ButtonAnimInfo animInfo = allAngleExpandableButton.animInfoMap.get(buttonData);
                RectF rectF = animInfo.getRectF();
                int buttonRadius = buttonData.getButtonSizePx() / 2;
                if (i == 0) {
                    rectF.left = rawButtonRect.left;
                    rectF.right = rawButtonRect.right;
                    rectF.top = rawButtonRect.top;
                    rectF.bottom = rawButtonRect.bottom;
                } else {
                    float leftTmp = rectF.left;
                    float topTmp = rectF.top;
                    rectF.left = leftTmp + rawButtonRect.centerX() - buttonRadius;
                    rectF.right = leftTmp + rawButtonRect.centerX() + buttonRadius;
                    rectF.top = topTmp + rawButtonRect.centerY() - buttonRadius;
                    rectF.bottom = topTmp + rawButtonRect.centerY() + buttonRadius;
                    initialSubButtonRectF.set(rectF);
                }
            }
        }

        private void updateButtons() {
            List<ButtonData> buttonDatas = allAngleExpandableButton.buttonDatas;
            ButtonData buttonData = allAngleExpandableButton.buttonDatas.get(0);
            int radiusMain = buttonData.getButtonSizePx() / 2;
            for (int i = 1; i < buttonDatas.size(); i++) {
                buttonData = buttonDatas.get(i);
                ButtonAnimInfo animInfo = allAngleExpandableButton.animInfoMap.get(buttonData);
                RectF rectF = animInfo.getRectF();
                int desX;
                int desY;
                int radiusCurrent = buttonData.getButtonSizePx() / 2;
                int radius = radiusMain + radiusCurrent + allAngleExpandableButton.buttonGapPx;
                if (allAngleExpandableButton.expanded) {
                    rectF.left = rectF.left - (rectF.left - initialSubButtonRectF.left) * (1 - allAngleExpandableButton.animateProgress);
                    rectF.right = rectF.right - (rectF.right - initialSubButtonRectF.right) * (1 - allAngleExpandableButton.animateProgress);
                    rectF.top = rectF.top - (rectF.top - initialSubButtonRectF.top) * (1 - allAngleExpandableButton.animateProgress);
                    rectF.bottom = rectF.bottom - (rectF.bottom - initialSubButtonRectF.bottom) * (1 - allAngleExpandableButton.animateProgress);
                } else {
                    desX = allAngleExpandableButton.angleCalculator.getDesX(radius, i);
                    desY = allAngleExpandableButton.angleCalculator.getDesY(radius, i);
                    rectF.left = initialSubButtonRectF.left + desX * allAngleExpandableButton.animateProgress;
                    rectF.right = initialSubButtonRectF.right + desX * allAngleExpandableButton.animateProgress;
                    rectF.top = initialSubButtonRectF.top - desY * allAngleExpandableButton.animateProgress;
                    rectF.bottom = initialSubButtonRectF.bottom - desY * allAngleExpandableButton.animateProgress;
                }
            }
        }

        private void drawButtons(Canvas canvas, Paint paint) {
            for (int i = allAngleExpandableButton.buttonDatas.size() - 1; i >= 0; i--) {
                ButtonData buttonData = allAngleExpandableButton.buttonDatas.get(i);
                allAngleExpandableButton.drawButton(canvas, paint, buttonData);
            }
        }
    }
}
