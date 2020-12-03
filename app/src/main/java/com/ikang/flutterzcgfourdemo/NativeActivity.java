package com.ikang.flutterzcgfourdemo;


import android.os.Bundle;
import android.widget.TextView;

import io.flutter.embedding.android.FlutterActivity;

public class NativeActivity extends FlutterActivity {

    private TextView tv;
    private String text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);

        tv = findViewById(R.id.tv);
        text = "什么也没传过来";
        text = getIntent().getStringExtra("text");

        tv.setText("我是接收到的参数：" + text);

    }
}