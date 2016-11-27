package cn.izouxiang.likeview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
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
    private long number;
    private String preNum;
    private String postOldNum;
    private String postNewNum;
    private String oldNumStr;
    private String newNumStr;
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
    //文本颜色
    private int textColor;
    @ColorInt
    //图形颜色
    private int graphColor;
    @ColorInt
    //图形动画颜色
    private int animateColor;
    private int textSize;
    private ObjectAnimator numAnimator;
    private ObjectAnimator graphAnimator;
    private boolean isActivated;
    //动画时间
    private long animateDuration;
    private int defWidth;
    private int defHeight;
    //图形与文本间的距离
    private int distance;
    //图形与文本间的高度比
    private float graphTextHeightRatio;
    private int graphStrokeWidth;
    private int textStrokeWidth;
    private Callback callback = new SimpleCallback();

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

    private void init(AttributeSet attrs) {
        if(null == attrs) {
            number = 0;
            graphColor = textColor = Color.parseColor("#888888");
            animateColor = Color.parseColor("#EE0000");
            textSize = sp2px(13);
            isActivated = false;
            animateDuration = 500L;
            distance = dp2px(3);
            graphStrokeWidth = dp2px(2);
            textStrokeWidth = dp2px(2);
            graphTextHeightRatio = 1.3f;
        }else {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LikeView);
            try {
                number = ta.getInt(R.styleable.LikeView_number,0);
                graphColor = ta.getColor(R.styleable.LikeView_graphColor,Color.parseColor("#888888"));
                textColor = ta.getColor(R.styleable.LikeView_textColor,Color.parseColor("#888888"));
                animateColor = ta.getColor(R.styleable.LikeView_animateColor,Color.parseColor("#EE0000"));
                textSize = ta.getDimensionPixelSize(R.styleable.LikeView_textSize,sp2px(16));
                isActivated = ta.getBoolean(R.styleable.LikeView_isActivated,false);
                animateDuration = ta.getInt(R.styleable.LikeView_animateDuration,500);
                distance = ta.getDimensionPixelSize(R.styleable.LikeView_distance,dp2px(3));
                graphStrokeWidth = ta.getDimensionPixelSize(R.styleable.LikeView_graphStrokeWidth,dp2px(2));
                textStrokeWidth = ta.getDimensionPixelSize(R.styleable.LikeView_textStrokeWidth,dp2px(2));
                graphTextHeightRatio = ta.getFloat(R.styleable.LikeView_graphTextHeightRatio,1.3f);
            }finally {
                ta.recycle();
            }
        }
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setStrokeWidth(textStrokeWidth);
        graphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        graphPaint.setStyle(Paint.Style.STROKE);
        graphPaint.setColor(graphColor);
        graphPaint.setStrokeWidth(graphStrokeWidth);
        animatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        animatePaint.setColor(animateColor);
        animatePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        measureTextHeightAndBaseLineHeight();
        measureDefWidthAndDefHeight();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!callback.onClick(LikeView.this)) {
                    trigger();
                }
            }
        });
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
     * 修改数值
     * @param number 新值
     * @param animate 是否启用动画
     */
    public void setNumber(long number,boolean animate) {
        if(animate){
            add(number - this.number);
        }else {
            this.number = number;
            postInvalidate();
        }
    }

    /**
     * 在原先的数值上加值
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

    /**
     * 激活
     */
    public void activate() {
        graphAnimate(0f, 1f);
        isActivated = true;
        callback.activate(this);
    }

    /**
     * 取消激活
     */
    public void deactivate() {
        graphAnimate(1f, 0f);
        isActivated = false;
        callback.deactivate(this);
    }

    /**
     * 切换当前状态
     */
    public void trigger(){
        if(isActivated){
            add(-1);
            deactivate();
        }else {
            add(1);
            activate();
        }
    }
    /**
     * 测量文本高度和基线高度
     */
    private void measureTextHeightAndBaseLineHeight() {
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        textHeight = metrics.bottom - metrics.top;
        textBaseLineHeight = -metrics.top;
    }

    /**
     * 测量View的默认宽高
     */
    private void measureDefWidthAndDefHeight() {
        defHeight = (int) (getPaddingBottom() + getPaddingTop() + textHeight * graphTextHeightRatio);
        float textWidth = textPaint.measureText(String.valueOf(number));
        defWidth = (int) (getPaddingLeft() + getPaddingRight() + defHeight + textWidth + distance);
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
        oldNumStr = oldStr;
        newNumStr = newStr;
        measureTextWidth();
    }

    /**
     * 测量文本宽度
     */
    private void measureTextWidth(){
        preNumWidth = textPaint.measureText(preNum);
        float oldTextWidth = textWidth;
        textWidth =  maxTextWidth(oldNumStr, newNumStr);
        if(oldTextWidth < textWidth){
            measureDefWidthAndDefHeight();
            requestLayout();
        }
    }
    /**
     * 获取最大的文本宽度
     */
    private float maxTextWidth(String oldStr, String newStr) {
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


    private void graphAnimate(float start, float end) {
        if (graphAnimator != null && graphAnimator.isRunning()) {
            graphAnimator.cancel();
        }
        graphAnimator = ObjectAnimator.ofFloat(this, "graphAnimateScale", start, end);
        graphAnimator.setDuration(animateDuration);
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
        setMeasuredDimension(measureMeasureSpec(widthMeasureSpec, defWidth), measureMeasureSpec(heightMeasureSpec, defHeight));
    }

    private int measureMeasureSpec(int measureSpec, int defaultVal) {
        int result = 0;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                result = defaultVal;
                break;
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        graphStartX = getPaddingLeft();
        graphStartY = getPaddingTop();
        graphLength = defHeight - getPaddingTop() - getPaddingBottom();
        textStartX = graphStartX + graphLength + distance;
        textStartY = graphStartY + textHeight * (graphTextHeightRatio - 1) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawHeart(canvas);
        drawNumber(canvas);
    }

    private void drawNumber(Canvas canvas) {
        canvas.save();
        canvas.translate(textStartX,textStartY);
        if (!isNumAnimateRunning) {
            canvas.drawText(String.valueOf(number), 0, textBaseLineHeight, textPaint);
        } else {
            numAnimateScale *= -direction;
            canvas.clipRect(0, 0, 0 + textWidth, 0 + textHeight);
            canvas.drawText(preNum, 0, textBaseLineHeight, textPaint);
            canvas.drawText(postOldNum, preNumWidth, textBaseLineHeight + textHeight * numAnimateScale, textPaint);
            canvas.drawText(postNewNum, preNumWidth, textBaseLineHeight + textHeight * (numAnimateScale + direction), textPaint);
        }
        canvas.restore();
    }

    private void drawHeart(Canvas canvas) {
        canvas.save();
        canvas.translate(graphStartX,graphStartY);
        Path heartPath = PathUtils.getHeartPath((int) graphLength);
        canvas.clipPath(heartPath);
        float radial = (float) (graphLength * Math.sqrt(2) / 2f);
        if (!isGraphAnimateRunning) {
            canvas.drawPath(heartPath, graphPaint);
            if (isActivated) {
                canvas.drawCircle(graphLength / 2, graphLength / 2, radial, animatePaint);
            }
        } else {
            canvas.drawPath(PathUtils.getHeartPath((int) graphLength, 1 - graphAnimateScale), graphPaint);
            canvas.drawCircle(graphLength / 2, graphLength / 2, radial * graphAnimateScale, animatePaint);
        }
        canvas.restore();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(@ColorInt int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
        postInvalidate();
    }

    public int getGraphColor() {
        return graphColor;
    }

    public void setGraphColor(@ColorInt int graphColor) {
        this.graphColor = graphColor;
        graphPaint.setColor(graphColor);
        postInvalidate();
    }

    public int getAnimateColor() {
        return animateColor;
    }

    public void setAnimateColor(@ColorInt int animateColor) {
        this.animateColor = animateColor;
        animatePaint.setColor(animateColor);
        postInvalidate();
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        measureTextWidth();
        measureTextHeightAndBaseLineHeight();
        measureDefWidthAndDefHeight();
        requestLayout();
    }

    public boolean isActivated() {
        return isActivated;
    }

    public long getAnimateDuration() {
        return animateDuration;
    }

    public void setAnimateDuration(long animateDuration) {
        this.animateDuration = animateDuration;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
        measureDefWidthAndDefHeight();
        requestLayout();
    }

    public float getGraphTextHeightRatio() {
        return graphTextHeightRatio;
    }

    public void setGraphTextHeightRatio(float graphTextHeightRatio) {
        this.graphTextHeightRatio = graphTextHeightRatio;
        measureDefWidthAndDefHeight();
        requestLayout();
    }

    public int getTextStrokeWidth() {
        return textStrokeWidth;
    }

    public void setTextStrokeWidth(int textStrokeWidth) {
        this.textStrokeWidth = textStrokeWidth;
        textPaint.setStrokeWidth(textStrokeWidth);
        postInvalidate();
    }

    public int getGraphStrokeWidth() {
        return graphStrokeWidth;
    }

    public void setGraphStrokeWidth(int graphStrokeWidth) {
        this.graphStrokeWidth = graphStrokeWidth;
        graphPaint.setStrokeWidth(graphStrokeWidth);
        postInvalidate();
    }

    public interface Callback {
        boolean onClick(LikeView view);
        void activate(LikeView view);
        void deactivate(LikeView view);
    }
    public static class SimpleCallback implements Callback{

        @Override
        public boolean onClick(LikeView view) {
            return false;
        }

        @Override
        public void activate(LikeView view) {

        }

        @Override
        public void deactivate(LikeView view) {

        }
    }
}
