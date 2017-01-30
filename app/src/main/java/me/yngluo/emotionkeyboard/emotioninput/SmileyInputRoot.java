package me.yngluo.emotionkeyboard.emotioninput;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import me.yngluo.emotionkeyboard.utils.DimmenUtils;


public class SmileyInputRoot extends LinearLayout {

    private int mOldHeight = -1;
    private SmileyContainer mSmileyContainer;
    private int maxHeight = 100;


    public SmileyInputRoot(Context context) {
        super(context);
        init();
    }

    public SmileyInputRoot(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SmileyInputRoot(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        final Activity activity = (Activity) getContext();
        mSmileyContainer = new SmileyContainer(activity);
        mSmileyContainer.setBackgroundColor(Color.parseColor("#fffefefe"));
        mSmileyContainer.setVisibility(GONE);
        addView(mSmileyContainer);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        Log.e("root onMeasure", "height is:" + height);

        if (height > maxHeight) {
            maxHeight = height;
        }

        if (height < 200 && mOldHeight == height) {
            return;
        }

        //测得的时键盘未展开的高度
        if (mOldHeight == -1) {
            mOldHeight = height;
            return;
        }

        final int offset = mOldHeight - height;
        mOldHeight = height;

        // 检测到布局变化非键盘引起
        if (Math.abs(offset) < DimmenUtils.dip2px(getContext(), 180)) {
            return;
        }

        // offset > 0 键盘弹起了
        if (mSmileyContainer != null)
            mSmileyContainer.onMainViewSizeChange(offset);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e("=========", changed + "||" + t + "||" + b);

        if (!changed
                && (maxHeight > (b - t))
                && mSmileyContainer.isVisible
                && mSmileyContainer.isKeyboardShowing) {
            Log.e("=========", "return");
            return;
        }
        int childTop = 0;
        // 遍历所有子视图
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                if (child instanceof SmileyContainer) {
                    continue;
                }

                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();

                final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
                childTop += lp.topMargin;

                child.layout(l, childTop, l + childWidth + lp.leftMargin, childTop + childHeight);
                childTop += childHeight + lp.bottomMargin;
            }
        }

        if (mSmileyContainer != null && mSmileyContainer.getVisibility() != GONE) {
            Log.e("onLayout", "mSmileyContainer layout");
            mSmileyContainer.layout(l, childTop, r, b);
        }
    }


    public SmileyContainer getmSmileyContainer() {
        return mSmileyContainer;
    }
}
