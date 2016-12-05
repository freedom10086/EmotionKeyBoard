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
import android.widget.ImageView;

import me.yluo.testkeyboard.keboard.PanelViewRoot;
import me.yluo.testkeyboard.keboard.SmileyInputRoot;
import me.yluo.testkeyboard.keboard.util.KeyboardUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ResolvedActivity";
    private RecyclerView listView;
    private EditText mSendEdt;
    private PanelViewRoot mPanelRoot;
    private ImageView smileyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (RecyclerView) findViewById(R.id.list_view);
        mSendEdt = (EditText) findViewById(R.id.ed_comment);
        SmileyInputRoot rootViewGroup = (SmileyInputRoot) findViewById(R.id.rootView);
        mPanelRoot = rootViewGroup.getmPanelLayout();
        smileyBtn = (ImageView) findViewById(R.id.btn_first);

        KeyboardUtil.attach(this, mPanelRoot, new KeyboardUtil.OnKeyboardShowingListener() {
            @Override
            public void onKeyboardShowing(boolean isShowing) {
                Log.d(TAG, String.format("Keyboard is %s", isShowing ? "showing" : "hiding"));
            }
        });

        mPanelRoot.init(mSendEdt, smileyBtn);

        listView.setLayoutManager(new LinearLayoutManager(this));

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mPanelRoot.hidePanelAndKeyboard();
                }
                return false;
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP &&
                event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mPanelRoot.getVisibility() == View.VISIBLE) {
                mPanelRoot.hidePanelAndKeyboard();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
