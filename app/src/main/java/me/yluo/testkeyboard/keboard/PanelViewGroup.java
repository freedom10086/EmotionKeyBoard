
package me.yluo.testkeyboard.keboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import me.yluo.testkeyboard.keboard.util.ViewUtil;


public class PanelViewGroup extends FrameLayout{

    private boolean mIsHide = false;
    private boolean mIsKeyboardShowing = false;
    private SmileyView smileyView;

    public PanelViewGroup(Context context) {
        super(context);
        init(null);
    }

    public PanelViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public PanelViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(final AttributeSet attrs) {
        smileyView = new SmileyView(getContext());
        smileyView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addView(smileyView);
    }

    public void refreshHeight(int panelHeight) {
        ViewUtil.refreshHeight(this, panelHeight);
    }

    public void onKeyboardShowing(boolean showing) {
        mIsKeyboardShowing = showing;
    }

    public boolean isKeyboardShowing() {
        return mIsKeyboardShowing;
    }

    @Override
    public void setVisibility(int visibility) {
        if (filterSetVisibility(visibility)) {
            return;
        }
        super.setVisibility(visibility);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int[] processedMeasureWHSpec = processOnMeasure(widthMeasureSpec, heightMeasureSpec);

        super.onMeasure(processedMeasureWHSpec[0], processedMeasureWHSpec[1]);
    }

    public boolean isVisible() {
        return !mIsHide;
    }


    public void handleShow() {
        super.setVisibility(View.VISIBLE);
    }

    public void handleHide() {
        this.mIsHide = true;
    }


    /**
     * @return whether filtered out or not.
     */
    public boolean filterSetVisibility(final int visibility) {
        if (visibility == View.VISIBLE) {
            this.mIsHide = false;
        }

        if (visibility == getVisibility()) {
            return true;
        }

        return isKeyboardShowing() && visibility == View.VISIBLE;

    }

    private final int[] processedMeasureWHSpec = new int[2];

    /**
     * Handle Panel -> Keyboard.
     * for handling the case of Panel->Keyboard.
     */
    public int[] processOnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mIsHide) {
            setVisibility(View.GONE);
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY);
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY);
        }

        processedMeasureWHSpec[0] = widthMeasureSpec;
        processedMeasureWHSpec[1] = heightMeasureSpec;

        return processedMeasureWHSpec;
    }


}
