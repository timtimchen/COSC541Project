package com.example.thumbs_upapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;

public class Test2EvaluateActivity extends AppCompatActivity {

    private TestMessagePackage msgPackage;
    private RatingBar mBar;
    private Intent intent;

    public void submitAndNext2(View view) {

        //Log.i("TAG", "rating: " + mBar.getRating());
        msgPackage.preference2 = mBar.getRating();
        if (msgPackage.lastTestNum == 2)
            intent = new Intent(this, SendEvaluationActivity.class);
        else
            intent = new Intent(this, Test1InstructActivity.class);
        intent.putExtra("TestMessage", msgPackage);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2_evaluate);

        // Get the Intent that started this activity and extract the string
        msgPackage = (TestMessagePackage) getIntent().getSerializableExtra("TestMessage");
        //Log.i("TAG", "accuracy: " + msgPackage.accuracy1);

        mBar = (RatingBar) findViewById(R.id.ratingBar2);
    }
}