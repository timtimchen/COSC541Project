package com.example.thumbs_upapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private TestMessagePackage msgPackage;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msgPackage = new TestMessagePackage();
        msgPackage.lastTestNum = 6;
        intent = new Intent(this, Test1InstructActivity.class);
    }

    /** Called when the user taps the left_hand button */
    public void chooseLeftHand(View view) {
        msgPackage.preferHand = "Left";
        intent.putExtra("TestMessage", msgPackage);
        startActivity(intent);
    }

    /** Called when the user taps the right_hand button */
    public void chooseRightHand(View view) {
        msgPackage.preferHand = "Right";
        intent.putExtra("TestMessage", msgPackage);
        startActivity(intent);
    }

}