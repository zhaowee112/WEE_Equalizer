package com.ihealth.myapplication.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.ihealth.myapplication.R;

/**
 * Created by WEE on 2020-04-17
 */
public class EqualizerView extends View {

    private Context mContext;
    private Paint mPaint;
    private Paint connectPaint;
    private int mWidth, mHeight;
    private PointF[] pointsArray;
    private final int STATE_NONE = 0;
    private final int STATE_TOUCH_MOVE = 2;
    private final int STATE_TOUCH_UP = 3;
    private int STATE_NOW = STATE_NONE;

    private int[] decibelArray;
    private float mRadius = 40;
    private float step;
    private updateDecibelListener listener;

    public interface updateDecibelListener {
        void updateDecibel(int[] decibels);

        void getDecibelWhenMoveUp(int[] decibels);


    }

    public EqualizerView(Context context) {
        this(context, null);
    }

    public EqualizerView(
        Context context,
        @Nullable
            AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EqualizerView(
        Context context,
        @Nullable
            AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }


    public void setUpdateDecibelListener(updateDecibelListener listener) {
        this.listener = listener;
    }

    public int[] getDecibelArray() {
        return decibelArray;
    }

    public void setDecibelArray(int[] decibelArray) {
        this.decibelArray = decibelArray;
        invalidate();
    }

    public void init() {
        curvePath = new Path();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        connectPaint = new Paint();
        connectPaint.setAntiAlias(true);
        connectPaint.setStrokeWidth(10);
        connectPaint.setStyle(Paint.Style.STROKE);
        connectPaint.setColor(ContextCompat.getColor(mContext, R.color.equalizer_point_connect_line));

        pointsArray = new PointF[6];
        decibelArray = new int[4];
    }

    private int measureView(int measureSpec, int defaultSize) {
        int measureSize;
        int mode = View.MeasureSpec.getMode(measureSpec);
        int size = View.MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            measureSize = size;
        } else {
            measureSize = defaultSize;
            if (mode == MeasureSpec.AT_MOST) {
                measureSize = Math.min(measureSize, defaultSize);
            }
        }
        return measureSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureView(widthMeasureSpec, 400),
            measureView(heightMeasureSpec, 200));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getWidth();
        mHeight = getHeight();
        step = mHeight / 14;
        canvas.drawColor(ContextCompat.getColor(mContext, R.color.equalizer_background));

        int stepSize = mWidth / 5;
        pointsArray[0] = new PointF(-50, step * 6);
        pointsArray[5] = new PointF(mWidth + 50, step * 6);
        if ((STATE_NOW == STATE_NONE)) {
            for (int i = 1; i <= 4; i++) {
                if(decibelArray[i - 1] == 6){
                    float cx = stepSize * i, cy = mRadius;
                    pointsArray[i] = new PointF(cx, cy);
                }else if(decibelArray[i - 1] == -6){
                    float cx = stepSize * i, cy = mHeight - mRadius;
                    pointsArray[i] = new PointF(cx, cy);
                }else{
                    float cx = stepSize * i, cy = step * (-decibelArray[i - 1] + 7);
                    pointsArray[i] = new PointF(cx, cy);
                }

            }
            refreshView(canvas, stepSize);
        } else {
            refreshView(canvas, stepSize);
        }
    }

    private void refreshView(Canvas canvas, int stepSize) {
        //1.先画点之间的曲线
        canvas.drawPath(drawCurveLine(), connectPaint);

        for (int i = 1; i <= 4; i++) {
            float cx = stepSize * i, cy = pointsArray[i].y;
            //mRadius = 40;
            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.point);
            //2.再画点
            canvas.drawBitmap(bitmap1, pointsArray[i].x - bitmap1.getWidth() / 2, pointsArray[i].y - bitmap1.getHeight() / 2,
                connectPaint);
            mPaint.setColor(ContextCompat.getColor(mContext, R.color.equalizer_vertical_line));
            mPaint.setStrokeWidth(6);
            //3.画纵线
            canvas.drawLine(cx, cy + mRadius - 3, stepSize * i, mHeight, mPaint);
            canvas.drawLine(cx, cy - mRadius + 3, stepSize * i, 0, mPaint);
        }

    }


    private Path curvePath;

    private Path drawCurveLine() {
        curvePath.reset();
        for (int i = 0; i < 7; i++) {
            if (i != 6) {
                switch(i) {
                    case 0:
                        curvePath.moveTo(pointsArray[1].x, pointsArray[1].y);
                        break;
                    case 1:
                    case 2:
                    case 3:
                        curvePath.cubicTo((pointsArray[i].x + pointsArray[i + 1].x) / 2,
                            pointsArray[i].y,
                            (pointsArray[i].x + pointsArray[i + 1].x) / 2,
                            pointsArray[i + 1].y,
                            pointsArray[i + 1].x,
                            pointsArray[i + 1].y);
                        break;
                }
            }
        }
        return curvePath;
    }

    private int mLastY = 0;
    private int index = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX(), y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                index = findTheIndex(x, y);
                if (index != 0) {
                    invalidate();
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float deltaY = y - mLastY;

                if (index != 0) {
                    STATE_NOW = STATE_TOUCH_MOVE;
                    pointsArray[index].y += deltaY;
                    if (y <= 40) {
                        pointsArray[index].y = 40;
                    }
                    if (y >= mHeight - 40) {
                        pointsArray[index].y = mHeight - 40;
                    }
                    decibelArray[index - 1] = getTheDecibel(pointsArray[index].y);
                    invalidate();
                    listener.updateDecibel(decibelArray);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {

                if (index != 0) {
                    STATE_NOW = STATE_TOUCH_UP;
                    if (decibelArray[index - 1] == 0) {
                        pointsArray[index].y = step * 7;
                    }
                    invalidate();
                    listener.getDecibelWhenMoveUp(decibelArray);
                }
                break;
            }
            default:
                break;
        }
        mLastY = y;
        return true;
    }

    /**
     * 查出当前正在操作的是哪个结点
     *
     * @param x
     * @param y
     *
     * @return
     */
    private int findTheIndex(float x, float y) {
        int result = 0;
        for (int i = 1; i < pointsArray.length; i++) {
            if (pointsArray[i].x - mRadius * 1.5 < x && pointsArray[i].x + mRadius * 1.5 > x &&
                pointsArray[i].y - mRadius * 1.5 < y && pointsArray[i].y + mRadius * 1.5 > y) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * 将坐标转换为-6到6之间的数字
     *
     * @param y
     *
     * @return
     */
    private int getTheDecibel(float y) {
        if (y == getHeight() - 40) {
            return -6;
        } else if (y == 40f) {
            return 6;
        } else {
            return 7 - Math.round(y / step);
        }
    }
}