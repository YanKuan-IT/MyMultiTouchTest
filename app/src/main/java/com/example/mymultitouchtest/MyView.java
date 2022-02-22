package com.example.mymultitouchtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;

public class MyView extends View {
    private static final String TAG = "AAAAAAAAAAAAA";
    private Bitmap mBitmap;
    private Paint mPaint;

    // 手指按下的位置
    private float mDownX;
    private float mDownY;

    // 当前按下的pointerId  即当前可以拖动图片的手指
    private int mCurrentPointerId;

    // 手指滑动偏移值
    private float mOffsetX;
    private float mOffsetY;

    // 上一次手指滑动偏移值
    private float mLastOffsetX;
    private float mLastOffsetY;

    public MyView(Context context) {
        this(context, null);
    }
    public MyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context) {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制bitmap
        canvas.drawBitmap(mBitmap, mOffsetX, mOffsetY, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: ");
        
        switch (event.getActionMasked()) {
            // 只触发一次 第一根手指按下时
            case MotionEvent.ACTION_DOWN:
                // 手指按下的位置
                mDownX = event.getX();
                mDownY = event.getY();

                mCurrentPointerId = 0;
                break;

            // 所有手指的移动都会触发
            case MotionEvent.ACTION_MOVE:
                // 根据 pointerId 获取 index，index 值不一定等于 pointerId 值
                int index = event.findPointerIndex(mCurrentPointerId);
                Log.d(TAG, "移动的: index: " + index);

                // 手指滑动偏移值 = 上一次手指滑动偏移值 + 本地首次滑动偏移值（即 当前x - 上次x）
                mOffsetX = mLastOffsetX + event.getX(index) - mDownX;
                mOffsetY = mLastOffsetY + event.getY(index) - mDownY;

                invalidate();

                break;

            // 只触发一次，最后一根手指抬起时
            case MotionEvent.ACTION_UP:
                // 手指抬起时，记录上次的偏移值，避免从头开始滑动
                mLastOffsetX = mOffsetX;
                mLastOffsetY = mOffsetY;
                break;

            // 非第一根手指按下时触发
            case MotionEvent.ACTION_POINTER_DOWN:
                // 获取 index
                int actionIndex = event.getActionIndex();

                Log.d(TAG, "按下的 非第一根手指: index: " + actionIndex);

                // 根据 index 获取 pointerId  ， 设置按下的手指去拖动图片
                mCurrentPointerId = event.getPointerId(actionIndex);

                mDownX = event.getX(actionIndex);
                mDownY = event.getY(actionIndex);

                mLastOffsetX = mOffsetX;
                mLastOffsetY = mOffsetY;

                break;

            // 非最后一根手指抬起时触发
            case MotionEvent.ACTION_POINTER_UP:
                // 需要重新分配的去拖动图片的index
                int tempIndex;

                // 抬起的手指的 index
                int upIndex = event.getActionIndex();

                Log.d(TAG, "抬起的 非最后一根手指: index: " + upIndex);

                // 抬起的手指的 pointerId
                int upPointerId = event.getPointerId(upIndex);

                // 抬起的手指 就是 当前可以拖动图片的手指，需要重新分配可以拖动图片的手指
                if (upPointerId == mCurrentPointerId) {
                    // 需要重新给mCurrentPointerId赋值，抬起后，这个

                    // 抬起手指的index 是否 最后一个手指
                    if (upIndex == event.getPointerCount() - 1) {
                        // 获取到倒数第二根手指（按照index进行排序的手指）
                        tempIndex = event.getPointerCount() - 2;
                    } else { // 抬起的手指，是index中间的某一个手指
                        // 获取后面一个手指作为可以拖动图片的手指
                        tempIndex = upIndex +1;
                    }

                    // 获取可以拖动图片的pointerId
                    mCurrentPointerId = event.getPointerId(tempIndex);

                    mDownX = event.getX(tempIndex);
                    mDownY = event.getY(tempIndex);

                    mLastOffsetX = mOffsetX;
                    mLastOffsetY = mOffsetY;
                }

                break;
        }

//        return super.onTouchEvent(event);
        return true;
    }
}
