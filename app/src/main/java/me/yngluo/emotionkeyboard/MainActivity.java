package me.yngluo.emotionkeyboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import me.yngluo.emotionkeyboard.emotioninput.SmileyInputRoot;

public class MainActivity extends Activity implements View.OnClickListener {

    private SmileyInputRoot rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initEmotionInput();
    }

    private void initEmotionInput() {
        EditText input = (EditText) findViewById(R.id.ed_comment);
        View smileyBtn = findViewById(R.id.btn_emotion);
        View btnMore = findViewById(R.id.btn_more);
        View btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
        rootView = (SmileyInputRoot) findViewById(R.id.root);

        rootView.initSmiley(input, smileyBtn, btnSend);

        /**
         * 设置more view 默认不显示
         */
        rootView.setMoreView(LayoutInflater.from(this).inflate(R.layout.my_smiley_menu, null), btnMore);
        findViewById(R.id.btn_star).setOnClickListener(this);
        findViewById(R.id.btn_link).setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String s = "";
        switch (v.getId()) {
            case R.id.btn_send:
                s = "发送";
                break;
            case R.id.btn_link:
                s = "复制连接";
                break;
            case R.id.btn_share:
                s = "分享";
                break;
            case R.id.btn_star:
                s = "收藏";
                break;
        }
        Toast.makeText(this, s + "被电击", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        if (!rootView.onActivityBackClick()) {
            super.onBackPressed();
        }
    }
}
