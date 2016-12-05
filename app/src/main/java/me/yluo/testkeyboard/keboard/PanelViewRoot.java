
package me.yluo.testkeyboard.keboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import me.yluo.testkeyboard.keboard.util.KeyboardUtil;
import me.yluo.testkeyboard.keboard.util.ViewUtil;


public class PanelViewRoot extends FrameLayout {

    private boolean mIsHide = false;
    private boolean mIsKeyboardShowing = false;
    private SmileyView smileyView;
    private List<MenuView> menus = new ArrayList<>();
    private EditText editText;

    public PanelViewRoot(Context context) {
        super(context);
        init(null);
    }

    public PanelViewRoot(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public PanelViewRoot(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(final AttributeSet attrs) {
        smileyView = new SmileyView(getContext());
        smileyView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addView(smileyView);
    }

    public void init(EditText editText, View v) {
        smileyView.setInputView(editText);
        this.editText = editText;
        MenuView menuView = new MenuView(smileyView, v);
        addMenuViews(menuView);
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


    public static class MenuView {
        final View subPanelView;
        final View triggerView;

        public MenuView(View subPanelView, View triggerView) {
            this.subPanelView = subPanelView;
            this.triggerView = triggerView;
        }
    }

    public void addMenuViews(MenuView... menuViews) {
        for (final MenuView m : menuViews) {
            menus.add(m);
            m.triggerView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (PanelViewRoot.this.getVisibility() == View.VISIBLE) {
                        if (m.subPanelView.getVisibility() != VISIBLE) {
                            showMenuView(m.subPanelView);
                        } else {
                            KeyboardUtil.showKeyboard(editText);
                        }
                    } else {
                        showMenuView(m.subPanelView);
                        showPanel(PanelViewRoot.this);
                    }
                }
            });
        }
    }


    public void showPanel(final View panelLayout) {
        final Activity activity = (Activity) panelLayout.getContext();
        panelLayout.setVisibility(View.VISIBLE);
        if (activity.getCurrentFocus() != null) {
            KeyboardUtil.hideKeyboard(activity.getCurrentFocus());
        }
    }

    private void showMenuView(final View menuView) {
        for (MenuView v : menus) {
            if (v.subPanelView != menuView) {
                v.subPanelView.setVisibility(GONE);
            } else {
                v.subPanelView.setVisibility(VISIBLE);
                if (v.subPanelView instanceof SmileyView) {
                    editText.requestFocus();
                }
            }
        }
    }

    /**
     * Hide the panel and the keyboard.
     */
    public void hidePanelAndKeyboard() {
        final Activity activity = (Activity) getContext();
        final View focusView = activity.getCurrentFocus();
        if (focusView != null) {
            KeyboardUtil.hideKeyboard(activity.getCurrentFocus());
            focusView.clearFocus();
        }
        setVisibility(View.GONE);
    }

}
