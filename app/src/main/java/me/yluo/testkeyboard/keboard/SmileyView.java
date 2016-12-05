package me.yluo.testkeyboard.keboard;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.yluo.testkeyboard.R;
import me.yluo.testkeyboard.keboard.util.DimmenUtils;


public class SmileyView extends LinearLayout implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private EditText inputView;
    private Context context;
    private PageAdapter adapter;
    private int dotImageResourseId;
    private LinearLayout dotContainer;
    private LinearLayout tabContainer;
    private List<SmileyDataSet> smileys;
    private int currentTabPosition = -1;
    private int totalPageSize = 0;
    private int SIZE_8 = 0;

    private static final int LMP = LinearLayout.LayoutParams.MATCH_PARENT;
    private static final int LWC = LinearLayout.LayoutParams.WRAP_CONTENT;
    private static final int ROW_COUNT = 4;
    private static final int COLOUM_COUNT = 8;
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
        COLOR_TAB = Color.parseColor("#ffffff");
        COLOR_TAB_SEL = Color.parseColor("#dddddd");

        viewPager = new ViewPager(context);
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(LMP, LWC, 1));
        viewPager.addOnPageChangeListener(this);
        dotImageResourseId = R.drawable.dot_bg;
        adapter = new PageAdapter();
        viewPager.setAdapter(adapter);
        addView(viewPager);

        dotContainer = new LinearLayout(context);
        dotContainer.setOrientation(LinearLayout.HORIZONTAL);
        dotContainer.setLayoutParams(new LinearLayout.LayoutParams(LMP, DimmenUtils.dip2px(context, 20)));
        dotContainer.setGravity(Gravity.CENTER);
        addView(dotContainer);


        View gap = new View(context);
        gap.setLayoutParams(new LinearLayout.LayoutParams(LMP, DimmenUtils.dip2px(context, 0.6f)));
        gap.setBackgroundColor(Color.parseColor("#1f000000"));
        addView(gap);

        tabContainer = new LinearLayout(context);
        tabContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabContainer.setGravity(Gravity.CENTER_VERTICAL);
        tabContainer.setLayoutParams(new LinearLayout.LayoutParams(LMP, DimmenUtils.dip2px(context, 32)));
        addView(tabContainer);

        List<SmileyDataSet> datas = new ArrayList<>();
        SmileyDataSet set = new SmileyDataSet("贴吧", true, "tieba/");
        set.logo = "file:///android_asset/tieba/tb001.png";
        try {
            String[] aas = context.getAssets().list("tieba");
            List<Pair<String, String>> pairs = new ArrayList<>();
            for (String a : aas) {
                Log.e("====", "file:///android_asset/tieba/" + a);
                pairs.add(new Pair<>(a, "file:///android_asset/tieba/" + a));
            }
            set.setSmileys(pairs);
            datas.add(set);

            SmileyDataSet sett = new SmileyDataSet("acn", true, "acn/");
            sett.logo = "file:///android_asset/acn/acn003.png";
            String[] aass = context.getAssets().list("acn");
            List<Pair<String, String>> pairss = new ArrayList<>();
            for (String a : aass) {
                Log.e("====", "file:///android_asset/acn/" + a);
                pairss.add(new Pair<>(a, "file:///android_asset/acn/" + a));
            }
            sett.setSmileys(pairss);
            datas.add(sett);
            setSmileys(datas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setInputView(EditText editText) {
        this.inputView = editText;
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
                dotContainer.getChildAt(i).setScaleX(1.35f);
                dotContainer.getChildAt(i).setScaleY(1.35f);
            } else {
                dotContainer.getChildAt(i).setEnabled(false);
                dotContainer.getChildAt(i).setScaleX(1.0f);
                dotContainer.getChildAt(i).setScaleY(1.0f);
            }
        }
    }

    public void initTabs() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LWC, LMP);
        for (int i = 0; i < smileys.size(); i++) {
            ImageView imageView = new ImageView(context);
            Picasso.with(context).load(smileys.get(i).logo).resize(SIZE_8 * 2, SIZE_8 * 2).into(imageView);
            imageView.setPadding(SIZE_8 * 2, SIZE_8 / 2, SIZE_8 * 2, SIZE_8 / 2);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setClickable(true);
            final int finalI = i;
            if (i == 0) imageView.setBackgroundColor(COLOR_TAB_SEL);
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchTab(finalI);
                    switchDot(0);
                    int pageStart = getPageCountBefore(finalI);
                    viewPager.setCurrentItem(pageStart, true);
                }
            });

            tabContainer.addView(imageView, params);

        }

        View v = new View(context);
        v.setLayoutParams(new LinearLayout.LayoutParams(LWC, LMP, 1));
        tabContainer.addView(v);

        ImageView delIcon = new ImageView(context);
        delIcon.setPadding(SIZE_8 * 2, SIZE_8 / 2, SIZE_8 * 2, SIZE_8 / 2);
        delIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        delIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_back_space));
        delIcon.setClickable(true);
        tabContainer.addView(delIcon, params);
    }

    public void setDots(int tabpos) {
        dotContainer.removeAllViews();
        LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(LWC, LWC);
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
            GridView v = (GridView) container.findViewWithTag(page);
            if (v == null) {
                v = new GridView(context);
                v.setNumColumns(COLOUM_COUNT);
                final int tabpos = pageToTabPos(page);
                v.setAdapter(new SmileyAdapter(tabpos, page, smileys.get(tabpos)));
                v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //// TODO: 2016/12/5
                        Log.e("onItemClick", "click tabpos:" + tabpos + " pos:" + i);
                    }
                });
                v.setLayoutParams(new LinearLayout.LayoutParams(LMP, LMP));
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

    private class SmileyAdapter extends BaseAdapter {
        private SmileyDataSet set;
        private int startIndex;
        private int pageStart = 0;
        private int tab = 0;

        SmileyAdapter(int tabpos, int page, SmileyDataSet set) {
            this.set = set;
            this.tab = tabpos;
            pageStart = page - getPageCountBefore(tabpos);
            startIndex = pageStart * ROW_COUNT * COLOUM_COUNT;
        }

        @Override
        public int getCount() {
            if (set == null) {
                return 0;
            }
            int pages = getPageSize(tab);
            if (pageStart < pages - 1) {
                return ROW_COUNT * COLOUM_COUNT;
            } else if (pageStart == pages - 1) {
                return set.getCount() - startIndex;
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int i) {
            return smileys.get(startIndex + i);
        }

        @Override
        public long getItemId(int i) {
            return i + startIndex;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            final int pos = startIndex + i;
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                if (set.isImage()) {
                    convertView = new ImageView(context);
                } else {
                    convertView = new TextView(context);
                }
                holder.emoticon = convertView;
                holder.emoticon.setPadding(SIZE_8, SIZE_8, SIZE_8, SIZE_8);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            int realItemHeight = viewPager.getMeasuredHeight() / ROW_COUNT;
            holder.emoticon.setLayoutParams(new LinearLayoutCompat.LayoutParams(LMP, realItemHeight));
            if (realItemHeight > 0) {
                if (set.isImage()) {
                    Picasso.with(context).load(set.getSmileys().get(pos).second).resize(realItemHeight / 2, realItemHeight / 2).into((ImageView) holder.emoticon);
                } else {
                    ((TextView) holder.emoticon).setText(set.getSmileys().get(pos).second);
                }
            }
            return convertView;
        }

        private class ViewHolder {
            View emoticon;
        }
    }

}
