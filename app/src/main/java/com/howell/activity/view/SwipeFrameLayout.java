package com.howell.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by howell on 2016/11/18.
 */

public class SwipeFrameLayout extends FrameLayout {
    Scroller mScroller;
    int startScrollX;
    float lastX;
    float lastY;
    float startX;
    float startY;
    boolean hasJudged = false;
    boolean ignore = false;

    public static final int DIRECTION_EXPAND = 0;
    public static final int DIRECTION_SHRINK = 1;

    OnSwipeListener onSwipeListener = null;



    static float MOVE_JUDGE_DISTANCE = 5;

    // 左边部分, 即从开始就显示的部分的长度
    int width_backgroud = 0;
    // 右边部分, 即在开始时是隐藏的部分的长度
    int width_top = 0;
    View mBack;
    View mTop;

    private boolean isClose = true;
    public boolean isClose() {
        return isClose;
    }
    public void setClose(boolean close) {
        isClose = close;
    }

    public SwipeFrameLayout(Context context) {
       this(context,null);
    }

    public SwipeFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }



    public SwipeFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mBack = getChildAt(0);
        mTop = getChildAt(1);
        width_backgroud = mBack.getMeasuredWidth();
        width_top = mTop.getMeasuredWidth();
     //   mScroller.startScroll(0,0,0,0);
    }

    private void disallowParentsInterceptTouchEvent(ViewParent parent) {
        if (null == parent) {
            return;
        }
        parent.requestDisallowInterceptTouchEvent(true);
        disallowParentsInterceptTouchEvent(parent.getParent());
    }

    private void allowParentsInterceptTouchEvent(ViewParent parent) {
        if (null == parent) {
            return;
        }
        parent.requestDisallowInterceptTouchEvent(false);
        allowParentsInterceptTouchEvent(parent.getParent());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                disallowParentsInterceptTouchEvent(getParent());
                hasJudged = false;
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float curX = ev.getX();
                float curY = ev.getY();
                if (hasJudged == false) {
                    float dx = curX - startX;
                    float dy = curY - startY;

                    if ((dx * dx + dy * dy > MOVE_JUDGE_DISTANCE * MOVE_JUDGE_DISTANCE)) {
                        if (Math.abs(dy) > Math.abs(dx)) {
                            allowParentsInterceptTouchEvent(getParent());

                            if (null != onSwipeListener) {
                                onSwipeListener.onDirectionJudged(this, false);
                            }
                        } else {
                            if (null != onSwipeListener) {
                                if (isClose&&(dx<0)){
                                    //   Log.i("123","dispatchTouchEvent    dx="+dx  +"isclose="+isClose);
//                                    allowParentsInterceptTouchEvent(getParent());
                                }
                                onSwipeListener.onDirectionJudged(this, true);
                            }
                            lastX = curX;
                            lastY = curY;
                        }
                        hasJudged = true;
                        ignore = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                startScrollX = getScrollX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (ignore) {
                    ignore = false;
                    Log.i("123","ignore");
                    break;
                }
                float curX = event.getX();
                float dX = curX - lastX;
                if (dX>0 && isClose){
                        Log.i("123","dx="+dX  +"isclose="+isClose);
                    return false;
                }
                lastX = curX;
                if (hasJudged) {
                    int targetScrollX = getScrollX() + (int)(-dX);
                    Log.i("123","scrollx="+getScrollX()+"  curX="+curX+"  dX="+dX+"   back="+width_backgroud);
//                    Log.i("123","X="+getScaleX()+"  dx="+dX+"  target="+targetScrollX+"   left="+width_backgroud);
                    mTop.scrollTo(targetScrollX, 0);
                    if (targetScrollX > width_backgroud) {//全开
//                        scrollTo(width_backgroud, 0);
                    } else if (targetScrollX < 0) {//全关
//                        scrollTo(0, 0);
                    } else {//滑动
//                        scrollTo(targetScrollX, 0);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                float finalX = event.getX();
                if (finalX < startX) {//左划  关闭
                    //   Log.e("123","scroll  SHRINK");
                    if (!isClose) {
                        scrollAuto(DIRECTION_SHRINK);
                    }
                }  else {//右划 展开
                    //    Log.e("123","scroll  EXPAND");
                    scrollAuto(DIRECTION_EXPAND);
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void scrollAuto(final int direction) {
        int curScrollX = getScrollX();
        if (direction == DIRECTION_EXPAND) {
            // 展开
            //    Log.i("123","展开");
//            mScroller.startScroll(curScrollX,0,-curScrollX,0,300);
            isClose = false;
//                 mScroller.startScroll(curScrollX, 0, -curScrollX, 0, 300);
        } else {
            // 缩回
            //   Log.i("123","缩回");

//            mScroller.startScroll(curScrollX,0,(width_backgroud-curScrollX),0,300);
            isClose = true;
//                mScroller.startScroll(curScrollX, 0, width_top - curScrollX, 0, 300);
        }
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            this.scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }

    public void setOnSwipeListener(OnSwipeListener listener) {
        this.onSwipeListener = listener;
    }

    public interface OnSwipeListener {
        /**
         * 手指滑动方向明确了
         * @param sfl  拖动的SwipeLinearLayout
         * @param isHorizontal 滑动方向是否为水平
         */
        void onDirectionJudged(SwipeFrameLayout sfl, boolean isHorizontal);


    }
}
