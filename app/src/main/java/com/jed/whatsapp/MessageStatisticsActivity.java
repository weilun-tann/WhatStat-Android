package com.jed.whatsapp;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.PrecomputedText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MessageStatisticsActivity extends AppCompatActivity {

    // FOR PROGRESS BAR
    static boolean proceed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_statistics);

        // TODO : IMPLEMENT PROGRESS BAR HERE
        // YOU CANNOT PERFORM THIS IN THE BACKGROUND
        // 1. LEAD USER TO WAITING SCREEN + ANIMATION
        // 2. PERFORM THESE 2 METHODS IN BACKGROUND
        try {
            FileProcessing.readFile(FileProcessing.getUserIntent().getData(), getApplicationContext());
            ReplyTiming.analyzeReplyTimings();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Umm... are you sure that was a WhatsApp text" +
                    " file?", Toast.LENGTH_SHORT).show();
        }

        // SET THE VALUES FOR USER DISPLAY
        TextView leftCircleOne = (TextView) findViewById(R.id.leftCircleOne);
        TextView leftCircleTwo = (TextView) findViewById(R.id.leftCircleTwo);
        TextView leftCircleThree = (TextView) findViewById(R.id.leftCircleThree);
        TextView leftCircleFour = (TextView) findViewById(R.id.leftCircleFour);

        TextView rightCircleOne = (TextView) findViewById(R.id.rightCircleOne);
        TextView rightCircleTwo = (TextView) findViewById(R.id.rightCircleTwo);
        TextView rightCircleThree = (TextView) findViewById(R.id.rightCircleThree);
        TextView rightCircleFour = (TextView) findViewById(R.id.rightCircleFour);

        // RETRIEVE AND DISPLAY ALL RELEVANT FIELDS
        try {
            int senderOneTotalMessages = ReplyTiming.getSenderOneTotalMessages();
            long senderOneTotalWords = ReplyTiming.getSenderOneTotalWords();
            long senderOneWPM = ReplyTiming.getSenderOneTotalWords() / ReplyTiming.getSenderOneTotalMessages();
            double senderOneAvgReplyTiming = ReplyTiming.getSenderOneAverageReplyTiming();

            int senderTwoTotalMessages = ReplyTiming.getSenderTwoTotalMessages();
            long senderTwoTotalWords = ReplyTiming.getSenderTwoTotalWords();
            long senderTwoWPM = ReplyTiming.getSenderTwoTotalWords() / ReplyTiming.getSenderTwoTotalMessages();
            double senderTwoAvgReplyTiming = ReplyTiming.getSenderTwoAverageReplyTiming();

            leftCircleOne.setText("" + senderOneTotalMessages);
            leftCircleTwo.setText("" + senderOneTotalWords);
            leftCircleThree.setText("" + senderOneWPM);
            leftCircleFour.setText("" + senderOneAvgReplyTiming);

            rightCircleOne.setText("" + senderTwoTotalMessages);
            rightCircleTwo.setText("" + senderTwoTotalWords);
            rightCircleThree.setText("" + senderTwoWPM);
            rightCircleFour.setText("" + senderTwoAvgReplyTiming);
        } catch (ArithmeticException e) {
            Toast.makeText(getApplicationContext(), "Umm... could you maybe upload the " +
                    "text file for me first?", Toast.LENGTH_SHORT).show();
        }
    }

    // TODO : FIX BUG IN ASYNC
    public class AsyncFileRead extends AsyncTask<PrecomputedText.Params, Integer, String> {

        @Override
        protected String doInBackground(PrecomputedText.Params... params) {
            try {
                FileProcessing.readFile(FileProcessing.getUserIntent().getData(), getApplicationContext());
                ReplyTiming.analyzeReplyTimings();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Hmm... are you sure that was a text " +
                        "file?", Toast.LENGTH_SHORT).show();
            }
            return "Done";
        }
    }
}

