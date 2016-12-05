package me.yluo.testkeyboard.emotioninput;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by free2 on 16-3-20.
 * 表情处理
 * 格式处理 加粗 斜体等。。。
 */
public class EmotionInputHandler implements TextWatcher {

    private final EditText mEditor;
    private final TextChangeListener listener;
    private final ArrayList<ImageSpan> mEmoticonsToRemove = new ArrayList<>();
    private final ArrayList<TextEmotionSpan> mEmoticonsToRemove2 = new ArrayList<>();

    public EmotionInputHandler(EditText editor, TextChangeListener listener) {
        mEditor = editor;
        mEditor.addTextChangedListener(this);
        this.listener = listener;
    }


    public void insertString(String s) {
        int start = mEditor.getSelectionStart();
        int end = mEditor.getSelectionEnd();
        Editable editableText = mEditor.getEditableText();

        editableText.replace(start, end, s);
        editableText.setSpan(new TextEmotionSpan(), start, start + s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public void insertSmiley(SmileyDataSet set, int index, Drawable drawable) {
        if (drawable != null) {
            int itemHeight;
            int itemWidth;
            itemHeight = drawable.getIntrinsicHeight();
            itemWidth = drawable.getIntrinsicWidth();
            drawable.setBounds(0, 0, itemWidth, itemHeight);
            EmoticonSpan emoticonSpan = new EmoticonSpan(drawable);
            int start = mEditor.getSelectionStart();
            int end = mEditor.getSelectionEnd();
            Editable editableText = mEditor.getEditableText();
            // Insert the emoticon.
            String name = set.getSmileys().get(index).second;
            editableText.replace(start, end, name);
            editableText.setSpan(emoticonSpan, start, start + name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public void backSpace() {
        int start = mEditor.getSelectionStart();
        int end = mEditor.getSelectionEnd();
        if (start == 0) {
            return;
        }
        if ((start == end) && start > 0) {
            start = start - 1;
        }
        mEditor.getText().delete(start, end);
    }

    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        if (count > 0) {
            int end = start + count;
            Editable message = mEditor.getEditableText();
            ImageSpan[] list = message.getSpans(start, end, ImageSpan.class);
            for (ImageSpan span : list) {
                int spanStart = message.getSpanStart(span);
                int spanEnd = message.getSpanEnd(span);
                if ((spanStart < end) && (spanEnd > start)) {
                    // Add to remove list
                    mEmoticonsToRemove.add(span);
                }
            }

            TextEmotionSpan[] list2 = message.getSpans(start, end, TextEmotionSpan.class);
            for (TextEmotionSpan span : list2) {
                int spanStart = message.getSpanStart(span);
                int spanEnd = message.getSpanEnd(span);
                if ((spanStart < end) && (spanEnd > start)) {
                    // Add to remove list
                    mEmoticonsToRemove2.add(span);
                }
            }

        }
    }

    @Override
    public void afterTextChanged(Editable text) {
        Editable message = mEditor.getEditableText();
        for (ImageSpan span : mEmoticonsToRemove) {
            int start = message.getSpanStart(span);
            int end = message.getSpanEnd(span);
            message.removeSpan(span);
            if (start != end) {
                message.delete(start, end);
            }
        }

        for (TextEmotionSpan span : mEmoticonsToRemove2) {
            int start = message.getSpanStart(span);
            int end = message.getSpanEnd(span);
            message.removeSpan(span);
            if (start != end) {
                message.delete(start, end);
            }
        }

        mEmoticonsToRemove.clear();
        mEmoticonsToRemove2.clear();

        if (!TextUtils.isEmpty(mEditor.getText().toString())) {
            listener.onTextChange(true, mEditor.getText().toString());
        } else {
            listener.onTextChange(false, mEditor.getText().toString());
        }
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
    }

    interface TextChangeListener {
        void onTextChange(boolean enable, String s);
    }

}