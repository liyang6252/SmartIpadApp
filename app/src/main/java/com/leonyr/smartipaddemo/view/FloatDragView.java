package com.leonyr.smartipaddemo.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.leonyr.lib.utils.ConvertUtil;
import com.leonyr.lib.utils.LogUtil;
import com.leonyr.lib.utils.ScreenUtil;
import com.leonyr.smartipaddemo.R;


public class FloatDragView {

    public static final String TAG = "FloatDragView";

    private final Activity context; // 上下文
    private ImageView mImageView; // 可拖动按钮
    private int mScreenWidth = -1; //屏幕的宽度
    private int mScreenHeight = -1; //屏幕的高度
    private int relativeMoveX; // 控件相对屏幕左上角移动的位置X
    private int relativeMoveY; // 控件相对屏幕左上角移动的位置Y
    private boolean isIntercept = false; // 是否截断touch事件
    private int startDownX; // 按下时的位置控件相对屏幕左上角的位置X
    private int startDownY; // 按下时的位置控件距离屏幕左上角的位置Y
    private static int[] lastPosition; // 用于记录上一次的位置(坐标0对应x,坐标1对应y)

    private int nowX = 0, nowY = 0;

    /**
     * @param context        上下文
     * @param mViewContainer 可拖动按钮要存放的对应的Layout
     * @param clickListener  可拖动按钮的点击事件
     */
    public static FloatDragView addFloatDragView(Activity context, RelativeLayout mViewContainer, Bitmap bitmap,
                                                 View.OnClickListener clickListener) {
        FloatDragView floatDragView = new FloatDragView(context);
        ImageView imageView = floatDragView.getFloatDragView(bitmap, mViewContainer, clickListener);
        mViewContainer.addView(imageView);
        return floatDragView;
    }

    private FloatDragView(Activity context) {
        this.context = context;
        lastPosition = new int[]{0, 0};
        setScreenHW();
    }

    public Bitmap getBitmap() {
        Bitmap reBitmap = null;
        if (null != mImageView) {
            mImageView.setDrawingCacheEnabled(true);
            reBitmap = Bitmap.createBitmap(mImageView.getDrawingCache());
            mImageView.setDrawingCacheEnabled(false);
        }
        return reBitmap;
    }

    public void setBitMap(Bitmap bitMap){
        mImageView.setImageBitmap(bitMap);
    }

    public int getPosX() {
        if (nowX == 0){
            int[] position = new int[2];
            mImageView.getLocationOnScreen(position);
            nowX = position[0];
            nowY = position[1];
        }
        return nowX;
    }

    public int getPosY() {
        if (nowY == 0){
            int[] position = new int[2];
            mImageView.getLocationOnScreen(position);
            nowX = position[0];
            nowY = position[1];
        }
        return nowY;
    }

    // 获取可拖动按钮的实例
    private ImageView getFloatDragView(Bitmap bitmap, RelativeLayout mViewContainer, View.OnClickListener clickListener) {
        if (mImageView != null) {
            mImageView.setImageBitmap(bitmap);
            return mImageView;
        } else {
            mImageView = new ImageView(context);
            mImageView.setClickable(true);
            mImageView.setFocusable(true);
            mImageView.setImageBitmap(bitmap);
//            mImageView.setBackgroundColor(Color.RED);
            mImageView.setOnClickListener(clickListener);

            mScreenHeight = Math.min(mViewContainer.getHeight(), mScreenHeight);

            setFloatDragViewParams(mImageView);
            setFloatDragViewTouch(mImageView);
            return mImageView;
        }
    }

    public ImageView getImageView() {
        return mImageView;
    }

    // 设置可拖动按钮的位置参数
    private void setFloatDragViewParams(View floatDragView) {
        // 记录最后图片在窗体的位置
        int moveX = lastPosition[0];
        int moveY = lastPosition[1];
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        if (0 != moveX || 0 != moveY) {
            // 移动后的位置
            // 每次移动都要设置其layout，不然由于父布局可能嵌套listView，
            // 当父布局发生改变冲毁（如下拉刷新时）则移动的view会回到原来的位置
            params.setMargins(moveX, moveY, 0, 0);
            floatDragView.setLayoutParams(params);
        } else {
            // 初始位置
            params.setMargins(0, 0, 0, ConvertUtil.dp2px(100));
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }

        floatDragView.setLayoutParams(params);

        int[] post = new int[2];
        floatDragView.getLocationOnScreen(post);
        LogUtil.e("x:"+ post);
    }

    // 可拖动按钮的touch事件
    @SuppressLint("ClickableViewAccessibility")
    private void setFloatDragViewTouch(final ImageView floatDragView) {
        floatDragView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        isIntercept = false;
                        startDownX = relativeMoveX = (int) event.getRawX();
                        startDownY = relativeMoveY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - relativeMoveX;
                        int dy = (int) event.getRawY() - relativeMoveY;

                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;

                        if (left < 0) {
                            left = 0;
                            right = left + v.getWidth();
                        }

                        if (right > mScreenWidth) {
                            right = mScreenWidth;
                            left = right - v.getWidth();
                        }

                        if (top < 0) {
                            top = 0;
                            bottom = top + v.getHeight();
                        }

                        if (bottom > mScreenHeight) {
                            bottom = mScreenHeight;
                            top = bottom - v.getHeight();
                        }

                        v.layout(left, top, right, bottom);
                        relativeMoveX = (int) event.getRawX();
                        relativeMoveY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_UP:
                        int lastMoveDx = Math.abs((int) event.getRawX() - startDownX);
                        int lastMoveDy = Math.abs((int) event.getRawY() - startDownY);

                        // 防止点击的时候稍微有点移动点击事件被拦截了
                        isIntercept = 5 < lastMoveDx || 5 < lastMoveDy;
                        // 每次移动都要设置其layout，不然由于父布局可能嵌套listview，
                        // 当父布局发生改变冲毁（如下拉刷新时）则移动的view会回到原来的位置
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);

                        nowX = v.getLeft();
                        nowY = v.getTop();
                        params.setMargins(nowX, nowY, 0, 0);
                        v.setLayoutParams(params);
                        // 设置靠近边沿的
//                        setImageViewNearEdge(v);
                        break;
                }

                return isIntercept;
            }
        });
    }

    // 将拖动按钮移动到边沿
    private void setImageViewNearEdge(final View v) {
        if (v.getLeft() < ((mScreenWidth) / 2)) {
            // 设置位移动画 向左移动控件位置
            TranslateAnimation animation = new TranslateAnimation(0, -v.getLeft(), 0, 0);
            animation.setDuration(300);// 设置动画持续时间
            animation.setRepeatCount(0);// 设置重复次数
            animation.setFillAfter(true);
            animation.setRepeatMode(Animation.ABSOLUTE);
            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation arg0) {
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    v.clearAnimation();
                    RelativeLayout.LayoutParams lpFeedback = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lpFeedback.setMargins(0, v.getTop(), 0, 0);
                    v.setLayoutParams(lpFeedback);
                    v.postInvalidateOnAnimation();
                    lastPosition[0] = 0;
                    lastPosition[1] = v.getTop();
                }
            });
            v.startAnimation(animation);
        } else {
            int toXDelta = mScreenWidth - v.getLeft() - v.getWidth();
            TranslateAnimation animation = new TranslateAnimation(0, toXDelta, 0, 0);
            animation.setDuration(300);// 设置动画持续时间
            animation.setRepeatCount(0);// 设置重复次数
            animation.setRepeatMode(Animation.ABSOLUTE);
            animation.setFillAfter(true);
            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation arg0) {
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    v.clearAnimation();
                    int left = mScreenWidth - v.getWidth();
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(left, v.getTop(), 0, 0);
                    v.setLayoutParams(lp);
                    v.postInvalidateOnAnimation();
                    lastPosition[0] = left;
                    lastPosition[1] = v.getTop();
                }
            });
            v.startAnimation(animation);
        }
    }

    // 计算屏幕的实际高宽
    private void setScreenHW() {
//        Point screen = Utils.getScreenSize(context);
        mScreenWidth = ScreenUtil.getScreenWidth();
        mScreenHeight = ScreenUtil.getScreenHeight();
        //  减去状态栏高度，否则挨着底部移动，导致图标变小
        //  mScreenHeight = screen.y - Utils.getStatusBarHeight(context);
    }

    public void reset() {
        lastPosition = new int[]{0, 0};
        setScreenHW();
        setFloatDragViewParams(mImageView);
    }

}
