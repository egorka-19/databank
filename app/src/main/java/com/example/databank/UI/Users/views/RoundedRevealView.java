package com.example.databank.UI.Users.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

public class RoundedRevealView extends View {

    private final Paint paint;
    private final Path path;
    private final RectF rect;
    private float progress; // 0..1
    private float cornerRadiusPx;
    private int startCorner; // 0=BL,1=BR

    public RoundedRevealView(Context context) {
        this(context, null);
    }

    public RoundedRevealView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFFFFFFF);
        path = new Path();
        rect = new RectF();
        cornerRadiusPx = dp(context, 24);
        progress = 0f;
        setClickable(false);
    }

    public void setCornerRadiusDp(float radiusDp) {
        this.cornerRadiusPx = dp(getContext(), radiusDp);
        invalidate();
    }

    public void revealFromBottomLeft() {
        startCorner = 0;
        startAnim();
    }

    public void revealFromBottomRight() {
        startCorner = 1;
        startAnim();
    }

    private void startAnim() {
        setVisibility(VISIBLE);
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(600);
        anim.addUpdateListener(a -> {
            progress = (float) a.getAnimatedValue();
            invalidate();
        });
        anim.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (progress <= 0f) return;

        float w = getWidth();
        float h = getHeight();

        // Target size of the rect as fraction of screen; grows with progress
        float maxWidth = Math.min(w, dp(getContext(), 200));
        float maxHeight = Math.min(h, dp(getContext(), 100));
        float currentW = maxWidth * progress;
        float currentH = maxHeight * progress;

        float left;
        float right;
        float top = h - currentH;
        float bottom = h;

        if (startCorner == 0) { // bottom-left
            left = 0f;
            right = currentW;
        } else { // bottom-right
            right = w;
            left = w - currentW;
        }

        rect.set(left, top, right, bottom);
        path.reset();

        float[] radii = new float[8]; // tl, tr, br, bl (each with [x, y])
        // Round only one upper corner depending on start corner
        if (startCorner == 0) {
            // started from bottom-left -> round top-right only
            radii[2] = cornerRadiusPx; // tr x
            radii[3] = cornerRadiusPx; // tr y
        } else {
            // started from bottom-right -> round top-left only
            radii[0] = cornerRadiusPx; // tl x
            radii[1] = cornerRadiusPx; // tl y
        }

        path.addRoundRect(rect, radii, Path.Direction.CW);
        canvas.drawPath(path, paint);
    }

    private static float dp(Context c, float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, c.getResources().getDisplayMetrics());
    }
}


