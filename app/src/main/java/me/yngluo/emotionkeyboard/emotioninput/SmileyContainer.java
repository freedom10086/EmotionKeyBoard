
package me.yngluo.emotionkeyboard.emotioninput;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import me.yngluo.emotionkeyboard.utils.DimmenUtils;
import me.yngluo.emotionkeyboard.utils.KeyboardUtil;


public class SmileyContainer extends FrameLayout {

    private boolean isKeyboardShowing;
    private EmotionInputHandler handler;
    private boolean isVisible = false;
    private SmileyView smileyView;
    private View moreView;
    private EditText editText;
    private int keyboardHeight;
    private View moreViewBtn, sendBtn;
    private Paint paint = new Paint();

    public SmileyContainer(Context context) {
        super(context);
        init();
    }

    private void init() {
        setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                DimmenUtils.dip2px(getContext(), 200)));
        paint.setColor(Color.parseColor("#d5d3d5"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.0f);

        smileyView = new SmileyView(getContext());
        smileyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(smileyView);
    }

    public void init(EditText editText, View smileyBtn, final View sendBtn) {
        sendBtn.setEnabled(false);
        this.sendBtn = sendBtn;
        handler = new EmotionInputHandler(editText, new EmotionInputHandler.TextChangeListener() {
            @Override
            public void onTextChange(boolean enable, String s) {
                sendBtn.setEnabled(enable);
                if (moreView != null && moreViewBtn != null) {
                    if (!enable) {
                        sendBtn.setVisibility(GONE);
                        moreViewBtn.setVisibility(VISIBLE);
                    } else {
                        moreViewBtn.setVisibility(GONE);
                        sendBtn.setVisibility(VISIBLE);
                    }
                }
            }
        });

        smileyView.setInputView(handler);

        editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideContainer();
            }
        });
        this.editText = editText;
        setSmileyView(smileyBtn);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isVisible) {
            setVisibility(GONE);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void setSmileyView(View smileyBtn) {
        smileyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("smileyBtn", "click");
                smileyView.setVisibility(VISIBLE);
                showContainer();
            }
        });
    }

    public void setMoreView(View panelView, View moreBtn) {
        moreView = panelView;
        this.sendBtn.setVisibility(GONE);
        addView(moreView);
        this.moreViewBtn = moreBtn;
        this.moreViewBtn.setVisibility(VISIBLE);
        this.sendBtn.setVisibility(GONE);
        moreViewBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SmileyContainer.this.getVisibility() == View.VISIBLE) {
                    if (moreView.getVisibility() != VISIBLE) {
                        moreView.setVisibility(VISIBLE);
                        smileyView.setVisibility(GONE);
                    } else {
                        KeyboardUtil.showKeyboard(editText);
                    }
                } else {
                    moreView.setVisibility(VISIBLE);
                    smileyView.setVisibility(GONE);
                    setVisibility(VISIBLE);
                }
            }
        });
    }

    private void showContainer() {
        isVisible = true;
        if (isKeyboardShowing && getVisibility() == GONE) {
            KeyboardUtil.hideKeyboard(editText);
            setVisibility(VISIBLE);
        } else if (!isKeyboardShowing && getVisibility() == GONE) {
            setVisibility(VISIBLE);
        }
    }

    private void hideContainer() {
        isVisible = false;
    }

    //offset > 0 可能时键盘弹起
    void onMainViewSizeChange(int offset) {
        Log.e("onMainViewSizeChange", "offset is visible:" + offset + " visible:" + getVisibility());
        if (offset > 0) {
            this.isKeyboardShowing = true;
            hideContainer();
            setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, offset));
        } else if (offset < 0) {
            Log.e("______", "keyboard hide :" + offset);
            this.isKeyboardShowing = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, 0.5f, getMeasuredWidth(), 0.5f, paint);
    }
}
