package me.yluo.testkeyboard.keboard;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;


public class SmileyDataSet {
    public String name;
    private boolean isImage = true;
    public String logo;
    private String dir;
    private List<Pair<String, String>> smileys;

    public SmileyDataSet(String name, boolean isImage, String dir) {
        this.isImage = isImage;
        this.name = name;
        this.dir = dir;
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
}
