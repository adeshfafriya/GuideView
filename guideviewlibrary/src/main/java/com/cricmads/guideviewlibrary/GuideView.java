package com.cricmads.guideviewlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GuideView extends FrameLayout {

    private static final int APPEARING_ANIMATION_DURATION = 400;
    private static final int INDICATOR_HEIGHT_IN_DP = 54;
    private static int LINE_HEIGHT_IN_DP = 46;
    private static final int RADIUS_SIZE_TARGET_RECT = 15;
    private static final int STATUS_BAR_HEIGHT_IN_DP = 25;
    private int indicatorHeight;
    private static final int BACKGROUND_COLOR = 0x99000000;

    private final Paint selfPaint = new Paint();
    private final Paint targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Xfermode X_FER_MODE_CLEAR = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    private View target;
    private RectF targetRect;
    private int xMessageView;
    private final Rect selfRect = new Rect();

    private boolean isClickable;
    private int yMessageView = 0;

    private View mMessageView;
    private GuideListener mGuideListener;
    private boolean isDrawCircle = true;
    private boolean isCalculateView = false;
    private int startX, startY, stopX, stopY;
    private Paint linePaint = new Paint();
    private Paint dotPaint = new Paint();
    private DashPathEffect dashPathEffect;
    private GuideView(Context context) {
        super(context);

    }
    public GuideView(View view,
                     String title, String content,
                     String buttonText, boolean isClickable) {
        super(view.getContext());
        this.isClickable = isClickable;
        indicatorHeight = dpToPx(INDICATOR_HEIGHT_IN_DP);
        setWillNotDraw(false);
        dashPathEffect = new DashPathEffect(new float[]{15,10}, 0);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        this.target = view;
        int[] locationTarget = new int[2];
        target.getLocationOnScreen(locationTarget);
        targetRect = new RectF(locationTarget[0] - 10,
                locationTarget[1] - 10,
                locationTarget[0] + 10 + target.getWidth(),
                locationTarget[1] + 10 + target.getHeight());
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        mMessageView = inflater.inflate(R.layout.message_view, null);

        TextView header = mMessageView.findViewById(R.id.header);
        if (!TextUtils.isEmpty(title))
            header.setText(title);
        else header.setVisibility(GONE);

        TextView button = mMessageView.findViewById(R.id.ok_btn);
        if (!TextUtils.isEmpty(buttonText))
            button.setText(buttonText);

        TextView contentView = mMessageView.findViewById(R.id.content);

        if (!TextUtils.isEmpty(content))
            contentView.setText(content);
        else contentView.setVisibility(GONE);
        mMessageView.findViewById(R.id.ok_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        isCalculateView = true;
        addView(mMessageView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int[] locationTarget = new int[2];
                target.getLocationOnScreen(locationTarget);

                targetRect = new RectF(locationTarget[0] - 10,
                        locationTarget[1] - 10,
                        locationTarget[0] + 10 + target.getWidth(),
                        locationTarget[1] + 10 + target.getHeight());

                selfRect.set(getPaddingLeft(),
                        getPaddingTop(),
                        getWidth() - getPaddingRight(),
                        getHeight() - getPaddingBottom());
                if (isCalculateView)
                    setMessageLocation(resolveMessageViewLocation());
                getViewTreeObserver().addOnGlobalLayoutListener(this);
            }
        };
        getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (isClickable){
            if (isViewContains(target, x, y)) dismiss();
            return !isViewContains(target, x, y);
        }else return true;
    }

    private boolean isViewContains(View view, float rx, float ry) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int w = view.getWidth();
        int h = view.getHeight();

        return !(rx < x || rx > x + w || ry < y || ry > y + h);
    }


    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (target != null) {
            selfPaint.setColor(BACKGROUND_COLOR);
            selfPaint.setStyle(Paint.Style.FILL);
            selfPaint.setAntiAlias(true);
            canvas.drawRect(selfRect, selfPaint);
            targetPaint.setXfermode(X_FER_MODE_CLEAR);
            targetPaint.setAntiAlias(true);
            canvas.drawRoundRect(targetRect, RADIUS_SIZE_TARGET_RECT, RADIUS_SIZE_TARGET_RECT, targetPaint);

            dotPaint.setARGB(255, 0, 0, 0);
            dotPaint.setAntiAlias(true);
            dotPaint.setColor(Color.WHITE);
            dotPaint.setStyle(Paint.Style.FILL);

            linePaint.setARGB(255, 0, 0, 0);
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setStrokeWidth(dpToPx(2));
            linePaint.setColor(Color.WHITE);
            linePaint.setPathEffect(dashPathEffect);
            linePaint.setAntiAlias(true);
            if (isDrawCircle)
                canvas.drawCircle(startX, startY, dpToPx(4), dotPaint);
            canvas.drawLine(startX, startY, stopX, stopY, linePaint);
        }

    }

    public void dismiss() {
        isCalculateView = false;
        ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(this);
        if (mGuideListener != null) {
            mGuideListener.onDismiss(target);
        }
    }

    private void setMessageLocation(Point p) {
        mMessageView.setX(p.x);
        mMessageView.setY(p.y);
        postInvalidate();
    }


    private Point resolveMessageViewLocation() {
        boolean isMessageTop, isTilted;
        isTilted = false;
        xMessageView = 0;
        LINE_HEIGHT_IN_DP = 46;
        xMessageView = (int) (targetRect.left - mMessageView.getWidth() / 2 + target.getWidth() / 2);
        int softButtonsBarHeight = getSoftButtonsBarHeight()+15;
        //set message view bottom
        if (targetRect.top + (target.getHeight()/2)+10 <= getHeight() / 2) {
            isMessageTop = false;
            yMessageView = (int) (targetRect.top + target.getHeight() + indicatorHeight);
            if (yMessageView + mMessageView.getHeight() > getHeight() - softButtonsBarHeight)
                {
                    int diff = (yMessageView + mMessageView.getHeight()) - (getHeight()-softButtonsBarHeight);
                    int diff_dp = pxToDp(diff);
                    int temp =  LINE_HEIGHT_IN_DP;
                    LINE_HEIGHT_IN_DP = LINE_HEIGHT_IN_DP - diff_dp;
                    if (LINE_HEIGHT_IN_DP < 0){
                        LINE_HEIGHT_IN_DP = 0;
                        yMessageView = yMessageView - dpToPx(temp);
                        isDrawCircle = false;
                        if (yMessageView + mMessageView.getHeight() > getHeight() - softButtonsBarHeight)
                            yMessageView = getHeight() - mMessageView.getHeight() - softButtonsBarHeight;

                    }else yMessageView = yMessageView - dpToPx(diff_dp);
                }

        }
        //set message view top
        else {
            isMessageTop = true;
            yMessageView = (int) (targetRect.top - mMessageView.getHeight() - indicatorHeight);
            if (yMessageView < dpToPx(STATUS_BAR_HEIGHT_IN_DP)){
                int diff = dpToPx(STATUS_BAR_HEIGHT_IN_DP)-yMessageView;
                int diff_dp = pxToDp(diff);
                int temp = LINE_HEIGHT_IN_DP;
                LINE_HEIGHT_IN_DP = LINE_HEIGHT_IN_DP - diff_dp;

                if (LINE_HEIGHT_IN_DP<0){
                    LINE_HEIGHT_IN_DP = 0;
                    isDrawCircle = false;
                    yMessageView = yMessageView + dpToPx(temp);
                }else yMessageView = yMessageView + dpToPx(diff_dp);
            }
        }
        int targetCentre = (int)targetRect.left + (target.getWidth()/2)+10;

        if (xMessageView + mMessageView.getWidth() > getWidth()){

            xMessageView = getWidth() - mMessageView.getWidth();
            int messageCentre = xMessageView + mMessageView.getWidth()/2;
            int quarterWidth = (int)(targetRect.right - targetRect.left)/4;
            if (messageCentre < targetCentre - quarterWidth)
                isTilted = true;

        }
        if (xMessageView < 0){
            xMessageView = 0;
            int messageCentre = xMessageView + mMessageView.getWidth()/2;
            int quarterWidth = (int)(targetRect.right - targetRect.left)/4;
            if (messageCentre > targetCentre+quarterWidth)
                isTilted = true;

        }

        if (yMessageView < dpToPx(STATUS_BAR_HEIGHT_IN_DP))
            yMessageView = dpToPx(STATUS_BAR_HEIGHT_IN_DP);
        if (isCalculateView)
            drawDashedLine(targetCentre, isTilted, isMessageTop);
        return new Point(xMessageView, yMessageView);
    }

    private void drawDashedLine(int targetCentre, boolean isTilted, boolean isMessageTop) {
        if (!isMessageTop){
            startY = (int) targetRect.bottom + dpToPx(6);
            stopY = startY + dpToPx(LINE_HEIGHT_IN_DP);
        }
        else {
            startY = (int) targetRect.top - dpToPx(6);
            stopY = startY - dpToPx(LINE_HEIGHT_IN_DP);
        }
        if (isTilted){
            startX = targetCentre;
            stopX = xMessageView + mMessageView.getWidth()/2;
        } else {
            startX = xMessageView + mMessageView.getWidth()/2;
            stopX = xMessageView + mMessageView.getWidth()/2;
        }
    }

    public void show() {
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        this.setClickable(false);
        ((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).addView(this);
        AlphaAnimation startAnimation = new AlphaAnimation(0.0f, 1.0f);
        startAnimation.setDuration(APPEARING_ANIMATION_DURATION);
        startAnimation.setFillAfter(true);
        this.startAnimation(startAnimation);

    }

    static class Builder {
        private View targetView;
        private String title, contentText, buttonText;
        private GuideListener guideListener;
        private boolean isClickable;
        Builder() {
        }

        Builder setGuideListener(GuideListener guideListener) {
            this.guideListener = guideListener;
            return this;
        }
        Builder setTargetView(View view) {
            this.targetView = view;
            return this;
        }
        Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        Builder setIsClickable(boolean isClickable) {
            this.isClickable = isClickable;
            return this;
        }
        Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        Builder setButtonText(String buttonText) {
            this.buttonText = buttonText;
            return this;
        }
        GuideView build() {
            GuideView guideView = new GuideView(targetView, title, contentText, buttonText, isClickable);
            if (guideListener != null) {
                guideView.mGuideListener = guideListener;
            }
            return guideView;
        }
    }

    private int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    private int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        try {
            ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }catch (Exception e){
            return 0;
        }

    }
}

