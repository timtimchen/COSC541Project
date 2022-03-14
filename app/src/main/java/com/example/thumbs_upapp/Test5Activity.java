package com.example.thumbs_upapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class Test5Activity extends AppCompatActivity {

    private boolean triggered = false;
    private boolean finished = false;
    private boolean isHit = false;
    private int failedHit = 0;
    private int centerX = 0;
    private int centerY = 0;
    private int extensionPercentage = 200;
    private int cursorX = 0;
    private int cursorY = 0;
    private int bigTouchThreshold = 1000; // 1000 ms = 1 second
    private int radius = 60;
    private int targetPositionX = 0;
    private int targetPositionY = 0;
    int maxHeight;
    int maxWidth;
    private TestMessagePackage msgPackage;
    private int userHits = 0;
    private int totalHits = 9;
    private boolean rightHand = true;
    private Long tStart, tEnd, bigTouchTimer;
    ImageView imageView;
    ImageView imageView1;
    RelativeLayout.LayoutParams par;
    RelativeLayout.LayoutParams par1;
    TextView textView;
    Button nextBtn;
    Bitmap myBitmap;
    Canvas myCanvas;

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

    public void gotoNext5(View view) {
        //Log.i("TAG", "testMessage: " + message);
        Intent intent = new Intent(this, Test5EvaluateActivity.class);
        intent.putExtra("TestMessage", msgPackage);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test5);

        // Get the Intent that started this activity and extract the string
        msgPackage = (TestMessagePackage) getIntent().getSerializableExtra("TestMessage");
        //Log.i("TAG", "hand: " + msgPackage.preferHand);
        if (msgPackage.preferHand.equals("Left")) {
            rightHand = false;
        } else {
            rightHand = true;
        }

        hideSystemUI();

        nextBtn = (Button) findViewById(R.id.NextBtn5);
        nextBtn.setVisibility(View.GONE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        maxHeight = displayMetrics.heightPixels;
        maxWidth = displayMetrics.widthPixels;
        imageView= (ImageView)findViewById(R.id.imageTarget5);
        par = (RelativeLayout.LayoutParams)imageView.getLayoutParams();
        imageView1= (ImageView)findViewById(R.id.background15);
        par1 = (RelativeLayout.LayoutParams)imageView1.getLayoutParams();
        par1.width = maxWidth;
        par1.height = maxWidth * 2;
        imageView1.setLayoutParams(par1);
        myBitmap = Bitmap.createBitmap(maxWidth, maxWidth * 2, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas(myBitmap);
        myCanvas.drawColor(Color.TRANSPARENT);

        textView = findViewById(R.id.targetHitText5);
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
                myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                imageView1.setImageBitmap(myBitmap);
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
                        msgPackage.timer1 = tEnd / totalHits;
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
                            centerX = x;
                            centerY = y;
                            cursorX = x;
                            cursorY = y;
                            Paint paint = new Paint();
                            paint.setColor(Color.GREEN);
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setStrokeWidth(16);
                            paint.setAntiAlias(true);
                            myCanvas.drawCircle(centerX, centerY, 8, paint);
                            myCanvas.drawCircle(centerX, centerY, 24, paint);
                            imageView1.setImageBitmap(myBitmap);
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
                    // triggered state
                    myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    Paint paint = new Paint();
                    paint.setColor(Color.GREEN);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(16);
                    paint.setAntiAlias(true);
                    myCanvas.drawCircle(x, y, 8, paint);
                    myCanvas.drawCircle(x, y, 24, paint);
                    cursorX = centerX + (centerX - x) * extensionPercentage / 100;
                    cursorY = centerY + (centerY - y) * extensionPercentage / 100;
                    myCanvas.drawLine(cursorX,cursorY, x, y, paint);
                    myCanvas.drawCircle(cursorX, cursorY, 8, paint);
                    myCanvas.drawCircle(cursorX, cursorY, 24, paint);
                    imageView1.setImageBitmap(myBitmap);
                }
                break;
            case MotionEvent.ACTION_UP:
                Long touchTime = System.currentTimeMillis() - bigTouchTimer;
                //Log.i("TAG", "action up: (" + x + ", " + y + "), touchTime = " + touchTime);
                myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                imageView1.setImageBitmap(myBitmap);
                if (!finished && !isHit)
                    if (triggered) {
                        // triggered state: detect if there is a screen sliding movement
                        if (isAHit(cursorX, cursorY)) {
                            triggered = false;
                            isHit = true;
                            refreshPosition();
                            userHits++;
                            textView.setText(currentHits());
                            // check if the test is finished
                            if (userHits >= totalHits) {
                                // record the spend total time in milliseconds
                                tEnd = System.currentTimeMillis() - tStart;
                                msgPackage.timer1 = tEnd / totalHits;
                                msgPackage.accuracy1 = (double) totalHits / (totalHits + failedHit);
                                // show goto the next button
                                disablePosition();
                                imageView.setVisibility(View.GONE);
                                nextBtn.setVisibility(View.VISIBLE);
                                finished = true;
                            }
                        } else {
                            failedHit++;
                        }
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