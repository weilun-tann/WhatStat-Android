package com.jed.whatsapp;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WaitingScreenStatsActivity extends AppCompatActivity {

    // CLASS ATTRIBUTES
    public static final String TAG = "WaitingScreenStatsActivity";

    private List<String> promptList = Arrays.asList(
            "Please wait while I work hard to analyze your texts!",
            "I promise this will be done soon!",
            "All right, we're more than halfway there...",
            "You guys sure are close, hang on while I process your long texts!",
            "Almost done...",
            "Aren't you curious to see the results? I know I am!",
            "Okay, let me get your graphs ready now!",
            "Do star this open source project on my GitHub if you like it!");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_screen);
        Log.d(TAG, "WaitingScreenStatsActivity onCreate() CALLED");


        // Perform analysis iff not already done
        if (!ReplyTiming.isInitialized()) {
            new logicThread().start();
            new backgroundThread().start();
            new textThread().start();
        } else {
            Intent intent = new Intent(WaitingScreenStatsActivity.this, MessageStatisticsActivity.class);
            startActivityForResult(intent, 300);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            WaitingScreenStatsActivity.this.finish();
        }
    }

    // This thread shall run our FileRead and AnalyzeReplyTimings methods
    public class logicThread extends Thread {
        @Override
        public void run() {
            try {
                // 1. PERFORM FILE READ
                if (FileProcessing.getUploadedFileURI().toString().contains("firebase")) {
                    FileProcessing.readFile(FileProcessing.getUploadedFileURI(),
                            getApplicationContext(), true);
                } else {
                    FileProcessing.readFile(FileProcessing.getUploadedFileURI(),
                            getApplicationContext(), false);
                }

                // 2. PERFORM FILE ANALYSIS
                ReplyTiming.analyzeReplyTimings();
//                ReplyTiming.debugReplyTiming();

                // 3. REDIRECT TO EITHER THE STATS OR GRAPHING SCREEN
                if (getIntent().getStringExtra("StatsOrGraph").equals("Stats")) {
                    // REDIRECT TO STATS ACTIVITY
                    Intent intent = new Intent(WaitingScreenStatsActivity.this, MessageStatisticsActivity.class);
                    startActivityForResult(intent, 300);
                    overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                    WaitingScreenStatsActivity.this.finish();
                } else {
                    // REDIRECT TO GRAPH ACTIVITY
                    Intent intent = new Intent(WaitingScreenStatsActivity.this, ScatterTimeActivity.class);
                    startActivityForResult(intent, 300);
                    overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                    Log.d(TAG, "logicThread() executed successfully");
                    WaitingScreenStatsActivity.this.finish();

                }
            } catch (IOException e) {
                // THROWN FROM FileProcessing.readFile(...)
                String error = "That's embarrassing, I couldn't quite read the file you gave me...";
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            } catch (NullPointerException e) {
                String error = "I'm sorry, but you haven't yet given me a file to work with!";
                e.printStackTrace();
                WaitingScreenStatsActivity.this.runOnUiThread(() -> Toast.makeText(WaitingScreenStatsActivity.this, error,
                        Toast.LENGTH_LONG).show());
                try {
                    sleep(4800);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                WaitingScreenStatsActivity.this.finish();
            }
        }
    }

    // This thread shall loop our background gradients
    public class backgroundThread extends Thread {
        @Override
        public void run() {
            RelativeLayout cl = findViewById(R.id.layout);
            AnimationDrawable ad = (AnimationDrawable) cl.getBackground();
            ad.setEnterFadeDuration(2000);
            ad.setExitFadeDuration(4000);
            ad.start();
        }
    }

    // This thread shall loop our user prompts
    public class textThread extends Thread {
        @Override
        public void run() {
            final TextView talkToUser = findViewById(R.id.talkToUser);
            for (int i = 0; i < 99; i++) {
                try {
                    talkToUser.setText(promptList.get(new Random().nextInt(promptList.size())));
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
