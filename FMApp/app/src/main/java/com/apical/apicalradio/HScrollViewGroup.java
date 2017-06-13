package com.apical.apicalradio;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.Scroller;

public class HScrollViewGroup extends ViewGroup {

	private static final String TAG = "HScrollViewGroup_dzt";
	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
	private static final int SNAP_VELOCITY = 400;// 滑动视图的速率
	private static final int INTERVAL = 13; // 每次滑动的间隔

	private Scroller mScroller; // 滑动控件
	private VelocityTracker mVelocityTracker; // 速度追踪器
	private Direction direction = Direction.NONE;

	private int mCurScreen; // 记录当前页
	private int mDefaultScreen = 0; // 默认页
	private int mTouchState = TOUCH_STATE_REST;// 设置触发状态
	private int mTouchSlop; // 触发移动的像素距离
	private float mLastMotionX; // 手指触碰屏幕的最后一次x坐标
	private float mLastMotionY; // 手指触碰屏幕的最后一次y坐标
	private int mTotalPage; // 总页数
	private int mMaxWidth; // 所有子控件加起来的总宽度
	private int mWidth; // 每个子控件的宽度
	private int mCtrlWidth = 0;
	private int mRemainder; // 总宽度除以每页的余数
	private int mMoveCount; // 移动计数器
	int[] mScreens = new int[5];// 每页的最前一个坐标

	public HScrollViewGroup(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public HScrollViewGroup(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public HScrollViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context) {
		mScroller = new Scroller(context);
		mCurScreen = mDefaultScreen;// 默认设置显示第一个VIEW
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	/**
	 * 父类为子类在屏幕上分配实际的宽度和高度,里面的四个参数分别表示，布局是否发生改变，布局左 上右下的边距
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onLayout changed = " + changed);
		if (changed) {
			int childLeft = 0;
			final int childCount = getChildCount();

			Log.d(TAG, "childCount = " + childCount);
			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					if (childCount > 5
							&& ((i != 0) && ((i % 4 == 0)) || (i == childCount - 1))) {
						childView.layout(childLeft, 0, childLeft
								+ (mWidth + mRemainder),
								childView.getMeasuredHeight());
						childLeft += (mWidth + mRemainder);
					} else {
						childView.layout(childLeft, 0, childLeft + mWidth,
								childView.getMeasuredHeight());
						childLeft += mWidth;
					}

					Log.d(TAG, "childLeft=" + childLeft + " childWidth="
							+ mWidth);
				} else {
					// 已经隐藏
				}
			}
			calculateScreens();
		}
	}

	void calculateScreens() {
		int childLeft = 0;
		int viewWidth = getWidth();

		int curPage = 0;
		mScreens[curPage] = childLeft;
		++curPage;

		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View childView = getChildAt(i);
			if (childView.getVisibility() != View.GONE) {
				if (childCount > 5
						&& ((i != 0) && ((i % 4 == 0)) || (i == childCount - 1))) {
					childLeft += (mWidth + mRemainder);
					if (childLeft > (viewWidth)) {
						mScreens[curPage] = childLeft - (mWidth + mRemainder)
								+ mScreens[curPage - 1];
						++curPage;
						childLeft = (mWidth + mRemainder);
					}
				} else {
					childLeft += mWidth;
					if (childLeft > (viewWidth)) {
						mScreens[curPage] = childLeft - mWidth
								+ mScreens[curPage - 1];
						++curPage;
						childLeft = mWidth;
					}
				}

			}
			Log.d(TAG, "childLeft = " + childLeft);
		}

		if (childLeft != 0 && curPage > 1) {
			mScreens[curPage - 1] = mScreens[curPage - 1] + childLeft
					- viewWidth;
		}
	}

	/**
	 * MeasureSpec类的静态方法getMode和getSize来译解。一个MeasureSpec包含一个尺寸和模式。
	 * 
	 * 有三种可能的模式：
	 * 
	 * UNSPECIFIED：父布局没有给子布局任何限制，子布局可以任意大小。
	 * EXACTLY：父布局决定子布局的确切大小。不论子布局多大，它都必须限制在这个界限里
	 * 。(当布局定义为一个固定像素或者fill_parent时就是EXACTLY模式)
	 * AT_MOST：子布局可以根据自己的大小选择任意大小。(当布局定义为wrap_content时就是AT_MOST模式)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int height = MeasureSpec.getSize(heightMeasureSpec);
		if (mCtrlWidth != width) {
			mCtrlWidth = width;
			mWidth = width / 5;
			mRemainder = width % 5;

			// The children are given the same width and height as the
			// scrollLayout
			final int count = getChildCount();
			for (int i = 0; i < count; i++) {
				if (count > 5
						&& ((i != 0) && ((i % 4 == 0)) || (i == count - 1))) {
					getChildAt(i).measure((mWidth + mRemainder),
							heightMeasureSpec);
				} else {
					getChildAt(i).measure(mWidth, heightMeasureSpec);
				}
			}
			mMaxWidth = (getChildCount() * mWidth) + mRemainder;
			mTotalPage = mMaxWidth / width;
			snapToScreen(mCurScreen);
			mScroller.abortAnimation();
			Log.d(TAG, "mTotalPage = " + mTotalPage + " width = " + width
					+ " height = " + height + " count = " + count
					+ " mCurScreen = " + mCurScreen);
		}
	}

	/**
	 * 根据滑动的距离判断移动到第几个视图
	 */
	public void snapToDestination() {
		final int screenWidth = getWidth();
		final int scrollX = getScrollX() > mMaxWidth ? mMaxWidth : getScrollX();
		final int destScreen = (scrollX + screenWidth / 2) / screenWidth;
		Log.d(TAG, "screenWidth = " + screenWidth + " destScreen = "
				+ destScreen + " scrollx = " + scrollX);
		snapToScreen(destScreen);
	}

	/**
	 * 滚动到制定的视图
	 * 
	 * @param whichScreen
	 *            视图下标
	 */
	public void snapToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {

			// final int delta = whichScreen * getWidth() - getScrollX();
			final int delta = mScreens[mCurScreen] - getScrollX();
			Log.d(TAG, "snapToScreen-whichScreen = " + whichScreen
					+ " delta = " + delta + " scrollX = " + getScrollX());
			mScroller.startScroll(getScrollX(), 0, delta, 0, 2000);
			mCurScreen = whichScreen;
			mMoveCount = getScrollX();
			invalidate();
		}
	}

	public void setDirection(Direction dir) {
		direction = dir;
	}

	public int getCurScreen() {
		return mCurScreen;
	}

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (mScroller.computeScrollOffset()) {

			if (direction == Direction.LEFT) {
				Log.d(TAG, "left mScreens[mCurScreen] = "
						+ mScreens[mCurScreen]);
				mMoveCount -= INTERVAL;
				if (mMoveCount < 0) {
					mMoveCount = 0;
					mScroller.abortAnimation();
				}
				scrollTo(mMoveCount, mScroller.getCurrY());
			} else if (direction == Direction.RIGHT) {
				if (mScroller.getCurrX() <= mScreens[mCurScreen]) {
					Log.d(TAG, "right mScreens[mCurScreen] = "
							+ mScreens[mCurScreen]);
					mMoveCount += INTERVAL;
					if (mMoveCount > mScreens[mCurScreen]) {
						mMoveCount = mScreens[mCurScreen];
						mScroller.abortAnimation();
					}
					scrollTo(mMoveCount, mScroller.getCurrY());
				} else {
					scrollTo(mScreens[mCurScreen], mScroller.getCurrY());
					mScroller.abortAnimation();
				}
			} else {
				mScroller.forceFinished(true);
			}
			postInvalidate();
			Log.d(TAG, "computeScroll----mMoveCount = " + mMoveCount);
			Log.d(TAG, "computeScroll----x = " + mScroller.getCurrX());
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		/*
		 * if (!mScroller.isFinished()) { return false; }
		 */
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		Log.d(TAG, "-----------onTouchEvent");
		mVelocityTracker.addMovement(event);

		final int action = event.getAction();
		final float x = event.getX();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished()) {
				// mScroller.abortAnimation();
				Log.d(TAG, "-----------onTouchEvent---ACTION_DOWN no finish");
				return false;
			}
			mLastMotionX = x;
			Log.d(TAG, "down mLastMotionX = " + mLastMotionX);
			break;

		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);
			mLastMotionX = x;
			Log.d(TAG, "move scroll " + getScrollX() + " mCurScreen = "
					+ mCurScreen + " mTotalPage = " + mTotalPage + " deltaX = "
					+ deltaX);
			if (getScrollX() > 0 && mCurScreen < mTotalPage)
				scrollBy(deltaX, 0);
			break;

		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX = (int) velocityTracker.getXVelocity();

			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// 向左移动
				Log.d(TAG, "left mCurScreen = " + mCurScreen);
				direction = Direction.LEFT;
				snapToScreen(mCurScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY && mCurScreen < mTotalPage) {
				// 向右移动
				Log.d(TAG, "right mCurScreen = " + mCurScreen);
				direction = Direction.RIGHT;
				snapToScreen(mCurScreen + 1);
			} else {
				direction = Direction.NONE;
				snapToDestination();
			}
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return true;
	}

	/**
	 * 用于拦截手势事件的，每个手势事件都会先调用这个方法。Layout里的onInterceptTouchEvent默认返回值是false,
	 * 这样touch事件会传递到View控件
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}
		Log.d(TAG, "-----------onInterceptTouchEvent");
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(mLastMotionX - x);
			if (xDiff > mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;
			}
			break;

		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;

	}

	/**
	 * 滑动的方向
	 * 
	 * @author Administrator
	 * 
	 */
	public enum Direction {
		LEFT, RIGHT, NONE
	}
}
