package com.example.thumbs_upapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class Test4Activity extends AppCompatActivity {

    private boolean triggered = false;
    private boolean finished = false;
    private boolean isHit = false;
    private int failedHit = 0;
    private int triggerThreshold1 = 100; // pixels
    private int triggerThreshold2 = 400; // pixels
    private int triggerStart;
    private int triggerEnd;
    private int radius = 60;
    private int reducePercentage = 70;
    private int targetPositionX = 0;
    private int targetPositionY = 0;
    private int originX = 0;
    private int originY = 0;
    int maxHeight;
    int maxWidth;
    private TestMessagePackage msgPackage;
    private int userHits = 0;
    private int totalHits = 8;
    private boolean rightHand = true;
    private Long tStart, tEnd;
    ImageView imageView;
    ImageView imageView1;
    ImageView imageView2;
    RelativeLayout.LayoutParams par;
    RelativeLayout.LayoutParams par1;
    RelativeLayout.LayoutParams par2;
    TextView textView;
    Button nextBtn;
    Random rand = new Random(System.currentTimeMillis());

    private String currentHits() {
        return "" + userHits + " out of " + totalHits + " hits";
    }

    private boolean isEdgeTrigger() {
        //Log.i("TAG", msgPackage.preferHand + "," + maxWidth + "," + triggerStart +"," + triggerEnd);
        if (rightHand) {
            return maxWidth - triggerStart < triggerThreshold1 && triggerStart - triggerEnd > triggerThreshold2;
        } else {
            return triggerStart < triggerThreshold1 && triggerEnd - triggerStart > triggerThreshold2;
        }
    }

    private boolean isAHit(int x, int y) {
        //Log.i("TAG", " (" + positionX + ", " + positionY + ") : (" + x + ", " + y + ")");
        int r = radius;
        if (triggered) {
            r = radius * reducePercentage / 100;
        }
        return (x - targetPositionX) * (x - targetPositionX) + (y - targetPositionY) * (y - targetPositionY) <= r * r;
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
        int r = radius;
        int w = maxWidth;
        if (triggered) {
            r = radius * reducePercentage / 100;
            w = maxWidth * reducePercentage / 100;
        }
        par.leftMargin = targetPositionX - r;
        par.topMargin = targetPositionY - r;
        par.width = r * 2;
        par.height = r * 2;
        imageView.setLayoutParams(par);
        par1.leftMargin = originX;
        par1.topMargin = originY;
        par1.width = w;
        par1.height = w;
        imageView1.setLayoutParams(par1);
        par2.leftMargin = originX;
        par2.topMargin = originY + w;
        par2.width = w;
        par2.height = w;
        imageView2.setLayoutParams(par2);
    }

    private void reduceScreen() {
        if (rightHand) {
            originX = maxWidth * (100 - reducePercentage) / 100;
            originY = maxHeight * (100 - reducePercentage) / 100;
            targetPositionX = maxWidth - (maxWidth - targetPositionX) * reducePercentage / 100;
            targetPositionY = maxHeight - (maxHeight - targetPositionY) * reducePercentage / 100;
        } else {
            originY = maxHeight * (100 - reducePercentage) / 100;
            targetPositionX = targetPositionX * reducePercentage / 100;
            targetPositionY = maxHeight - (maxHeight - targetPositionY) * reducePercentage / 100;
        }
        showImageInNewPosition();
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

    public void gotoNext4(View view) {
        //Long timer = tEnd / totalHits;
        //Log.i("TAG", "time: " + timer.toString());
        //Log.i("TAG", "testMessage: " + message);
        Intent intent = new Intent(this, Test4EvaluateActivity.class);
        intent.putExtra("TestMessage", msgPackage);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test4);

        // Get the Intent that started this activity and extract the string
        msgPackage = (TestMessagePackage) getIntent().getSerializableExtra("TestMessage");
        //Log.i("TAG", "hand: " + msgPackage.preferHand);
        if (msgPackage.preferHand.equals("Left")) {
            rightHand = false;
        } else {
            rightHand = true;
        }

        hideSystemUI();
        nextBtn = (Button) findViewById(R.id.NextBtn4);
        nextBtn.setVisibility(View.GONE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        maxHeight = displayMetrics.heightPixels;
        maxWidth = displayMetrics.widthPixels;
        imageView= (ImageView)findViewById(R.id.imageTarget4);
        par = (RelativeLayout.LayoutParams)imageView.getLayoutParams();
        imageView1= (ImageView)findViewById(R.id.background14);
        par1 = (RelativeLayout.LayoutParams)imageView1.getLayoutParams();
        imageView2= (ImageView)findViewById(R.id.background24);
        par2 = (RelativeLayout.LayoutParams)imageView2.getLayoutParams();
        textView = findViewById(R.id.targetHitText4);
        refreshPosition();
        textView.setText(currentHits());

        // record the start time of listening the user hit the target
        tStart = System.currentTimeMillis();
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
                        msgPackage.timer4 = tEnd / totalHits;
                        msgPackage.accuracy4 = (double) totalHits / (totalHits + failedHit);
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
                            failedHit++;
                        } else {
                            // normal state: detect if there is edge trigger
                            triggerStart = x;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.i("TAG", "moving: (" + x + ", " + y + ")");
                if (triggered) {
                    //Log.i("TAG", "moving: (" + x + ", " + y + ")");
                }
                break;
            case MotionEvent.ACTION_UP:
                //Log.i("TAG", "action up: (" + x + ", " + y + "), touchTime = " + touchTime);
                if (!finished && !isHit)
                    if (triggered) {
                        // triggered state: detect if there is a screen sliding movement
                    } else {
                        // normal state: detect if there is edge trigger
                        triggerEnd = x;
                        if (isEdgeTrigger()) {
                            triggered = true;
                            triggerEnd = 0;
                            triggerStart = 0;
                            reduceScreen();
                        } else {
                            failedHit++;
                        }
                    }
                break;
        }
        return false;
    }
}