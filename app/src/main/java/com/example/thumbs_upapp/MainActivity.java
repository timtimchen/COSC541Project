package com.example.thumbs_upapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the left_hand button */
    public void chooseLeftHand(View view) {
        Intent intent = new Intent(this, FirstTechInstructActivity.class);
        String message = "Left";
        intent.putExtra("PreferHand", message);
        startActivity(intent);
    }

    /** Called when the user taps the right_hand button */
    public void chooseRightHand(View view) {
        Intent intent = new Intent(this, FirstTechInstructActivity.class);
        String message = "Right";
        intent.putExtra("PreferHand", message);
        startActivity(intent);
    }

}