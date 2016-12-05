
package me.yluo.testkeyboard.emotioninput;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import me.yluo.testkeyboard.emotioninput.util.KeyboardUtil;
import me.yluo.testkeyboard.emotioninput.util.ViewUtil;


public class PanelViewRoot extends FrameLayout {

    private boolean mIsHide = false;
    private boolean mIsKeyboardShowing = false;
    private EmotionInputHandler handler;
    private SmileyView smileyView;
    private View moreView;
    private EditText editText;
    private View moreViewBtn, sendBtn;
    private Paint paint = new Paint();

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
        paint.setColor(Color.parseColor("#d5d3d5"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.0f);

        smileyView = new SmileyView(getContext());
        smileyView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addView(smileyView);
    }

    public void init(EditText editText, View smileyBtn, final View sendBtn) {
        sendBtn.setEnabled(false);
        this.sendBtn = sendBtn;
        handler = new EmotionInputHandler(editText, new EmotionInputHandler.TextChangeListener() {
            @Override
            public void onTextChange(boolean enable, String s) {
                sendBtn.setEnabled(enable);
                if (!enable) {
                    sendBtn.setVisibility(GONE);
                    moreViewBtn.setVisibility(VISIBLE);
                } else {
                    moreViewBtn.setVisibility(GONE);
                    sendBtn.setVisibility(VISIBLE);
                }
                Log.e("enable", "" + enable);
            }
        });
        smileyView.setInputView(handler);
        this.editText = editText;
        setSmileyView(smileyBtn);
    }

    private void setSmileyView(View smileyBtn) {
        smileyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PanelViewRoot.this.getVisibility() == View.VISIBLE) {
                    if (smileyView.getVisibility() != VISIBLE) {
                        smileyView.setVisibility(VISIBLE);
                        moreView.setVisibility(GONE);
                    } else {
                        KeyboardUtil.showKeyboard(editText);
                    }
                } else {
                    smileyView.setVisibility(VISIBLE);
                    moreView.setVisibility(GONE);
                    showPanel(PanelViewRoot.this);
                }
            }
        });
    }

    public void setMoreView(View panelView, View moreBtn) {
        moreView = panelView;
        this.sendBtn.setVisibility(GONE);
        addView(moreView);
        this.moreViewBtn = moreBtn;
        moreViewBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PanelViewRoot.this.getVisibility() == View.VISIBLE) {
                    if (moreView.getVisibility() != VISIBLE) {
                        moreView.setVisibility(VISIBLE);
                        smileyView.setVisibility(GONE);
                    } else {
                        KeyboardUtil.showKeyboard(editText);
                    }
                } else {
                    moreView.setVisibility(VISIBLE);
                    smileyView.setVisibility(GONE);
                    showPanel(PanelViewRoot.this);
                }
            }
        });
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


    public void showPanel(final View panelLayout) {
        final Activity activity = (Activity) panelLayout.getContext();
        panelLayout.setVisibility(View.VISIBLE);
        if (activity.getCurrentFocus() != null) {
            KeyboardUtil.hideKeyboard(activity.getCurrentFocus());
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


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, 0.5f, getMeasuredWidth(), 0.5f, paint);
    }
}
