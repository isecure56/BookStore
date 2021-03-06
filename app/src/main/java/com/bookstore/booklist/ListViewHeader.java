package com.bookstore.booklist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bookstore.main.R;

/**
 * Created by Administrator on 2016/3/3.
 */
public class ListViewHeader extends FrameLayout {
    private static final int DISTANCE_BETWEEN_STRETCH_READY = 100;
    private LinearLayout mContainer;
    private int stretchHeight;
    private int readyHeight;
    private WaterDropView mWaterDropView;
    private ProgressBar mProgressBar;
    private STATE mHeaderViewState = STATE.NORMAL_STATE;
    private IStateChangedListener mStateChangedListener;

    public ListViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ListViewHeader(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.listview_header, null);
        mWaterDropView = (WaterDropView) mContainer.findViewById(R.id.listheader_waterdrop);
        mProgressBar = (ProgressBar) mContainer.findViewById(R.id.listheader_progressbar);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        addView(mContainer, lp);
        initHeight();
    }

    private void initHeight() {
        mContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                stretchHeight = mWaterDropView.getHeight();
                readyHeight = stretchHeight + DISTANCE_BETWEEN_STRETCH_READY;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    public int getStretchHeight() {
        return stretchHeight;
    }

    public int getReadyHeight() {
        return readyHeight;
    }

    public int getVisibleHeight() {
        return mContainer.getHeight();
    }

    public void setVisibleHeight(int height) {
        Log.i("csf", "setVisibleHeight " + height);
        if (height < 0) {
            height = 0;
        }
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
        if (mHeaderViewState == STATE.STRETCH_STATE) {
            float pullOffset = (float) mapValueFromRangeToRange(height, stretchHeight, readyHeight, 0, 1);
            if (pullOffset < 0 || pullOffset > 1) {
                throw new IllegalArgumentException("csf pullOffset should between 0 and 1!" + mHeaderViewState + " " + height + " " + stretchHeight + " " + readyHeight + " " + pullOffset);
            }
            Log.e("pullOffset", "pullOffset:" + pullOffset);
            mWaterDropView.updateCompleteState(pullOffset);
        }
    }

    public double mapValueFromRangeToRange(
            double value,
            double fromLow,
            double fromHigh,
            double toLow,
            double toHigh) {
        double fromRangeSize = fromHigh - fromLow;
        double toRangeSize = toHigh - toLow;
        double valueScale = (value - fromLow) / fromRangeSize;
        return toLow + (valueScale * toRangeSize);
    }

    public STATE getCurrentState() {
        return mHeaderViewState;
    }

    public void updateState(STATE state) {
        Log.i("csf", "updateState " + state);
        if (state == mHeaderViewState) return;
        STATE oldstate = mHeaderViewState;
        mHeaderViewState = state;
        if (mStateChangedListener != null) {
            mStateChangedListener.notifyStateChanged(oldstate, mHeaderViewState);
        }

        switch (mHeaderViewState) {
            case NORMAL_STATE:
                handleNormalState();
                break;
            case STRETCH_STATE:
                handleStretchState();
                break;
            case READY_STATE:
                handleReadyState();
                break;
            case REFRESHING_STATE:
                handleRefreshingState();
                break;
            case END_STATE:
                handleEndState();
                break;
            default:
                break;
        }
    }

    private void handleNormalState() {
        mWaterDropView.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mContainer.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    }

    private void handleStretchState() {
        mWaterDropView.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        mContainer.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
    }

    private void handleReadyState() {
        mWaterDropView.setVisibility(VISIBLE);
        mProgressBar.setVisibility(GONE);
        Animator shrinkAnimator = mWaterDropView.createAnimator();
        shrinkAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                updateState(STATE.REFRESHING_STATE);
            }
        });
        shrinkAnimator.start();
    }

    private void handleRefreshingState() {
        mWaterDropView.setVisibility(GONE);
        mProgressBar.setVisibility(VISIBLE);
    }

    private void handleEndState() {
        mWaterDropView.setVisibility(GONE);
        mProgressBar.setVisibility(GONE);
    }

    public void setStateChangedListener(IStateChangedListener listener) {
        mStateChangedListener = listener;
    }

    public enum STATE {
        NORMAL_STATE,
        STRETCH_STATE,
        READY_STATE,
        REFRESHING_STATE,
        END_STATE,
    }

    public interface IStateChangedListener {
        void notifyStateChanged(STATE oldState, STATE newState);
    }
}
