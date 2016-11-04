package com.fangxu.allangleexpandablebutton;

import android.animation.Animator;
import android.animation.ObjectAnimator;
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
import android.view.ViewTreeObserver;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

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
    private ButtonEventListener buttonEventListener;

    private static final int BUTTON_SHADOW_COLOR = 0xff000000;
    private static final int BUTTON_SHADOW_ALPHA = 32;

    //default attribute values
    private static final int DEFAULT_EXPAND_ANIMATE_DURATION = 225;
    private static final int DEFAULT_ROTATE_ANIMATE_DURATION = 300;
    private static final int DEFAULT_BUTTON_GAP_DP = 25;
    private static final int DEFAULT_BUTTON_MAIN_SIZE_DP = 60;
    private static final int DEFAULT_BUTTON_SUB_SIZE_DP = 60;
    private static final int DEFAULT_BUTTON_ELEVATION_DP = 4;
    private static final int DEFAULT_BUTTON_TEXT_SIZE_SP = 20;
    private static final int DEFAULT_START_ANGLE = 90;
    private static final int DEFAULT_END_ANGLE = 90;
    private static final int DEFAULT_BUTTON_TEXT_COLOR = Color.BLACK;
    private static final int DEFAULT_MASK_BACKGROUND_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_BLUR_RADIUS = 10;

    private boolean expanded = false;

    //attributes can be set in xml
    private float startAngle;
    private float endAngle;
    private int buttonGapPx;
    private int mainButtonRotateDegree;
    private int rotateAnimDuration;
    private int mainButtonSizePx;
    private int subButtonSizePx;
    private int mainButtonTextSize;
    private int subButtonTextSize;
    private int mainButtonTextColor;
    private int subButtonTextColor;
    private int expandAnimDuration;
    private int maskBackgroundColor;
    private int buttonElevationPx;
    private boolean isSelectionMode;
    private boolean rippleEffect;
    private int rippleColor = Integer.MIN_VALUE;
    private boolean blurBackground;
    private float blurRadius;

    private Bitmap mainShadowBitmap = null;
    private Bitmap subShadowBitmap = null;
    Matrix shadowMatrix;

    private int buttonSideMarginPx;

    private Paint paint;
    private Paint textPaint;

    private AngleCalculator angleCalculator;
    private boolean animating = false;
    private boolean maskAttached = false;
    private float expandProgress;
    private float rotateProgress;
    private ValueAnimator expandValueAnimator;
    private ValueAnimator collapseValueAnimator;
    private ValueAnimator rotateValueAnimator;
    private Interpolator overshootInterpolator;
    private Interpolator anticipateInterpolator;
    private Path ripplePath;
    private RippleInfo rippleInfo;
    private MaskView maskView;
    private Blur blur;
    private ImageView blurImageView;
    private ObjectAnimator blurAnimator;
    private Animator.AnimatorListener blurListener;
    private PointF pressPointF;
    private Rect rawButtonRect;//act as the param of getGlobalVisibleRect(Rect rect) method
    private RectF rawButtonRectF;
    private int pressTmpColor;
    private boolean pressInButton;

    private QuickClickChecker checker;
    private int checkThreshold;

    //store ripple effect params
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

        expandAnimDuration = ta.getInteger(R.styleable.AllAngleExpandableButton_aebAnimDurationMillis, DEFAULT_EXPAND_ANIMATE_DURATION);
        rotateAnimDuration = ta.getInteger(R.styleable.AllAngleExpandableButton_aebMainButtonRotateAnimDurationMillis, DEFAULT_ROTATE_ANIMATE_DURATION);
        maskBackgroundColor = ta.getInteger(R.styleable.AllAngleExpandableButton_aebMaskBackgroundColor, DEFAULT_MASK_BACKGROUND_COLOR);
        mainButtonRotateDegree = ta.getInteger(R.styleable.AllAngleExpandableButton_aebMainButtonRotateDegree, mainButtonRotateDegree);
        isSelectionMode = ta.getBoolean(R.styleable.AllAngleExpandableButton_aebIsSelectionMode, false);
        rippleEffect = ta.getBoolean(R.styleable.AllAngleExpandableButton_aebRippleEffect, true);
        rippleColor = ta.getColor(R.styleable.AllAngleExpandableButton_aebRippleColor, rippleColor);
        blurBackground = ta.getBoolean(R.styleable.AllAngleExpandableButton_aebBlurBackground, false);
        blurRadius = ta.getFloat(R.styleable.AllAngleExpandableButton_aebBlurRadius, DEFAULT_BLUR_RADIUS);
        ta.recycle();

        if (blurBackground) {
            blur = new Blur();
            blurImageView = new ImageView(getContext());
        }

        if (mainButtonRotateDegree != 0) {
            checkThreshold = expandAnimDuration > rotateAnimDuration ? expandAnimDuration : rotateAnimDuration;
        } else {
            checkThreshold = expandAnimDuration;
        }
        checker = new QuickClickChecker(checkThreshold);

        rippleInfo = new RippleInfo();
        pressPointF = new PointF();
        rawButtonRect = new Rect();
        rawButtonRectF = new RectF();
        shadowMatrix = new Matrix();

        initViewTreeObserver();
        initAnimators();
    }

    private void initViewTreeObserver() {
        ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getGlobalVisibleRect(rawButtonRect);
                rawButtonRectF.set(rawButtonRect.left, rawButtonRect.top, rawButtonRect.right, rawButtonRect.bottom);
            }
        });
    }

    private void initAnimators() {
        overshootInterpolator = new OvershootInterpolator();
        anticipateInterpolator = new AnticipateInterpolator();

        expandValueAnimator = ValueAnimator.ofFloat(0, 1);
        expandValueAnimator.setDuration(expandAnimDuration);
        expandValueAnimator.setInterpolator(overshootInterpolator);
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
        collapseValueAnimator.setDuration(expandAnimDuration);
        collapseValueAnimator.setInterpolator(anticipateInterpolator);
        collapseValueAnimator.addUpdateListener(this);
        collapseValueAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                animating = true;
                hideBlur();
                maskView.reset();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animating = false;
                expanded = false;
                if (rotateValueAnimator == null) {
                    detachMask();
                } else {
                    if (expandAnimDuration >= rotateAnimDuration) {
                        detachMask();
                    }//else call detachMask() until rotateValueAnimator ended
                }
            }
        });

        if (mainButtonRotateDegree == 0) {
            return;
        }

        rotateValueAnimator = ValueAnimator.ofFloat(0, 1);
        rotateValueAnimator.setDuration(rotateAnimDuration);
        rotateValueAnimator.addUpdateListener(this);
        rotateValueAnimator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (!expanded && expandAnimDuration < rotateAnimDuration) {
                    //call detachMask() when rotateValueAnimator ended
                    detachMask();
                }
            }
        });
    }

    public void setButtonEventListener(ButtonEventListener listener) {
        buttonEventListener = listener;
    }

    public void setExpandAnimatorInterpolator(Interpolator interpolator) {
        if (interpolator != null) {
            expandValueAnimator.setInterpolator(interpolator);
        }
    }

    public void setCollapseAnimatorInterpolator(Interpolator interpolator) {
        if (interpolator != null) {
            collapseValueAnimator.setInterpolator(interpolator);
        }
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

    public List<ButtonData> getButtonDatas() {
        return this.buttonDatas;
    }

    private ButtonData getMainButtonData() {
        return this.buttonDatas.get(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //the button size is only decided by mainButtonSizePx and buttonSideMarginPx that you configure in xml
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
        initButtonInfo();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        pressPointF.set(event.getRawX(), event.getRawY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (checker.isQuick()) {
                    return false;
                }
                pressInButton = true;
                boolean executeActionUp = !animating && buttonDatas != null && !buttonDatas.isEmpty();
                if (executeActionUp) {
                    updatePressState(0, true);
                }
                return executeActionUp;
            case MotionEvent.ACTION_MOVE:
                updatePressPosition(0, rawButtonRectF);
                break;
            case MotionEvent.ACTION_UP:
                if (!isPointInRectF(pressPointF, rawButtonRectF)) {
                    return true;
                }
                updatePressState(0, false);
                expand();
                return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * used for update press effect when finger move
     *
     * @param rectF the rectF of the button to present button position
     */
    private void updatePressPosition(int buttonIndex, RectF rectF) {
        if (buttonIndex < 0) {
            return;
        }
        if (isPointInRectF(pressPointF, rectF)) {
            if (!pressInButton) {
                updatePressState(buttonIndex, true);
                pressInButton = true;
            }
        } else {
            if (pressInButton) {
                updatePressState(buttonIndex, false);
                pressInButton = false;
            }
        }
    }

    private boolean isPointInRectF(PointF pointF, RectF rectF) {
        return pointF.x >= rectF.left && pointF.x <= rectF.right && pointF.y >= rectF.top && pointF.y <= rectF.bottom;
    }

    private void updatePressState(int buttonIndex, boolean down) {
        if (buttonIndex < 0) {
            return;
        }
        ButtonData buttonData = buttonDatas.get(buttonIndex);
        if (down) {
            pressTmpColor = buttonData.getBackgroundColor();
            buttonData.setBackgroundColor(getPressedColor(pressTmpColor));
        } else {
            buttonData.setBackgroundColor(pressTmpColor);
        }
        if (expanded) {
            maskView.invalidate();
        } else {
            invalidate();
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        if (valueAnimator == expandValueAnimator || valueAnimator == collapseValueAnimator) {
            expandProgress = (float) valueAnimator.getAnimatedValue();
        }
        if (valueAnimator == rotateValueAnimator) {
            rotateProgress = (float) valueAnimator.getAnimatedValue();
        }
        if (maskAttached) {
            maskView.updateButtons();
            maskView.invalidate();
        }
    }

    private void expand() {
        if (expandValueAnimator.isRunning()) {
            expandValueAnimator.cancel();
        }
        expandValueAnimator.start();
        startRotateAnimator(true);
        if (buttonEventListener != null) {
            buttonEventListener.onExpand();
        }
    }

    private void collapse() {
        if (collapseValueAnimator.isRunning()) {
            collapseValueAnimator.cancel();
        }
        collapseValueAnimator.start();
        startRotateAnimator(false);
        if (buttonEventListener != null) {
            buttonEventListener.onCollapse();
        }
    }

    private void startRotateAnimator(boolean expand) {
        if (rotateValueAnimator != null) {
            if (rotateValueAnimator.isRunning()) {
                rotateValueAnimator.cancel();
            }
            if (expand) {
                rotateValueAnimator.setInterpolator(overshootInterpolator);
                rotateValueAnimator.setFloatValues(0, 1);
            } else {
                rotateValueAnimator.setInterpolator(anticipateInterpolator);
                rotateValueAnimator.setFloatValues(1, 0);
            }
            rotateValueAnimator.start();
        }
    }

    private void attachMask() {
        if (maskView == null) {
            maskView = new MaskView(getContext(), this);
        }

        if (!maskAttached && !showBlur()) {
            ViewGroup root = (ViewGroup) getRootView();
            root.addView(maskView);
            maskAttached = true;
            maskView.reset();
            maskView.initButtonRect();
            maskView.onClickMainButton();
        }
    }

    private boolean showBlur() {
        if (!blurBackground) {
            return false;
        }

        //set invisible to avoid be blurred that resulting in show the blurred button edge when expanded,
        //must be called before do blur
        setVisibility(INVISIBLE);

        final ViewGroup root = (ViewGroup) getRootView();
        root.setDrawingCacheEnabled(true);
        Bitmap bitmap = root.getDrawingCache();
        checkBlurRadius();

        blur.setParams(new Blur.Callback() {
            @Override
            public void onBlurred(Bitmap blurredBitmap) {
                blurImageView.setImageBitmap(blurredBitmap);
                root.setDrawingCacheEnabled(false);
                root.addView(blurImageView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                blurAnimator = ObjectAnimator.ofFloat(blurImageView, "alpha", 0.0f, 1.0f).setDuration(expandAnimDuration);
                if (blurListener != null) {
                    blurAnimator.removeListener(blurListener);
                }
                blurAnimator.start();

                root.addView(maskView);
                maskAttached = true;
                maskView.reset();
                maskView.initButtonRect();
                maskView.onClickMainButton();
            }
        }, getContext(), bitmap, blurRadius);
        blur.execute();

        return true;
    }

    private void checkBlurRadius() {
        if (blurRadius <= 0 || blurRadius > 25) {
            blurRadius = DEFAULT_BLUR_RADIUS;
        }
    }

    private void hideBlur() {
        if (!blurBackground) {
            return;
        }

        setVisibility(VISIBLE);

        final ViewGroup root = (ViewGroup) getRootView();
        blurAnimator.setFloatValues(1.0f, 0.0f);
        if (blurListener == null) {
            blurListener = new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    root.removeView(blurImageView);
                }
            };
        }
        blurAnimator.addListener(blurListener);
        blurAnimator.start();
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
        if (buttonDatas == null || buttonDatas.isEmpty()) {
            return;
        }

        ButtonData buttonData = getMainButtonData();
        drawButton(canvas, paint, buttonData);
    }

    /**
     * this method is called by both {@link AllAngleExpandableButton} and {@link AllAngleExpandableButton.MaskView} at draw process
     */
    private void drawButton(Canvas canvas, Paint paint, ButtonData buttonData) {
        drawShadow(canvas, paint, buttonData);
        drawContent(canvas, paint, buttonData);
        drawRipple(canvas, paint, buttonData);
    }

    private void drawShadow(Canvas canvas, Paint paint, ButtonData buttonData) {
        if (buttonElevationPx <= 0) {
            return;
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
            shadowMatrix.postScale(expandProgress, expandProgress, bitmap.getWidth() / 2, bitmap.getHeight() / 2 + shadowOffset);
        }
        shadowMatrix.postTranslate(left, top);
        if (buttonData.isMainButton()) {
            //shadow did not need to perform rotate as button,so rotate reverse
            shadowMatrix.postRotate(-mainButtonRotateDegree * rotateProgress, rectF.centerX(), rectF.centerY());
        }
        paint.setAlpha(255);
        canvas.drawBitmap(bitmap, shadowMatrix, paint);
    }

    private void drawContent(Canvas canvas, Paint paint, ButtonData buttonData) {
        paint.setAlpha(255);
        paint.setColor(buttonData.getBackgroundColor());
        RectF rectF = buttonRects.get(buttonData);
        canvas.drawOval(rectF, paint);
        if (buttonData.isIconButton()) {
            Drawable drawable = buttonData.getIcon();
            if (drawable == null) {
                throw new IllegalArgumentException("iconData is true, drawable cannot be null");
            }
            int left = (int) rectF.left + dp2px(getContext(), buttonData.getIconPaddingDp());
            int right = (int) rectF.right - dp2px(getContext(), buttonData.getIconPaddingDp());
            int top = (int) rectF.top + dp2px(getContext(), buttonData.getIconPaddingDp());
            int bottom = (int) rectF.bottom - dp2px(getContext(), buttonData.getIconPaddingDp());
            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
        } else {
            if (buttonData.getTexts() == null) {
                throw new IllegalArgumentException("iconData is false, text cannot be null");
            }
            String[] texts = buttonData.getTexts();
            int sizePx = buttonData.isMainButton() ? mainButtonTextSize : subButtonTextSize;
            int textColor = buttonData.isMainButton() ? mainButtonTextColor : subButtonTextColor;
            textPaint = getTextPaint(sizePx, textColor);
            drawTexts(texts, canvas, rectF.centerX(), rectF.centerY());
        }
    }

    /**
     * draw texts in rows
     */
    private void drawTexts(String[] strings, Canvas canvas, float x, float y) {
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        int length = strings.length;
        float total = (length - 1) * (-top + bottom) + (-fontMetrics.ascent + fontMetrics.descent);
        float offset = total / 2 - bottom;
        for (int i = 0; i < length; i++) {
            float yAxis = -(length - i - 1) * (-top + bottom) + offset;
            canvas.drawText(strings[i], x, y + yAxis, textPaint);
        }
    }

    private void drawRipple(Canvas canvas, Paint paint, ButtonData buttonData) {
        int pressIndex = buttonDatas.indexOf(buttonData);
        if (!rippleEffect || pressIndex == -1 || pressIndex != rippleInfo.buttonIndex) {
            return;
        }

        paint.setColor(rippleInfo.rippleColor);
        paint.setAlpha(128);
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
        ViewGroup root = (ViewGroup) getRootView();
        getGlobalVisibleRect(rawButtonRect);
        rawButtonRectF.set(rawButtonRect.left, rawButtonRect.top, rawButtonRect.right, rawButtonRect.bottom);
    }

    private int getLighterColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 1.1f;
        return Color.HSVToColor(hsv);
    }

    private int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.9f;
        return Color.HSVToColor(hsv);
    }

    private int getPressedColor(int color) {
        return getDarkerColor(color);
    }

    public int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * when expand,draw MaskView to overlap the fullscreen,the background is transparent default
     */
    @SuppressLint("ViewConstructor")
    private static class MaskView extends View {
        private AllAngleExpandableButton allAngleExpandableButton;
        private RectF initialSubButtonRectF;//all of the sub button's initial rectF
        private RectF touchRectF;//set when one of buttons are touched
        private ValueAnimator touchRippleAnimator;
        private Paint paint;
        private Map<ButtonData, ExpandMoveCoordinate> expandDesCoordinateMap;
        private int rippleState;
        private float rippleRadius;
        private int clickIndex = 0;
        private Matrix[] matrixArray;//each button has a Matrix to perform expand/collapse animation
        private QuickClickChecker checker;

        private static final int IDLE = 0;
        private static final int RIPPLING = 1;
        private static final int RIPPLED = 2;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({IDLE, RIPPLING, RIPPLED})
        private @interface RippleState {

        }

        private static class ExpandMoveCoordinate {
            float moveX;
            float moveY;

            /**
             * the members are set by getMoveX() and getMoveY() of {@link AngleCalculator}
             */
            public ExpandMoveCoordinate(float moveX, float moveY) {
                this.moveX = moveX;
                this.moveY = moveY;
            }
        }

        public MaskView(Context context, AllAngleExpandableButton button) {
            super(context);
            allAngleExpandableButton = button;

            checker = new QuickClickChecker(allAngleExpandableButton.checkThreshold);

            paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);

            matrixArray = new Matrix[allAngleExpandableButton.buttonDatas.size()];
            for (int i = 0; i < matrixArray.length; i++) {
                matrixArray[i] = new Matrix();
            }

            initialSubButtonRectF = new RectF();
            touchRectF = new RectF();

            expandDesCoordinateMap = new HashMap<>(allAngleExpandableButton.buttonDatas.size());
            setBackgroundColor(allAngleExpandableButton.maskBackgroundColor);

            touchRippleAnimator = ValueAnimator.ofFloat(0, 1);
            touchRippleAnimator.setDuration((long) ((float) allAngleExpandableButton.expandAnimDuration * 0.9f));
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
            allAngleExpandableButton.pressPointF.set(event.getX(), event.getY());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (checker.isQuick()) {
                        return false;
                    }
                    clickIndex = getTouchedButtonIndex();
                    if (allAngleExpandableButton.expanded) {
                        allAngleExpandableButton.updatePressState(clickIndex, true);
                    }
                    allAngleExpandableButton.pressInButton = true;
                    return allAngleExpandableButton.expanded;
                case MotionEvent.ACTION_MOVE:
                    allAngleExpandableButton.updatePressPosition(clickIndex, touchRectF);
                    break;
                case MotionEvent.ACTION_UP:
                    if (!allAngleExpandableButton.isPointInRectF(allAngleExpandableButton.pressPointF, touchRectF)) {
                        if (clickIndex < 0) {
                            allAngleExpandableButton.collapse();
                        }
                        return true;
                    }
                    allAngleExpandableButton.updatePressState(clickIndex, false);
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
            if (allAngleExpandableButton.buttonEventListener != null) {
                if (clickIndex > 0) {
                    allAngleExpandableButton.buttonEventListener.onButtonClicked(clickIndex);
                }
            }
            if (allAngleExpandableButton.isSelectionMode) {
                if (clickIndex > 0) {
                    ButtonData buttonData = allAngleExpandableButton.buttonDatas.get(clickIndex);
                    ButtonData mainButton = allAngleExpandableButton.getMainButtonData();
                    if (buttonData.isIconButton()) {
                        mainButton.setIsIconButton(true);
                        mainButton.setIcon(buttonData.getIcon());
                    } else {
                        mainButton.setIsIconButton(false);
                        mainButton.setTexts(buttonData.getTexts());
                    }
                    mainButton.setBackgroundColor(buttonData.getBackgroundColor());
                }
            }
            allAngleExpandableButton.collapse();
        }

        private int getTouchedButtonIndex() {
            for (int i = 0; i < allAngleExpandableButton.buttonDatas.size(); i++) {
                ButtonData buttonData = allAngleExpandableButton.buttonDatas.get(i);
                ExpandMoveCoordinate coordinate = expandDesCoordinateMap.get(buttonData);
                if (i == 0) {
                    RectF rectF = allAngleExpandableButton.buttonRects.get(buttonData);
                    touchRectF.set(rectF);
                } else {
                    touchRectF.set(initialSubButtonRectF);
                    touchRectF.offset(coordinate.moveX, -coordinate.moveY);
                }

                if (allAngleExpandableButton.isPointInRectF(allAngleExpandableButton.pressPointF, touchRectF)) {
                    return i;
                }
            }
            return -1;
        }

        private void initButtonRect() {
            for (int i = 0; i < allAngleExpandableButton.buttonDatas.size(); i++) {
                ButtonData buttonData = allAngleExpandableButton.buttonDatas.get(i);
                RectF rectF = allAngleExpandableButton.buttonRects.get(buttonData);
                if (i == 0) {
                    rectF.left = allAngleExpandableButton.rawButtonRectF.left + allAngleExpandableButton.buttonSideMarginPx;
                    rectF.right = allAngleExpandableButton.rawButtonRectF.right - allAngleExpandableButton.buttonSideMarginPx;
                    rectF.top = allAngleExpandableButton.rawButtonRectF.top + allAngleExpandableButton.buttonSideMarginPx;
                    rectF.bottom = allAngleExpandableButton.rawButtonRectF.bottom - allAngleExpandableButton.buttonSideMarginPx;
                } else {
                    float leftTmp = rectF.left;
                    float topTmp = rectF.top;
                    int buttonRadius = allAngleExpandableButton.subButtonSizePx / 2;
                    rectF.left = leftTmp + allAngleExpandableButton.rawButtonRectF.centerX() - allAngleExpandableButton.buttonSideMarginPx - buttonRadius;
                    rectF.right = leftTmp + allAngleExpandableButton.rawButtonRectF.centerX() - allAngleExpandableButton.buttonSideMarginPx + buttonRadius;
                    rectF.top = topTmp + allAngleExpandableButton.rawButtonRectF.centerY() - allAngleExpandableButton.buttonSideMarginPx - buttonRadius;
                    rectF.bottom = topTmp + allAngleExpandableButton.rawButtonRectF.centerY() - allAngleExpandableButton.buttonSideMarginPx + buttonRadius;
                    initialSubButtonRectF.set(rectF);
                    touchRectF.set(rectF);
                }
            }
        }

        /**
         * called before draw an expand/collapse frame
         */
        private void updateButtons() {
            List<ButtonData> buttonDatas = allAngleExpandableButton.buttonDatas;
            int mainButtonRadius = allAngleExpandableButton.mainButtonSizePx / 2;
            int subButtonRadius = allAngleExpandableButton.subButtonSizePx / 2;
            Matrix matrix = matrixArray[0];
            matrix.reset();
            matrix.postRotate(allAngleExpandableButton.mainButtonRotateDegree * allAngleExpandableButton.rotateProgress
                    , allAngleExpandableButton.rawButtonRectF.centerX(), allAngleExpandableButton.rawButtonRectF.centerY());
            for (int i = 1; i < buttonDatas.size(); i++) {
                matrix = matrixArray[i];
                ButtonData buttonData = buttonDatas.get(i);
                matrix.reset();
                if (allAngleExpandableButton.expanded) {
                    ExpandMoveCoordinate coordinate = expandDesCoordinateMap.get(buttonData);
                    float dx = allAngleExpandableButton.expandProgress * (coordinate.moveX);
                    float dy = allAngleExpandableButton.expandProgress * (-coordinate.moveY);
                    matrix.postTranslate(dx, dy);
                } else {
                    int radius = mainButtonRadius + subButtonRadius + allAngleExpandableButton.buttonGapPx;
                    float moveX;
                    float moveY;
                    ExpandMoveCoordinate coordinate = expandDesCoordinateMap.get(buttonData);
                    if (coordinate == null) {
                        moveX = allAngleExpandableButton.angleCalculator.getMoveX(radius, i);
                        moveY = allAngleExpandableButton.angleCalculator.getMoveY(radius, i);
                        coordinate = new ExpandMoveCoordinate(moveX, moveY);
                        expandDesCoordinateMap.put(buttonData, coordinate);
                    } else {
                        moveX = coordinate.moveX;
                        moveY = coordinate.moveY;
                    }
                    float dx = allAngleExpandableButton.expandProgress * (moveX);
                    float dy = allAngleExpandableButton.expandProgress * (-moveY);
                    matrix.postTranslate(dx, dy);
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
                ripple(0, allAngleExpandableButton.pressPointF.x, allAngleExpandableButton.pressPointF.y);
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
                    buttonData.getBackgroundColor() : allAngleExpandableButton.rippleColor);

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

            if (color == allAngleExpandableButton.getLighterColor(color)) {
                return allAngleExpandableButton.getDarkerColor(color);
            } else {
                return allAngleExpandableButton.getLighterColor(color);
            }
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
