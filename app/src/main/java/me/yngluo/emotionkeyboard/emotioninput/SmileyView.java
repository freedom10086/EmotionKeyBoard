package me.yngluo.emotionkeyboard.emotioninput;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.yngluo.emotionkeyboard.R;
import me.yngluo.emotionkeyboard.utils.DimmenUtils;


public class SmileyView extends LinearLayout
        implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private Context context;
    private PageAdapter adapter;
    private int dotImageResourseId;
    private LinearLayout dotContainer;
    private LinearLayout tabContainer;
    private List<SmileyDataSet> smileys;
    private EmotionInputHandler emotionInputHandler;
    private int currentTabPosition = -1;
    private int totalPageSize = 0;
    private int SIZE_8 = 8;

    private static final int LMP = LayoutParams.MATCH_PARENT;
    private static final int LWC = LayoutParams.WRAP_CONTENT;
    private boolean isInitSize = false;
    private static int ROW_COUNT = 4;
    private static int COLOUM_COUNT = 7;
    private int COLOR_TAB = Color.WHITE;
    private int COLOR_TAB_SEL = Color.GRAY;

    public SmileyView(Context context) {
        super(context);
        init(context);
    }

    public SmileyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SmileyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        this.context = context;
        SIZE_8 = DimmenUtils.dip2px(context, 8);
        setOrientation(VERTICAL);
        setBackgroundColor(ContextCompat.getColor(context, R.color.bg_primary));
        COLOR_TAB = ContextCompat.getColor(context, R.color.bg_primary);
        COLOR_TAB_SEL = ContextCompat.getColor(context, R.color.bg_select);

        viewPager = new ViewPager(context);
        viewPager.setLayoutParams(new LayoutParams(LMP, LWC, 1));
        viewPager.addOnPageChangeListener(this);
        dotImageResourseId = R.drawable.dot_bg;
        addView(viewPager);

        dotContainer = new LinearLayout(context);
        dotContainer.setOrientation(LinearLayout.HORIZONTAL);
        dotContainer.setLayoutParams(new LayoutParams(LMP, DimmenUtils.dip2px(context, 16)));
        dotContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(dotContainer);


        View gap = new View(context);
        gap.setLayoutParams(new LayoutParams(LMP, DimmenUtils.dip2px(context, 0.6f)));
        gap.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDivider));
        addView(gap);

        tabContainer = new LinearLayout(context);
        tabContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabContainer.setGravity(Gravity.CENTER_VERTICAL);
        tabContainer.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_primary));
        tabContainer.setLayoutParams(new LayoutParams(LMP, DimmenUtils.dip2px(context, 36)));
        addView(tabContainer);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!isInitSize) {
            isInitSize = true;
            int width = DimmenUtils.px2dip(context, r - l);
            int height = DimmenUtils.px2dip(context, b - t);
            if (width / 60 > 0) {
                COLOUM_COUNT = width / 60;
            }
            int col = (height - (36 + 16)) / 60;
            if(col>0){
                ROW_COUNT = col;
            }

            Log.d("SmileyView onLayout", "width: " + width + " height:" + height);

            adapter = new PageAdapter();
            List<SmileyDataSet> smileys = new ArrayList<>();
            SmileyDataSet setTieba = SmileyDataSet.getDataSet(context, "贴吧", true, R.array.smiley_tieba);
            SmileyDataSet setAcn = SmileyDataSet.getDataSet(context, "ac娘", true, R.array.smiley_acn);
            SmileyDataSet setJgz = SmileyDataSet.getDataSet(context, "金馆长", true, R.array.smiley_jgz);
            SmileyDataSet setYwz = SmileyDataSet.getDataSet(context, "颜文字", false, R.array.smiley_ywz);

            smileys.add(setTieba);
            smileys.add(setAcn);
            smileys.add(setJgz);
            smileys.add(SmileyEmoji.getEmojis());
            smileys.add(setYwz);
            setSmileys(smileys);
            viewPager.setAdapter(adapter);
        }
    }


    public void setInputView(EmotionInputHandler handler) {
        emotionInputHandler = handler;
    }


    public void setSmileys(List<SmileyDataSet> smileys) {
        if (smileys == null) return;
        this.smileys = smileys;
        totalPageSize = getTotalPageSize();
        adapter.notifyDataSetChanged();
        initTabs();
        setDots(0);
        switchDot(0);
    }


    //获得某一类表情的页数
    private int getPageSize(int pos) {
        if (smileys == null) {
            return 0;
        } else {
            int singlePageCount = ROW_COUNT * COLOUM_COUNT;
            int size = smileys.get(pos).getCount();
            int page = size / singlePageCount;
            if (size % singlePageCount != 0) {
                page++;
            }
            return page;
        }
    }

    //获得中的页数
    private int getTotalPageSize() {
        if (smileys == null) {
            return 0;
        } else {
            int count = 0;
            for (int i = 0; i < smileys.size(); i++) {
                count += getPageSize(i);
            }
            return count;
        }
    }


    private int getPageCountBefore(int tabpos) {
        int p = 0;

        for (int i = 0; i < tabpos; i++) {
            p += getPageSize(i);
        }

        return p;
    }


    //页转tab
    private int pageToTabPos(int pageIndex) {
        if (pageIndex <= 0) return 0;
        if (pageIndex >= totalPageSize - 1) return smileys.size() - 1;
        int p = 0;
        for (int i = 0; i < smileys.size(); i++) {
            p += getPageSize(i);
            if (pageIndex < p) {
                return i;
            }
        }
        return smileys.size() - 1;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int page) {
        int tabpos = pageToTabPos(page);
        int dotindex = page - getPageCountBefore(tabpos);
        switchTab(tabpos);
        switchDot(dotindex);
    }

    private void switchTab(int pos) {
        if (currentTabPosition == -1 || pos != currentTabPosition) {
            if (currentTabPosition == -1) currentTabPosition = 0;
            tabContainer.getChildAt(currentTabPosition).setBackgroundColor(COLOR_TAB);
            currentTabPosition = pos;
            tabContainer.getChildAt(currentTabPosition).setBackgroundColor(COLOR_TAB_SEL);
            setDots(currentTabPosition);
        }
    }

    private void switchDot(int index) {
        for (int i = 0; i < dotContainer.getChildCount(); i++) {
            if (i == index) {
                dotContainer.getChildAt(i).setEnabled(true);
                dotContainer.getChildAt(i).setScaleX(1.2f);
                dotContainer.getChildAt(i).setScaleY(1.2f);
            } else {
                dotContainer.getChildAt(i).setEnabled(false);
                dotContainer.getChildAt(i).setScaleX(1.0f);
                dotContainer.getChildAt(i).setScaleY(1.0f);
            }
        }
    }

    public void initTabs() {
        LayoutParams params = new LayoutParams(LWC, LMP);
        for (int i = 0; i < smileys.size(); i++) {
            View itemView;
            SmileyDataSet set = smileys.get(i);

            itemView = new TextView(context);
            ((TextView) itemView).setText(set.name);
            ((TextView) itemView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            ((TextView) itemView).setGravity(Gravity.CENTER);

            itemView.setPadding(SIZE_8 * 2, SIZE_8 / 2, SIZE_8 * 2, SIZE_8 / 2);
            itemView.setClickable(true);
            final int finalI = i;
            if (i == 0) itemView.setBackgroundColor(COLOR_TAB_SEL);

            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchTab(finalI);
                    switchDot(0);
                    int pageStart = getPageCountBefore(finalI);
                    viewPager.setCurrentItem(pageStart, true);
                }
            });

            tabContainer.addView(itemView, params);
        }

        View v = new View(context);
        v.setLayoutParams(new LayoutParams(LWC, LMP, 1));
        tabContainer.addView(v);

        ImageView delIcon = new ImageView(context);
        delIcon.setPadding(SIZE_8 * 2, SIZE_8 / 2, SIZE_8 * 2, SIZE_8 / 2);
        delIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        delIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_back_space));
        delIcon.setClickable(true);
        delIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                emotionInputHandler.backSpace();
            }
        });
        tabContainer.addView(delIcon, params);
    }

    public void setDots(int tabpos) {
        dotContainer.removeAllViews();
        LayoutParams lpp = new LayoutParams(LWC, LWC);
        lpp.setMargins(SIZE_8 / 2, 0, SIZE_8 / 2, 0);
        lpp.gravity = Gravity.CENTER_VERTICAL;

        for (int i = 0; i < getPageSize(tabpos); i++) {
            ImageView dotImageView = new ImageView(context);
            dotImageView.setImageResource(dotImageResourseId);
            dotImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            dotImageView.setEnabled(false);
            dotContainer.addView(dotImageView, lpp);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private class PageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return totalPageSize;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int page) {
            EmotionGridView v = (EmotionGridView) container.findViewWithTag(page);
            if (v == null) {
                final int tabpos = pageToTabPos(page);
                int pageStart = page - getPageCountBefore(tabpos);
                int index = pageStart * ROW_COUNT * COLOUM_COUNT;

                v = new EmotionGridView(context, smileys.get(tabpos),
                        COLOUM_COUNT, ROW_COUNT, index, emotionInputHandler);
                v.setLayoutParams(new LayoutParams(LMP, LMP));
                v.setTag(page);
                container.addView(v);
            }
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

}
