package com.photo.utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by li on 2016/6/21.
 */
public class TestActivity extends Activity{
    private TextView bofang;
    private String s="请任明到3号输液室";
    TextSpeaker textSpeaker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        bofang= (TextView) findViewById(R.id.bofang);
        textSpeaker=new TextSpeaker(TestActivity.this);
        bofang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSpeaker.speak(s);
            }
        });
    }
}
