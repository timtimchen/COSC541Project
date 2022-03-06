package com.example.thumbs_upapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class EvaluationActivity extends AppCompatActivity {

    private TestMessagePackage msgPackage;
    private RatingBar mBar;

    public void submitAndNext(View view) {

        //Log.i("TAG", "rating: " + mBar.getRating());
        msgPackage.preference1 = mBar.getRating();
        Intent intent = new Intent(this, SecondTechTestActivity.class);
        intent.putExtra("TestMessage", msgPackage);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        // Get the Intent that started this activity and extract the string
        msgPackage = (TestMessagePackage) getIntent().getSerializableExtra("TestMessage");
        //Log.i("TAG", "accuracy: " + msgPackage.accuracy1);

        TextView tv1 = (TextView)findViewById(R.id.textView12);
        tv1.setText("You finished first test. Please give your rate: ");

        mBar = (RatingBar) findViewById(R.id.ratingBar1);
    }
}