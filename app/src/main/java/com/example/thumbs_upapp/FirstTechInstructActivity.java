package com.example.thumbs_upapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class FirstTechInstructActivity extends AppCompatActivity {

    private TestMessagePackage msgPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_tech_instruct);
        // Get the Intent that started this activity and extract the string
        msgPackage = new TestMessagePackage();
        msgPackage.preferHand = getIntent().getStringExtra("PreferHand");
    }

    /** Called when the user taps the START button */
    public void StartFirstTest(View view) {

        Intent intent = new Intent(this, FirstTechTestActivity.class);
        intent.putExtra("TestMessage", msgPackage);
        startActivity(intent);
    }
}