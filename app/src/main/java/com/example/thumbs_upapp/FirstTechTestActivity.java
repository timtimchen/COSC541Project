package com.example.thumbs_upapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Random;

public class FirstTechTestActivity extends AppCompatActivity {

    private boolean triggered = false;
    private boolean finished = false;
    private boolean isHit = false;
    private int failedHit = 0;
    private int slidingScreenX = 0;
    private int slidingScreenY = 0;
    private int bigTouchThreshold = 1000; // 1000 ms = 1 second
    private int radius = 60;
    private int targetPositionX = 0;
    private int targetPositionY = 0;
    private int originX = 0;
    private int originY = 0;
    private int shiftX = 0;
    private int shiftY = 0;
    int maxHeight;
    int maxWidth;
    private TestMessagePackage msgPackage;
    private int userHits = 0;
    private int totalHits = 3;
    private boolean rightHand = true;
    private Long tStart, tEnd, bigTouchTimer;
    ImageView imageView;
    ImageView imageView1;
//    ImageView imageView2;
    RelativeLayout.LayoutParams par;
    RelativeLayout.LayoutParams par1;
//    RelativeLayout.LayoutParams par2;
    TextView textView;
    Button nextBtn;
    Random rand = new Random(System.currentTimeMillis());

    private String currentHits() {
        return "" + userHits + " out of " + totalHits + " hits";
    }

    private boolean isAHit(int x, int y) {
        //Log.i("TAG", " (" + positionX + ", " + positionY + ") : (" + x + ", " + y + ")");
        return (x - targetPositionX) * (x - targetPositionX) + (y - targetPositionY) * (y - targetPositionY) <= radius * radius;
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showImageInNewPosition() {
        par.leftMargin = targetPositionX - radius;
        par.topMargin = targetPositionY - radius;
        imageView.setLayoutParams(par);
         par1.leftMargin = originX;
        par1.topMargin = originY;
        par1.width = maxWidth;
        par1.height = maxWidth * 2;
        imageView1.setLayoutParams(par1);
        /*
        par2.leftMargin = originX;
        par2.topMargin = originY + maxWidth;
        par2.width = maxWidth;
        par2.height = maxWidth;
        imageView2.setLayoutParams(par2);
        */
    }
    private void refreshPosition() {
        int x, y;
        int threshold = maxWidth + maxHeight - 1500;
        x = rand.nextInt(maxWidth - 2 * radius);
        y = rand.nextInt(maxHeight - 2 * radius);
        while (x + y > threshold) {
            x = rand.nextInt(maxWidth - 2 * radius);
            y = rand.nextInt(maxHeight - 2 * radius);
        }
        if (!rightHand) {
            x = maxWidth - 100 - x;
        }
        targetPositionX = x + radius;
        targetPositionY = y + radius;
        originX = 0;
        originY = 0;
        showImageInNewPosition();
    }

    private void disablePosition() {
        int x, y;
        x = maxWidth + 200;
        y = maxHeight + 200;

        par.leftMargin = x;
        par.topMargin = y;
        targetPositionX = x + radius;
        targetPositionY = y + radius;
        imageView.setLayoutParams(par);
    }

    public void gotoNext(View view) {
        Long timer = tEnd / totalHits;
        //Log.i("TAG", "time: " + timer.toString());
        //Log.i("TAG", "testMessage: " + message);
        Intent intent = new Intent(this, EvaluationActivity.class);
        intent.putExtra("TestMessage", msgPackage);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_tech_test);

        // Get the Intent that started this activity and extract the string
        msgPackage = (TestMessagePackage) getIntent().getSerializableExtra("TestMessage");
        //Log.i("TAG", "hand: " + msgPackage.preferHand);
        if (msgPackage.preferHand.equals("Left")) {
            rightHand = false;
        } else {
            rightHand = true;
        }

        hideSystemUI();

        nextBtn = (Button) findViewById(R.id.FirstToNextBtn);
        nextBtn.setVisibility(View.GONE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        maxHeight = displayMetrics.heightPixels;
        maxWidth = displayMetrics.widthPixels;
        imageView= (ImageView)findViewById(R.id.imageTarget);
        par = (RelativeLayout.LayoutParams)imageView.getLayoutParams();
        imageView1= (ImageView)findViewById(R.id.background1);
        par1 = (RelativeLayout.LayoutParams)imageView1.getLayoutParams();
        /*
        imageView2= (ImageView)findViewById(R.id.background2);
        par2 = (RelativeLayout.LayoutParams)imageView2.getLayoutParams();
         */
        textView = findViewById(R.id.targetHitText);
        refreshPosition();
        textView.setText(currentHits());

        // record the start time of listening the user hit the target
        tStart = System.currentTimeMillis();
        bigTouchTimer = tStart;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Log.i("TAG", "action down: (" + x + ", " + y + ")");
                if (isAHit(x, y)) {
                    triggered = false;
                    isHit = true;
                    refreshPosition();
                    userHits++;
                    textView.setText(currentHits());
                    // check if the test is finished
                    if (userHits >= totalHits) {
                        // record the spend total time in milliseconds
                        tEnd = System.currentTimeMillis() - tStart;
                        msgPackage.timer1 = tEnd;
                        msgPackage.accuracy1 = (double) totalHits / (totalHits + failedHit);
                        // show goto the next button
                        disablePosition();
                        imageView.setVisibility(View.GONE);
                        nextBtn.setVisibility(View.VISIBLE);
                        finished = true;
                    }
                } else {
                    isHit = false;
                    if (!finished) {
                        if (triggered) {
                            // triggered state: detect if there is a screen sliding movement
                            slidingScreenX = x;
                            slidingScreenY = y;
                        } else {
                            // normal state: detect if there is big touch
                            bigTouchTimer = System.currentTimeMillis();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.i("TAG", "moving: (" + x + ", " + y + ")");
                if (triggered) {
                    //Log.i("TAG", "moving: (" + x + ", " + y + ")");
                    // triggered state: detect if there is a screen sliding movement
                    shiftX = x - slidingScreenX;
                    shiftY = y - slidingScreenY;
                    originX += shiftX;
                    originY += shiftY;
                    targetPositionX += shiftX;
                    targetPositionY += shiftY;
                    showImageInNewPosition();

                    slidingScreenX = x;
                    slidingScreenY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                Long touchTime = System.currentTimeMillis() - bigTouchTimer;
                //Log.i("TAG", "action up: (" + x + ", " + y + "), touchTime = " + touchTime);
                if (!finished && !isHit)
                    if (triggered) {
                        // triggered state: detect if there is a screen sliding movement
                    } else {
                        // normal state: detect if there is big touch
                         if (touchTime > bigTouchThreshold) {
                             triggered = true;
                         } else {
                             failedHit++;
                         }
                    }
                break;
        }

        return false;
    }

}