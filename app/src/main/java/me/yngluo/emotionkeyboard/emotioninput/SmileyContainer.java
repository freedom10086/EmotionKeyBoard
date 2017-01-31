
package me.yngluo.emotionkeyboard.emotioninput;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import me.yngluo.emotionkeyboard.utils.KeyboardUtil;


public class SmileyContainer extends FrameLayout {

    boolean isVisible = false;
    boolean isKeyboardShowing;
    private SmileyView smileyView;
    private View moreView;
    private EditText editText;
    private int savedHeight = 0;

    private View moreViewBtn, sendBtn;
    private Paint paint = new Paint();

    public SmileyContainer(Context context) {
        super(context);
        init();
    }

    private void init() {
        savedHeight = KeyBoardHeightPreference.get(getContext(), 200);
        setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, savedHeight));
        paint.setColor(Color.parseColor("#d5d3d5"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.0f);

        smileyView = new SmileyView(getContext());
        smileyView.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        addView(smileyView);
    }

    public void init(EditText editText, View smileyBtn, final View sendBtn) {
        sendBtn.setEnabled(false);
        this.sendBtn = sendBtn;
        EmotionInputHandler handler = new EmotionInputHandler(editText, new EmotionInputHandler.TextChangeListener() {
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


        this.editText = editText;
        this.editText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideContainer(true);
                return false;
            }
        });
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
                if (getVisibility() != VISIBLE) {
                    smileyView.setVisibility(VISIBLE);
                    if (moreView != null) moreView.setVisibility(GONE);
                    showContainer();
                } else {
                    if (smileyView.getVisibility() == VISIBLE) {
                        hideContainer(true);
                        KeyboardUtil.showKeyboard(editText);
                    } else {
                        smileyView.setVisibility(VISIBLE);
                        if (moreView != null) moreView.setVisibility(GONE);
                    }
                }
            }
        });
    }

    public void setMoreView(View moreViewIn, View moreBtn) {
        this.moreView = moreViewIn;
        this.sendBtn.setVisibility(GONE);
        addView(this.moreView);
        this.moreViewBtn = moreBtn;
        this.moreViewBtn.setVisibility(VISIBLE);
        this.sendBtn.setVisibility(GONE);

        moreViewBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getVisibility() != VISIBLE) {
                    moreView.setVisibility(VISIBLE);
                    smileyView.setVisibility(GONE);
                    showContainer();
                } else {
                    if (moreView.getVisibility() == VISIBLE) {
                        hideContainer(true);
                        KeyboardUtil.showKeyboard(editText);
                    } else {
                        moreView.setVisibility(VISIBLE);
                        smileyView.setVisibility(GONE);
                    }
                }
            }
        });
    }

    public void showContainer() {
        if (isVisible) return;
        isVisible = true;
        if (isKeyboardShowing) KeyboardUtil.hideKeyboard(editText);
        if (getVisibility() == GONE) {
            setVisibility(VISIBLE);
        }
    }


    //参数代表是否由键盘弹起
    public void hideContainer(boolean isCauseByKeyboard) {
        isVisible = false;
        if (!isCauseByKeyboard) {
            setVisibility(GONE);
        }
    }


    //offset > 0 可能时键盘弹起
    void onMainViewSizeChange(int offset) {
        if (offset > 0) {//键盘弹起
            isVisible = false;
            this.isKeyboardShowing = true;
            if (offset != savedHeight) {
                KeyBoardHeightPreference.save(getContext(), offset);
                savedHeight = offset;
                setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, offset));
            }
            hideContainer(true);
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
