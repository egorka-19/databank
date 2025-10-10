package com.example.databank.UI.Users.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;


import androidx.annotation.Nullable;

import com.example.databank.R;

public class RoundedRevealView extends View {

    private final Paint paint;
    private final Path path;
    private final RectF rect;
    private float progress; // 0..1
    private float fillProgress; // 0..1 (вторая фаза – заливка экрана)
    private float cornerRadiusPx;
    private int startCorner; // 0=BL,1=BR
    private long holdMs = 1500L;
    private Runnable onSequenceComplete;
    private View parentView; // Ссылка на родительский View для скрытия текста

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
        fillProgress = 0f;
        setClickable(false);
    }

    public void setCornerRadiusDp(float radiusDp) {
        this.cornerRadiusPx = dp(getContext(), radiusDp);
        invalidate();
    }

    public void setParentView(View parentView) {
        this.parentView = parentView;
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
        anim.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                // После первой фазы – подождать holdMs и начать заливку
                postDelayed(() -> startFillAnim(), holdMs);
            }
        });
        anim.start();
    }

    public void startSequenceFromBottomLeft(long holdMillis, @Nullable Runnable onComplete) {
        this.holdMs = holdMillis;
        this.onSequenceComplete = onComplete;
        revealFromBottomLeft();
    }

    public void startSequenceFromBottomRight(long holdMillis, @Nullable Runnable onComplete) {
        this.holdMs = holdMillis;
        this.onSequenceComplete = onComplete;
        revealFromBottomRight();
    }

    private void startFillAnim() {
        // Скрываем текст кнопок в начале второй фазы
        if (parentView != null) {
            // Ищем контейнер с кнопками по ID
            View buttonsContainer = parentView.findViewById(R.id.bottom_buttons_container);
            if (buttonsContainer != null) {
                buttonsContainer.setAlpha(0f);
            }
        }
        
        // Добавляем вибрацию во второй фазе
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            // Короткая вибрация в начале второй фазы
            vibrator.vibrate(100);
        }
        
        ValueAnimator fill = ValueAnimator.ofFloat(0f, 1f);
        fill.setDuration(400);
        fill.addUpdateListener(a -> {
            fillProgress = (float) a.getAnimatedValue();
            invalidate();
        });
        fill.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (onSequenceComplete != null) {
                    onSequenceComplete.run();
                }
            }
        });
        fill.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (progress <= 0f && fillProgress <= 0f) return;

        float w = getWidth();
        float h = getHeight();

        // Фаза 1: текущая анимация (локальный прямоугольник внизу экрана)
        if (progress > 0f) {
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

        // Фаза 2: заливка экрана по диагонали от выбранного нижнего угла к противоположному верхнему
        if (fillProgress > 0f) {
            // Базовые размеры финального прямоугольника из фазы 1
            float baseMaxW = Math.min(w, dp(getContext(), 200));
            float baseMaxH = Math.min(h, dp(getContext(), 100));

            // Дополнительный прирост до полного экрана
            float extraW = (w - baseMaxW) * fillProgress;
            float extraH = (h - baseMaxH) * fillProgress;

            float grownW = baseMaxW + extraW;
            float grownH = baseMaxH + extraH;

            float left;
            float right;
            float top;
            float bottom;

            if (startCorner == 0) { // bottom-left -> растём к правому верхнему
                left = 0f;
                right = grownW;
                top = h - grownH;
                bottom = h;
            } else { // bottom-right -> растём к левому верхнему
                left = w - grownW;
                right = w;
                top = h - grownH;
                bottom = h;
            }

            rect.set(left, top, right, bottom);
            path.reset();
            // Скругляем только один верхний угол в зависимости от стартового
            float[] radii2 = new float[8];
            if (startCorner == 0) {
                // bottom-left -> скругляем верхний правый
                radii2[2] = cornerRadiusPx;
                radii2[3] = cornerRadiusPx;
            } else {
                // bottom-right -> скругляем верхний левый
                radii2[0] = cornerRadiusPx;
                radii2[1] = cornerRadiusPx;
            }
            path.addRoundRect(rect, radii2, Path.Direction.CW);
            canvas.drawPath(path, paint);
        }
    }

    private static float dp(Context c, float v) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, c.getResources().getDisplayMetrics());
    }
}


