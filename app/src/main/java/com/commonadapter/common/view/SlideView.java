package com.commonadapter.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.commonadapter.R;


public class SlideView extends LinearLayout {

    private static final String TAG = "SlideView";

    private Context mContext;

    // 用来放置所有view的容器
    private LinearLayout mViewContent;

    // 用来放置内置view的容器，比如删除 按钮
    private RelativeLayout mHolder;

    // 弹性滑动对象，提供弹性滑动效果
    private Scroller mScroller;

    // 滑动回调接口，用来向上层通知滑动事件
    private OnSlideListener mOnSlideListener;

    // 内置容器的宽度 单位：dp
    private int mHolderWidth = 120;

    // 分别记录上次滑动的坐标
    private int mLastX = 0;
    private int mLastY = 0;

    // 用来控制滑动角度，仅当角度a满足如下条件才进行滑动：tan a = deltaX / deltaY > 2
    private static final int TAN = 2;

    public interface OnSlideListener {
        // SlideView的三种状态：开始滑动，打开，关闭
        public static final int SLIDE_STATUS_OFF = 0;
        public static final int SLIDE_STATUS_START_SCROLL = 1;
        public static final int SLIDE_STATUS_ON = 2;

        /**
         * @param view   current SlideView
         * @param status SLIDE_STATUS_ON or SLIDE_STATUS_OFF
         */
        public void onSlide(View view, int status);
    }

    public SlideView(Context context) {
        super(context);
        initView();
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mContext = getContext();
        mScroller = new Scroller(mContext);

        setOrientation(LinearLayout.HORIZONTAL);
        View.inflate(mContext, R.layout.slide_view_merge, this);
        mViewContent = (LinearLayout) findViewById(R.id.view_content);
        mHolderWidth = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mHolderWidth, getResources()
                        .getDisplayMetrics()));
    }

    public void setButtonText(CharSequence text) {
        ((TextView) findViewById(R.id.delete)).setText(text);
    }

    public void setContentView(View view) {
        mViewContent.addView(view);
    }

    public void setOnSlideListener(OnSlideListener onSlideListener) {
        mOnSlideListener = onSlideListener;
    }

    public void shrink() {
        if (getScrollX() != 0) {
            this.smoothScrollTo(0, 0);
        }
    }

    public void onRequireTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        //向左滑动的距离，点击时为0
        int scrollX = getScrollX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                //判断滑动是否停止
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                if (mOnSlideListener != null) {
                    mOnSlideListener.onSlide(this,
                            OnSlideListener.SLIDE_STATUS_START_SCROLL);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastX;//滑动的X轴
                int deltaY = y - mLastY;
                if (Math.abs(deltaX) < Math.abs(deltaY) * TAN) {
                    break;
                }
                int newScrollX = scrollX - deltaX;
                if (deltaX != 0) {
                    if (newScrollX < 0) {
                        newScrollX = 0;
                    } else if (newScrollX > mHolderWidth) {
                        newScrollX = mHolderWidth;
                    }
                    this.scrollTo(newScrollX, 0);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                int newScrollX = 0;
                if (scrollX - mHolderWidth * 0.75 > 0) {
                    newScrollX = mHolderWidth;
                }
                this.smoothScrollTo(newScrollX, 0);
                if (mOnSlideListener != null) {
                    mOnSlideListener.onSlide(this,
                            newScrollX == 0 ? OnSlideListener.SLIDE_STATUS_OFF
                                    : OnSlideListener.SLIDE_STATUS_ON);
                }
                break;
            }
            default:
                break;
        }

        mLastX = x;
        mLastY = y;
    }

    private void smoothScrollTo(int destX, int destY) {
        // 缓慢滚动到指定位置
        int scrollX = getScrollX();
        int delta = destX - scrollX;
        mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

}
