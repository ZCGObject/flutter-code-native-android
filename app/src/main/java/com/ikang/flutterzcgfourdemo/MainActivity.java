package com.ikang.flutterzcgfourdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.flutter.embedding.android.FlutterView;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.renderer.FlutterUiDisplayListener;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    private FrameLayout frameLayout;
    private FlutterView flutterView;
    FlutterEngine flutterEngine;

    public static final String FlutterToAndroidCHANNEL  = "com.litngzhe.toandroid/plugin";
    public static final String  AndroidToFlutterCHANNEL= "com.litngzhe.toflutter/plugin";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        

        textView = findViewById(R.id.params);

        frameLayout = findViewById(R.id.rl_flutter);

        flutterEngine = new FlutterEngine(this);
        flutterEngine.getDartExecutor().executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault());
        flutterEngine.getNavigationChannel().setInitialRoute("route2");


        flutterView = new FlutterView(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        frameLayout.addView(flutterView, layoutParams);
        // 关键代码，将Flutter页面显示到FlutterView中
        flutterView.attachToFlutterEngine(flutterEngine);


        flutterView.addOnFirstFrameRenderedListener(new FlutterUiDisplayListener() {
            @Override
            public void onFlutterUiDisplayed() {
                Log.e("FirstFrame--->>>", "onFlutterUiDisplayed");
                frameLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFlutterUiNoLongerDisplayed() {
                Log.e("FirstFrame--->>>", "onFlutterUiNoLongerDisplayed");
            }
        });





        String params = getIntent().getStringExtra("test");
        if (!TextUtils.isEmpty(params)) {
            Toast.makeText(this, "" + params, Toast.LENGTH_SHORT).show();

            textView.setText("flutter 传参:" + params);

        }

        new EventChannel(flutterEngine.getDartExecutor(), AndroidToFlutterCHANNEL)
                .setStreamHandler(new EventChannel.StreamHandler() {
                    @Override
                    public void onListen(Object o, EventChannel.EventSink eventSink) {
                        String androidParmas = "来自android原生的参数";
                        eventSink.success(androidParmas);
                    }

                    @Override
                    public void onCancel(Object o) {

                    }
                });


        new MethodChannel(flutterEngine.getDartExecutor(), FlutterToAndroidCHANNEL).setMethodCallHandler(new MethodChannel.MethodCallHandler() {
            @Override
            public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {

                //接收来自flutter的指令oneAct
                if (methodCall.method.equals("withoutParams")) {

                    //跳转到指定Activity
                    Intent intent = new Intent(MainActivity.this, com.ikang.flutterzcgfourdemo.NativeActivity.class);
                    startActivity(intent);

                    //返回给flutter的参数
                    result.success("success");
                }
                //接收来自flutter的指令twoAct
                else if (methodCall.method.equals("withParams")) {

                    //解析参数
                    String text = methodCall.argument("flutter");

                    //带参数跳转到指定Activity
                    Intent intent = new Intent(MainActivity.this, com.ikang.flutterzcgfourdemo.NativeActivity.class);
                    intent.putExtra("text", text);
                    startActivity(intent);

                    //返回给flutter的参数
                    result.success("success");
                } else {
                    result.notImplemented();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        flutterEngine.getLifecycleChannel().appIsResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        flutterEngine.getLifecycleChannel().appIsInactive();
    }

    @Override
    protected void onStop() {
        super.onStop();
        flutterEngine.getLifecycleChannel().appIsPaused();
    }
}