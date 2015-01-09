package com.appxy.pocketexpensepro.entity;


import com.appxy.pocketexpensepro.expinterface.AgendaListenerInterface;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.AbsListView.OnScrollListener;

public class LoadMoreListView extends ExpandableListView implements OnScrollListener {

	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	private AgendaListenerInterface mListViewListener;

	private boolean mEnablePullLoad;
	private boolean mEnablePullRefresh;

	private boolean mPullRefreshing;
	private boolean mPullLoading;

	private int mTotalItemCount;

	private boolean mIsUp;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;

	/**
	 * @param context
	 */
	public LoadMoreListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public LoadMoreListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public LoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		// XListView need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		super.setOnScrollListener(this);

	}

	/**
	 * enable or disable pull up load more feature.
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
		} else {
			mPullLoading = false;
			// both "pull up" and "click" will invoke load more.
		}
	}

	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) {
		} else {
			mPullRefreshing = false;
		}
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
		}
	}

	/**
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
		}
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnAgendaScrollListener) {
			OnAgendaScrollListener l = (OnAgendaScrollListener) mScrollListener;
			l.onAgendaScrolling(this);
		}
	}

	private void startLoadMore() {
		mPullLoading = true;
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}

	private void startRefresh() {

		mPullRefreshing = true;
		if (mListViewListener != null) {
			mListViewListener.onRefresh();
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			Log.v("mtest", mLastY + "ssss");
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			Log.v("mtest", "move");
			if (getFirstVisiblePosition() == 0 && (deltaY > 0)) {

				mIsUp = true;

				invokeOnScrolling();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& (deltaY < 0)) {
				if (mEnablePullLoad) {
					startLoadMore();
					Log.v("mtest", "aaaload");
				}
			}
			break;
		default:
			Log.v("mtest", "default" + getLastVisiblePosition()
					+ "lastposition" + getFirstVisiblePosition());
			mLastY = -1; // reset
			if (getFirstVisiblePosition() == 0) {
				if (mIsUp && mEnablePullRefresh) {
					mIsUp = false;
					startRefresh();
					Log.v("mtest", "bbbbrefresh");
				}
			}
			if (getLastVisiblePosition() == mTotalItemCount - 1) {
//				if (mTotalItemCount < 25) {
//
//				} else {
//
//					if (mEnablePullLoad) {
//						startLoadMore();
//						Log.v("mtest", "aaaload");
//					}
//				}
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {

			} else {

			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// send to user's listener
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
		Log.v("mtest", mTotalItemCount + "count");
	}

	public void setXListViewListener(AgendaListenerInterface l) {
		mListViewListener = l;
	}

}
