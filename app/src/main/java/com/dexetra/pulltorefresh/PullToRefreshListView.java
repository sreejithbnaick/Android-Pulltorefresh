package com.dexetra.pulltorefresh;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by dexadmin on 11/9/13.
 */
public class PullToRefreshListView extends ListView {
	private boolean mTouchDown = false, mOverScroll = false,
			mRefreshing = false, mBottomOverScroll = false;
	private int mDist = 0, mLevel = 0, mLimit = -1;
	private PullToRefresh mPullToRefresh;
	private GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD
					&& !mRefreshing) {
				if (getFirstVisiblePosition() == 0 && distanceY < 0) {
					setSelection(0);
					mOverScroll = true;
					mOverScrollListener.onOverScrollStarted();
				}
			}
			if (distanceY > 0 && mOverScroll && !mRefreshing) {
				mDist -= (distanceY);
				if (mDist < 0)
					mDist = 0;
				if (mPullToRefresh != null)
					mPullToRefresh.onPull((mDist * 100 / mLimit));
			} else if (distanceY < 0 && mOverScroll && !mRefreshing) {
				mDist += (-distanceY);
				if (mDist > mLimit)
					mDist = mLimit;
				if (mPullToRefresh != null)
					mPullToRefresh.onPull((mDist * 100 / mLimit));
				if (mDist >= mLimit)
					setOverScrollDone();

			}
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	};
	private OnOverScrollListener mOverScrollListener = new OnOverScrollListener() {
		@Override
		public void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
				boolean clampedY) {
			if (getFirstVisiblePosition() == 0)
				mOverScroll = true;
			if (getAdapter() != null
					&& getLastVisiblePosition() == getAdapter().getCount() - 1
					&& !mBottomOverScroll
					&& getChildAt(getChildCount() - 1).getBottom() == getBottom()) {
				mBottomOverScroll = true;
				if (mOnScrollEndListener != null) {
					mOnScrollEndListener.onScrollEnd();
				}
			}
		}

		@Override
		public void onOverScrollStarted() {
			if (mPullToRefresh != null)
				mPullToRefresh.onPullStarted();
		}

		@Override
		public void onOverScollDone() {
			if (mPullToRefresh != null)
				mPullToRefresh.onRefresh();
		}

		@Override
		public void onReverseScroll(float distanceY) {
			mDist = 0;
			mLevel = 0;
			mOverScroll = false;
			/*
			 * if (mPullToRefresh != null) { mPullToRefresh.onCancel(); }
			 */
		}
	};
	private GestureDetector mGestureDetector;
	private OnScrollEndListener mOnScrollEndListener;

	public PullToRefreshListView(Context context) {
		super(context);
		init();
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PullToRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void setOverScrollDone() {
		mRefreshing = true;
		mDist = 0;
		mLevel = 0;
		if (mOverScrollListener != null && mOverScroll) {
			mOverScrollListener.onOverScollDone();
		}
		mOverScroll = false;
	}

	private void setOverScrollCancel() {
		mRefreshing = false;
		mDist = 0;
		mLevel = 0;
		if (mPullToRefresh != null && mOverScroll) {
			mPullToRefresh.onCancel();
		}
		mOverScroll = false;
	}

	private void init() {
		setScrollingCacheEnabled(false);
		mGestureDetector = new GestureDetector(getContext(), mOnGestureListener);
	}

	public void setPullToRefresh(int maxLimit, PullToRefresh pullToRefresh) {
		mLimit = maxLimit;
		mPullToRefresh = pullToRefresh;
	}

	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
			boolean clampedY) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
			super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
			if (mOverScrollListener != null && !mRefreshing) {
				if (scrollY == 0 && !mOverScroll && mTouchDown) {
					mOverScroll = true;
					mOverScrollListener.onOverScrollStarted();
				}
				mOverScrollListener.onOverScrolled(scrollX, scrollY, clampedX,
						clampedY);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mGestureDetector.onTouchEvent(ev);
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mBottomOverScroll = false;
			mTouchDown = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if (getChildCount() < 1)
				mOverScroll = true;
			else {
				if (getChildAt(0).getTop() == 0
						&& getFirstVisiblePosition() == 0)
					mOverScroll = true;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mBottomOverScroll = false;
			if (!mRefreshing && mOverScroll) {
				setOverScrollCancel();
			} else {
				mDist = 0;
				mLevel = 0;
				mOverScroll = false;
			}
			mTouchDown = false;
			break;
		}
		boolean ret = super.onTouchEvent(ev);
		return ret;
	}

	public void setRefreshDone() {
		mRefreshing = false;
		mDist = 0;
		mLevel = 0;
		mTouchDown = false;
		mOverScroll = false;
	}

	public void setOnScrollEndListener(OnScrollEndListener onScrollEndListener) {
		mOnScrollEndListener = onScrollEndListener;
	}

	public interface PullToRefresh {
		public void onPullStarted();

		public void onPull(int progress);

		public void onRefresh();

		public void onCancel();
	}

	public interface OnScrollEndListener {
		public void onScrollEnd();
	}

	private interface OnOverScrollListener {
		public void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
                                   boolean clampedY);

		public void onOverScrollStarted();

		public void onOverScollDone();

		public void onReverseScroll(float distanceY);
	}
}
