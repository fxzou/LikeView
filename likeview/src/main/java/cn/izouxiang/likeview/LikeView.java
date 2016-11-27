package cn.izouxiang.likeview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import cn.izouxiang.likeview.util.PathUtils;

/**
 * Created by zouxiang on 2016/11/23.
 */

public class LikeView extends View {
    private static final String TAG = "VerticalScrollView";
    private long number = 9995;
    private String preNum;
    private String postOldNum;
    private String postNewNum;
    private float numAnimateScale = 0;
    private float graphAnimateScale = 0;
    private boolean isNumAnimateRunning = false;
    private boolean isGraphAnimateRunning = false;
    private TextPaint textPaint;
    private Paint graphPaint;
    private Paint animatePaint;
    private float graphStartX;
    private float graphStartY;
    private float graphLength;
    private float textStartX;
    private float textStartY;
    private float textWidth;
    private float preNumWidth;
    private float textHeight;
    private float textBaseLineHeight;
    private int direction;
    @ColorInt
    private int textColor;
    @ColorInt
    private int graphColor;
    @ColorInt
    private int animatColor;
    private int textSize;
    private ObjectAnimator numAnimator;
    private ObjectAnimator graphAnimator;
    private boolean isEnabled;
    private long animateDuration;

    public LikeView(Context context) {
        super(context);
        init(null);
    }

    public LikeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LikeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void setNumAnimateScale(float numAnimateScale) {
        this.numAnimateScale = numAnimateScale;
        postInvalidate();
    }

    public void setGraphAnimateScale(float graphAnimateScale) {
        this.graphAnimateScale = graphAnimateScale;
        postInvalidate();
    }

    /**
     * 修改数字数值
     */
    public void add(long num) {
        if (num == 0) {
            return;
        }
        long newNum = number + num;
        String oldStr = String.valueOf(number);
        String newStr = String.valueOf(newNum);
        number = newNum;
        handleNewString(oldStr, newStr);
        roll(num > 0);
    }

    private void init(AttributeSet attrs) {
        graphColor = textColor = Color.parseColor("#888888");
        animatColor = Color.RED;
        textSize = sp2px(20);
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        measureTextHeightAndBaseLineHeight(textPaint);
        graphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        graphPaint.setStyle(Paint.Style.STROKE);
        graphPaint.setColor(graphColor);
        isEnabled = false;
        animateDuration = 500L;
        animatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        animatePaint.setColor(Color.parseColor("#EE0000"));
        animatePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    /**
     * 根据paint求出文本高度和基线高度
     *
     * @param paint
     */
    private void measureTextHeightAndBaseLineHeight(Paint paint) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        textHeight = metrics.bottom - metrics.top;
        textBaseLineHeight = -metrics.top;
    }


    /**
     * 对比字符串,并测量文本宽度
     */
    private void handleNewString(String oldStr, String newStr) {
        if (oldStr == null || newStr == null || oldStr.equals(newStr)) {
            return;
        }
        if (oldStr.length() != newStr.length()) {
            preNum = "";
            postOldNum = oldStr;
            postNewNum = newStr;
        } else {
            int length = oldStr.length();
            int i;
            for (i = 0; i < length; i++) {
                if (newStr.charAt(i) != oldStr.charAt(i)) {
                    break;
                }
            }
            preNum = oldStr.substring(0, i);
            postOldNum = oldStr.substring(i);
            postNewNum = newStr.substring(i);
        }
        textWidth = measureTextWidth(oldStr, newStr);
        preNumWidth = textPaint.measureText(preNum);
    }

    /**
     * 获取最大的文本宽度
     */
    private float measureTextWidth(String oldStr, String newStr) {
        return Math.max(textPaint.measureText(oldStr), textPaint.measureText(newStr));
    }

    /**
     * 滑动数字
     *
     * @param rollUp 为true时向上滑
     */
    private void roll(boolean rollUp) {
        if (numAnimator != null && numAnimator.isRunning()) {
            numAnimator.cancel();
        }
        direction = rollUp ? 1 : -1;
        numAnimator = ObjectAnimator.ofFloat(this, "numAnimateScale", 0f, 1f);
        numAnimator.setDuration(animateDuration);
        isNumAnimateRunning = true;
        numAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isNumAnimateRunning = false;
            }
        });
        numAnimator.start();
    }

    public void activate() {
        graphAnimate(0f, 1f);
        isEnabled = true;
    }

    public void deactivate() {
        graphAnimate(1f, 0f);
        isEnabled = false;
    }

    private void graphAnimate(float start, float end) {
        if (graphAnimator != null && graphAnimator.isRunning()) {
            graphAnimator.cancel();
        }
        graphAnimator = ObjectAnimator.ofFloat(this, "graphAnimateScale", start, end);
        isGraphAnimateRunning = true;
        graphAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isGraphAnimateRunning = false;
            }
        });
        graphAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(100, 100);
        drawHeart(canvas, 100, 100);
        canvas.clipRect(0, 0, 100, 100);
        canvas.restore();
        canvas.save();
        canvas.translate(205, 100 + (100 - textHeight) / 2);
        drawNumber(canvas);
        canvas.restore();
    }

    private void drawNumber(Canvas canvas) {
        if (!isNumAnimateRunning) {
            canvas.drawText(String.valueOf(number), 0, textBaseLineHeight, textPaint);
        } else {
            numAnimateScale *= -direction;
            canvas.clipRect(0, 0, 0 + textWidth, 0 + textHeight);
            canvas.drawText(preNum, 0, textBaseLineHeight, textPaint);
            canvas.drawText(postOldNum, preNumWidth, textBaseLineHeight + textHeight * numAnimateScale, textPaint);
            canvas.drawText(postNewNum, preNumWidth, textBaseLineHeight + textHeight * (numAnimateScale + direction), textPaint);
        }
    }

    private void drawHeart(Canvas canvas, int w, int h) {
        canvas.save();
        int length = Math.min(w, h);
        Path heartPath = PathUtils.getHeartPath(length);
        canvas.clipPath(heartPath);
        float radial = (float) (length * Math.sqrt(2) / 2f);
        if (!isGraphAnimateRunning) {
            canvas.drawPath(heartPath,graphPaint);
            if (isEnabled) {
                canvas.drawCircle(length / 2, length / 2, radial, animatePaint);
            }
        } else {
            canvas.drawPath(PathUtils.getHeartPath(length, 1 - graphAnimateScale), graphPaint);
            canvas.drawCircle(length / 2, length / 2, radial * graphAnimateScale, animatePaint);
        }
        canvas.restore();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}
