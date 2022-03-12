package com.example.thumbs_upapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Test1InstructActivity extends AppCompatActivity {

    private TestMessagePackage msgPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_1_instruct);
        // Get the Intent that started this activity and extract the string
        msgPackage = (TestMessagePackage) getIntent().getSerializableExtra("TestMessage");
    }

    /** Called when the user taps the START button */
    public void StartFirstTest(View view) {

        Intent intent = new Intent(this, Test1Activity.class);
        intent.putExtra("TestMessage", msgPackage);
        startActivity(intent);
    }
}