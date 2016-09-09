package fangxu.com.library.refactor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dear33 on 2016/9/8.
 */
public class AllAngleExpandableButton extends View {

    private List<Drawable> buttonDrawables;
    private List<String> buttonTexts;
    private boolean iconButton;

    private float startAngle;
    private float endAngle;
    private int buttonColor = 0xffff0000;

    private RectF buttonOval;
    private PointF buttonCenter;
    private int buttonRadius;
    private int width;
    private int height;

    private Paint paint;

    public AllAngleExpandableButton(Context context) {
        this(context, null);
    }

    public AllAngleExpandableButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllAngleExpandableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(buttonColor);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBaseButton(canvas);
    }

    private void drawBaseButton(Canvas canvas) {
        if (buttonOval == null) {
            return;
        }
        canvas.drawOval(buttonOval, paint);
        if (iconButton) {
            if (buttonDrawables != null && !buttonDrawables.isEmpty()) {
                Drawable drawable = buttonDrawables.get(0);
                drawable.draw(canvas);
            }
        } else {
            if (buttonTexts != null && !buttonTexts.isEmpty()) {
                String text = buttonTexts.get(0);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        initButtonInfo();
        invalidate();
    }

    private void initButtonInfo() {
        float innerWidth = width - (getPaddingLeft() + getPaddingRight());
        float innerHeight = height - (getPaddingTop() + getPaddingBottom());
        buttonRadius = (int)(Math.min(innerWidth/2, innerHeight/2));
        buttonCenter = new PointF(getPaddingLeft() + innerWidth / 2, getPaddingTop() + innerHeight / 2);
        buttonOval = new RectF(buttonCenter.x - buttonRadius, buttonCenter.y - buttonRadius
                , buttonCenter.x + buttonRadius, buttonCenter.y + buttonRadius);
    }

    public AllAngleExpandableButton setStartAngle(float startAngle) {
        this.startAngle = startAngle;
        return this;
    }

    public AllAngleExpandableButton setEndAngle(float endAngle) {
        this.endAngle = endAngle;
        return this;
    }

    public AllAngleExpandableButton setButtonDrawableResId(int resId) {
        Drawable drawable = getContext().getResources().getDrawable(resId);
        return setButtonDrawable(drawable);
    }

    public AllAngleExpandableButton setButtonDrawable(Drawable drawable) {
        if (buttonDrawables == null) {
            buttonDrawables = new ArrayList<>();
        }
        iconButton = true;
        buttonDrawables.add(drawable);
        return this;
    }

    public AllAngleExpandableButton setButtonStringId(int resId) {
        String text = getContext().getResources().getString(resId);
        return setButtonString(text);
    }

    public AllAngleExpandableButton setButtonString(String text) {
        if (buttonTexts == null) {
            buttonTexts = new ArrayList<>();
        }
        iconButton = false;
        buttonTexts.add(text);
        return this;
    }
}
