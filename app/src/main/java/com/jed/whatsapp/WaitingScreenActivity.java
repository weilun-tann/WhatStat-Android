package com.jed.whatsapp;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
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
            "Okay, let me get your graphs ready now!");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_screen);

//        RelativeLayout cl = findViewById(R.id.layout);
//        AnimationDrawable ad = (AnimationDrawable) cl.getBackground();
//        ad.setEnterFadeDuration(2000);
//        ad.setExitFadeDuration(4000);
//        ad.start();
        new logicThread().start();
        new backgroundThread().start();
        new textThread().start();

        // Make the UI interactive via changing text
//        Handler textHandler = new Handler();
//        for (int i = 0; i < 99; i++) {
//            textHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    talkToUser.setText(promptList.get(new Random().nextInt(promptList.size())));
//                }
//            }, 3000);
//        }
    }


    // This thread shall run our 2 computationally-heavy methods
    public class logicThread extends Thread {
        @Override
        public void run() {
            try {
                FileProcessing.readFile(FileProcessing.getUserIntent().getData(), getApplicationContext());
                ReplyTiming.analyzeReplyTimings();
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
            Handler textHandler = new Handler();
            for (int i = 0; i < 99; i++) {
                textHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        talkToUser.setText(promptList.get(new Random().nextInt(promptList.size())));
                    }
                }, 3000);
            }
        }
    }
}
