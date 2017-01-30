package me.yngluo.emotionkeyboard.emotioninput;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class EmotionGridView extends ViewGroup implements View.OnClickListener {
    private int colNum, rowNum;
    private int itemWidth, itemHeight;
    private Context context;
    private boolean isInitView;
    private SmileyDataSet set;
    private int startIndex;
    private EmotionInputHandler handler;
    private static final int TAG_INDEX = 0x7f056666;

    public EmotionGridView(Context context,
                           SmileyDataSet set,
                           int colNum,
                           int rowNum,
                           int startIndex,
                           EmotionInputHandler handler) {
        super(context);
        this.colNum = colNum == 0 ? 6 : colNum;
        this.rowNum = rowNum == 0 ? 4 : rowNum;
        this.set = set;
        this.context = context;
        this.startIndex = startIndex;
        this.handler = handler;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(sizeWidth, sizeHeight);

        itemWidth = sizeWidth / colNum;
        itemHeight = sizeHeight / rowNum;

        if (!isInitView && itemHeight > 0) {
            isInitView = true;
            initViews();
        }
    }

    public void initViews() {
        removeAllViews();
        int size = (int) (Math.min(itemHeight, itemWidth) * 0.8f);
        int marginLR = (itemWidth - size) / 2;
        int marginTB = (itemHeight - size) / 2;

        LayoutParams lp = new LayoutParams(itemWidth, itemHeight);
        for (int i = startIndex; i < set.getCount() && ((i - startIndex) < colNum * rowNum); i++) {
            Pair<String, String> d = set.getSmileys().get(i);
            View v;
            if (set.isImage()) {
                v = new ImageView(context);
                v.setPadding(marginLR, marginTB, marginLR, marginTB);
                Picasso.with(context).load(d.first)
                        .resize(size, size)
                        .into((ImageView) v);
            } else {
                v = new TextView(context);
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                //v.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                ((TextView) v).setGravity(Gravity.CENTER);
                ((TextView) v).setText(d.first);
                v.setPadding(marginLR / 2, marginTB, marginLR / 2, marginTB);
            }
            v.setTag(TAG_INDEX, d.second);
            v.setClickable(true);
            v.setOnClickListener(this);
            addView(v, lp);

        }

        invalidate();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int startX, startY;
        Log.d("EmotionGridView.layout", l + "||" + t);
        for (int i = 0; i < childCount; i++) {
            startX = ((i % colNum) * itemWidth);
            startY = ((i / colNum) * itemHeight);
            int endX = Math.min(startX + itemWidth, r);
            int endY = Math.min(startY + itemHeight, b);
            getChildAt(i).layout(startX, startY, endX, endY);
        }
    }

    @Override
    public void onClick(View view) {
        if (view instanceof ImageView) {
            ImageView v = (ImageView) view;
            handler.insertSmiley((String) v.getTag(TAG_INDEX), v.getDrawable());
        } else if (view instanceof TextView) {
            TextView v = (TextView) view;
            handler.insertString(v.getText().toString());
        }
    }
}
