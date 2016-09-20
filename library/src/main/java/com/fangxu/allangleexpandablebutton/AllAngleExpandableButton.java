package com.fangxu.allangleexpandablebutton;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
    private Map<ButtonData, RectF> buttonRects;
    private OnButtonClickListener buttonClickListener;

    private static final int BUTTON_SHADOW_COLOR = 0xff000000;
    private static final int BUTTON_SHADOW_ALPHA = 24;

    private static final int DEFAULT_EXPAND_ANIMATE_DURATION = 225;
    private static final int DEFAULT_BUTTON_GAP_DP = 50;
    private static final int DEFAULT_BUTTON_MAIN_SIZE_DP = 60;
    private static final int DEFAULT_BUTTON_SUB_SIZE_DP = 60;
    private static final int DEFAULT_BUTTON_ELEVATION_DP = 4;
    private static final int DEFAULT_BUTTON_TEXT_SIZE_SP = 20;
    private static final int DEFAULT_START_ANGLE = 90;
    private static final int DEFAULT_END_ANGLE = 180;
    private static final int DEFAULT_BUTTON_TEXT_COLOR = Color.BLACK;
    private static final int DEFAULT_MASK_BACKGROUND_COLOR = 0x00000000;

    private boolean expanded = false;

    private float startAngle;
    private float endAngle;
    private int buttonGapPx;
    private int mainButtonSizePx;
    private int subButtonSizePx;
    private int mainButtonTextSize;
    private int subButtonTextSize;
    private int mainButtonTextColor;
    private int subButtonTextColor;
    private int animDuration;
    private int maskBackgroundColor;
    private int buttonElevationPx;
    private boolean isSelectionMode;
    private boolean rippleEffect;
    private int rippleColor = Integer.MIN_VALUE;

    private Bitmap mainShadowBitmap = null;
    private Bitmap subShadowBitmap = null;
    Matrix shadowMatrix = new Matrix();

    private int buttonSideMarginPx;
    private RectF buttonOval;
    private int width;
    private int height;

    private Paint paint;
    private Paint textPaint;

    private boolean animating = false;
    private boolean maskAttached = false;
    private float animateProgress;
    private ValueAnimator expandValueAnimator;
    private ValueAnimator collapseValueAnimator;
    private Path ripplePath;
    private RippleInfo rippleInfo = new RippleInfo();
    private MaskView maskView;
    private AngleCalculator angleCalculator;
    private float pressX, pressY;

    private static class RippleInfo {
        float pressX;
        float pressY;
        float rippleRadius;
        int buttonIndex;
        int rippleColor = Integer.MIN_VALUE;
    }

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

        buttonGapPx = ta.getDimensionPixelSize(R.styleable.AllAngleExpandableButton_aebButtonGapDp, dp2px(context, DEFAULT_BUTTON_GAP_DP));
        mainButtonSizePx = ta.getDimensionPixelSize(R.styleable.AllAngleExpandableButton_aebMainButtonSizeDp, dp2px(context, DEFAULT_BUTTON_MAIN_SIZE_DP));
        subButtonSizePx = ta.getDimensionPixelSize(R.styleable.AllAngleExpandableButton_aebSubButtonSizeDp, dp2px(context, DEFAULT_BUTTON_SUB_SIZE_DP));
        buttonElevationPx = ta.getDimensionPixelSize(R.styleable.AllAngleExpandableButton_aebButtonElevation, dp2px(context, DEFAULT_BUTTON_ELEVATION_DP));
        buttonSideMarginPx = buttonElevationPx * 2;
        mainButtonTextSize = ta.getDimensionPixelSize(R.styleable.AllAngleExpandableButton_aebMainButtonTextSizeSp, sp2px(context, DEFAULT_BUTTON_TEXT_SIZE_SP));
        subButtonTextSize = ta.getDimensionPixelSize(R.styleable.AllAngleExpandableButton_aebSubButtonTextSizeSp, sp2px(context, DEFAULT_BUTTON_TEXT_SIZE_SP));
        mainButtonTextColor = ta.getColor(R.styleable.AllAngleExpandableButton_aebMainButtonTextColor, DEFAULT_BUTTON_TEXT_COLOR);
        subButtonTextColor = ta.getColor(R.styleable.AllAngleExpandableButton_aebSubButtonTextColor, DEFAULT_BUTTON_TEXT_COLOR);

        animDuration = ta.getInteger(R.styleable.AllAngleExpandableButton_aebAnimDurationMillis, DEFAULT_EXPAND_ANIMATE_DURATION);
        maskBackgroundColor = ta.getInteger(R.styleable.AllAngleExpandableButton_aebMaskBackgroundColor, DEFAULT_MASK_BACKGROUND_COLOR);
        isSelectionMode = ta.getBoolean(R.styleable.AllAngleExpandableButton_aebIsSelectionMode, true);
        rippleEffect = ta.getBoolean(R.styleable.AllAngleExpandableButton_aebRippleEffect, true);
        rippleColor = ta.getColor(R.styleable.AllAngleExpandableButton_aebRippleColor, rippleColor);
        ta.recycle();

        initAnimators();
    }

    private void initAnimators() {
        expandValueAnimator = ValueAnimator.ofFloat(0, 1);
        expandValueAnimator.setDuration(animDuration);
        expandValueAnimator.setInterpolator(new OvershootInterpolator());
        expandValueAnimator.addUpdateListener(this);
        expandValueAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                animating = true;
                attachMask();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animating = false;
                expanded = true;
            }
        });

        collapseValueAnimator = ValueAnimator.ofFloat(1, 0);
        collapseValueAnimator.setDuration(animDuration);
        collapseValueAnimator.setInterpolator(new AnticipateInterpolator());
        collapseValueAnimator.addUpdateListener(this);
        collapseValueAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                animating = true;
                maskView.reset();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animating = false;
                expanded = false;
                detachMask();
            }
        });
    }

    public void setButtonClickListener(OnButtonClickListener listener) {
        buttonClickListener = listener;
    }

    public AllAngleExpandableButton setButtonDatas(List<ButtonData> buttonDatas) {
        if (buttonDatas == null || buttonDatas.isEmpty()) {
            return this;
        }
        this.buttonDatas = new ArrayList<>(buttonDatas);
        if (isSelectionMode) {
            try {
                this.buttonDatas.add(0, (ButtonData) buttonDatas.get(0).clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        buttonRects = new HashMap<>(this.buttonDatas.size());
        for (int i = 0, size = this.buttonDatas.size(); i < size; i++) {
            ButtonData buttonData = this.buttonDatas.get(i);
            buttonData.setIsMainButton(i == 0);
            int buttonSizePx = buttonData.isMainButton() ? mainButtonSizePx : subButtonSizePx;
            RectF rectF = new RectF(buttonSideMarginPx, buttonSideMarginPx
                    , buttonSizePx + buttonSideMarginPx, buttonSizePx + buttonSideMarginPx);
            buttonRects.put(buttonData, rectF);
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

        int desiredWidth = mainButtonSizePx + buttonSideMarginPx * 2;
        int desiredHeight = mainButtonSizePx + buttonSideMarginPx * 2;

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
                return !animating && buttonDatas != null && !buttonDatas.isEmpty();
            case MotionEvent.ACTION_UP:
                pressX = event.getRawX();
                pressY = event.getRawY();
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
        animateProgress = (float) valueAnimator.getAnimatedValue();
        if (maskAttached) {
            maskView.updateButtons2();
            maskView.invalidate();
        }
    }

    private void expand() {
        if (expandValueAnimator.isRunning()) {
            expandValueAnimator.cancel();
        }
        expandValueAnimator.start();
    }

    private void collapse() {
        if (collapseValueAnimator.isRunning()) {
            collapseValueAnimator.cancel();
        }
        collapseValueAnimator.start();
    }

    private void attachMask() {
        if (maskView == null) {
            maskView = new MaskView(getContext(), this);
        }

        if (!maskAttached) {
            ViewGroup root = (ViewGroup) getRootView();
            root.addView(maskView);
            maskAttached = true;
            maskView.reset();
            maskView.initButtonRect();
            maskView.onClickMainButton();
        }
    }

    private void detachMask() {
        if (maskAttached) {
            ViewGroup root = (ViewGroup) getRootView();
            root.removeView(maskView);
            maskAttached = false;
            for (int i = 0; i < buttonDatas.size(); i++) {
                ButtonData buttonData = buttonDatas.get(i);
                RectF rectF = buttonRects.get(buttonData);
                int size = buttonData.isMainButton() ? mainButtonSizePx : subButtonSizePx;
                rectF.set(buttonSideMarginPx, buttonSideMarginPx, buttonSideMarginPx + size, buttonSideMarginPx + size);
            }
        }
        invalidate();
    }

    private void resetRippleInfo() {
        rippleInfo.buttonIndex = Integer.MIN_VALUE;
        rippleInfo.pressX = 0;
        rippleInfo.pressY = 0;
        rippleInfo.rippleRadius = 0;
    }

    private void drawButton(Canvas canvas) {
        if (buttonOval == null || buttonDatas == null || buttonDatas.isEmpty()) {
            return;
        }

        ButtonData buttonData = buttonDatas.get(0);
        drawButton(canvas, paint, buttonData);
    }

    private void drawButton(Canvas canvas, Paint paint, ButtonData buttonData) {
        drawShadow(canvas, paint, buttonData);
        drawContent(canvas, paint, buttonData);
        drawRipple(canvas, paint, buttonData);
    }

    private void drawShadow(Canvas canvas, Paint paint, ButtonData buttonData) {
        if (buttonElevationPx <= 0) {
            return;
        }

        if (buttonData.isMainButton()) {
            if (animating || expanded) {
                return;
            }
        }

        float left, top;
        Bitmap bitmap;
        if (buttonData.isMainButton()) {
            mainShadowBitmap = getButtonShadowBitmap(buttonData);
            bitmap = mainShadowBitmap;
        } else {
            subShadowBitmap = getButtonShadowBitmap(buttonData);
            bitmap = subShadowBitmap;
        }

        int shadowOffset = buttonElevationPx / 2;
        RectF rectF = buttonRects.get(buttonData);
        left = rectF.centerX() - bitmap.getWidth() / 2;
        top = rectF.centerY() - bitmap.getHeight() / 2 + shadowOffset;
        shadowMatrix.reset();
        if (!buttonData.isMainButton()) {
            shadowMatrix.postScale(animateProgress, animateProgress, bitmap.getWidth() / 2, bitmap.getHeight() / 2 + shadowOffset);
        }
        shadowMatrix.postTranslate(left, top);
        paint.setAlpha(255);
        canvas.drawBitmap(bitmap, shadowMatrix, paint);
    }

    private void drawContent(Canvas canvas, Paint paint, ButtonData buttonData) {
        paint.setAlpha(255);
        paint.setColor(getContext().getResources().getColor(buttonData.getBackgroundColorId()));
        RectF rectF = buttonRects.get(buttonData);
        canvas.drawOval(rectF, paint);
        if (buttonData.isIconButton()) {
            int iconRes = buttonData.getIconResId();
            Drawable drawable = getContext().getResources().getDrawable(iconRes);
            if (drawable == null) {
                throw new RuntimeException("can not get Drawable by iconRes id");
            }
            int left = (int) rectF.left + dp2px(getContext(), buttonData.getIconPaddingDp());
            int right = (int) rectF.right - dp2px(getContext(), buttonData.getIconPaddingDp());
            int top = (int) rectF.top + dp2px(getContext(), buttonData.getIconPaddingDp());
            int bottom = (int) rectF.bottom - dp2px(getContext(), buttonData.getIconPaddingDp());
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
        } else {
            if (buttonData.getText() == null) {
                throw new IllegalArgumentException("iconData is false, text cannot be null");
            }
            String text = buttonData.getText();
            int sizePx = buttonData.isMainButton() ? mainButtonTextSize : subButtonTextSize;
            int textColor = buttonData.isMainButton() ? mainButtonTextColor : subButtonTextColor;
            textPaint = getTextPaint(sizePx, textColor);
            canvas.drawText(text, rectF.centerX(), rectF.centerY() - (textPaint.ascent() + textPaint.descent()) / 2, textPaint);
        }
    }

    private void drawRipple(Canvas canvas, Paint paint, ButtonData buttonData) {
        int pressIndex = buttonDatas.indexOf(buttonData);
        if (!rippleEffect || pressIndex == -1 || pressIndex != rippleInfo.buttonIndex) {
            return;
        }

        paint.setColor(rippleInfo.rippleColor);
        paint.setAlpha(48);
        canvas.save();
        if (ripplePath == null) {
            ripplePath = new Path();
        }
        ripplePath.reset();
        RectF rectF = buttonRects.get(buttonData);
        float radius = rectF.right - rectF.centerX();
        ripplePath.addCircle(rectF.centerX(), rectF.centerY(), radius, Path.Direction.CW);
        canvas.clipPath(ripplePath);
        canvas.drawCircle(rippleInfo.pressX, rippleInfo.pressY, rippleInfo.rippleRadius, paint);
        canvas.restore();
    }

    private Bitmap getButtonShadowBitmap(ButtonData buttonData) {
        if (buttonData.isMainButton()) {
            if (mainShadowBitmap != null) {
                return mainShadowBitmap;
            }
        } else {
            if (subShadowBitmap != null) {
                return subShadowBitmap;
            }
        }

        int buttonSizePx = buttonData.isMainButton() ? mainButtonSizePx : subButtonSizePx;
        int buttonRadius = buttonSizePx / 2;
        int bitmapRadius = buttonRadius + buttonElevationPx;
        int bitmapSize = bitmapRadius * 2;
        Bitmap bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(0x0);
        int colors[] = {ColorUtils.setAlphaComponent(BUTTON_SHADOW_COLOR, BUTTON_SHADOW_ALPHA),
                ColorUtils.setAlphaComponent(BUTTON_SHADOW_COLOR, 0)};
        float stops[] = {(float) (buttonRadius - buttonElevationPx) / (float) bitmapRadius, 1};
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new RadialGradient(bitmapRadius, bitmapRadius, bitmapRadius, colors, stops, Shader.TileMode.CLAMP));
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, bitmapSize, bitmapSize, paint);
        if (buttonData.isMainButton()) {
            mainShadowBitmap = bitmap;
            return mainShadowBitmap;
        } else {
            subShadowBitmap = bitmap;
            return subShadowBitmap;
        }
    }

    private Paint getTextPaint(int sizePx, int color) {
        if (textPaint == null) {
            textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setTextAlign(Paint.Align.CENTER);
        }

        textPaint.setTextSize(sizePx);
        textPaint.setColor(color);
        return textPaint;
    }

    private void initButtonInfo() {
        float innerWidth = width - (getPaddingLeft() + getPaddingRight() + buttonSideMarginPx * 2);
        float innerHeight = height - (getPaddingTop() + getPaddingBottom() + buttonSideMarginPx * 2);
        float buttonRadius = Math.min(innerWidth / 2, innerHeight / 2);
        PointF buttonCenter = new PointF(getPaddingLeft() + width / 2, getPaddingTop() + height / 2);
        buttonOval = new RectF(buttonCenter.x - buttonRadius, buttonCenter.y - buttonRadius
                , buttonCenter.x + buttonRadius, buttonCenter.y + buttonRadius);
    }

    public int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @SuppressLint("ViewConstructor")
    private static class MaskView extends View {
        private AllAngleExpandableButton allAngleExpandableButton;
        private Rect rawButtonRect;
        private RectF initialSubButtonRectF;
        private RectF touchRectF;
        private ValueAnimator touchRippleAnimator;
        private Paint paint;
        private Map<ButtonData, ExpandDesCoordinate> expandDesCoordinateMap;
        private int rippleState;
        private float rippleRadius;
        private int clickIndex = 0;
        private Matrix[] matrixArray;

        private static final int IDLE = 0;
        private static final int RIPPLING = 1;
        private static final int RIPPLED = 2;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({IDLE, RIPPLING, RIPPLED})
        private @interface RippleState {

        }

        private static class ExpandDesCoordinate {
            float moveX;
            float moveY;

            public ExpandDesCoordinate(float moveX, float moveY) {
                this.moveX = moveX;
                this.moveY = moveY;
            }
        }

        public MaskView(Context context, AllAngleExpandableButton button) {
            super(context);
            allAngleExpandableButton = button;

            paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);

            matrixArray = new Matrix[allAngleExpandableButton.buttonDatas.size()];
            for (int i = 0; i < matrixArray.length; i++) {
                matrixArray[i] = new Matrix();
            }

            rawButtonRect = new Rect();
            initialSubButtonRectF = new RectF();
            touchRectF = new RectF();

            expandDesCoordinateMap = new HashMap<>(allAngleExpandableButton.buttonDatas.size());
            setBackgroundColor(allAngleExpandableButton.maskBackgroundColor);

            touchRippleAnimator = ValueAnimator.ofFloat(0, 1);
            touchRippleAnimator.setDuration((long) ((float) allAngleExpandableButton.animDuration * 0.9f));
            touchRippleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float animateProgress = (float) valueAnimator.getAnimatedValue();
                    allAngleExpandableButton.rippleInfo.rippleRadius = rippleRadius * animateProgress;
                }
            });
            touchRippleAnimator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    allAngleExpandableButton.rippleInfo.rippleRadius = 0;
                    setRippleState(RIPPLED);
                }
            });
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
                    clickIndex = getTouchedButtonIndex(event.getX(), event.getY());
                    onButtonPressed();
                    break;
            }
            return super.onTouchEvent(event);
        }

        private void reset() {
            setRippleState(IDLE);
        }

        private void setRippleState(@RippleState int state) {
            rippleState = state;
        }

        @RippleState
        private int getRippleState() {
            return rippleState;
        }

        public void onClickMainButton() {
            clickIndex = 0;
        }

        private void onButtonPressed() {
            if (allAngleExpandableButton.buttonClickListener != null) {
                if (clickIndex > 0) {
                    allAngleExpandableButton.buttonClickListener.onButtonClicked(clickIndex);
                }
            }
            if (allAngleExpandableButton.isSelectionMode) {
                if (clickIndex > 0) {
                    ButtonData buttonData = allAngleExpandableButton.buttonDatas.get(clickIndex);
                    ButtonData mainButton = allAngleExpandableButton.buttonDatas.get(0);
                    if (buttonData.isIconButton()) {
                        mainButton.setIsIconButton(true);
                        mainButton.setIconResId(buttonData.getIconResId());
                    } else {
                        mainButton.setIsIconButton(false);
                        mainButton.setText(buttonData.getText());
                    }
                    mainButton.setBackgroundColorId(buttonData.getBackgroundColorId());
                }
            }
            allAngleExpandableButton.collapse();
        }

        private int getTouchedButtonIndex(float x, float y) {
            for (int i = 0; i < allAngleExpandableButton.buttonDatas.size(); i++) {
                ButtonData buttonData = allAngleExpandableButton.buttonDatas.get(i);
                ExpandDesCoordinate coordinate = expandDesCoordinateMap.get(buttonData);
                if (i == 0) {
                    RectF rectF = allAngleExpandableButton.buttonRects.get(buttonData);
                    touchRectF.set(rectF);
                } else {
                    touchRectF.set(initialSubButtonRectF);
                    touchRectF.offset(coordinate.moveX, -coordinate.moveY);
                }

                if (x >= touchRectF.left && x <= touchRectF.right && y >= touchRectF.top && y <= touchRectF.bottom) {
                    if (i > 0) {
                        touchRectF.offset(-coordinate.moveX, coordinate.moveY);
                    }
                    return i;
                }
            }
            return -1;
        }

        private void initButtonRect() {
            allAngleExpandableButton.getGlobalVisibleRect(rawButtonRect);
            for (int i = 0; i < allAngleExpandableButton.buttonDatas.size(); i++) {
                ButtonData buttonData = allAngleExpandableButton.buttonDatas.get(i);
                RectF rectF = allAngleExpandableButton.buttonRects.get(buttonData);
                if (i == 0) {
                    rectF.left = rawButtonRect.left + allAngleExpandableButton.buttonSideMarginPx;
                    rectF.right = rawButtonRect.right - allAngleExpandableButton.buttonSideMarginPx;
                    rectF.top = rawButtonRect.top + allAngleExpandableButton.buttonSideMarginPx;
                    rectF.bottom = rawButtonRect.bottom - allAngleExpandableButton.buttonSideMarginPx;
                } else {
                    float leftTmp = rectF.left;
                    float topTmp = rectF.top;
                    int buttonRadius = allAngleExpandableButton.subButtonSizePx / 2;
                    rectF.left = leftTmp + rawButtonRect.centerX() - allAngleExpandableButton.buttonSideMarginPx - buttonRadius;
                    rectF.right = leftTmp + rawButtonRect.centerX() - allAngleExpandableButton.buttonSideMarginPx + buttonRadius;
                    rectF.top = topTmp + rawButtonRect.centerY() - allAngleExpandableButton.buttonSideMarginPx - buttonRadius;
                    rectF.bottom = topTmp + rawButtonRect.centerY() - allAngleExpandableButton.buttonSideMarginPx + buttonRadius;
                    initialSubButtonRectF.set(rectF);
                    touchRectF.set(rectF);
                }
            }
        }

        private void updateButtons2() {
            List<ButtonData> buttonDatas = allAngleExpandableButton.buttonDatas;
            int mainButtonRadius = allAngleExpandableButton.mainButtonSizePx / 2;
            int subButtonRadius = allAngleExpandableButton.subButtonSizePx / 2;
            for (int i = 1; i < buttonDatas.size(); i++) {
                Matrix matrix = matrixArray[i];
                ButtonData buttonData = buttonDatas.get(i);
                matrix.reset();
                if (allAngleExpandableButton.expanded) {
                    ExpandDesCoordinate coordinate = expandDesCoordinateMap.get(buttonData);
                    float dx = allAngleExpandableButton.animateProgress * (coordinate.moveX);
                    float dy = allAngleExpandableButton.animateProgress * (-coordinate.moveY);
                    matrix.postTranslate(dx, dy);
                } else {
                    int radius = mainButtonRadius + subButtonRadius + allAngleExpandableButton.buttonGapPx;
                    float moveX;
                    float moveY;
                    ExpandDesCoordinate coordinate = expandDesCoordinateMap.get(buttonData);
                    if (coordinate == null) {
                        moveX = allAngleExpandableButton.angleCalculator.getMoveX(radius, i);
                        moveY = allAngleExpandableButton.angleCalculator.getMoveY(radius, i);
                        coordinate = new ExpandDesCoordinate(moveX, moveY);
                        expandDesCoordinateMap.put(buttonData, coordinate);
                    } else {
                        moveX = coordinate.moveX;
                        moveY = coordinate.moveY;
                    }
                    float dx = allAngleExpandableButton.animateProgress * (moveX);
                    float dy = allAngleExpandableButton.animateProgress * (-moveY);
                    matrix.postTranslate(dx, dy);
                }
            }
        }

        private void updateButtons() {
            List<ButtonData> buttonDatas = allAngleExpandableButton.buttonDatas;
            int radiusMain = allAngleExpandableButton.mainButtonSizePx / 2;
            for (int i = 1; i < buttonDatas.size(); i++) {
                ButtonData buttonData = buttonDatas.get(i);
                RectF rectF = allAngleExpandableButton.buttonRects.get(buttonData);
                if (allAngleExpandableButton.expanded) {
                    rectF.left = rectF.left - (rectF.left - initialSubButtonRectF.left) * (1 - allAngleExpandableButton.animateProgress);
                    rectF.right = rectF.right - (rectF.right - initialSubButtonRectF.right) * (1 - allAngleExpandableButton.animateProgress);
                    rectF.top = rectF.top - (rectF.top - initialSubButtonRectF.top) * (1 - allAngleExpandableButton.animateProgress);
                    rectF.bottom = rectF.bottom - (rectF.bottom - initialSubButtonRectF.bottom) * (1 - allAngleExpandableButton.animateProgress);
                } else {
                    int radiusCurrent = allAngleExpandableButton.subButtonSizePx / 2;
                    int radius = radiusMain + radiusCurrent + allAngleExpandableButton.buttonGapPx;
                    float moveX;
                    float moveY;
                    ExpandDesCoordinate coordinate = expandDesCoordinateMap.get(buttonData);
                    if (coordinate == null) {
                        moveX = allAngleExpandableButton.angleCalculator.getMoveX(radius, i);
                        moveY = allAngleExpandableButton.angleCalculator.getMoveY(radius, i);
                        coordinate = new ExpandDesCoordinate(moveX, moveY);
                        expandDesCoordinateMap.put(buttonData, coordinate);
                    } else {
                        moveX = coordinate.moveX;
                        moveY = coordinate.moveY;
                    }
                    rectF.left = initialSubButtonRectF.left + moveX * allAngleExpandableButton.animateProgress;
                    rectF.right = initialSubButtonRectF.right + moveX * allAngleExpandableButton.animateProgress;
                    rectF.top = initialSubButtonRectF.top - moveY * allAngleExpandableButton.animateProgress;
                    rectF.bottom = initialSubButtonRectF.bottom - moveY * allAngleExpandableButton.animateProgress;
                }
            }
        }

        private void drawButtons(Canvas canvas, Paint paint) {
            for (int i = allAngleExpandableButton.buttonDatas.size() - 1; i >= 0; i--) {
                canvas.save();
                canvas.concat(matrixArray[i]);
                ButtonData buttonData = allAngleExpandableButton.buttonDatas.get(i);
                allAngleExpandableButton.drawButton(canvas, paint, buttonData);
                if (i == 0 && clickIndex == 0) {
                    performRipple();
                }
                canvas.restore();
            }
        }

        private void performRipple() {
            if (getRippleState() == IDLE) {
                ripple(0, allAngleExpandableButton.pressX, allAngleExpandableButton.pressY);
                setRippleState(RIPPLING);
            }
        }

        private void ripple(int index, float pressX, float pressY) {
            if (index < 0 || !allAngleExpandableButton.rippleEffect) {
                return;
            }
            allAngleExpandableButton.resetRippleInfo();
            ButtonData buttonData = allAngleExpandableButton.buttonDatas.get(index);
            RectF rectF = allAngleExpandableButton.buttonRects.get(buttonData);
            float centerX = rectF.centerX();
            float centerY = rectF.centerY();
            float radius = rectF.centerX() - rectF.left;
            float distanceX = pressX - centerX;
            float distanceY = pressY - centerY;
            float pressToCenterDistance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);
            if (pressToCenterDistance > radius) {
                //press out of the button circle
                return;
            }
            allAngleExpandableButton.rippleInfo.pressX = pressX;
            allAngleExpandableButton.rippleInfo.pressY = pressY;
            allAngleExpandableButton.rippleInfo.buttonIndex = index;
            allAngleExpandableButton.rippleInfo.rippleRadius = radius + pressToCenterDistance;
            allAngleExpandableButton.rippleInfo.rippleColor = getRippleColor(allAngleExpandableButton.rippleColor == Integer.MIN_VALUE ?
                    buttonData.getBackgroundColorId() : allAngleExpandableButton.rippleColor);

            rippleRadius = allAngleExpandableButton.rippleInfo.rippleRadius;
            startRippleAnimator();
        }

        private int getRippleColor(int color) {
            if (allAngleExpandableButton.rippleColor != Integer.MIN_VALUE) {
                return allAngleExpandableButton.rippleColor;
            }
            if (allAngleExpandableButton.rippleInfo.rippleColor != Integer.MIN_VALUE) {
                return allAngleExpandableButton.rippleInfo.rippleColor;
            }
            int red = Color.red(color);
            int blue = Color.blue(color);
            int green = Color.green(color);
            if (red == 0 && blue == 0 && green == 0) {
                red = 55;
                blue = 55;
                green = 55;
            }
            if (red > 128) {
                red = red >> 1;
            } else {
                red = red << 1;
            }
            if (blue > 128) {
                blue = blue >> 1;
            } else {
                blue = blue << 1;
            }
            if (green > 128) {
                green = green >> 1;
            } else {
                green = green << 1;
            }

            return Color.rgb(red, blue, green);
        }

        private void startRippleAnimator() {
            if (touchRippleAnimator.isRunning()) {
                touchRippleAnimator.cancel();
            }
            touchRippleAnimator.start();
        }
    }

    private static class SimpleAnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }
}
