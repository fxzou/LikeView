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
    private static final String TAG = "LikeView";
    public static final int GRAVITY_CENTER = 1;
    public static final int GRAVITY_LEFT = 2;
    public static final int GRAVITY_RIGHT = 3;
    public static final int GRAVITY_START = 4;
    private long number;
    private String preNumStr = "";
    private String postOldNumStr = "";
    private String postNewNumStr = "";
    private String oldNumStr = "";
    private String newNumStr = "";
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
    private GraphAdapter graphAdapter = new HeartGraphAdapter();
    private Path graphPath;
    //是否开启预先处理文本宽度,即测量当前值+1/-1后文本宽度变化,取最大值
    private boolean autoMeasureMaxTextWidth;
    //是否允许取消
    private boolean notAllowedCancel;
    private int gravity;

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
        if (null == attrs) {
            number = 0;
            graphColor = textColor = Color.parseColor("#888888");
            animateColor = Color.parseColor("#ca5f5f");
            textSize = sp2px(14);
            isActivated = false;
            animateDuration = 300L;
            distance = dp2px(3);
            graphStrokeWidth = dp2px(3);
            textStrokeWidth = dp2px(2);
            graphTextHeightRatio = 1.3f;
            autoMeasureMaxTextWidth = true;
            notAllowedCancel = false;
            gravity = GRAVITY_CENTER;
        } else {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LikeView);
            try {
                number = ta.getInt(R.styleable.LikeView_number, 0);
                graphColor = ta.getColor(R.styleable.LikeView_graphColor, Color.parseColor("#888888"));
                textColor = ta.getColor(R.styleable.LikeView_textColor, Color.parseColor("#888888"));
                animateColor = ta.getColor(R.styleable.LikeView_animateColor, Color.parseColor("#ca5f5f"));
                textSize = ta.getDimensionPixelSize(R.styleable.LikeView_textSize, sp2px(14));
                isActivated = ta.getBoolean(R.styleable.LikeView_isActivated, false);
                autoMeasureMaxTextWidth = ta.getBoolean(R.styleable.LikeView_autoMeasureMaxWidth, true);
                notAllowedCancel = ta.getBoolean(R.styleable.LikeView_notAllowedCancel, false);
                animateDuration = ta.getInt(R.styleable.LikeView_animateDuration, 300);
                distance = ta.getDimensionPixelSize(R.styleable.LikeView_distance, dp2px(3));
                graphStrokeWidth = ta.getDimensionPixelSize(R.styleable.LikeView_graphStrokeWidth, dp2px(3));
                textStrokeWidth = ta.getDimensionPixelSize(R.styleable.LikeView_textStrokeWidth, dp2px(2));
                graphTextHeightRatio = ta.getFloat(R.styleable.LikeView_graphTextHeightRatio, 1.3f);
                gravity = ta.getInteger(R.styleable.LikeView_gravity,GRAVITY_CENTER);
            } finally {
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
        newNumStr = String.valueOf(number);
        measureTextHeightAndBaseLineHeight();
        measureTextWidth();
        measureDefWidthAndDefHeight();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!callback.onClick(LikeView.this)) {
                    trigger();
                }
            }
        });
    }

    /**
     * 修改数值
     *
     * @param number 新值
     */
    public void setNumber(long number) {
        setNumber(number, false);
    }

    /**
     * 修改数值
     *
     * @param number  新值
     * @param animate 是否启用动画
     */
    public void setNumber(long number, boolean animate) {
        if (animate) {
            add(number - this.number);
        } else {
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
        oldNumStr = String.valueOf(number);
        newNumStr = String.valueOf(newNum);
        number = newNum;
        handleNewString();
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
    public void trigger() {
        if (isActivated) {
            if (!notAllowedCancel) {
                deactivateAndTallyDown();
            }
        } else {
            activateAndPlusOne();
        }
    }

    /**
     * 激活并且给数值加1
     */
    public void activateAndPlusOne() {
        add(1);
        activate();
    }

    /**
     * 取消激活并且给数组减1
     */
    public void deactivateAndTallyDown() {
        add(-1);
        deactivate();
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
        int oldDefHeight = defHeight;
        defHeight = (int) (getPaddingBottom() + getPaddingTop() + textHeight * graphTextHeightRatio);
        defWidth = (int) (getPaddingLeft() + getPaddingRight() + defHeight + textWidth + distance);
        if(defHeight != oldDefHeight){
            measureGraphLength();
            getGraphPath();
            measureStartPoint();
        }
    }

    private void measureGraphLength(){
        graphLength = defHeight - getPaddingTop() - getPaddingBottom();
    }
    private void getGraphPath(){
        graphPath = graphAdapter.getGraphPath(this, (int) graphLength);
    }
    /**
     * 对比字符串,并测量文本宽度
     */
    private void handleNewString() {
        if (oldNumStr == null || newNumStr == null || oldNumStr.equals(newNumStr)) {
            return;
        }
        if (oldNumStr.length() != newNumStr.length()) {
            preNumStr = "";
            postOldNumStr = oldNumStr;
            postNewNumStr = newNumStr;
        } else {
            int length = oldNumStr.length();
            int i;
            for (i = 0; i < length; i++) {
                if (newNumStr.charAt(i) != oldNumStr.charAt(i)) {
                    break;
                }
            }
            preNumStr = oldNumStr.substring(0, i);
            postOldNumStr = oldNumStr.substring(i);
            postNewNumStr = newNumStr.substring(i);
        }
        measureTextWidthChanged();
    }
    private void measureTextWidthChanged(){
        float oldTextWidth = textWidth;
        measureTextWidth();
        //如果宽度改变,并且初始化完成,则要重新测量默认宽高
        if (oldTextWidth != textWidth) {
            int oldDefWidth = defWidth;
            int oldDefHeight = defHeight;
            measureDefWidthAndDefHeight();
            //如果新的默认宽高不等于原来的值,就需要请求重新布局
            if ((defWidth != oldDefWidth || defHeight != oldDefHeight)) {
                requestLayout();
            }
        }
    }
    /**
     * 测量文本宽度
     */
    private void measureTextWidth() {
        //测量共同文本的宽度
        preNumWidth = textPaint.measureText(preNumStr);

        if (autoMeasureMaxTextWidth) {//是否自动测量变化文本宽度
            //先算出新旧字符串的最大宽度
            float width = maxTextWidth(oldNumStr, newNumStr);
            //与加一比较还是与减一比较
            int offset = isActivated ? -1 : 1;
            //再和修改后的字符比较
            textWidth = Math.max(width, textPaint.measureText(String.valueOf(number + offset)));
        } else {
            textWidth = maxTextWidth(oldNumStr, newNumStr);
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

    /**
     * 图形动画
     *
     * @param start 开始值
     * @param end   结束值
     */
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

    /**
     * 测量View的宽高
     *
     * @param measureSpec MeasureSpec实例
     * @param defaultVal  默认值,当mode为UNSPECIFIED和AT_MOST时使用默认值
     * @return 测量后的宽高
     */
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        measureStartPoint();
    }

    private void measureStartPoint(){
        int w = getWidth();
        int h = getHeight();
        //对齐显示偏移量
        int dx;
        int dy;
        switch (gravity){
            case GRAVITY_CENTER :
                dx = (w - defWidth) / 2;
                dy = (h - defHeight) / 2;
                break;
            case GRAVITY_LEFT :
                dx = 0;
                dy = (h - defHeight) / 2;
                break;
            case GRAVITY_RIGHT :
                dx = w - defWidth;
                dy = (h - defHeight) / 2;
                break;
            case GRAVITY_START :
            default:
                dx = 0;
                dy = 0;

        }
        graphStartX = getPaddingLeft() + dx;
        graphStartY = getPaddingTop() + dy;
        textStartX = graphStartX + graphLength + distance;
        textStartY = graphStartY + textHeight * (graphTextHeightRatio - 1) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawGraph(canvas);
        drawNumber(canvas);
    }

    /**
     * 画数字
     */
    private void drawNumber(Canvas canvas) {
        canvas.save();
        canvas.translate(textStartX, textStartY);
        if (!isNumAnimateRunning) { //是否正在动画中
            //直接绘制数字
            canvas.drawText(String.valueOf(number), 0, textBaseLineHeight, textPaint);
        } else {
            //根据方向来计算位置
            numAnimateScale *= -direction;
            //裁剪文本框
            canvas.clipRect(0, 0, 0 + textWidth, 0 + textHeight);
            //绘制相同文本
            canvas.drawText(preNumStr, 0, textBaseLineHeight, textPaint);
            //绘制旧文本
            canvas.drawText(postOldNumStr, preNumWidth, textBaseLineHeight + textHeight * numAnimateScale, textPaint);
            //绘制新文本
            canvas.drawText(postNewNumStr, preNumWidth, textBaseLineHeight + textHeight * (numAnimateScale + direction), textPaint);
        }
        canvas.restore();
    }

    /**
     * 画图形
     */
    private void drawGraph(Canvas canvas) {
        canvas.save();
        canvas.translate(graphStartX, graphStartY);
        canvas.clipPath(graphPath);
        float radial = (float) (graphLength * Math.sqrt(2) / 2f);

        if (!isGraphAnimateRunning) {//如果不是动画中,就直接绘画
            if (isActivated) {
                //绘制激活状态
                canvas.drawCircle(graphLength / 2, graphLength / 2, radial, animatePaint);
            } else {
                //绘制图形
                canvas.drawPath(graphPath, graphPaint);
            }
        } else { //是动画中就要按比例绘画
            //修改外圈图形比例
            drawGraphPathByScale(canvas, 1 - graphAnimateScale);
            //修改内圈圆形比例
            canvas.drawCircle(graphLength / 2, graphLength / 2, radial * graphAnimateScale, animatePaint);
        }
        canvas.restore();
    }

    /**
     * 将图形缩放绘画
     *
     * @param canvas 画笔
     * @param scale  比例
     */
    private void drawGraphPathByScale(Canvas canvas, float scale) {
        canvas.save();
        canvas.scale(scale, scale, graphLength / 2, graphLength / 2);
        canvas.drawPath(graphPath, graphPaint);
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

    @Override
    public void setActivated(boolean activated) {
        isActivated = activated;
        postInvalidate();
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

    public void setGraphAdapter(GraphAdapter graphAdapter) {
        if (graphAdapter != null) {
            this.graphAdapter = graphAdapter;
            postInvalidate();
        }
    }

    public void setCallback(Callback callback) {
        if (callback != null) {
            this.callback = callback;
        }
    }

    public boolean isAutoMeasureMaxTextWidth() {
        return autoMeasureMaxTextWidth;
    }

    public void setAutoMeasureMaxTextWidth(boolean autoMeasureMaxTextWidth) {
        this.autoMeasureMaxTextWidth = autoMeasureMaxTextWidth;
        measureTextWidth();
    }

    public boolean isNotAllowedCancel() {
        return notAllowedCancel;
    }

    public void setNotAllowedCancel(boolean notAllowedCancel) {
        this.notAllowedCancel = notAllowedCancel;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    /**
     * 动画
     */
    public void setNumAnimateScale(float numAnimateScale) {
        this.numAnimateScale = numAnimateScale;
        postInvalidate();
    }

    public void setGraphAnimateScale(float graphAnimateScale) {
        this.graphAnimateScale = graphAnimateScale;
        postInvalidate();
    }

    /**
     * 事件监听接口
     */
    public interface Callback {
        /**
         * 点击事件监听
         *
         * @param view 当前View
         * @return 返回true则代表不使用默认的点击事件
         */
        boolean onClick(LikeView view);

        /**
         * 变为激活状态
         *
         * @param view 当前View
         */
        void activate(LikeView view);

        /**
         * 变为不激活状态
         *
         * @param view 当前View
         */
        void deactivate(LikeView view);
    }

    /**
     * 事件监听的默认实现
     */
    public static class SimpleCallback implements Callback {

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

    /**
     * 获取图形Path接口
     */
    public interface GraphAdapter {
        /**
         * 获取图形的Path
         *
         * @param view   当前View
         * @param length 可绘制图形区域正方形的边长
         * @return 带有图形的Path
         */
        Path getGraphPath(LikeView view, int length);
    }

    /**
     * 桃心图形类
     */
    public static class HeartGraphAdapter implements GraphAdapter {

        @Override
        public Path getGraphPath(LikeView view, int length) {
            return PathUtils.getHeartPath(length);
        }
    }
}
