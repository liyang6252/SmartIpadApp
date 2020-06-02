package com.leonyr.smartipaddemo.view.pen;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.leonyr.smartipaddemo.R;

/**
 * Author:Double
 * Time:2019/4/22
 * Description:This is StrokesView
 */
public class StrokesView extends View {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //笔划
    private Strokes strokes;
    private DrawingStrokes mDrawing;
    private PenType penType;

    private int penColor = Color.BLACK;
    private int penWidth = 2;
    private int brushWidth = 8;

    public StrokesView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
        strokes = new Strokes();
        mPaint.setColor(penColor);
        mDrawing = new DrawingStrokes(this, strokes);
        mDrawing.setMaxWidth(penWidth);//刚笔
        penType = new PenType();
        penType.setPenType(PenType.PEN);
        mDrawing.setPenType(penType);

    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StrokesView);
        penColor = a.getColor(R.styleable.StrokesView_paint_color, Color.BLACK);
        penWidth = a.getInt(R.styleable.StrokesView_paint_pen_width, 2);
        brushWidth = a.getInt(R.styleable.StrokesView_paint_brush_width, 8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawing != null) {
            mDrawing.setSize(canvas.getWidth(), canvas.getHeight(), mPaint);
            mDrawing.draw(canvas, mPaint);
        }
    }

    public Bitmap getBitmap() {
       return mDrawing.myBitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mDrawing.moveTo(event.getX(), event.getY(), event.getPressure());
        } else if (action == MotionEvent.ACTION_MOVE) {
            int historySize = event.getHistorySize();
            for (int i = 0; i < historySize; i++) {
                float historicalX = event.getHistoricalX(i);
                float historicalY = event.getHistoricalY(i);
                //判断两点之间的距离是否太短
                double distance = Math.sqrt((historicalX - mDrawing.mPoint.get(mDrawing.mPoint.size() - 1).getX())
                        * (historicalX - mDrawing.mPoint.get(mDrawing.mPoint.size() - 1).getX())
                        + (historicalY - mDrawing.mPoint.get(mDrawing.mPoint.size() - 1).getY())
                        * (historicalY - mDrawing.mPoint.get(mDrawing.mPoint.size() - 1).getY()));
                if (mDrawing.mPoint.size() > 0 && distance > 0.2)
                    mDrawing.lineTo(historicalX, historicalY, event.getHistoricalPressure(i), false);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            mDrawing.lineTo(event.getX(), event.getY(), event.getPressure(), true);
        }
        invalidate();
        return true;
    }

    public void setPenWidth(int penWidth) {
        switch (penType.getPenType()) {
            case PenType.BRUSH:
                brushWidth = penWidth;
                mDrawing.setMaxWidth(brushWidth);
                break;
            case PenType.PEN:
                this.penWidth = penWidth;
                mDrawing.setMaxWidth(penWidth);
                break;
        }
    }

    public boolean isDrawed() {
        return strokes.getMyPathList().size() > 0;
    }

    public void setPenColor(int penColor) {
        this.penColor = penColor;
        mPaint.setColor(this.penColor);
        mDrawing.setPaint(mPaint);
    }

    public void reDo() {
        mDrawing.reDo();
    }

    public void unDo() {
        mDrawing.unDo();
    }

    public void recover() {
        mDrawing.recover();
    }

    public void setPenType(int penType) {
        this.penType.setPenType(penType);
        switch (penType) {
            case PenType.BRUSH:
                mDrawing.setMaxWidth(brushWidth);//毛笔
                break;
            case PenType.PEN:
                mDrawing.setMaxWidth(penWidth);//刚笔
                break;
        }
    }

    public void onDestroy() {
        mDrawing.onDestroy();
    }
}

