package me.yngluo.emotionkeyboard.emotioninput;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;


public class SmileyDataSet {
    public String name;
    private boolean isImage = true;
    private List<Pair<String, String>> smileys;
    private static final String SMILEY_BASE = "file:///android_asset/smiley/";

    public SmileyDataSet(String name, boolean isImage) {
        this.isImage = isImage;
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

    public List<Pair<String, String>> getSmileys() {
        return smileys;
    }

    public boolean isImage() {
        return isImage;
    }

    public String getLogo() {
        if (smileys == null || smileys.isEmpty()) {
            return null;
        } else {
            return smileys.get(0).first;
        }
    }

    public static SmileyDataSet getDataSet(Context context, String name, boolean isImage, int stringId) {
        SmileyDataSet set = new SmileyDataSet(name, isImage);
        String[] smileyArray = context.getResources().getStringArray(stringId);
        List<Pair<String, String>> smileys = new ArrayList<>();
        if (isImage) {
            for (String aSmileyArray : smileyArray) {
                smileys.add(new Pair<>(SMILEY_BASE + aSmileyArray.split(",")[0], aSmileyArray.split(",")[1]));
            }
        } else {
            set.isImage = false;
            for (String aSmileyArray : smileyArray) {
                smileys.add(new Pair<>(aSmileyArray, aSmileyArray));
            }
        }

        set.setSmileys(smileys);
        return set;
    }
}
