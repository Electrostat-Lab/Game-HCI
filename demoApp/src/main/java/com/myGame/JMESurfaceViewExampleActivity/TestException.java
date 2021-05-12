package com.myGame.JMESurfaceViewExampleActivity;

import android.content.Intent;
import android.os.Bundle;

import com.myGame.R;

import androidx.appcompat.app.AppCompatActivity;

public class TestException extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_exception);
        findViewById(R.id.startButton).setOnClickListener(v -> startActivity(new Intent(TestException.this,JmESurfaceViewExample.class)));

    }
}