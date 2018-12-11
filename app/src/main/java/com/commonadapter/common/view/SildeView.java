package com.commonadapter.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.commonadapter.R;
import com.commonadapter.common.util.UIHelper;

public class SildeView extends View {


    private Context mContext;

    private DisplayMetrics dm;
    private int mWidth;
    private int mHeight;


    private Bitmap bitmap;
    private Paint bitmapPaint;//画线的画笔
    private Rect mDestRect;
    private int mBitWidth;
    private int mBitHeight;

    private int position;
    private int top = 10;


    private Paint linePaint;
    private Paint linePaint2;

    private Path path1;
    private Path path2;


    private int lift;
    private int right;


    private int bitmapLift;
    private int bitmapRight;


    private int liftStartX;
    private int liftStopX;

    private int rightStartX;
    private int rightStopX;


    private int rightoveindex;

    private int liftMoveindex=20;

    public SildeView(Context context) {
        super(context);
        mContext = context;
        init();
        initLint();
    }

    public SildeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
        initLint();
    }

    public SildeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
        initLint();
    }


    private void init() {

//30

        dm = getResources().getDisplayMetrics();
        mHeight = dm.heightPixels;
        mWidth = dm.widthPixels;

        lift = UIHelper.dip2px(mContext, 50);
        right = mWidth - UIHelper.dip2px(mContext, 50);


        Log.i("Log", "right:============" + right);

        bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.jingbi);
        mBitWidth = bitmap.getWidth();
        mBitHeight = bitmap.getHeight();
        mDestRect = new Rect(0, 0, mBitWidth, mBitHeight);
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);//抗锯齿功能


        bitmapLift = lift - (mBitWidth / 2);
        bitmapRight = right - (mBitWidth / 2);

        position = bitmapLift;
    }

    private void initLint() {

        linePaint = new Paint();
        linePaint.setColor(Color.RED);  //设置画笔颜色
        linePaint.setStrokeWidth(15);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setAntiAlias(true);


        linePaint2 = new Paint();
        linePaint2.setColor(Color.RED);  //设置画笔颜色
        linePaint2.setStrokeWidth(15);
        linePaint2.setStyle(Paint.Style.FILL);
        linePaint2.setAntiAlias(true);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, position, UIHelper.dip2px(mContext, top), bitmapPaint);


        canvas.drawLine(lift, 20 -liftMoveindex, lift, 50 - liftMoveindex, linePaint);

        canvas.drawLine(right, 20 - rightoveindex, right, 50 - rightoveindex, linePaint);


    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 获得当前按下的 X轴坐标

                int downX = (int) ev.getRawX();
                //   int a = inPosition(downX);
                //   position = a;


                position = downX;


                postInvalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getRawX();
                // int b = inPosition(moveX);
                //position = b;

                if (moveX < bitmapLift) {
                    moveX = bitmapLift;
                }

                if (moveX > bitmapRight) {
                    moveX = bitmapRight;
                }

                position = moveX;

                int shiftRight = Math.abs(position - bitmapRight);

                int shiftLift = Math.abs(position - bitmapLift);

                if (shiftRight <= 200) {
                    rightoveindex = (200 - shiftRight) / 10;
                }

                if (shiftLift <= 200) {
                    liftMoveindex = (200 - shiftLift) / 10;
                }


                Log.i("Log", "position:" + position + "    lift：" + lift + "    shiftLift：===" + shiftLift);

                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

}
