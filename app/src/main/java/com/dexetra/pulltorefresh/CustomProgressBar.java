package com.dexetra.pulltorefresh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

public class CustomProgressBar extends View {
	private int mColor = Color.parseColor("#33b5e5");
	private Paint mLoadingPaint;
	private int mProgress = 0;
	private boolean mIsLoading = false;
	private int mSeparatorLength = 10;
	private int mSectionsCount = 5;
	private Interpolator mInterpolator;
	private float mCurrentOffset;
	private float mMaxOffset;
	private float mSpeed = 1.0f;
	private final static float OFFSET_PER_FRAME = 0.01f;

	public CustomProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomProgressBar(Context context) {
		super(context);
	}

	public void startLoading() {
		Log.d("Scroll", "startLoading");
		mIsLoading = true;
		mProgress = 0;
		invalidate();
	}

	public void stopLoading() {
		Log.d("Scroll", "stopLoading");
		mIsLoading = false;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mLoadingPaint = new Paint();
		mLoadingPaint.setAntiAlias(true);
		mLoadingPaint.setDither(true);
		mLoadingPaint.setColor(mColor);
		mLoadingPaint.setStyle(Style.STROKE);
		mInterpolator = new AccelerateInterpolator();
		mMaxOffset = 1f / mSectionsCount;
		mCurrentOffset %= mMaxOffset;
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mLoadingPaint.setStrokeWidth(getMeasuredHeight());
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mProgress > 0 && mProgress <= 100 && !mIsLoading) {
			int width = getWidth() * mProgress / 100;
			int left = (getWidth() - width) / 2;
			canvas.save();
			canvas.drawLine(left, getHeight() / 2, left + width,
					getHeight() / 2, mLoadingPaint);
			canvas.restore();
			if (mProgress == 100)
				startLoading();
		} else if (mIsLoading) {
			drawStrokes(canvas);
			mUpdater.run();
		}
	}

	public void setProgress(int progress) {
		mProgress = progress;
		invalidate();
	}

	private void drawStrokes(Canvas canvas) {
		float prevValue = 0f;
		int boundsWidth = getWidth();
		int width = boundsWidth + mSeparatorLength + mSectionsCount;
		float xSectionWidth = 1f / mSectionsCount;

		float prev;
		float end;
		float spaceLength;
		float xOffset;
		float ratioSectionWidth;
		float sectionWidth;
		float drawLength;
		for (int i = 0; i <= mSectionsCount; ++i) {
			xOffset = xSectionWidth * i + mCurrentOffset;
			prev = Math.max(0f, xOffset - xSectionWidth);
			ratioSectionWidth = Math.abs(mInterpolator.getInterpolation(prev)
					- mInterpolator.getInterpolation(Math.min(xOffset, 1f)));
			sectionWidth = (int) (width * ratioSectionWidth);

			if (sectionWidth + prev < width)
				spaceLength = Math.min(sectionWidth, mSeparatorLength);
			else
				spaceLength = 0f;

			drawLength = sectionWidth > spaceLength ? sectionWidth
					- spaceLength : 0;
			end = prevValue + drawLength;
			if (end > prevValue) {
				drawLine(canvas, boundsWidth, Math.min(boundsWidth, prevValue),
						getHeight() / 2, Math.min(boundsWidth, end),
						getHeight() / 2);
			}
			prevValue = end + spaceLength;
		}
	}

	private void drawLine(Canvas canvas, int canvasWidth, float startX,
			float startY, float stopX, float stopY) {
		canvas.drawLine(startX, startY, stopX, stopY, mLoadingPaint);
	}

	private final Runnable mUpdater = new Runnable() {

		@Override
		public void run() {
			if (!mIsLoading)
				return;
			mCurrentOffset += (OFFSET_PER_FRAME * mSpeed);
			if (mCurrentOffset >= mMaxOffset) {
				mCurrentOffset -= mMaxOffset;
			}
			invalidate();
		}
	};
}
