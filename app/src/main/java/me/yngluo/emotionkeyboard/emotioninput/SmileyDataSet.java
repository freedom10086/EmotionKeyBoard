package me.yngluo.emotionkeyboard.emotioninput;

import android.content.Context;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class SmileyDataSet {
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_EMOJI = 2;

    public static final int TAG_INDEX = 0x7f056666;

    public String name;
    public int type;
    private List<Pair<String, String>> smileys;
    private static final String SMILEY_BASE = "file:///android_asset/smiley/";

    public SmileyDataSet(String name, int type) {
        this.type = type;
        this.name = name;
        smileys = new ArrayList<>();
    }

    public void setSmileys(List<Pair<String, String>> smileys) {
        this.smileys = smileys;
    }

    public int getCount() {
        if (smileys == null) {
            return 0;
        } else {
            return smileys.size();
        }
    }


    public static SmileyDataSet getDataSet(Context context, String name, int type, int stringId) {
        SmileyDataSet set = new SmileyDataSet(name, type);
        String[] smileyArray = context.getResources().getStringArray(stringId);
        List<Pair<String, String>> smileys = new ArrayList<>();

        if (type == TYPE_IMAGE) {
            for (String aSmileyArray : smileyArray) {
                smileys.add(new Pair<>(SMILEY_BASE + aSmileyArray.split(",")[0], aSmileyArray.split(",")[1]));
            }
        } else {
            for (String aSmileyArray : smileyArray) {
                smileys.add(new Pair<>(aSmileyArray, aSmileyArray));
            }
        }
        set.setSmileys(smileys);
        return set;
    }

    public View getSmileyItem(Context context, int index, int size) {
        if (index >= smileys.size()) return null;
        Pair<String, String> d = smileys.get(index);

        View v;
        if (type == SmileyDataSet.TYPE_IMAGE) {
            v = new ImageView(context);
            Picasso.with(context).load(d.first)
                    .resize(size, size)
                    .into((ImageView) v);
        } else {
            v = new TextView(context);
            if (type == SmileyDataSet.TYPE_EMOJI) {
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, size / 2);
            } else {
                ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, size / 4);
            }
            //v.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            ((TextView) v).setGravity(Gravity.CENTER);
            ((TextView) v).setText(d.first);
        }
        v.setTag(TAG_INDEX, d.second);
        v.setClickable(true);

        return v;
    }
}
