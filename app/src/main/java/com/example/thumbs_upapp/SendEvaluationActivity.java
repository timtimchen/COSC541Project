package com.example.thumbs_upapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class SendEvaluationActivity extends AppCompatActivity {

    private TestMessagePackage msgPackage;

    public void sendEmail(View view) {
        // send out message
        Log.i("Final Results", msgPackage.getCSVlog());

        String[] TO = {"zkagdiwala@gmail.com"};
        //String[] CC = {"xyz@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Thumps up App evaluation result");
        emailIntent.putExtra(Intent.EXTRA_TEXT, msgPackage.getCSVlog());

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SendEvaluationActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_evaluation);

        msgPackage = (TestMessagePackage) getIntent().getSerializableExtra("TestMessage");

    }
}