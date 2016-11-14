package com.xuqi.propertyanimationdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        //设置该窗口显示MyAnimationView组件
        container.addView(new MyAnimationView(this));
    }
}
