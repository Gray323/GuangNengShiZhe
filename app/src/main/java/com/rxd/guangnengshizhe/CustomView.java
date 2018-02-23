package com.rxd.guangnengshizhe;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2018/2/23.
 * 光能使者召唤图
 */

public class CustomView extends View{

    private static final String TAG = "TAG";
    //画笔
    private Paint mPaint;

    //外圈path
    private Path mBigCirclePath;
    //内圈path
    private Path mSmallCirclePath;
    //正三角形path
    private Path mFirstTriPath;
    //反三角形
    private Path mSecondTriPath;

    private PathMeasure mPathMeasure;

    //View的宽
    private int mWidth;
    //View的高
    private int mHeight;

    //动画的几种不同的状态
    private enum State{
        NONE,//什么都没有的状态
        CIRCLE_ING,//画圆中
        TRIANGLE_FIRST,//第一遍画三角形中
        TRIANGLE_SECOND,//第二遍画三角形中
        END //结束状态
    }

    private State mCurrentState = State.NONE;

    //画圆动画
    private ValueAnimator mCircleAnimator;
    //第一遍画三角形动画
    private ValueAnimator mTriFirstAnimator;
    //第二遍画三角形动画
    private ValueAnimator mTriSecondAnimator;

    //动画监听器
    private Animator.AnimatorListener mAnimatorListener;
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener;

    //动画过程中的中间值
    private float mAnimatorValue;

    private Handler mAnimatorHandler;

    private int mDefaultAnimatorDuration = 2000;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        initPath();
        initListeners();
        initAnimators();
        initHandler();
    }

    public void startAnimator(){
        mCurrentState = State.CIRCLE_ING;
        mCircleAnimator.start();
    }

    private void initHandler() {
        mAnimatorHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (mCurrentState){
                    case CIRCLE_ING:
                        mCurrentState = State.TRIANGLE_FIRST;
                        mTriFirstAnimator.start();
                        break;
                    case TRIANGLE_FIRST:
                        mCurrentState = State.TRIANGLE_SECOND;
                        mTriSecondAnimator.start();
                        break;
                    case TRIANGLE_SECOND:
                        mCurrentState = State.END;
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void initListeners() {
        mAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
               mAnimatorHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        };
    }

    private void initAnimators() {
        mCircleAnimator = ValueAnimator.ofFloat(0, 1).setDuration(mDefaultAnimatorDuration);
        mTriFirstAnimator = ValueAnimator.ofFloat(0, 1).setDuration(mDefaultAnimatorDuration);
        mTriSecondAnimator = ValueAnimator.ofFloat(0, 1).setDuration(mDefaultAnimatorDuration);

        mCircleAnimator.addUpdateListener(mAnimatorUpdateListener);
        mTriFirstAnimator.addUpdateListener(mAnimatorUpdateListener);
        mTriSecondAnimator.addUpdateListener(mAnimatorUpdateListener);

        mCircleAnimator.addListener(mAnimatorListener);
        mTriFirstAnimator.addListener(mAnimatorListener);
        mTriSecondAnimator.addListener(mAnimatorListener);
    }

    private void initPaint(){
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setShadowLayer(15, 0, 0, Color.WHITE);
    }

    private void initPath(){
        mBigCirclePath = new Path();
        mSmallCirclePath = new Path();
        mFirstTriPath = new Path();
        mSecondTriPath = new Path();

        //外圈
        RectF ovalBig = new RectF(-400, -400, 400, 400);
        mBigCirclePath.addArc(ovalBig, 150, 359.9f);

        RectF ovalSmall = new RectF(-350, -350, 350, 350);
        mSmallCirclePath.addArc(ovalSmall, 30, 359.9f);

        mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(mSmallCirclePath, false);
        //圆的六分之一长度
        float arcLength = mPathMeasure.getLength() / 6;

        //第一个点
        float[] firstPoint = new float[2];
        mPathMeasure.getPosTan(0, firstPoint, null);

        //第二个点
        float[] secondPoint = new float[2];
        mPathMeasure.getPosTan(arcLength, secondPoint, null);

        //第三个点
        float[] thirdPoint = new float[2];
        mPathMeasure.getPosTan(arcLength * 2, thirdPoint, null);

        //第四个点
        float[] forthPoint = new float[2];
        mPathMeasure.getPosTan(arcLength * 3, forthPoint, null);

        //第五个点
        float[] fifthPoint = new float[2];
        mPathMeasure.getPosTan(arcLength * 4, fifthPoint, null);

        //第六个点
        float[] sixthPoint = new float[2];
        mPathMeasure.getPosTan(arcLength * 5, sixthPoint, null);

        //画正三角形
        mFirstTriPath.moveTo(firstPoint[0], firstPoint[1]);
        mFirstTriPath.lineTo(thirdPoint[0], thirdPoint[1]);
        mFirstTriPath.lineTo(fifthPoint[0], fifthPoint[1]);
        mFirstTriPath.close();

        //画反三角形
        mSecondTriPath.moveTo(forthPoint[0], forthPoint[1]);
        mSecondTriPath.lineTo(sixthPoint[0], sixthPoint[1]);
        mSecondTriPath.lineTo(secondPoint[0], secondPoint[1]);
        mSecondTriPath.close();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawView(canvas);
    }

    private void drawView(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#0082D7"));
        canvas.translate(mWidth / 2, mHeight / 2);
        switch (mCurrentState){
            case NONE:
                break;
            case CIRCLE_ING:
                //外圈
                mPathMeasure.setPath(mBigCirclePath, false);
                Path bigDst = new Path();
                mPathMeasure.getSegment(0, mPathMeasure.getLength() * mAnimatorValue, bigDst, true);
                canvas.drawPath(bigDst, mPaint);

                //内圈
                mPathMeasure.setPath(mSmallCirclePath, false);
                Path smallDst = new Path();
                mPathMeasure.getSegment(0, mPathMeasure.getLength() * mAnimatorValue, smallDst, true);
                canvas.drawPath(smallDst, mPaint);
                break;
            case TRIANGLE_FIRST:
                canvas.drawPath(mBigCirclePath, mPaint);
                canvas.drawPath(mSmallCirclePath, mPaint);

                float value =0.1f - Math.abs(mAnimatorValue - 0.5f) * 0.2f;
                Log.d(TAG, "value == " + value);
                mPathMeasure.setPath(mFirstTriPath, false);
                Path firstDst = new Path();
                mPathMeasure.getSegment(mPathMeasure.getLength() * mAnimatorValue, mPathMeasure.getLength() * (mAnimatorValue + value), firstDst, true);
                canvas.drawPath(firstDst, mPaint);

                mPathMeasure.setPath(mSecondTriPath, false);
                Path secondDst = new Path();
                mPathMeasure.getSegment(mPathMeasure.getLength() * mAnimatorValue, mPathMeasure.getLength() * (mAnimatorValue + value), secondDst, true);
                canvas.drawPath(secondDst, mPaint);

                break;
            case TRIANGLE_SECOND:
                canvas.drawPath(mBigCirclePath, mPaint);
                canvas.drawPath(mSmallCirclePath, mPaint);

                mPathMeasure.setPath(mFirstTriPath, false);
                Path firstOtherDst = new Path();
                mPathMeasure.getSegment(0, mPathMeasure.getLength() * mAnimatorValue, firstOtherDst, true);
                canvas.drawPath(firstOtherDst, mPaint);

                mPathMeasure.setPath(mSecondTriPath, false);
                Path secondOtherDst = new Path();
                mPathMeasure.getSegment(0, mPathMeasure.getLength() * mAnimatorValue, secondOtherDst, true);
                canvas.drawPath(secondOtherDst, mPaint);
                break;
            case END:
                canvas.drawPath(mBigCirclePath, mPaint);
                canvas.drawPath(mSmallCirclePath, mPaint);
                canvas.drawPath(mFirstTriPath, mPaint);
                canvas.drawPath(mSecondTriPath, mPaint);
                break;
        }


    }
}
