package com.huipengpay.sdk.demo.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView viewById = (TextView)findViewById(R.id.text_view);
        viewById.setText("Hello Word!");
    }
}