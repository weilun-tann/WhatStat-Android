package com.jed.whatsapp;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WaitingScreenActivity extends AppCompatActivity {

    // CLASS ATTRIBUTES
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
        try {
            getSupportActionBar().hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        // Perform analysis iff not already done
        if (!FileProcessing.isInitialized()) {
            new logicThread().start();
            new backgroundThread().start();
            new textThread().start();
        } else {
            Intent intent = new Intent(WaitingScreenActivity.this, MessageStatisticsActivity.class);
            startActivityForResult(intent, 300);
            overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
            WaitingScreenActivity.this.finish();
        }
    }

    // This thread shall run our 2 computationally-heavy methods
    public class logicThread extends Thread {
        @Override
        public void run() {
            try {
                // 2 computationally-heavy methods
                FileProcessing.readFile(FileProcessing.getUserIntent().getData(), getApplicationContext());
                ReplyTiming.analyzeReplyTimings();

                // Redirect user to analysis screen
                Intent intent = new Intent(WaitingScreenActivity.this, MessageStatisticsActivity.class);
                startActivityForResult(intent, 300);
                overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                WaitingScreenActivity.this.finish();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Umm... are you sure that was a WhatsApp text" +
                        " file?", Toast.LENGTH_SHORT).show();
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
            final TextView talkToUser = (TextView) findViewById(R.id.talkToUser);
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
