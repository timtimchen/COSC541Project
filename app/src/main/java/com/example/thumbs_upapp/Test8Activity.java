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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class Test8Activity extends AppCompatActivity {

    private boolean triggered = false;
    private boolean finished = false;
    private boolean isHit = false;
    private int failedHit = 0;
    private int cursorX = 0;
    private int cursorY = 0;
    private int handleX = 0;
    private int handleY = 0;
    private int handleRadius = 64;
    private boolean gotHandle = false;
    private int triggerThreshold1 = 100; // pixels
    private int triggerThreshold2 = 400; // pixels
    private int triggerStart;
    private int triggerEnd;
    private int radius = 60;
    private int targetPositionX = 0;
    private int targetPositionY = 0;
    int maxHeight;
    int maxWidth;
    private TestMessagePackage msgPackage;
    private int userHits = 0;
    private int totalHits = 10;
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
        return (x - targetPositionX) * (x - targetPositionX) + (y - targetPositionY) * (y - targetPositionY) <= radius * radius;
    }

    private boolean getHandle(int x, int y) {
        //Log.i("TAG", " (" + positionX + ", " + positionY + ") : (" + x + ", " + y + ")");
        return (x - handleX) * (x - handleX) + (y - handleY) * (y - handleY) <= handleRadius * handleRadius;
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
        int threshold = maxWidth + maxHeight - 1800;
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

    public void gotoNext8(View view) {
        //Log.i("TAG", "testMessage: " + message);
        Intent intent = new Intent(this, Test8EvaluateActivity.class);
        intent.putExtra("TestMessage", msgPackage);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test8);

        // Get the Intent that started this activity and extract the string
        msgPackage = (TestMessagePackage) getIntent().getSerializableExtra("TestMessage");
        //Log.i("TAG", "hand: " + msgPackage.preferHand);
        if (msgPackage.preferHand.equals("Left")) {
            rightHand = false;
        } else {
            rightHand = true;
        }

        hideSystemUI();

        nextBtn = (Button) findViewById(R.id.NextBtn8);
        nextBtn.setVisibility(View.GONE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        maxHeight = displayMetrics.heightPixels;
        maxWidth = displayMetrics.widthPixels;
        imageView= (ImageView)findViewById(R.id.imageTarget8);
        par = (RelativeLayout.LayoutParams)imageView.getLayoutParams();
        imageView1= (ImageView)findViewById(R.id.background18);
        par1 = (RelativeLayout.LayoutParams)imageView1.getLayoutParams();
        par1.width = maxWidth;
        par1.height = maxWidth * 2;
        imageView1.setLayoutParams(par1);
        myBitmap = Bitmap.createBitmap(maxWidth, maxWidth * 2, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas(myBitmap);
        myCanvas.drawColor(Color.TRANSPARENT);

        textView = findViewById(R.id.targetHitText8);
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
                    myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    imageView1.setImageBitmap(myBitmap);
                    triggered = false;
                    isHit = true;
                    refreshPosition();
                    userHits++;
                    textView.setText(currentHits());
                    // check if the test is finished
                    if (userHits >= totalHits) {
                        // record the spend total time in milliseconds
                        tEnd = System.currentTimeMillis() - tStart;
                        msgPackage.timer8 = tEnd / totalHits;
                        msgPackage.accuracy8 = (double) totalHits / (totalHits + failedHit);
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
                            if (getHandle(x, y)) {
                                // triggered state: detect if there is a screen sliding movement
                                gotHandle = true;
                            } else {
                                // triggered state: drawing cursor
                                gotHandle = false;
                                failedHit++;
                                myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                                cursorX = x;
                                cursorY = y;
                                handleX = x;
                                handleY = y;
                                Paint paint = new Paint();
                                paint.setColor(Color.GREEN);
                                paint.setStyle(Paint.Style.STROKE);
                                paint.setStrokeWidth(16);
                                paint.setAntiAlias(true);
                                myCanvas.drawCircle(handleX, handleY, 8, paint);
                                myCanvas.drawCircle(handleX, handleY, 24, paint);
                                myCanvas.drawCircle(handleX, handleY, 40, paint);
                                myCanvas.drawCircle(handleX, handleY, 56, paint);
                                imageView1.setImageBitmap(myBitmap);
                            }
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
                    // triggered state 1
                    if (gotHandle) {
                        cursorX += x - handleX;
                        cursorY += y - handleY;
                        handleX = x;
                        handleY = y;
                    } else {
                        handleX = x;
                        handleY = y;
                    }
                    myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    Paint paint = new Paint();
                    paint.setColor(Color.GREEN);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(16);
                    paint.setAntiAlias(true);
                    myCanvas.drawCircle(handleX, handleY, 8, paint);
                    myCanvas.drawCircle(handleX, handleY, 24, paint);
                    myCanvas.drawCircle(handleX, handleY, 40, paint);
                    myCanvas.drawCircle(handleX, handleY, 56, paint);
                    myCanvas.drawLine(cursorX,cursorY, x, y, paint);
                    myCanvas.drawCircle(cursorX, cursorY, 8, paint);
                    myCanvas.drawCircle(cursorX, cursorY, 24, paint);
                    imageView1.setImageBitmap(myBitmap);
                }
                break;
            case MotionEvent.ACTION_UP:
                Long touchTime = System.currentTimeMillis() - bigTouchTimer;
                //Log.i("TAG", "action up: (" + x + ", " + y + "), touchTime = " + touchTime);
                if (!finished && !isHit)
                    if (triggered) {
                        // triggered state: detect if there is a screen sliding movement
                        if (isAHit(cursorX, cursorY)) {
                            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                            imageView1.setImageBitmap(myBitmap);
                            triggered = false;
                            isHit = true;
                            refreshPosition();
                            userHits++;
                            textView.setText(currentHits());
                            // check if the test is finished
                            if (userHits >= totalHits) {
                                // record the spend total time in milliseconds
                                tEnd = System.currentTimeMillis() - tStart;
                                msgPackage.timer8 = tEnd / totalHits;
                                msgPackage.accuracy8 = (double) totalHits / (totalHits + failedHit);
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
                        // normal state: detect if there is edge trigger
                        triggerEnd = x;
                        if (isEdgeTrigger()) {
                            triggered = true;
                            triggerEnd = 0;
                            triggerStart = 0;
                        } else {
                            failedHit++;
                        }
                    }
                break;
        }

        return false;
    }
}