package me.yluo.testkeyboard.keboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import me.yluo.testkeyboard.keboard.util.DimmenUtils;
import me.yluo.testkeyboard.keboard.util.ViewUtil;


public class SmileyInputRoot extends LinearLayout {

    private int mOldHeight = -1;
    private int mStatusBarHeight;
    private PanelViewRoot mPanelLayout;
    private boolean mIsTranslucentStatus;
    private static final String TAG = "KPSRootLayoutHandler";


    public SmileyInputRoot(Context context) {
        super(context);
        init();
    }

    public SmileyInputRoot(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SmileyInputRoot(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.mStatusBarHeight = ViewUtil.getStatusBarHeight(getContext());
        final Activity activity = (Activity) getContext();
        this.mIsTranslucentStatus = ViewUtil.isTranslucentStatus(activity);

        mPanelLayout = new PanelViewRoot(activity);
        mPanelLayout.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimmenUtils.dip2px(activity, 200)));
        mPanelLayout.setBackgroundColor(Color.parseColor("#fffefefe"));
        mPanelLayout.setVisibility(GONE);
        addView(mPanelLayout);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        handleBeforeMeasure(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 记录总高度
        int mTotalHeight = 0;
        // 遍历所有子视图
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (!(childView instanceof PanelViewRoot)) {
                // 获取在onMeasure中计算的视图尺寸
                int measureHeight = childView.getMeasuredHeight();
                int measuredWidth = childView.getMeasuredWidth();
                childView.layout(l, mTotalHeight, measuredWidth, mTotalHeight + measureHeight);
                mTotalHeight += measureHeight;
            }
        }

        if (mPanelLayout != null) {
            mPanelLayout.layout(l, mTotalHeight, r, b);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void handleBeforeMeasure(final int width, int height) {
        // 由当前布局被键盘挤压，获知，由于键盘的活动，导致布局将要发生变化。
        if (mIsTranslucentStatus) {
            if (getFitsSystemWindows()) {
                // In this case, the height is always the same one, so, we have to calculate below.
                final Rect rect = new Rect();
                getWindowVisibleDisplayFrame(rect);
                height = rect.bottom - rect.top;
            }
        }

        Log.d(TAG, "onMeasure, width: " + width + " height: " + height);
        if (height < 0) {
            return;
        }

        if (mOldHeight < 0) {
            mOldHeight = height;
            return;
        }

        final int offset = mOldHeight - height;

        if (offset == 0) {
            Log.d(TAG, "" + offset + " == 0 break;");
            return;
        }

        if (Math.abs(offset) == mStatusBarHeight) {
            Log.w(TAG, String.format("offset just equal statusBar height %d", offset));
            return;
        }

        mOldHeight = height;

        if (mPanelLayout == null) {
            Log.w(TAG, "can't find the valid panel conflict layout, give up!");
            return;
        }

        // 检测到布局变化非键盘引起
        if (Math.abs(offset) < DimmenUtils.dip2px(getContext(), 80)) {
            Log.w(TAG, "system bottom-menu-bar(such as HuaWei Mate7) causes layout changed");
            return;
        }

        if (offset > 0) {
            //键盘弹起 (offset > 0，高度变小)
            mPanelLayout.handleHide();
        } else if (mPanelLayout.isKeyboardShowing()) {
            //在Android L下使用V7.Theme.AppCompat主题，并且不使用系统的ActionBar/ToolBar，V7.Theme.AppCompat主题,还是会先默认绘制一帧默认ActionBar，然后再将他去掉（略无语）
            //键盘收回 (offset < 0，高度变大)
            if (mPanelLayout.isVisible()) {
                // the panel is showing/will showing
                mPanelLayout.handleShow();
            }
        }
    }

    public PanelViewRoot getmPanelLayout() {
        return mPanelLayout;
    }
}
