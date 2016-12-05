package me.yluo.testkeyboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yluo.testkeyboard.keboard.PanelViewGroup;
import me.yluo.testkeyboard.keboard.RootViewGroup;
import me.yluo.testkeyboard.keboard.util.KPSwitchConflictUtil;
import me.yluo.testkeyboard.keboard.util.KeyboardUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ResolvedActivity";
    private RecyclerView listView;
    private EditText mSendEdt;
    private PanelViewGroup mPanelRoot;
    private ImageView smileyBtn;
    private GridView smiley_grid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (RecyclerView) findViewById(R.id.list_view);
        mSendEdt = (EditText) findViewById(R.id.ed_comment);
        RootViewGroup rootViewGroup = (RootViewGroup) findViewById(R.id.rootView);
        mPanelRoot = rootViewGroup.getmPanelLayout();
        smileyBtn = (ImageView) findViewById(R.id.btn_first);

        KeyboardUtil.attach(this, mPanelRoot, new KeyboardUtil.OnKeyboardShowingListener() {
            @Override
            public void onKeyboardShowing(boolean isShowing) {
                Log.d(TAG, String.format("Keyboard is %s", isShowing ? "showing" : "hiding"));
            }
        });

        // In the normal case.
        KPSwitchConflictUtil.attach(mPanelRoot, smileyBtn, mSendEdt, new KPSwitchConflictUtil.SwitchClickListener() {
            @Override
            public void onClickSwitch(boolean switchToPanel) {
                if (switchToPanel) {
                    mSendEdt.clearFocus();
                } else {
                    mSendEdt.requestFocus();
                }
            }
        });


        listView.setLayoutManager(new LinearLayoutManager(this));

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    KPSwitchConflictUtil.hidePanelAndKeyboard(mPanelRoot);
                }
                return false;
            }
        });

        /*
        String[] from = {"img"};
        int[] to = {R.id.smiley_item};
        SimpleAdapter sim_adapter = new SimpleAdapter(this, getData(), R.layout.smiley_item, from, to);
        smiley_grid = (GridView) findViewById(R.id.smiley_grid);
        smiley_grid.setAdapter(sim_adapter);
        */
    }


    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> datas = new ArrayList<>();
        //cion和iconName的长度是相同的，这里任选其一都可以
        for (int i = 0; i < 30; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("img", R.drawable.pd_000);
            //map.put("text", iconName[i]);
            datas.add(map);
        }

        return datas;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP &&
                event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mPanelRoot.getVisibility() == View.VISIBLE) {
                KPSwitchConflictUtil.hidePanelAndKeyboard(mPanelRoot);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
