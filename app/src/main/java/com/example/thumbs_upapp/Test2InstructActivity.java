package com.example.thumbs_upapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Test2InstructActivity extends AppCompatActivity {

    private TestMessagePackage msgPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2_instruct);
        // Get the Intent that started this activity and extract the string
        msgPackage = (TestMessagePackage) getIntent().getSerializableExtra("TestMessage");
    }

    /** Called when the user taps the START button */
    public void StartTest2(View view) {

        Intent intent = new Intent(this, Test2Activity.class);
        intent.putExtra("TestMessage", msgPackage);
        startActivity(intent);
    }
}